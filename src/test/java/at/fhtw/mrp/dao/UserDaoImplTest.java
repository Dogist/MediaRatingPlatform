package at.fhtw.mrp.dao;

import at.fhtw.mrp.dao.general.DataConflictException;
import at.fhtw.mrp.dao.general.DatabaseManager;
import at.fhtw.mrp.dto.MediaEntryType;
import at.fhtw.mrp.dto.UserProfileUpdateDTO;
import at.fhtw.mrp.entity.MediaEntryEntity;
import at.fhtw.mrp.entity.RatingEntity;
import at.fhtw.mrp.entity.UserEntity;
import at.fhtw.mrp.service.CDI;
import at.fhtw.mrp.service.HashUtil;
import at.fhtw.mrp.util.AbstractDBTest;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.migrationsupport.rules.EnableRuleMigrationSupport;

import java.sql.SQLException;
import java.util.List;

@EnableRuleMigrationSupport
class UserDaoImplTest extends AbstractDBTest {

    private UserDao userDao;
    private RatingDao ratingDao;
    private MediaEntryDao mediaEntryDao;

    @BeforeEach
    public void setup() throws SQLException {
        super.setup();
        userDao = new UserDaoImpl(CDI.INSTANCE.getService(DatabaseManager.class));
        ratingDao = new RatingDaoImpl(CDI.INSTANCE.getService(DatabaseManager.class), userDao);
        mediaEntryDao = new MediaEntryDaoImpl(CDI.INSTANCE.getService(DatabaseManager.class), userDao, ratingDao);
    }

    @Test
    void createUser_valid() throws DataConflictException {
        userDao.createUser("Test", HashUtil.generateHashedPassword("Test"));
    }

    @Test
    void createUser_duplicateFail() throws DataConflictException {
        userDao.createUser("Test", HashUtil.generateHashedPassword("Test"));
        Assert.assertThrows(DataConflictException.class,
                () -> userDao.createUser("Test", HashUtil.generateHashedPassword("Test1")));
    }

    @Test
    void getUserByIdAndUsername_valid() throws DataConflictException {
        userDao.createUser("Test", HashUtil.generateHashedPassword("Test"));
        UserEntity user = userDao.getUserByUsername("Test");

        Assertions.assertNotNull(user);

        UserEntity userById = userDao.getUserById(user.getId());

        Assertions.assertEquals(user, userById);
    }

    @Test
    void getUserByIdAndUsername_null() {
        UserEntity user = userDao.getUserByUsername("Test");
        Assertions.assertNull(user);

        UserEntity userById = userDao.getUserById(1L);
        Assertions.assertNull(userById);
    }

    @Test
    void hydrateUserStatistics() throws DataConflictException {
        userDao.createUser("Test", HashUtil.generateHashedPassword("Test"));

        UserEntity user = userDao.getUserByUsername("Test");
        Assertions.assertNull(user.getRatingAvg());
        Assertions.assertNull(user.getRatingCount());

        userDao.hydrateUserStatistics(user);

        Assertions.assertEquals(0, user.getRatingAvg());
        Assertions.assertEquals(0, user.getRatingCount());
    }

    @Test
    void getUsersByFavoriteMedia() throws DataConflictException {
        userDao.createUser("Test", HashUtil.generateHashedPassword("Test"));
        UserEntity user = userDao.getUserByUsername("Test");

        MediaEntryEntity mediaEntry = mediaEntryDao.createMediaEntry(new MediaEntryEntity(MediaEntryType.GAME, "TestMedia",
                "TestDesc", 2025, List.of("action", "sci-fi"), 18, user));

        List<UserEntity> usersByFavoriteMedia_empty = userDao.getUsersByFavoriteMedia(mediaEntry.getId());
        Assertions.assertTrue(usersByFavoriteMedia_empty.isEmpty());

        mediaEntryDao.setMediaEntryFavorite(mediaEntry.getId(), user.getId());

        List<UserEntity> usersByFavoriteMedia_filled = userDao.getUsersByFavoriteMedia(mediaEntry.getId());
        Assertions.assertTrue(usersByFavoriteMedia_filled.contains(user));

    }

