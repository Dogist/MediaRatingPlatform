package at.fhtw.mrp.dao;

import at.fhtw.mrp.dao.general.DataConflictException;
import at.fhtw.mrp.dao.general.DatabaseManager;
import at.fhtw.mrp.dto.MediaEntryType;
import at.fhtw.mrp.entity.MediaEntryEntity;
import at.fhtw.mrp.entity.RatingEntity;
import at.fhtw.mrp.entity.UserEntity;
import at.fhtw.mrp.service.CDI;
import at.fhtw.mrp.service.HashUtil;
import at.fhtw.mrp.util.AbstractDBTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.migrationsupport.rules.EnableRuleMigrationSupport;

import java.sql.SQLException;
import java.util.List;

/**
 * Verschiedene Tests für {@link RatingDaoImpl} welche die verschiedenen Funktionen aufruft,
 * und sie so weit testet, dass zumindest das SQL dahinter mit Postgres funktioniert, via In-Memory-DB.
 */
@EnableRuleMigrationSupport
class RatingDaoImplTest extends AbstractDBTest {

    private RatingDao ratingDao;
    private UserEntity user;
    private MediaEntryEntity firstMediaEntry;
    private RatingEntity firstRating;

    @BeforeEach
    public void setup() throws SQLException {
        super.setup();
        UserDao userDao = new UserDaoImpl(CDI.INSTANCE.getService(DatabaseManager.class));
        ratingDao = new RatingDaoImpl(CDI.INSTANCE.getService(DatabaseManager.class), userDao);
        MediaEntryDao mediaEntryDao = new MediaEntryDaoImpl(CDI.INSTANCE.getService(DatabaseManager.class), userDao, ratingDao);

        try {
            userDao.createUser("Test", HashUtil.generateHashedPassword("Test"));
            user = userDao.getUserByUsername("Test");

            MediaEntryEntity mediaEntry = mediaEntryDao.createMediaEntry(new MediaEntryEntity(MediaEntryType.GAME, "TestMedia",
                    "TestDesc", 2025, List.of("action", "sci-fi"), 18, user));
            firstMediaEntry = mediaEntry;
            firstRating = ratingDao.createRating(new RatingEntity(user, mediaEntry.getId(), (short) 5, "TEST Comment1"));


            mediaEntry = mediaEntryDao.createMediaEntry(new MediaEntryEntity(MediaEntryType.MOVIE, "TestMediaMovie",
                    "TestDesc", 2024, List.of("action", "sci-fi"), 18, user));

            ratingDao.createRating(new RatingEntity(user, mediaEntry.getId(), (short) 4, "TEST Comment2"));

            mediaEntry = mediaEntryDao.createMediaEntry(new MediaEntryEntity(MediaEntryType.SERIES, "TestMediaSeries",
                    "TestDesc", 2023, List.of("action", "sci-fi"), 18, user));

            ratingDao.createRating(new RatingEntity(user, mediaEntry.getId(), (short) 2, "TEST Comment2"));
        } catch (DataConflictException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void getRatingsForUser() {
        List<RatingEntity> ratingsForUser = ratingDao.getRatingsForUser(user.getId());
        Assertions.assertEquals(3, ratingsForUser.size());

        List<RatingEntity> ratingsForNonUser = ratingDao.getRatingsForUser(0L);
        Assertions.assertEquals(0, ratingsForNonUser.size());
    }

    @Test
    void getRatingsForMediaEntry() {
        List<RatingEntity> ratingsForMediaEntry = ratingDao.getRatingsForMediaEntry(firstMediaEntry.getId());
        Assertions.assertEquals(1, ratingsForMediaEntry.size());

        List<RatingEntity> ratingsForNonMediaEntry = ratingDao.getRatingsForMediaEntry(0);
        Assertions.assertEquals(0, ratingsForNonMediaEntry.size());
    }

    @Test
    void getRating() {
        RatingEntity rating = ratingDao.getRating(firstRating.getId());
        Assertions.assertEquals(firstRating, rating);
    }

    @Test
    void createRating() {
        RatingEntity newRating = new RatingEntity(user, firstMediaEntry.getId(), (short) 1, "TEST Comment7");
        RatingEntity createdRating = ratingDao.createRating(newRating);
        RatingEntity newGotRating = ratingDao.getRating(createdRating.getId());

        Assertions.assertEquals(createdRating, newGotRating);
    }

    @Test
    void updateRating() {
        RatingEntity firstRatingCopy = new RatingEntity(firstRating.getId(), firstRating.getCreator(),
                firstRating.getMediaEntryId(), firstRating.getRating(), firstRating.getComment(),
                firstRating.getTimestamp(), firstRating.isConfirmed(), firstRating.getLikedByUsers());

        String newComment = "Neues Test Comment";
        short newRating = (short) 1;
        firstRatingCopy.setRating(newRating);
        firstRatingCopy.setComment(newComment);
        ratingDao.updateRating(firstRatingCopy);

        RatingEntity newModifiedRating = ratingDao.getRating(firstRating.getId());
        Assertions.assertEquals(newRating, newModifiedRating.getRating());
        Assertions.assertEquals(newComment, newModifiedRating.getComment());

    }

    @Test
    void deleteRating() {
        ratingDao.deleteRating(firstRating.getId());
        RatingEntity rating = ratingDao.getRating(firstRating.getId());
        Assertions.assertNull(rating);
    }

    @Test
    void setRatingLiked() {
        Assertions.assertFalse(firstRating.getLikedByUsers().contains(user));

        ratingDao.setRatingLiked(firstRating.getId(), user.getId());

        RatingEntity rating = ratingDao.getRating(firstRating.getId());
        Assertions.assertTrue(rating.getLikedByUsers().contains(user));
    }

    @Test
    void removeRatingLiked() {
        Assertions.assertFalse(firstRating.getLikedByUsers().contains(user));

        ratingDao.setRatingLiked(firstRating.getId(), user.getId());

        RatingEntity ratingLiked = ratingDao.getRating(firstRating.getId());
        Assertions.assertTrue(ratingLiked.getLikedByUsers().contains(user));

        ratingDao.removeRatingLiked(firstRating.getId(), user.getId());

        RatingEntity ratingUnliked = ratingDao.getRating(firstRating.getId());
        Assertions.assertFalse(ratingUnliked.getLikedByUsers().contains(user));
    }
}