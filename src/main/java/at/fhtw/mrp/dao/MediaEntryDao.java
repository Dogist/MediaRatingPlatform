package at.fhtw.mrp.dao;

import at.fhtw.mrp.entity.MediaEntryEntity;

import java.util.List;

public interface MediaEntryDao {
    List<MediaEntryEntity> searchMediaEntries(String title,
                                              String genre,
                                              String mediaType,
                                              Integer releaseYear,
                                              Integer ageRestriction,
                                              Integer rating,
                                              String sortBy);

    List<MediaEntryEntity> getMediaEntriesFavoritedByUser(Long userId);

    MediaEntryEntity createMediaEntry(MediaEntryEntity mediaEntry);

    MediaEntryEntity getMediaEntry(Long mediaEntryId);

    void updateMediaEntry(MediaEntryEntity mediaEntry);

    boolean deleteMediaEntry(long mediaEntryId);

    void setMediaEntryFavorite(Long mediaEntryId, Long userEntityId);

    boolean removeMediaEntryFavorite(Long mediaEntryId, Long userEntityId);
}
