package at.fhtw.mrp.dao;

import at.fhtw.mrp.dao.general.ConnectionWrapper;
import at.fhtw.mrp.dao.general.DataAccessException;
import at.fhtw.mrp.dao.general.DataConflictException;
import at.fhtw.mrp.entity.UserEntity;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDao {

    public void createUser(String username, String passwordHash) throws DataConflictException {
        try (ConnectionWrapper cw = new ConnectionWrapper()) {
            PreparedStatement preparedStatement = cw.prepareStatement("INSERT INTO USER_ACC(USERNAME, PASSWORD) VALUES (?, ?)");
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, passwordHash);

            preparedStatement.executeUpdate();

            cw.commitTransaction();
        } catch (SQLException e) {
            if (e.getSQLState().equals("23505")) {
                throw new DataConflictException(e.getMessage());
            } else {
                throw new DataAccessException("Es gab einen Fehler beim Erstellen des Benutzers.", e);
            }
        }
    }

    public boolean checkUserAuth(String username, String passwordHash) {
        try (ConnectionWrapper cw = new ConnectionWrapper()) {
            PreparedStatement preparedStatement = cw.prepareStatement("SELECT COUNT(*) FROM USER_ACC WHERE USERNAME = ? AND PASSWORD = ?");
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, passwordHash);

            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) == 1;
            }
            return false;
        } catch (SQLException e) {
            throw new DataAccessException("Es gab einen Fehler beim Erstellen des Benutzers.", e);
        }
    }

    public UserEntity getUser(String username) {
        try (ConnectionWrapper cw = new ConnectionWrapper()) {
            PreparedStatement preparedStatement = cw.prepareStatement("SELECT * FROM USER_ACC WHERE USERNAME = ?");
            preparedStatement.setString(1, username);

            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return new UserEntity(rs.getLong("USER_ID"),
                        rs.getString("USERNAME"),
                        "",
                        rs.getString("EMAIL"),
                        rs.getString("FAVORITE_GENRE")
                );
            }
            return null;

        } catch (SQLException e) {
            throw new DataAccessException("Es gab einen Fehler beim Holen des Benutzers.", e);
        }
    }
}
