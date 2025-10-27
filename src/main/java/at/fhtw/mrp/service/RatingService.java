package at.fhtw.mrp.service;

import at.fhtw.mrp.dto.RatingInDTO;
import at.fhtw.mrp.dto.RatingOutDTO;

import java.util.List;

public interface RatingService {
    List<RatingOutDTO> getRatingsForUser(Long userId);

    RatingOutDTO getRating(Long ratingId);

    RatingOutDTO createRating(Long mediaId, RatingInDTO rating);

    void updateRating(Long ratingId, RatingInDTO rating);

    void deleteRating(Long ratingId);

    void likeRating(Long ratingId);

    void unlikeRating(Long ratingId);

    void confirmRating(Long ratingId);
}
