package at.fhtw.mrp.dao;

import at.fhtw.mrp.dao.general.ConnectionWrapper;
import at.fhtw.mrp.dao.general.DataAccessException;
import at.fhtw.mrp.dao.general.DataConflictException;
import at.fhtw.mrp.dto.MediaEntryCreateDTO;
import at.fhtw.mrp.dto.UserProfileUpdateDTO;
import at.fhtw.mrp.entity.MediaEntryEntity;
import at.fhtw.mrp.entity.UserEntity;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MediaEntryDao {

    public MediaEntryEntity createMediaEntry(MediaEntryEntity mediaEntry) {
        try (ConnectionWrapper cw = new ConnectionWrapper()) {
            PreparedStatement mainStatement = cw.prepareStatement("INSERT INTO media_entry(media_type, title, description, creator, release_year, age_restriction) VALUES (?, ?, ?, ?, ?, ?)");
            mainStatement.setString(1, mediaEntry.getMediaType().name());
            mainStatement.setString(2, mediaEntry.getTitle());
            mainStatement.setString(3, mediaEntry.getDescription());
            mainStatement.setLong(4, mediaEntry.getCreator().getId());
            mainStatement.setInt(5, mediaEntry.getReleaseYear());
            mainStatement.setInt(6, mediaEntry.getAgeRestriction());

            mainStatement.executeUpdate();
            ResultSet generatedKeys = mainStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                long mediaEntryId = generatedKeys.getLong(1);

                if (!mediaEntry.getGenres().isEmpty()) {
                    PreparedStatement childStatements = cw.prepareStatement("INSERT INTO media_entry_genres(media_entry_id, genre) VALUES(?, ?)");
                    for (String genre : mediaEntry.getGenres()) {
                        childStatements.setLong(1, mediaEntryId);
                        childStatements.setString(2, genre);
                        childStatements.addBatch();
                    }
                    childStatements.executeBatch();
                }
                cw.commitTransaction();
                mediaEntry.setId(mediaEntryId);
                return mediaEntry;
            }


        } catch (SQLException e) {
            throw new DataAccessException("Es gab einen Fehler beim Erstellen des Benutzers.", e);
        }
        return null;
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
}
