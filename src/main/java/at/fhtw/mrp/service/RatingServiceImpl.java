package at.fhtw.mrp.service;

import at.fhtw.mrp.dao.MediaEntryDao;
import at.fhtw.mrp.dao.RatingDao;
import at.fhtw.mrp.dto.RatingInDTO;
import at.fhtw.mrp.dto.RatingOutDTO;
import at.fhtw.mrp.entity.MediaEntryEntity;
import at.fhtw.mrp.entity.RatingEntity;
import at.fhtw.mrp.entity.UserEntity;
import at.fhtw.mrp.exceptions.InvalidInputException;
import at.fhtw.mrp.exceptions.NotFoundException;
import at.fhtw.mrp.exceptions.UnauthorizedException;

import java.util.List;
import java.util.Objects;

public class RatingServiceImpl implements RatingService {
    private final MediaEntryDao mediaEntryDao;
    private final RatingDao ratingDao;

    public RatingServiceImpl(MediaEntryDao mediaEntryDao, RatingDao ratingDao) {
        this.mediaEntryDao = mediaEntryDao;
        this.ratingDao = ratingDao;
    }

    @Override
    public List<RatingOutDTO> getRatingsForUser(Long userId) {
        ValidationUtil.validateEntityId(userId, "User");

        UserEntity currentUser = UserSessionService.getUserSession();
        List<RatingEntity> ratings = ratingDao.getRatingsForUser(userId);

        return ratings.stream().map(rating -> {
            if (!Objects.equals(currentUser.getId(), rating.getCreator().getId()) && !rating.isConfirmed()) {
                rating.setComment("");
            }
            return new RatingOutDTO(rating);
        }).toList();
    }

    @Override
    public RatingOutDTO getRating(Long ratingId) {
        ValidationUtil.validateEntityId(ratingId, "Rating");

        UserEntity currentUser = UserSessionService.getUserSession();
        RatingEntity rating = ratingDao.getRating(ratingId);

        if (rating == null) {
            throw new NotFoundException("Dieses Rating kann nicht gefunden werden.");
        }

        if (!Objects.equals(currentUser.getId(), rating.getCreator().getId()) && !rating.isConfirmed()) {
            rating.setComment("");
        }
        return new RatingOutDTO(rating);
    }

    @Override
    public RatingOutDTO createRating(Long mediaId, RatingInDTO rating) {
        ValidationUtil.validateEntityId(mediaId, "MediaEntry");

        MediaEntryEntity mediaEntry = mediaEntryDao.getMediaEntry(mediaId);
        if (mediaEntry == null) {
            throw new NotFoundException("Es gibt dieses MediaEntry nicht.");
        }
        UserEntity currentUser = UserSessionService.getUserSession();
        RatingEntity ratingEntity = new RatingEntity(currentUser,
                mediaId,
                rating.stars(),
                rating.comment());

        ratingEntity = ratingDao.createRating(ratingEntity);

        return new RatingOutDTO(ratingEntity);
    }

    @Override
    public void updateRating(Long ratingId, RatingInDTO rating) {
        ValidationUtil.validateEntityId(ratingId, "Rating");

        UserEntity currentUser = UserSessionService.getUserSession();
        RatingEntity ratingEntity = ratingDao.getRating(ratingId);
        if (ratingEntity == null) {
            throw new NotFoundException("Dieses Rating existiert nicht.");
        } else if (!Objects.equals(ratingEntity.getCreator(), currentUser)) {
            throw new UnauthorizedException("Der aktuelle Benutzer darf dieses Rating nicht ändern.");
        }

        ratingEntity.setRating(rating.stars());
        ratingEntity.setComment(rating.comment());

        ratingDao.updateRating(ratingEntity);
    }

    @Override
    public void deleteRating(Long ratingId) {
        ValidationUtil.validateEntityId(ratingId, "Rating");

        UserEntity currentUser = UserSessionService.getUserSession();
        RatingEntity ratingEntity = ratingDao.getRating(ratingId);
        if (ratingEntity == null) {
            throw new NotFoundException("Dieses Rating existiert nicht.");
        } else if (!Objects.equals(ratingEntity.getCreator(), currentUser)) {
            throw new UnauthorizedException("Der aktuelle Benutzer darf dieses Rating nicht löschen.");
        }

        ratingDao.deleteRating(ratingId);
    }

    @Override
    public void likeRating(Long ratingId) {
        ValidationUtil.validateEntityId(ratingId, "Rating");

        UserEntity currentUser = UserSessionService.getUserSession();
        RatingEntity ratingEntity = ratingDao.getRating(ratingId);
        if (ratingEntity == null) {
            throw new NotFoundException("Dieses Rating existiert nicht.");
        } else if (ratingEntity.getLikedByUsers().contains(currentUser)) {
            throw new InvalidInputException("Dieses Rating ist bereits geliked.");
        }

        ratingDao.setRatingLiked(ratingEntity.getId(), currentUser.getId());
    }

    @Override
    public void unlikeRating(Long ratingId) {
        ValidationUtil.validateEntityId(ratingId, "Rating");

        UserEntity currentUser = UserSessionService.getUserSession();
        RatingEntity ratingEntity = ratingDao.getRating(ratingId);
        if (ratingEntity == null) {
            throw new NotFoundException("Dieses Rating existiert nicht.");
        } else if (!ratingEntity.getLikedByUsers().contains(currentUser)) {
            throw new InvalidInputException("Dieses Rating war nicht geliked.");
        }

        ratingDao.removeRatingLiked(ratingEntity.getId(), currentUser.getId());
    }

    @Override
    public void confirmRating(Long ratingId) {
        ValidationUtil.validateEntityId(ratingId, "Rating");

        UserEntity currentUser = UserSessionService.getUserSession();
        RatingEntity ratingEntity = ratingDao.getRating(ratingId);
        if (ratingEntity == null) {
            throw new NotFoundException("Dieses Rating existiert nicht.");
        } else if (ratingEntity.isConfirmed()) {
            throw new InvalidInputException("Dieses Rating ist bereits bestätigt.");
        } else if (!Objects.equals(ratingEntity.getCreator(), currentUser)) {
            throw new UnauthorizedException("Der aktuelle Benutzer darf dieses Rating nicht ändern.");
        }

        ratingEntity.setConfirmed(true);
        ratingDao.updateRating(ratingEntity);
    }

}
