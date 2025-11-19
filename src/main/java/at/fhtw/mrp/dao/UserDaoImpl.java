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
        try (ConnectionWrapper cw = new ConnectionWrapper();
             PreparedStatement preparedStatement = cw.prepareStatement("INSERT INTO USER_ACC(USERNAME, PASSWORD) VALUES (?, ?)")) {
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
    public UserEntity getUserById(Long userId) {
        try (ConnectionWrapper cw = new ConnectionWrapper();
             PreparedStatement preparedStatement = cw.prepareStatement("SELECT u.* FROM USER_ACC u WHERE u.USER_ID = ?")) {
            preparedStatement.setLong(1, userId);

            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    return new UserEntity(rs.getLong("USER_ID"),
                            rs.getString("USERNAME"),
                            rs.getString("PASSWORD"),
                            rs.getString("EMAIL"),
                            rs.getString("FAVORITE_GENRE")
                    );
                }
            }
            return null;

        } catch (SQLException e) {
            throw new DataAccessException("Es gab einen Fehler beim Holen des Benutzers.", e);
        }
    }


    @Override
    public UserEntity getUserByUsername(String username) {
        try (ConnectionWrapper cw = new ConnectionWrapper();
             PreparedStatement preparedStatement = cw.prepareStatement("SELECT * FROM USER_ACC WHERE USERNAME = ?")
        ) {
            preparedStatement.setString(1, username);

            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    return new UserEntity(rs.getLong("USER_ID"),
                            rs.getString("USERNAME"),
                            rs.getString("PASSWORD"),
                            rs.getString("EMAIL"),
                            rs.getString("FAVORITE_GENRE")
                    );
                }
            }
            return null;

        } catch (SQLException e) {
            throw new DataAccessException("Es gab einen Fehler beim Holen des Benutzers.", e);
        }
    }

    @Override
    public void hydrateUserStatistics(UserEntity user) {
        try (ConnectionWrapper cw = new ConnectionWrapper();
             PreparedStatement preparedStatement = cw.prepareStatement("SELECT COUNT(r.rating) rating_count, AVG(r.rating) rating_avg FROM USER_ACC u LEFT join rating r on u.user_id=r.user_id WHERE u.USER_ID = ? GROUP BY u.user_id")) {
            preparedStatement.setLong(1, user.getId());

            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    double ratingAvg = rs.getDouble("RATING_AVG");
                    user.setRatingAvg(ratingAvg);
                    long ratingCount = rs.getLong("RATING_COUNT");
                    user.setRatingCount(ratingCount);
                } else {
                    throw new DataAccessException("Es gab einen Fehler beim Holen der Benutzer-Statistik. " + user.getId());
                }
            }

        } catch (SQLException e) {
            throw new DataAccessException("Es gab einen Fehler beim Holen der Benutzer-Statistik.", e);
        }
    }

    @Override
    public List<UserEntity> getUsersByFavoriteMedia(long mediaEntryId) {
        try (ConnectionWrapper cw = new ConnectionWrapper();
             PreparedStatement preparedStatement = cw.prepareStatement("SELECT USER_ACC.* FROM USER_ACC JOIN public.user_favorite_media ufm on USER_ACC.user_id = ufm.user_id WHERE ufm.media_entry_id = ?")) {
            preparedStatement.setLong(1, mediaEntryId);

            return mapUserList(preparedStatement);

        } catch (SQLException e) {
            throw new DataAccessException("Es gab einen Fehler beim Holen des Benutzers.", e);
        }
    }

    @Override
    public List<UserEntity> getUsersByLikedRating(long ratingId) {
        try (ConnectionWrapper cw = new ConnectionWrapper();
             PreparedStatement preparedStatement = cw.prepareStatement("SELECT USER_ACC.* FROM USER_ACC JOIN public.user_like_rating ufr on USER_ACC.user_id = ufr.user_id WHERE ufr.rating_id = ?")) {
            preparedStatement.setLong(1, ratingId);

            return mapUserList(preparedStatement);

        } catch (SQLException e) {
            throw new DataAccessException("Es gab einen Fehler beim Holen des Benutzers.", e);
        }
    }

    @Override
    public boolean updateUser(Long userId, UserProfileUpdateDTO userEntity) {
        try (ConnectionWrapper cw = new ConnectionWrapper();
             PreparedStatement preparedStatement = cw.prepareStatement("UPDATE USER_ACC SET email = ?, favorite_genre = ? WHERE USER_ID = ?")) {
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

    @Override
    public List<UserEntity> getUsersByRatingCount() {
        try (ConnectionWrapper cw = new ConnectionWrapper();
             PreparedStatement preparedStatement = cw.prepareStatement("SELECT USER_ACC.*, count(r.*) r_count FROM USER_ACC LEFT JOIN public.rating r on USER_ACC.user_id = r.user_id GROUP BY user_acc.user_id ORDER BY r_count DESC")) {

            return mapUserList(preparedStatement);

        } catch (SQLException e) {
            throw new DataAccessException("Es gab einen Fehler beim Holen der Benutzer.", e);
        }
    }

    private static List<UserEntity> mapUserList(PreparedStatement preparedStatement) throws SQLException {
        List<UserEntity> users = new ArrayList<>();
        try (ResultSet rs = preparedStatement.executeQuery()) {
            while (rs.next()) {
                users.add(new UserEntity(rs.getLong("USER_ID"),
                        rs.getString("USERNAME"),
                        "",
                        rs.getString("EMAIL"),
                        rs.getString("FAVORITE_GENRE")
                ));
            }
        }
        return users;
    }
}
