package at.fhtw.mrp.entity;

import at.fhtw.mrp.dto.UserProfileDTO;

import java.util.Objects;

public class UserEntity {
    private Long id;
    private String username;
    private String password;
    private String email;
    private String favoriteGenre;

    public UserEntity() {
    }

    public UserEntity(UserProfileDTO userProfileDTO) {
        this.id = userProfileDTO.getId();
        this.username = userProfileDTO.getUsername();
        this.email = userProfileDTO.getEmail();
        this.favoriteGenre = userProfileDTO.getFavoriteGenre();
    }

    public UserEntity(Long id, String username, String password, String email, String favoriteGenre) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.favoriteGenre = favoriteGenre;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        UserEntity that = (UserEntity) o;
        return Objects.equals(id, that.id) && Objects.equals(username, that.username) && Objects.equals(password, that.password) && Objects.equals(email, that.email) && Objects.equals(favoriteGenre, that.favoriteGenre);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, password, email, favoriteGenre);
    }
}
