package at.fhtw.mrp.dto;

public class UserProfileUpdateDTO {
    private String email;
    private String favoriteGenre;

    public UserProfileUpdateDTO() {
    }

    public UserProfileUpdateDTO(String email, String favoriteGenre) {
        this.email = email;
        this.favoriteGenre = favoriteGenre;
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
}
