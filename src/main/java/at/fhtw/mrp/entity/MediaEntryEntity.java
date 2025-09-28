package at.fhtw.mrp.entity;

import at.fhtw.mrp.dto.MediaEntryType;

import java.util.List;

public class MediaEntryEntity {
    private Long id;
    private MediaEntryType type;
    private String title;
    private String description;
    private Integer releaseYear;
    private List<String> genres;
    private Integer minAge;
    private UserEntity creator;

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

    public Integer getMinAge() {
        return minAge;
    }

    public void setMinAge(Integer minAge) {
        this.minAge = minAge;
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

    public MediaEntryType getType() {
        return type;
    }

    public void setType(MediaEntryType type) {
        this.type = type;
    }
}
