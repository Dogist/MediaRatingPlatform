package at.fhtw.mrp.rest;

import at.fhtw.mrp.dao.general.DataConflictException;
import at.fhtw.mrp.dto.UserAuth;
import at.fhtw.mrp.rest.http.ContentType;
import at.fhtw.mrp.rest.http.HttpMethod;
import at.fhtw.mrp.rest.http.HttpStatus;
import at.fhtw.mrp.rest.server.PathParam;
import at.fhtw.mrp.rest.server.REST;
import at.fhtw.mrp.rest.server.Response;
import at.fhtw.mrp.service.AuthService;
import at.fhtw.mrp.service.UserService;
import org.apache.commons.lang3.StringUtils;

import java.util.logging.Level;
import java.util.logging.Logger;

public class UserRestFacade extends AbstractRestFacade {

    public static final Logger LOGGER = Logger.getLogger(UserRestFacade.class.getSimpleName());
    private final UserService userService;
    private final AuthService authService;

    public UserRestFacade() {
        super("users");
        userService = new UserService();
        authService = new AuthService();
    }

    @REST(path = "register", method = HttpMethod.POST, authRequired = false)
    public Response registerUser(UserAuth user) {
        try {
            userService.createUser(user);
            return new Response(HttpStatus.CREATED, ContentType.PLAIN_TEXT, "Der Benutzer wurde erfolgreich registriert.");

        } catch (DataConflictException e) {
            return new Response(HttpStatus.CONFLICT, ContentType.PLAIN_TEXT, "Dieser Benutzer existiert bereits.");
        }
    }

    @REST(path = "login", method = HttpMethod.POST, authRequired = false)
    public Response loginUser(UserAuth user) {
        if (user == null || StringUtils.isBlank(user.password()) || StringUtils.isBlank(user.username())) {
            return new Response(HttpStatus.BAD_REQUEST, ContentType.PLAIN_TEXT, "Die Benutzerdaten sind unvollständig.");
        }
        String token = authService.loginUser(user);
        LOGGER.log(Level.FINE, "{} hat sich eingeloggt.", user.username());
        return new Response(HttpStatus.CREATED, ContentType.PLAIN_TEXT, token);
    }

    @REST(path = "{id}/profile", method = HttpMethod.GET)
    public Response getProfile(@PathParam("id") String id) {
        return new Response(HttpStatus.CREATED, ContentType.PLAIN_TEXT, "Get Profile Successfully" + id);
    }
}
