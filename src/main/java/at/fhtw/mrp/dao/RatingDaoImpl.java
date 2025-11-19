package at.fhtw.mrp.dao;

import at.fhtw.mrp.dao.general.ConnectionWrapper;
import at.fhtw.mrp.dao.general.DataAccessException;
import at.fhtw.mrp.entity.RatingEntity;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class RatingDaoImpl implements RatingDao {
    private final UserDao userDao;

    public RatingDaoImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public List<RatingEntity> getRatingsForUser(Long userId) {
        try (ConnectionWrapper cw = new ConnectionWrapper();
             PreparedStatement statement = cw.prepareStatement("SELECT * FROM rating WHERE user_id = ?")) {
            statement.setLong(1, userId);

            ResultSet rs = statement.executeQuery();
            List<RatingEntity> ratings = new ArrayList<>();
            while (rs.next()) {
                ratings.add(parseRatingEntity(rs));
            }
            return ratings;
        } catch (SQLException e) {
            throw new DataAccessException("Es gab einen Fehler beim Holen der Ratings.", e);
        }
    }

    @Override
    public List<RatingEntity> getRatingsForMediaEntry(long mediaEntryId) {
        try (ConnectionWrapper cw = new ConnectionWrapper();
             PreparedStatement statement = cw.prepareStatement("SELECT * FROM rating WHERE media_entry_id = ?")) {
            statement.setLong(1, mediaEntryId);

            List<RatingEntity> ratings = new ArrayList<>();
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    ratings.add(parseRatingEntity(rs));
                }
            }
            return ratings;
        } catch (SQLException e) {
            throw new DataAccessException("Es gab einen Fehler beim Holen der Ratings.", e);
        }
    }

    @Override
    public RatingEntity getRating(long ratingId) {
        try (ConnectionWrapper cw = new ConnectionWrapper();
             PreparedStatement statement = cw.prepareStatement("SELECT * FROM rating WHERE rating_id = ?")) {
            statement.setLong(1, ratingId);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return parseRatingEntity(rs);
                }
            }
            return null;
        } catch (SQLException e) {
            throw new DataAccessException("Es gab einen Fehler beim Holen des Ratings.", e);
        }
    }

    @Override
    public RatingEntity createRating(RatingEntity rating) {
        try (ConnectionWrapper cw = new ConnectionWrapper();
             PreparedStatement statement = cw.prepareStatement("INSERT INTO rating(user_id, media_entry_id, rating, comment, confirmed) VALUES (?, ?, ?, ?, ?)")) {
            statement.setLong(1, rating.getCreator().getId());
            statement.setLong(2, rating.getMediaEntryId());
            statement.setShort(3, rating.getRating());
            statement.setString(4, rating.getComment());
            statement.setBoolean(5, rating.isConfirmed());

            statement.executeUpdate();
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    long ratingId = generatedKeys.getLong("RATING_ID");
                    Timestamp timestamp = generatedKeys.getTimestamp("timestamp");
                    cw.commitTransaction();
                    rating.setId(ratingId);
                    rating.setTimestamp(timestamp.toLocalDateTime());
                    return rating;
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Es gab einen Fehler beim Erstellen des Ratings.", e);
        }
        return null;
    }

    @Override
    public void updateRating(RatingEntity rating) {
        try (ConnectionWrapper cw = new ConnectionWrapper();
             PreparedStatement statement = cw.prepareStatement("UPDATE RATING SET rating=?, comment=?, confirmed=? WHERE media_entry_id=?")) {
            statement.setShort(1, rating.getRating());
            statement.setString(2, rating.getComment());
            statement.setBoolean(3, rating.isConfirmed());
            statement.setLong(4, rating.getMediaEntryId());

            statement.executeUpdate();
            cw.commitTransaction();
        } catch (SQLException e) {
            throw new DataAccessException("Es gab einen Fehler beim Updaten des Ratings.", e);
        }
    }

    @Override
    public boolean deleteRating(long ratingId) {
        try (ConnectionWrapper cw = new ConnectionWrapper();
             PreparedStatement mainStatement = cw.prepareStatement("DELETE FROM rating WHERE rating_id = ?")) {
            mainStatement.setLong(1, ratingId);

            int i = mainStatement.executeUpdate();
            cw.commitTransaction();
            return i > 0;
        } catch (SQLException e) {
            throw new DataAccessException("Es gab einen Fehler beim Löschen des Ratings.", e);
        }
    }

    @Override
    public void setRatingLiked(Long ratingId, Long userId) {
        try (ConnectionWrapper cw = new ConnectionWrapper();
             PreparedStatement statement = cw.prepareStatement("INSERT INTO user_like_rating(rating_id, user_id) VALUES (?, ?)")) {
            statement.setLong(1, ratingId);
            statement.setLong(2, userId);

            statement.executeUpdate();
            cw.commitTransaction();
        } catch (SQLException e) {
            throw new DataAccessException("Es gab einen Fehler beim Liken des Ratings.", e);
        }
    }

    @Override
    public boolean removeRatingLiked(Long ratingId, Long userId) {
        try (ConnectionWrapper cw = new ConnectionWrapper();
             PreparedStatement statement = cw.prepareStatement("DELETE FROM user_like_rating WHERE rating_id = ? AND user_id = ?")) {
            statement.setLong(1, ratingId);
            statement.setLong(2, userId);

            int i = statement.executeUpdate();
            cw.commitTransaction();
            return i > 0;
        } catch (SQLException e) {
            throw new DataAccessException("Es gab einen Fehler beim Unliken des Ratings.", e);
        }
    }

    private RatingEntity parseRatingEntity(ResultSet rs) throws SQLException {
        long ratingAuthor = rs.getLong("user_id");

        return new RatingEntity(
                rs.getLong("rating_id"),
                userDao.getUserById(ratingAuthor),
                rs.getLong("media_entry_id"),
                rs.getShort("rating"),
                rs.getString("comment"),
                rs.getTimestamp("timestamp").toLocalDateTime(),
                rs.getBoolean("confirmed"),
                userDao.getUsersByLikedRating(rs.getLong("rating_id"))
        );
    }
}
