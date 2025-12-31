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
 * Verschiedene Tests für {@link MediaEntryDaoImpl} welche die verschiedenen Funktionen aufruft,
 * und sie so weit testet, dass zumindest das SQL dahinter mit Postgres funktioniert, via In-Memory-DB.
 */
@EnableRuleMigrationSupport
class MediaEntryDaoImplTest extends AbstractDBTest {

    private RatingDao ratingDao;
    private MediaEntryDao mediaEntryDao;
    private UserEntity user;
    private MediaEntryEntity firstMediaEntry;

    @BeforeEach
    public void setup() throws SQLException {
        super.setup();
        UserDao userDao = new UserDaoImpl(CDI.INSTANCE.getService(DatabaseManager.class));
        ratingDao = new RatingDaoImpl(CDI.INSTANCE.getService(DatabaseManager.class), userDao);
        mediaEntryDao = new MediaEntryDaoImpl(CDI.INSTANCE.getService(DatabaseManager.class), userDao, ratingDao);

        try {
            userDao.createUser("Test", HashUtil.generateHashedPassword("Test"));
            user = userDao.getUserByUsername("Test");

            firstMediaEntry = mediaEntryDao.createMediaEntry(new MediaEntryEntity(MediaEntryType.GAME, "TestMedia",
                    "TestDesc", 2025, List.of("action", "sci-fi"), 18, user));
        } catch (DataConflictException e) {
            throw new RuntimeException(e);
        }
    }


    @Test
    void getMediaEntriesByUserNotRated() {
        List<MediaEntryEntity> mediaEntriesByUser = mediaEntryDao.getMediaEntriesByUser(user.getId(), false);

        Assertions.assertTrue(mediaEntriesByUser.contains(firstMediaEntry));
    }

    @Test
    void getMediaEntriesByUserRated() {
        List<MediaEntryEntity> mediaEntriesByUser = mediaEntryDao.getMediaEntriesByUser(user.getId(), true);

        Assertions.assertTrue(mediaEntriesByUser.isEmpty());

        ratingDao.createRating(new RatingEntity(user, firstMediaEntry.getId(), (short) 3, "comment"));
        mediaEntriesByUser = mediaEntryDao.getMediaEntriesByUser(user.getId(), true);

        Assertions.assertFalse(mediaEntriesByUser.isEmpty());
    }

    @Test
    void searchMediaEntries() {
        RatingEntity rating1 = ratingDao.createRating(new RatingEntity(user, firstMediaEntry.getId(), (short) 5, "TEST Comment1"));

        MediaEntryEntity mediaEntry2 = mediaEntryDao.createMediaEntry(new MediaEntryEntity(MediaEntryType.MOVIE, "TestMediaMovie",
                "TestDesc", 2024, List.of("action", "sci-fi"), 18, user));
        ratingDao.createRating(new RatingEntity(user, mediaEntry2.getId(), (short) 4, "TEST Comment2"));

        MediaEntryEntity mediaEntry3 = mediaEntryDao.createMediaEntry(new MediaEntryEntity(MediaEntryType.SERIES, "TestMediaSeries",
                "TestDesc", 2023, List.of("action", "sci-fi"), 18, user));
        ratingDao.createRating(new RatingEntity(user, mediaEntry3.getId(), (short) 3, "TEST Comment2"));

        List<MediaEntryEntity> searchAllEntries = mediaEntryDao.searchMediaEntries(firstMediaEntry.getTitle().substring(0, 3),
                "action",
                null, null,
                firstMediaEntry.getAgeRestriction(), (short) 1,
                "score");
        // check count
        Assertions.assertEquals(3, searchAllEntries.size());
        // check order
        Assertions.assertEquals(firstMediaEntry, searchAllEntries.get(0));
        Assertions.assertEquals(mediaEntry2, searchAllEntries.get(1));
        Assertions.assertEquals(mediaEntry3, searchAllEntries.get(2));

        searchAllEntries = mediaEntryDao.searchMediaEntries(firstMediaEntry.getTitle().substring(0, 3),
                "action",
                firstMediaEntry.getMediaType().name(), firstMediaEntry.getReleaseYear(),
                firstMediaEntry.getAgeRestriction(), rating1.getRating(),
                "title");
        // check count
        Assertions.assertEquals(1, searchAllEntries.size());
        // check order
        Assertions.assertEquals(firstMediaEntry, searchAllEntries.getFirst());
    }

