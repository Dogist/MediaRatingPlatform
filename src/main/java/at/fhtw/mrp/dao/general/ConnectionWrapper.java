package at.fhtw.mrp.dao.general;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class ConnectionWrapper implements AutoCloseable {

    private Connection connection;

    public ConnectionWrapper() {
        this.connection = DatabaseManager.INSTANCE.getConnection();
        try {
            this.connection.setAutoCommit(false);
        } catch (SQLException e) {
            throw new DataAccessException("Autocommit nicht deaktivierbar", e);
        }
    }

    public void commitTransaction() {
        if (this.connection != null) {
            try {
                this.connection.commit();
            } catch (SQLException e) {
                throw new DataAccessException("Commit der Transaktion nicht erfolgreich", e);
            }
        }
    }

    public void rollbackTransaction() {
        if (this.connection != null) {
            try {
                this.connection.rollback();
            } catch (SQLException e) {
                throw new DataAccessException("Rollback der Transaktion nicht erfolgreich", e);
            }
        }
    }

    public void finishWork() {
        if (this.connection != null) {
            try {
                this.connection.close();
                this.connection = null;
            } catch (SQLException e) {
                throw new DataAccessException("Schließen der Connection nicht erfolgreich", e);
            }
        }
    }

    /**
     *
     * @see Connection#prepareStatement(String)
     */
    public PreparedStatement prepareStatement(String sql) {
        if (this.connection != null) {
            try {
                return this.connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            } catch (SQLException e) {
                throw new DataAccessException("Erstellen eines PreparedStatements nicht erfolgreich", e);
            }
        }
        throw new DataAccessException("ConnectionWrapper hat keine aktive Connection zur Verfügung");
    }

    @Override
    public void close() {
        this.finishWork();
    }
}
