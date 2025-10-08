package at.fhtw.mrp.dto;

import at.fhtw.mrp.entity.MediaEntryEntity;

import java.util.List;

public class MediaEntryOutDTO {
    private Long id;
    private String mediaType;
    private String title;
    private String description;
    private Integer releaseYear;
    private List<String> genres;
    private Integer ageRestriction;
    private String creator;

    public MediaEntryOutDTO() {
    }

    public MediaEntryOutDTO(MediaEntryEntity mediaEntry) {
        this.id = mediaEntry.getId();
        this.mediaType = mediaEntry.getMediaType().name();
        this.title = mediaEntry.getTitle();
        this.description = mediaEntry.getDescription();
        this.releaseYear = mediaEntry.getReleaseYear();
        this.genres = mediaEntry.getGenres();
        this.ageRestriction = mediaEntry.getAgeRestriction();
        this.creator = mediaEntry.getCreator().getUsername();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(Integer releaseYear) {
        this.releaseYear = releaseYear;
    }

    public List<String> getGenres() {
        return genres;
    }

    public void setGenres(List<String> genres) {
        this.genres = genres;
    }

    public Integer getAgeRestriction() {
        return ageRestriction;
    }

    public void setAgeRestriction(Integer ageRestriction) {
        this.ageRestriction = ageRestriction;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }
}
