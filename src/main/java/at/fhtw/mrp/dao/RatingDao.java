package at.fhtw.mrp.dao;

import at.fhtw.mrp.entity.RatingEntity;

import java.util.List;

public interface RatingDao {
    List<RatingEntity> getRatingsForUser(Long userId);

    List<RatingEntity> getRatingsForMediaEntry(long mediaEntryId);

    RatingEntity getRating(long ratingId);

    RatingEntity createRating(RatingEntity rating);

    void updateRating(RatingEntity rating);

    boolean deleteRating(long ratingId);

    void setRatingLiked(Long ratingId, Long userId);

    boolean removeRatingLiked(Long ratingId, Long userId);
}
