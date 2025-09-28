package at.fhtw.mrp.service;

import at.fhtw.mrp.dao.MediaEntryDao;
import at.fhtw.mrp.dto.MediaEntryCreateDTO;
import at.fhtw.mrp.dto.MediaEntryDTO;
import at.fhtw.mrp.dto.MediaEntryType;
import at.fhtw.mrp.entity.MediaEntryEntity;
import at.fhtw.mrp.exceptions.InvalidInputException;

import java.awt.desktop.UserSessionEvent;
import java.util.List;

public class MediaService {

    private MediaEntryDao mediaEntryDao = new MediaEntryDao();

    public List<MediaEntryDTO> searchMediaEntries(String title,
                                                  String genre,
                                                  String mediaType,
                                                  Integer releaseYear,
                                                  Integer ageRestriction,
                                                  Integer rating,
                                                  String sortBy) {

        // TODO Implement
        return List.of();
    }

    public MediaEntryDTO createMediaEntry(MediaEntryCreateDTO mediaEntry) {

        MediaEntryType mediaType;
        try {
            mediaType = MediaEntryType.parse(mediaEntry.mediaType());
        } catch (IllegalArgumentException e) {
            throw new InvalidInputException("Der angegebene MediaTyp ist invalid.");
        }
        if (mediaEntry.ageRestriction() < 0) {
            throw new InvalidInputException("Der angegebene Altersbeschränkung ist invalid.");
        }
        if (mediaEntry.releaseYear() < 0) {
            throw new InvalidInputException("Das angegebene Veröffentlichungsjahr ist invalid.");
        }

        MediaEntryEntity mediaEntryEntity = new MediaEntryEntity(mediaType,
                mediaEntry.title(), mediaEntry.description(),
                mediaEntry.releaseYear(), mediaEntry.genres(),
                mediaEntry.ageRestriction(),
                UserSessionService.getUserSession());

        mediaEntryDao.createMediaEntry(mediaEntryEntity);

        return new MediaEntryDTO(mediaEntryEntity);
    }
}
