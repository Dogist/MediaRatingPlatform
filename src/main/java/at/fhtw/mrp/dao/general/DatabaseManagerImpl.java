package at.fhtw.mrp.dao.general;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManagerImpl implements DatabaseManager {

    @Override
    public ConnectionWrapper getConnection() {
        try {
            Connection connection = DriverManager.getConnection(
                    "jdbc:postgresql://localhost:5432/mrp",
                    "mrp",
                    "mrp");

            return new ConnectionWrapper(connection);
        } catch (SQLException e) {
            throw new DataAccessException("Datenbankverbindungsaufbau nicht erfolgreich", e);
        }
    }
}
