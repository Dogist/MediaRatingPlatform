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

    /**
     * Methode, welche die Mock-DB im CDI vorbereitet.
     * <p>
     * Diese Methode muss zusammen mit {@link #setupMockConnection(DatabaseManager)} ()} verwendet werden, damit sie funktioniert.
     * Wird im Integration-Test benötigt, da ein Starten des Servers pro Test nicht funktioniert, aber die DB pro Test initialisiert wird.
     */
    public static DatabaseManager setupMockCDI() {
        DatabaseManager databaseManager = Mockito.mock(DatabaseManager.class);

        initCDI(databaseManager);

        return databaseManager;
    }

    /**
     * Methode welche die Mock-Postgres-Connections vorbereitet.
     * Diese Methode muss zusammen mit {@link #setupMockCDI()} verwendet werden, damit sie funktioniert.
     */
    public void setupMockConnection(DatabaseManager databaseManager) {
        Mockito.reset(databaseManager);
        Mockito.when(databaseManager.getConnection()).thenAnswer(invocation -> new ConnectionWrapper(db.getTestDatabase().getConnection()));
    }

    /**
     * Methode welche CDI und Mock-Postgres-DB komplett aufsetzt.
     */
    public void setup() throws SQLException {
        DatabaseManager databaseManager = setupMockCDI();
        setupMockConnection(databaseManager);
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
