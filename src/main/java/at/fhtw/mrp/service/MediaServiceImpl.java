package at.fhtw.mrp.service;

import at.fhtw.mrp.dao.MediaEntryDao;
import at.fhtw.mrp.dto.MediaEntryInDTO;
import at.fhtw.mrp.dto.MediaEntryOutDTO;
import at.fhtw.mrp.dto.MediaEntryType;
import at.fhtw.mrp.entity.MediaEntryEntity;
import at.fhtw.mrp.entity.RatingEntity;
import at.fhtw.mrp.entity.UserEntity;
import at.fhtw.mrp.exceptions.InvalidInputException;
import at.fhtw.mrp.exceptions.NotFoundException;
import at.fhtw.mrp.exceptions.UnauthorizedException;
import org.apache.commons.lang3.Strings;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MediaServiceImpl implements MediaService {

    private final MediaEntryDao mediaEntryDao;

    public MediaServiceImpl(MediaEntryDao mediaEntryDao) {
        this.mediaEntryDao = mediaEntryDao;
    }

    @Override
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

    @Override
    public MediaEntryOutDTO getMediaEntry(Long mediaEntryId) {
        ValidationUtil.validateEntityId(mediaEntryId, "MediaEntry");

        MediaEntryEntity mediaEntryEntity = mediaEntryDao.getMediaEntry(mediaEntryId);

        if (mediaEntryEntity == null) {
            throw new NotFoundException("Dieser MediaEntry kann nicht gefunden werden.");
        }

        return new MediaEntryOutDTO(mediaEntryEntity);
    }

    @Override
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

    @Override
    public void updateMediaEntry(Long mediaEntryId, MediaEntryInDTO mediaEntry) {
        MediaEntryEntity mediaEntryEntity = getMediaEntryEntityAndValidateUser(mediaEntryId);

        mediaEntryEntity.setMediaType(MediaEntryType.parse(mediaEntry.mediaType()));
        mediaEntryEntity.setTitle(mediaEntry.title());
        mediaEntryEntity.setDescription(mediaEntry.description());
        mediaEntryEntity.setReleaseYear(mediaEntry.releaseYear());
        mediaEntryEntity.setGenres(mediaEntry.genres());
        mediaEntryEntity.setAgeRestriction(mediaEntry.ageRestriction());

        mediaEntryDao.updateMediaEntry(mediaEntryEntity);
    }

    @Override
    public void deleteMediaEntry(Long mediaEntryId) {
        getMediaEntryEntityAndValidateUser(mediaEntryId);

        mediaEntryDao.deleteMediaEntry(mediaEntryId);
    }

    @Override
    public void favoriteMediaEntry(Long mediaEntryId) {
        ValidationUtil.validateEntityId(mediaEntryId, "MediaEntry");

        UserEntity currentUser = UserSessionService.getUserSession();
        MediaEntryEntity mediaEntry = mediaEntryDao.getMediaEntry(mediaEntryId);
        if (mediaEntry == null) {
            throw new NotFoundException("Dieser MediaEntry existiert nicht.");
        } else if (mediaEntry.getUsersFavorited().contains(currentUser)) {
            throw new InvalidInputException("Dieser MediaEntry ist bereits favorisiert.");
        }

        mediaEntryDao.setMediaEntryFavorite(mediaEntry.getId(), currentUser.getId());
    }

    @Override
    public void unfavoriteMediaEntry(Long mediaEntryId) {
        ValidationUtil.validateEntityId(mediaEntryId, "MediaEntry");

        UserEntity currentUser = UserSessionService.getUserSession();
        MediaEntryEntity mediaEntry = mediaEntryDao.getMediaEntry(mediaEntryId);
        if (mediaEntry == null) {
            throw new NotFoundException("Dieser MediaEntry existiert nicht.");
        } else if (!mediaEntry.getUsersFavorited().contains(currentUser)) {
            throw new InvalidInputException("Dieser MediaEntry war nicht favorisiert.");
        }

        mediaEntryDao.removeMediaEntryFavorite(mediaEntry.getId(), currentUser.getId());
    }

    @Override
    public List<MediaEntryOutDTO> getMediaEntriesFavoritedByUser(Long userId) {
        ValidationUtil.validateEntityId(userId, "User");

        return mediaEntryDao.getMediaEntriesFavoritedByUser(userId)
                .stream()
                .map(MediaEntryOutDTO::new)
                .toList();
    }

    @Override
    public List<MediaEntryOutDTO> getRecommendationsForUserByGenre(Long userId) {
        ValidationUtil.validateEntityId(userId, "User");

        List<MediaEntryEntity> ratedEntries = mediaEntryDao.getMediaEntriesByUser(userId, true);
        List<MediaEntryEntity> unratedEntries = mediaEntryDao.getMediaEntriesByUser(userId, false);

        Map<String, Long> summedGenreScores = ratedEntries.stream()
                .flatMap(mediaEntryEntity -> {
                    RatingEntity ownRating = mediaEntryEntity.getRatings().stream()
                            .filter(r -> Objects.equals(r.getCreator().getId(), userId))
                            .findFirst().orElse(null);
                    assert ownRating != null;
                    return mediaEntryEntity.getGenres().stream().map(genre -> new ScoreHolder(genre, ownRating.getRating()));
                }).collect(Collectors.groupingBy(ScoreHolder::key,
                        Collectors.<ScoreHolder>summingLong(ScoreHolder::score)));

        return filterAndSortMediaEntriesByScore(unratedEntries.stream()
                .map(m -> new ScoredMediaEntry(m, m.getGenres().stream()
                        .mapToLong(g -> summedGenreScores.getOrDefault(g, 0L))
                        .sum())));
    }


    @Override
    public List<MediaEntryOutDTO> getRecommendationsForUserByContent(Long userId) {
        ValidationUtil.validateEntityId(userId, "User");

        List<MediaEntryEntity> ratedEntries = mediaEntryDao.getMediaEntriesByUser(userId, true);
        List<MediaEntryEntity> unratedEntries = mediaEntryDao.getMediaEntriesByUser(userId, false);

        Map<String, Long> genreScores = ratedEntries.stream().flatMap(m -> m.getGenres().stream())
                .collect(Collectors.groupingBy(genre -> genre, Collectors.counting()));

        Map<MediaEntryType, Long> typeScores = ratedEntries.stream()
                .collect(Collectors.groupingBy(MediaEntryEntity::getMediaType, Collectors.counting()));
        Map<Integer, Long> ageScores = ratedEntries.stream()
                .collect(Collectors.groupingBy(MediaEntryEntity::getAgeRestriction,
                        Collectors.counting()));

        return filterAndSortMediaEntriesByScore(unratedEntries.stream()
                .map(m -> new ScoredMediaEntry(m,
                        m.getGenres().stream()
                                .mapToLong(g -> genreScores.getOrDefault(g, 0L))
                                .sum()
                                + typeScores.getOrDefault(m.getMediaType(), 0L)
                                + ageScores.getOrDefault(m.getAgeRestriction(), 0L)))
        );
    }

    private static List<MediaEntryOutDTO> filterAndSortMediaEntriesByScore(Stream<ScoredMediaEntry> entries) {
        return entries
                .filter(m -> m.score() > 0)
                .sorted(Comparator.comparingLong(MediaServiceImpl.ScoredMediaEntry::score))
                .map(rm -> new MediaEntryOutDTO(rm.mediaEntry()))
                .toList();
    }

    private MediaEntryEntity getMediaEntryEntityAndValidateUser(Long mediaEntryId) {
        ValidationUtil.validateEntityId(mediaEntryId, "MediaEntry");

        UserEntity currentUser = UserSessionService.getUserSession();
        MediaEntryEntity mediaEntryEntity = mediaEntryDao.getMediaEntry(mediaEntryId);
        if (mediaEntryEntity == null) {
            throw new NotFoundException("Dieser MediaEntry existiert nicht.");
        }
        if (!Objects.equals(mediaEntryEntity.getCreator(), currentUser)) {
            throw new UnauthorizedException("Der aktuelle Benutzer darf diesen MediaEntry nicht löschen.");
        }
        return mediaEntryEntity;
    }


    private record ScoreHolder(String key, int score) {
    }

    private record ScoredMediaEntry(MediaEntryEntity mediaEntry, long score) {
    }
}
