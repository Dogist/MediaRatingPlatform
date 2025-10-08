package at.fhtw.mrp.service;

import at.fhtw.mrp.dao.MediaEntryDao;
import at.fhtw.mrp.dto.MediaEntryInDTO;
import at.fhtw.mrp.dto.MediaEntryOutDTO;
import at.fhtw.mrp.dto.MediaEntryType;
import at.fhtw.mrp.entity.MediaEntryEntity;
import at.fhtw.mrp.exceptions.InvalidInputException;
import at.fhtw.mrp.exceptions.NotFoundException;
import at.fhtw.mrp.exceptions.UnauthorizedException;
import org.apache.commons.lang3.Strings;

import java.util.List;
import java.util.Objects;

public class MediaService {

    private final MediaEntryDao mediaEntryDao = new MediaEntryDao();

    public List<MediaEntryOutDTO> searchMediaEntries(String title,
                                                     String genre,
                                                     String mediaType,
                                                     Integer releaseYear,
                                                     Integer ageRestriction,
                                                     Integer rating,
                                                     String sortBy) {
        if (sortBy != null && !Strings.CI.equalsAny(sortBy, "title", "genre", "mediaType", "releaseYear", "ageRestriction", "rating", "score")) {
            throw new InvalidInputException("Es kann nicht nach \"" + sortBy + "\" sortiert werden.");
        }

        return mediaEntryDao.searchMediaEntries(title,
                        genre,
                        mediaType,
                        releaseYear,
                        ageRestriction,
                        rating,
                        sortBy).stream()
                .map(MediaEntryOutDTO::new)
                .toList();
    }

    public MediaEntryOutDTO getMediaEntry(Long mediaEntryId) {
        MediaEntryEntity mediaEntryEntity = mediaEntryDao.getMediaEntry(mediaEntryId);

        return new MediaEntryOutDTO(mediaEntryEntity);
    }

    public MediaEntryOutDTO createMediaEntry(MediaEntryInDTO mediaEntry) {

        MediaEntryEntity mediaEntryEntity = new MediaEntryEntity(
                MediaEntryType.parse(mediaEntry.mediaType()),
                mediaEntry.title(), mediaEntry.description(),
                mediaEntry.releaseYear(), mediaEntry.genres(),
                mediaEntry.ageRestriction(),
                UserSessionService.getUserSession());

        mediaEntryEntity = mediaEntryDao.createMediaEntry(mediaEntryEntity);

        return new MediaEntryOutDTO(mediaEntryEntity);
    }

    public void updateMediaEntry(Long id, MediaEntryInDTO mediaEntry) {

        MediaEntryEntity mediaEntryEntity = mediaEntryDao.getMediaEntry(id);
        if (mediaEntry == null) {
            throw new NotFoundException("Dieser MediaEntry existiert nicht.");
        }
        if (!Objects.equals(mediaEntryEntity.getCreator(), UserSessionService.getUserSession())) {
            throw new UnauthorizedException("Der aktuelle Benutzer darf diesen MediaEntry nicht löschen.");
        }

        mediaEntryEntity.setMediaType(MediaEntryType.parse(mediaEntry.mediaType()));
        mediaEntryEntity.setTitle(mediaEntry.title());
        mediaEntryEntity.setDescription(mediaEntry.description());
        mediaEntryEntity.setReleaseYear(mediaEntry.releaseYear());
        mediaEntryEntity.setGenres(mediaEntry.genres());
        mediaEntryEntity.setAgeRestriction(mediaEntry.ageRestriction());


        mediaEntryDao.updateMediaEntry(mediaEntryEntity);
    }

    public void deleteMediaEntry(long mediaEntryId) {
        MediaEntryEntity mediaEntry = mediaEntryDao.getMediaEntry(mediaEntryId);
        if (mediaEntry == null) {
            throw new NotFoundException("Dieser MediaEntry existiert nicht.");
        }
        if (!Objects.equals(mediaEntry.getCreator(), UserSessionService.getUserSession())) {
            throw new UnauthorizedException("Der aktuelle Benutzer darf diesen MediaEntry nicht löschen.");
        }
        mediaEntryDao.deleteMediaEntry(mediaEntryId);
    }
}
