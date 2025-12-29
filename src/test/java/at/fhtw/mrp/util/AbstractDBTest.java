package at.fhtw.mrp.util;

import at.fhtw.mrp.dao.*;
import at.fhtw.mrp.dao.general.ConnectionWrapper;
import at.fhtw.mrp.dao.general.DatabaseManager;
import at.fhtw.mrp.service.*;
import io.zonky.test.db.postgres.embedded.DatabasePreparer;
import io.zonky.test.db.postgres.junit.EmbeddedPostgresRules;
import io.zonky.test.db.postgres.junit.PreparedDbRule;
import org.junit.Rule;
import org.mockito.Mockito;

import javax.sql.DataSource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Abstrakter Basis-Test welcher Code beinhält um eine In-Memory Postgres-DB zu verwenden
 * und diese bei {@link CDI} zu registrieren.
 */
public class AbstractDBTest implements DatabasePreparer {

    @Rule
    public PreparedDbRule db = EmbeddedPostgresRules.preparedDatabase(this);


    public void setup() throws SQLException {
        DatabaseManager databaseManager = Mockito.mock(DatabaseManager.class);
        Mockito.when(databaseManager.getConnection()).thenAnswer(invocation -> new ConnectionWrapper(db.getTestDatabase().getConnection()));

        initCDI(databaseManager);
    }

    private static void initCDI(DatabaseManager databaseManager) {
        UserDao userDao = new UserDaoImpl(databaseManager);
        RatingDao ratingDao = new RatingDaoImpl(databaseManager, userDao);
        MediaEntryDao mediaEntryDao = new MediaEntryDaoImpl(databaseManager, userDao, ratingDao);

        CDI.INSTANCE.overrideService(DatabaseManager.class, databaseManager);
        CDI.INSTANCE.overrideService(UserDao.class, userDao);
        CDI.INSTANCE.overrideService(RatingDao.class, ratingDao);
        CDI.INSTANCE.overrideService(MediaEntryDao.class, mediaEntryDao);
        CDI.INSTANCE.overrideService(AuthService.class, new BearerAuthServiceImpl(userDao));
        CDI.INSTANCE.overrideService(UserService.class, new UserServiceImpl(userDao));
        CDI.INSTANCE.overrideService(MediaService.class, new MediaServiceImpl(mediaEntryDao));
        CDI.INSTANCE.overrideService(RatingService.class, new RatingServiceImpl(mediaEntryDao, ratingDao));
    }

    @Override
    public void prepare(DataSource ds) throws SQLException {
        try (Connection connection = ds.getConnection()) {
            String sql = Files.readString(Path.of("init.sql").toAbsolutePath());
            connection.prepareStatement(sql).execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
