package at.fhtw.mrp.dto;

import at.fhtw.mrp.entity.UserEntity;

public class UserProfileDTO {
    private Long id;
    private String username;
    private String email;
    private String favoriteGenre;
    private Long ratingCount;
    private Double ratingAvg;

    public UserProfileDTO() {
    }

    public UserProfileDTO(UserEntity user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.favoriteGenre = user.getFavoriteGenre();
        this.ratingCount = user.getRatingCount();
        this.ratingAvg = user.getRatingAvg();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFavoriteGenre() {
        return favoriteGenre;
    }

    public void setFavoriteGenre(String favoriteGenre) {
        this.favoriteGenre = favoriteGenre;
    }

    public Long getRatingCount() {
        return ratingCount;
    }

    public void setRatingCount(Long ratingCount) {
        this.ratingCount = ratingCount;
    }

    public Double getRatingAvg() {
        return ratingAvg;
    }

    public void setRatingAvg(Double ratingAvg) {
        this.ratingAvg = ratingAvg;
    }
}
