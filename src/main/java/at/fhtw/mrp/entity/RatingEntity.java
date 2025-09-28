package at.fhtw.mrp.entity;

import java.time.LocalDateTime;

public class RatingEntity {
    private Long id;
    private UserEntity user;
    private MediaEntryEntity mediaEntry;
    private Short rating;
    private String comment;
    private LocalDateTime timestamp;
    private boolean confirmed;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public MediaEntryEntity getMediaEntry() {
        return mediaEntry;
    }

    public void setMediaEntry(MediaEntryEntity mediaEntry) {
        this.mediaEntry = mediaEntry;
    }

    public Short getRating() {
        return rating;
    }

    public void setRating(Short rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }
}
