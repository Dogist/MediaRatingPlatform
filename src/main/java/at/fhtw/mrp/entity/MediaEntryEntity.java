package at.fhtw.mrp.entity;

import at.fhtw.mrp.dto.MediaEntryType;

import java.util.List;

public class MediaEntryEntity {
    private Long id;
    private MediaEntryType mediaType;
    private String title;
    private String description;
    private Integer releaseYear;
    private List<String> genres;
    private Integer ageRestriction;
    private UserEntity creator;
    private List<UserEntity> usersFavorited;

    public MediaEntryEntity(MediaEntryType mediaType, String title, String description, Integer releaseYear, List<String> genres, Integer ageRestriction, UserEntity creator) {
        this.mediaType = mediaType;
        this.title = title;
        this.description = description;
        this.releaseYear = releaseYear;
        this.genres = genres;
        this.ageRestriction = ageRestriction;
        this.creator = creator;
    }

    public MediaEntryEntity(Long id, MediaEntryType mediaType, String title, String description, Integer releaseYear, List<String> genres, Integer ageRestriction, UserEntity creator, List<UserEntity> usersFavorited) {
        this.id = id;
        this.mediaType = mediaType;
        this.title = title;
        this.description = description;
        this.releaseYear = releaseYear;
        this.genres = genres;
        this.ageRestriction = ageRestriction;
        this.creator = creator;
        this.usersFavorited = usersFavorited;
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

    public Integer getAgeRestriction() {
        return ageRestriction;
    }

    public void setAgeRestriction(Integer ageRestriction) {
        this.ageRestriction = ageRestriction;
    }

    public List<String> getGenres() {
        return genres;
    }

    public void setGenres(List<String> genres) {
        this.genres = genres;
    }

    public Integer getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(Integer releaseYear) {
        this.releaseYear = releaseYear;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public MediaEntryType getMediaType() {
        return mediaType;
    }

    public void setMediaType(MediaEntryType mediaType) {
        this.mediaType = mediaType;
    }

    public List<UserEntity> getUsersFavorited() {
        return usersFavorited;
    }

    public void setUsersFavorited(List<UserEntity> usersFavorited) {
        this.usersFavorited = usersFavorited;
    }
}
