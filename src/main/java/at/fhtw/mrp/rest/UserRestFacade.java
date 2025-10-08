package at.fhtw.mrp.rest;

import at.fhtw.mrp.dao.general.DataConflictException;
import at.fhtw.mrp.dto.UserAuthDTO;
import at.fhtw.mrp.dto.UserProfileDTO;
import at.fhtw.mrp.dto.UserProfileUpdateDTO;
import at.fhtw.mrp.rest.http.ContentType;
import at.fhtw.mrp.rest.http.HttpMethod;
import at.fhtw.mrp.rest.http.HttpStatus;
import at.fhtw.mrp.rest.server.PathParam;
import at.fhtw.mrp.rest.server.QueryParam;
import at.fhtw.mrp.rest.server.REST;
import at.fhtw.mrp.rest.server.Response;
import at.fhtw.mrp.service.AuthService;
import at.fhtw.mrp.service.BearerAuthServiceImpl;
import at.fhtw.mrp.service.UserService;

import java.util.logging.Level;
import java.util.logging.Logger;

public class UserRestFacade extends AbstractRestFacade {

    public static final Logger LOGGER = Logger.getLogger(UserRestFacade.class.getSimpleName());
    private final UserService userService;
    private final AuthService authService;

    public UserRestFacade() {
        super("users");
        userService = new UserService();
        authService = new BearerAuthServiceImpl();
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
    public Response getRatings(@PathParam("id") Long userId) {
        // TODO Implement
        return new Response(HttpStatus.OK, ContentType.PLAIN_TEXT, "Get Ratings Successfully " + userId);
    }

    @REST(path = "{id}/favorites", method = HttpMethod.GET)
    public Response getFavorites(@PathParam("id") Long userId) {
        // TODO Implement
        return new Response(HttpStatus.OK, ContentType.PLAIN_TEXT, "Get Ratings Successfully " + userId);
    }

    @REST(path = "{id}/recommendations", method = HttpMethod.GET)
    public Response getRecommendations(@PathParam("id") Long userId, @QueryParam("type") String type) {
        // TODO Implement
        return new Response(HttpStatus.OK, ContentType.PLAIN_TEXT, "Get Recommendations Successfully " + userId + " :: " + type);
    }
}
