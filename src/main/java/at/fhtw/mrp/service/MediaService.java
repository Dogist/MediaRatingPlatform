package at.fhtw.mrp.service;

import at.fhtw.mrp.dto.MediaEntryInDTO;
import at.fhtw.mrp.dto.MediaEntryOutDTO;

import java.util.List;

public interface MediaService {

    List<MediaEntryOutDTO> searchMediaEntries(String title,
                                              String genre,
                                              String mediaType,
                                              Integer releaseYear,
                                              Integer ageRestriction,
                                              Short rating,
                                              String sortBy);

    MediaEntryOutDTO getMediaEntry(Long mediaEntryId);

    MediaEntryOutDTO createMediaEntry(MediaEntryInDTO mediaEntry);

    void updateMediaEntry(Long mediaEntryId, MediaEntryInDTO mediaEntry);

    void deleteMediaEntry(Long mediaEntryId);

    void favoriteMediaEntry(Long mediaEntryId);

    void unfavoriteMediaEntry(Long mediaEntryId);

    List<MediaEntryOutDTO> getMediaEntriesFavoritedByUser(Long userId);

    List<MediaEntryOutDTO> getRecommendationsForUserByGenre(Long userId);

    List<MediaEntryOutDTO> getRecommendationsForUserByContent(Long userId);
}
