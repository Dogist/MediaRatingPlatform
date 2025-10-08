package at.fhtw.mrp.dao;

import at.fhtw.mrp.dao.general.ConnectionWrapper;
import at.fhtw.mrp.dao.general.DataAccessException;
import at.fhtw.mrp.dto.MediaEntryType;
import at.fhtw.mrp.entity.MediaEntryEntity;
import org.apache.commons.lang3.StringUtils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class MediaEntryDao {
    private final UserDao userDao = new UserDao();

    public List<MediaEntryEntity> searchMediaEntries(String title,
                                                     String genre,
                                                     String mediaType,
                                                     Integer releaseYear,
                                                     Integer ageRestriction,
                                                     Integer rating,
                                                     String sortBy) {
        StringBuilder sqlString = new StringBuilder("SELECT * FROM media_entry WHERE ");
        // TODO Select erweitern für Rating

        List<String> whereClauses = new ArrayList<>();
        List<Object> params = new ArrayList<>();
        if (StringUtils.isNotBlank(title)) {
            whereClauses.add("LOWER(TITLE) LIKE LOWER(?)");
            params.add('%' + title + '%');
        }
        if (StringUtils.isNotBlank(genre)) {
            whereClauses.add("? = ANY (GENRES)");
            params.add(genre.toLowerCase());
        }
        if (StringUtils.isNotBlank(mediaType)) {
            whereClauses.add("MEDIA_TYPE = ?");
            params.add(mediaType.toUpperCase());
        }
        if (Objects.nonNull(releaseYear)) {
            whereClauses.add("RELEASE_YEAR = ?");
            params.add(releaseYear);
        }
        if (Objects.nonNull(ageRestriction)) {
            whereClauses.add("AGE_RESTRICTION = ?");
            params.add(ageRestriction);
        }
        if (Objects.nonNull(rating)) {
            whereClauses.add("RATING = ?");
            params.add(rating);
        }

        sqlString.append(String.join(" AND ", whereClauses));

        if (StringUtils.isNotBlank(sortBy)) {
            sqlString.append(" ORDER BY ").append(sortBy);
        }

        try (ConnectionWrapper cw = new ConnectionWrapper()) {
            PreparedStatement preparedStatement = cw.prepareStatement(sqlString.toString());

            for (int i = 0; i < params.size(); i++) {
                Object param = params.get(i);
                preparedStatement.setObject(i + 1, param);
            }

            ResultSet rs = preparedStatement.executeQuery();
            List<MediaEntryEntity> mediaEntries = new LinkedList<>();
            while (rs.next()) {
                mediaEntries.add(parseResultSet(rs));
            }
            return mediaEntries;
        } catch (SQLException e) {
            throw new DataAccessException("Es gab einen Fehler beim Suchen von MediaEntries.", e);
        }
    }

    public MediaEntryEntity createMediaEntry(MediaEntryEntity mediaEntry) {
        try (ConnectionWrapper cw = new ConnectionWrapper()) {
            PreparedStatement mainStatement = cw.prepareStatement("INSERT INTO media_entry(media_type, title, description, creator, release_year, genres, age_restriction) VALUES (?, ?, ?, ?, ?, ?, ?)");
            mainStatement.setString(1, mediaEntry.getMediaType().name());
            mainStatement.setString(2, mediaEntry.getTitle());
            mainStatement.setString(3, mediaEntry.getDescription());
            mainStatement.setLong(4, mediaEntry.getCreator().getId());
            mainStatement.setInt(5, mediaEntry.getReleaseYear());
            mainStatement.setArray(6,
                    cw.createStringArray(mediaEntry.getGenres()
                            .stream()
                            .map(String::toLowerCase)
                            .toArray(String[]::new)));
            mainStatement.setInt(7, mediaEntry.getAgeRestriction());

            mainStatement.executeUpdate();
            ResultSet generatedKeys = mainStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                long mediaEntryId = generatedKeys.getLong(1);
                cw.commitTransaction();
                mediaEntry.setId(mediaEntryId);
                return mediaEntry;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Es gab einen Fehler beim Erstellen des MediaEntry.", e);
        }
        return null;
    }


    public MediaEntryEntity getMediaEntry(Long mediaEntryId) {
        try (ConnectionWrapper cw = new ConnectionWrapper()) {
            PreparedStatement preparedStatement = cw.prepareStatement("SELECT * FROM media_entry WHERE media_entry_id = ?");
            // TODO Select erweitern für Rating
            preparedStatement.setLong(1, mediaEntryId);

            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return parseResultSet(rs);
            }
            return null;

        } catch (SQLException e) {
            throw new DataAccessException("Es gab einen Fehler beim Holen des Benutzers.", e);
        }
    }

    public void updateMediaEntry(MediaEntryEntity mediaEntry) {
        try (ConnectionWrapper cw = new ConnectionWrapper()) {
            PreparedStatement mainStatement = cw.prepareStatement("UPDATE media_entry SET media_type = ?, title = ?, description = ?, release_year = ?, genres = ?, age_restriction = ? WHERE media_entry_id = ?");
            mainStatement.setString(1, mediaEntry.getMediaType().name());
            mainStatement.setString(2, mediaEntry.getTitle());
            mainStatement.setString(3, mediaEntry.getDescription());
            mainStatement.setInt(4, mediaEntry.getReleaseYear());
            mainStatement.setArray(5,
                    cw.createStringArray(mediaEntry.getGenres()
                            .stream()
                            .map(String::toLowerCase)
                            .toArray(String[]::new)));
            mainStatement.setInt(6, mediaEntry.getAgeRestriction());
            mainStatement.setLong(7, mediaEntry.getId());

            mainStatement.executeUpdate();
            cw.commitTransaction();

        } catch (SQLException e) {
            throw new DataAccessException("Es gab einen Fehler beim Updaten des MediaEntry.", e);
        }
    }

    public boolean deleteMediaEntry(long mediaEntryId) {
        try (ConnectionWrapper cw = new ConnectionWrapper()) {
            PreparedStatement mainStatement = cw.prepareStatement("DELETE FROM media_entry WHERE media_entry_id = ?");
            mainStatement.setLong(1, mediaEntryId);

            int i = mainStatement.executeUpdate();
            cw.commitTransaction();
            return i > 0;
        } catch (SQLException e) {
            throw new DataAccessException("Es gab einen Fehler beim Löschen des MediaEntry.", e);
        }
    }

    private MediaEntryEntity parseResultSet(ResultSet rs) throws SQLException {
        return new MediaEntryEntity(rs.getLong("media_entry_id"),
                MediaEntryType.parse(rs.getString("media_type")),
                rs.getString("title"),
                rs.getString("description"),
                rs.getInt("release_year"),
                Arrays.asList((String[]) rs.getArray("genres").getArray()),
                rs.getInt("age_restriction"),
                userDao.getUserById(rs.getLong("creator"))
        );
    }


}
