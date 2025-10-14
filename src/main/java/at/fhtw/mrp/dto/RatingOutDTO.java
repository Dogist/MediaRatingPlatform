package at.fhtw.mrp.dto;

import at.fhtw.mrp.entity.RatingEntity;

import java.time.format.DateTimeFormatter;

public record RatingOutDTO(Long id,
                           Short stars,
                           String comment,
                           Boolean commentConfirmed,
                           String author,
                           String timestamp) {

    public RatingOutDTO(RatingEntity ratingEntity) {
        this(ratingEntity.getId(),
                ratingEntity.getRating(),
                ratingEntity.getComment(),
                ratingEntity.isConfirmed(),
                ratingEntity.getCreator().getUsername(),
                ratingEntity.getTimestamp().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }
}
