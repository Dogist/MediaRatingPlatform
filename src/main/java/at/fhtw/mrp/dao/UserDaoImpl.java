package at.fhtw.mrp.dao;

import at.fhtw.mrp.dao.general.ConnectionWrapper;
import at.fhtw.mrp.dao.general.DataAccessException;
import at.fhtw.mrp.dao.general.DataConflictException;
import at.fhtw.mrp.dto.UserProfileUpdateDTO;
import at.fhtw.mrp.entity.UserEntity;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDaoImpl implements UserDao {

    @Override
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

    @Override
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

    @Override
    public UserEntity getUserById(Long userId) {
        try (ConnectionWrapper cw = new ConnectionWrapper()) {
            PreparedStatement preparedStatement = cw.prepareStatement("SELECT * FROM USER_ACC WHERE USER_ID = ?");
            preparedStatement.setLong(1, userId);

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

    @Override
    public UserEntity getUserByUsername(String username) {
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

    @Override
    public List<UserEntity> getUserByFavoriteMedia(long mediaEntryId) {
        try (ConnectionWrapper cw = new ConnectionWrapper()) {
            PreparedStatement preparedStatement = cw.prepareStatement("SELECT USER_ACC.* FROM USER_ACC JOIN public.user_favorite_media ufm on USER_ACC.user_id = ufm.user_id WHERE ufm.media_entry_id = ?");
            preparedStatement.setLong(1, mediaEntryId);

            return mapUserList(preparedStatement);

        } catch (SQLException e) {
            throw new DataAccessException("Es gab einen Fehler beim Holen des Benutzers.", e);
        }
    }

    @Override
    public List<UserEntity> getUserByLikedRating(long ratingId) {
        try (ConnectionWrapper cw = new ConnectionWrapper()) {
            PreparedStatement preparedStatement = cw.prepareStatement("SELECT USER_ACC.* FROM USER_ACC JOIN public.user_like_rating ufr on USER_ACC.user_id = ufr.user_id WHERE ufr.rating_id = ?");
            preparedStatement.setLong(1, ratingId);

            return mapUserList(preparedStatement);

        } catch (SQLException e) {
            throw new DataAccessException("Es gab einen Fehler beim Holen des Benutzers.", e);
        }
    }

    @Override
    public boolean updateUser(Long userId, UserProfileUpdateDTO userEntity) {
        try (ConnectionWrapper cw = new ConnectionWrapper()) {
            PreparedStatement preparedStatement = cw.prepareStatement("UPDATE USER_ACC SET email = ?, favorite_genre = ? WHERE USER_ID = ?");
            preparedStatement.setString(1, userEntity.getEmail());
            preparedStatement.setString(2, userEntity.getFavoriteGenre());
            preparedStatement.setLong(3, userId);

            int count = preparedStatement.executeUpdate();

            cw.commitTransaction();

            return count > 0;
        } catch (SQLException e) {
            throw new DataAccessException("Es gab einen Fehler beim Updaten des Benutzers.", e);
        }
    }

    private static List<UserEntity> mapUserList(PreparedStatement preparedStatement) throws SQLException {
        List<UserEntity> users = new ArrayList<>();
        ResultSet rs = preparedStatement.executeQuery();
        while (rs.next()) {
            users.add(new UserEntity(rs.getLong("USER_ID"),
                    rs.getString("USERNAME"),
                    "",
                    rs.getString("EMAIL"),
                    rs.getString("FAVORITE_GENRE")
            ));
        }
        return users;
    }
}
