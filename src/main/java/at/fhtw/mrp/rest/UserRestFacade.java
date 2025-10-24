package at.fhtw.mrp.rest;

import at.fhtw.mrp.dao.general.DataConflictException;
import at.fhtw.mrp.dto.*;
import at.fhtw.mrp.exceptions.InvalidInputException;
import at.fhtw.mrp.rest.http.ContentType;
import at.fhtw.mrp.rest.http.HttpMethod;
import at.fhtw.mrp.rest.http.HttpStatus;
import at.fhtw.mrp.rest.server.PathParam;
import at.fhtw.mrp.rest.server.QueryParam;
import at.fhtw.mrp.rest.server.REST;
import at.fhtw.mrp.rest.server.Response;
import at.fhtw.mrp.service.*;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserRestFacade extends AbstractRestFacade {

    public static final Logger LOGGER = Logger.getLogger(UserRestFacade.class.getSimpleName());
    private final UserService userService;
    private final MediaService mediaService;
    private final RatingService ratingService;
    private final AuthService authService;

    public UserRestFacade() {
        super("users");
        userService = new UserService();
        mediaService = new MediaService();
        ratingService = new RatingService();
        authService = CDI.INSTANCE.getService(AuthService.class);
    }

    @REST(path = "register", method = HttpMethod.POST, authRequired = false)
    public Response registerUser(UserAuthDTO user) {
        try {
            userService.createUser(user);
            return new Response(HttpStatus.CREATED, ContentType.PLAIN_TEXT, "Der Benutzer wurde erfolgreich registriert.");
        } catch (DataConflictException e) {
            return new Response(HttpStatus.CONFLICT, ContentType.PLAIN_TEXT, "Dieser Benutzer existiert bereits.");
        }
    }

    @REST(path = "login", method = HttpMethod.POST, authRequired = false)
    public Response loginUser(UserAuthDTO user) {
        String token = authService.loginUser(user);
        LOGGER.log(Level.FINE, "{} hat sich eingeloggt.", user.username());
        return new Response(HttpStatus.OK, ContentType.PLAIN_TEXT, token);
    }

    @REST(path = "{id}/profile", method = HttpMethod.GET)
    public UserProfileDTO getProfile(@PathParam("id") Long userId) {
        return userService.getUserProfile(userId);
    }

    @REST(path = "{id}/profile", method = HttpMethod.PUT)
    public void updateProfile(@PathParam("id") Long userId, UserProfileUpdateDTO userProfile) {
        userService.updateUserProfile(userId, userProfile);
    }

    @REST(path = "{id}/ratings", method = HttpMethod.GET)
    public List<RatingOutDTO> getRatings(@PathParam("id") Long userId) {
        return ratingService.getRatingsForUser(userId);
    }

    @REST(path = "{id}/favorites", method = HttpMethod.GET)
    public List<MediaEntryOutDTO> getFavorites(@PathParam("id") Long userId) {
        return mediaService.getMediaEntriesFavoritedByUser(userId);
    }

    @REST(path = "{id}/recommendations", method = HttpMethod.GET)
    public List<MediaEntryOutDTO> getRecommendations(@PathParam("id") Long userId, @QueryParam("type") String type) {
        if (type.equals("genre")) {
            return mediaService.getRecommendationsForUserByGenre(userId);
        } else if (type.equals("content")) {
            return mediaService.getRecommendationsForUserByContent(userId);
        }
        throw new InvalidInputException("Dieser Typ von Empfehlungen ist nicht möglich.");
    }
}
