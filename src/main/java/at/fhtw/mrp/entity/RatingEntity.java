package at.fhtw.mrp.entity;

import java.time.LocalDateTime;
import java.util.List;

public class RatingEntity {
    private Long id;
    private UserEntity creator;
    private MediaEntryEntity mediaEntry;
    private Short rating;
    private String comment;
    private LocalDateTime timestamp;
    private boolean confirmed;
    private List<UserEntity> likedByUsers;

    public RatingEntity(UserEntity creator, MediaEntryEntity mediaEntry, Short rating, String comment) {
        this.creator = creator;
        this.mediaEntry = mediaEntry;
        this.rating = rating;
        this.comment = comment;
        confirmed = false;
    }

    public RatingEntity(Long id, UserEntity creator, MediaEntryEntity mediaEntry, Short rating, String comment, LocalDateTime timestamp, boolean confirmed, List<UserEntity> likedByUsers) {
        this.id = id;
        this.creator = creator;
        this.mediaEntry = mediaEntry;
        this.rating = rating;
        this.comment = comment;
        this.timestamp = timestamp;
        this.confirmed = confirmed;
        this.likedByUsers = likedByUsers;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserEntity getCreator() {
        return creator;
    }

    public void setCreator(UserEntity creator) {
        this.creator = creator;
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

    public List<UserEntity> getLikedByUsers() {
        return likedByUsers;
    }

    public void setLikedByUsers(List<UserEntity> likedByUsers) {
        this.likedByUsers = likedByUsers;
    }
}