    @Test
    void getMediaEntriesFavoritedByUserAndSetMediaEntryFavorite() {
        List<MediaEntryEntity> mediaEntriesFavoritedByUser = mediaEntryDao.getMediaEntriesFavoritedByUser(user.getId());
        Assertions.assertFalse(mediaEntriesFavoritedByUser.contains(firstMediaEntry));

        mediaEntryDao.setMediaEntryFavorite(firstMediaEntry.getId(), user.getId());

        mediaEntriesFavoritedByUser = mediaEntryDao.getMediaEntriesFavoritedByUser(user.getId());
        Assertions.assertTrue(mediaEntriesFavoritedByUser.contains(firstMediaEntry));
    }

    @Test
    void createMediaEntry() {
        MediaEntryEntity mediaEntry = mediaEntryDao.createMediaEntry(new MediaEntryEntity(MediaEntryType.MOVIE, "TestMediaMovie",
                "TestDesc", 2024, List.of("action", "sci-fi"), 18, user));

        MediaEntryEntity retrievedMediaEntry = mediaEntryDao.getMediaEntry(mediaEntry.getId());

        Assertions.assertEquals(mediaEntry, retrievedMediaEntry);
    }

    @Test
    void getMediaEntry() {
        MediaEntryEntity retrievedMediaEntry = mediaEntryDao.getMediaEntry(firstMediaEntry.getId());

        Assertions.assertEquals(firstMediaEntry, retrievedMediaEntry);
    }

    @Test
    void updateMediaEntry() {
        MediaEntryEntity mediaEntryCopy = new MediaEntryEntity(firstMediaEntry.getId(), firstMediaEntry.getMediaType(),
                firstMediaEntry.getTitle(), firstMediaEntry.getDescription(), firstMediaEntry.getReleaseYear(),
                firstMediaEntry.getGenres(), firstMediaEntry.getAgeRestriction(), firstMediaEntry.getCreator(),
                firstMediaEntry.getUsersFavorited(), firstMediaEntry.getRatings(), firstMediaEntry.getAverageRating());

        String newTitle = "New Title";
        mediaEntryCopy.setTitle(newTitle);
        String newDesc = "Neue TestDesc";
        mediaEntryCopy.setDescription(newDesc);
        List<String> newActions = List.of("action");
        mediaEntryCopy.setGenres(newActions);

        mediaEntryDao.updateMediaEntry(mediaEntryCopy);
        firstMediaEntry = mediaEntryDao.getMediaEntry(mediaEntryCopy.getId());

        Assertions.assertEquals(newTitle, firstMediaEntry.getTitle());
        Assertions.assertEquals(newDesc, firstMediaEntry.getDescription());
        Assertions.assertEquals(newActions, firstMediaEntry.getGenres());
    }

    @Test
    void deleteMediaEntry() {
        MediaEntryEntity retrievedMediaEntry = mediaEntryDao.getMediaEntry(firstMediaEntry.getId());
        Assertions.assertNotNull(retrievedMediaEntry);

        mediaEntryDao.deleteMediaEntry(firstMediaEntry.getId());

        retrievedMediaEntry = mediaEntryDao.getMediaEntry(firstMediaEntry.getId());
        Assertions.assertNull(retrievedMediaEntry);
    }

    @Test
    void removeMediaEntryFavorite() {
        List<MediaEntryEntity> mediaEntriesFavoritedByUser = mediaEntryDao.getMediaEntriesFavoritedByUser(user.getId());
        Assertions.assertFalse(mediaEntriesFavoritedByUser.contains(firstMediaEntry));

        mediaEntryDao.setMediaEntryFavorite(firstMediaEntry.getId(), user.getId());

        mediaEntriesFavoritedByUser = mediaEntryDao.getMediaEntriesFavoritedByUser(user.getId());
        Assertions.assertTrue(mediaEntriesFavoritedByUser.contains(firstMediaEntry));

        mediaEntryDao.removeMediaEntryFavorite(firstMediaEntry.getId(), user.getId());

        mediaEntriesFavoritedByUser = mediaEntryDao.getMediaEntriesFavoritedByUser(user.getId());
        Assertions.assertFalse(mediaEntriesFavoritedByUser.contains(firstMediaEntry));
    }
}