    @Test
    void getUsersByLikedRating() throws DataConflictException {
        userDao.createUser("Test", HashUtil.generateHashedPassword("Test"));
        UserEntity user = userDao.getUserByUsername("Test");

        MediaEntryEntity mediaEntry = mediaEntryDao.createMediaEntry(new MediaEntryEntity(MediaEntryType.GAME, "TestMedia",
                "TestDesc", 2025, List.of("action", "sci-fi"), 18, user));

        RatingEntity rating = ratingDao.createRating(new RatingEntity(user, mediaEntry.getId(), (short) 5, "TEST Comment"));

        List<UserEntity> usersByLikedRating_empty = userDao.getUsersByLikedRating(rating.getId());
        Assertions.assertTrue(usersByLikedRating_empty.isEmpty());

        ratingDao.setRatingLiked(rating.getId(), user.getId());

        List<UserEntity> usersByLikedRating_filled = userDao.getUsersByLikedRating(rating.getId());
        Assertions.assertTrue(usersByLikedRating_filled.contains(user));
    }

    @Test
    void updateUser() throws DataConflictException {
        userDao.createUser("Test", HashUtil.generateHashedPassword("Test"));
        UserEntity user = userDao.getUserByUsername("Test");

        Assertions.assertNull(user.getEmail());
        Assertions.assertNull(user.getFavoriteGenre());

        String mail = "mail@test.com";
        String genre = "action";
        userDao.updateUser(user.getId(), new UserProfileUpdateDTO(mail, genre));
        user = userDao.getUserByUsername("Test");


        Assertions.assertEquals(mail, user.getEmail());
        Assertions.assertEquals(genre, user.getFavoriteGenre());
    }

    @Test
    void getUsersByRatingCount() throws DataConflictException {
        userDao.createUser("Test1", HashUtil.generateHashedPassword("Test"));
        userDao.createUser("Test2", HashUtil.generateHashedPassword("Test"));
        userDao.createUser("Test3", HashUtil.generateHashedPassword("Test"));

        UserEntity user1 = userDao.getUserByUsername("Test1");
        UserEntity user2 = userDao.getUserByUsername("Test2");
        UserEntity user3 = userDao.getUserByUsername("Test3");

        MediaEntryEntity mediaEntry = mediaEntryDao.createMediaEntry(new MediaEntryEntity(MediaEntryType.GAME, "TestMedia",
                "TestDesc", 2025, List.of("action", "sci-fi"), 18, user1));

        ratingDao.createRating(new RatingEntity(user1, mediaEntry.getId(), (short) 5, "TEST Comment1"));
        ratingDao.createRating(new RatingEntity(user2, mediaEntry.getId(), (short) 4, "TEST Comment2"));
        ratingDao.createRating(new RatingEntity(user3, mediaEntry.getId(), (short) 3, "TEST Comment3"));

        mediaEntry = mediaEntryDao.createMediaEntry(new MediaEntryEntity(MediaEntryType.MOVIE, "TestMediaMovie",
                "TestDesc", 2024, List.of("action", "sci-fi"), 18, user1));

        ratingDao.createRating(new RatingEntity(user1, mediaEntry.getId(), (short) 5, "TEST Comment1"));
        ratingDao.createRating(new RatingEntity(user2, mediaEntry.getId(), (short) 4, "TEST Comment2"));

        mediaEntry = mediaEntryDao.createMediaEntry(new MediaEntryEntity(MediaEntryType.SERIES, "TestMediaSeries",
                "TestDesc", 2023, List.of("action", "sci-fi"), 18, user1));

        ratingDao.createRating(new RatingEntity(user2, mediaEntry.getId(), (short) 2, "TEST Comment2"));


        List<UserEntity> usersByRatingCount = userDao.getUsersByRatingCount();
        // Reihenfolge der User prüfen
        Assertions.assertEquals(1, usersByRatingCount.indexOf(user1));
        Assertions.assertEquals(0, usersByRatingCount.indexOf(user2));
        Assertions.assertEquals(2, usersByRatingCount.indexOf(user3));
        Assertions.assertEquals(3, usersByRatingCount.size());
    }
}