package at.fhtw.mrp.rest;

import at.fhtw.mrp.dto.UserProfileUpdateDTO;
import at.fhtw.mrp.rest.server.Server;
import at.fhtw.mrp.util.AbstractDBTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.migrationsupport.rules.EnableRuleMigrationSupport;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.SQLException;

@EnableRuleMigrationSupport
public class UserIntegerationTest extends AbstractDBTest {

    public static final String BASE_API = "http://localhost:8080/api/";
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @BeforeEach
    public void setup() throws SQLException {
        super.setup();
        try {
            new Server().start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void generalUserIntegrationTest() throws IOException, InterruptedException, URISyntaxException {
        try (HttpClient httpClient = HttpClient.newHttpClient()) {

            HttpResponse<String> registerResponse = httpClient.send(preparePostRequest("users/register", """
                    {
                        "username": "user1",
                        "password": "pass123"
                    }
                    """).build(), HttpResponse.BodyHandlers.ofString());

            Assertions.assertEquals(201, registerResponse.statusCode(), "Register User failed.");

            HttpResponse<String> loginResponse = httpClient.send(preparePostRequest("users/login", """
                    {
                        "username": "user1",
                        "password": "pass123"
                    }
                    """).build(), HttpResponse.BodyHandlers.ofString());
            String authToken = loginResponse.body();

            Assertions.assertEquals(200, loginResponse.statusCode(), "Login User failed.");
            Assertions.assertTrue(StringUtils.isNotBlank(authToken), "AuthToken is missing.");

            HttpResponse<String> getProfileResponse = httpClient.send(prepareGetRequest("users/1/profile", authToken).build(),
                    HttpResponse.BodyHandlers.ofString());
            String userProfile = getProfileResponse.body();

            Assertions.assertEquals(200, getProfileResponse.statusCode(), "Get User-Profile failed.");
            String expectedInitialUserProfile = "{\"id\":1,\"username\":\"user1\",\"email\":null,\"favoriteGenre\":null,\"ratingCount\":0,\"ratingAvg\":0.0}";
            Assertions.assertEquals(expectedInitialUserProfile, userProfile, "Correct User-Profile returned.");

            HttpResponse<String> updateProfileResponse = httpClient.send(preparePutRequest("users/1/profile",
                    MAPPER.writeValueAsString(new UserProfileUpdateDTO("test@email.com", "action")),
                    authToken).build(), HttpResponse.BodyHandlers.ofString());

            Assertions.assertEquals(204, updateProfileResponse.statusCode(), "Update User-Profile failed.");

            HttpResponse<String> getProfile2Response = httpClient.send(prepareGetRequest("users/1/profile", authToken).build(),
                    HttpResponse.BodyHandlers.ofString());
            String userProfile2 = getProfile2Response.body();

            Assertions.assertEquals(200, getProfileResponse.statusCode(), "Get User-Profile failed.");
            String expectedUpdatedUserProfile = "{\"id\":1,\"username\":\"user1\",\"email\":\"test@email.com\",\"favoriteGenre\":\"action\",\"ratingCount\":0,\"ratingAvg\":0.0}";
            Assertions.assertEquals(expectedUpdatedUserProfile, userProfile2, "Correct Updated User-Profile returned.");
        }
    }

    /**
     * Bereitet eine GET-Request mit Authentifizierung.
     */
    public static HttpRequest.Builder prepareGetRequest(String apiUrl, String authToken) throws URISyntaxException {
        return HttpRequest.newBuilder(new URI(BASE_API + apiUrl)).GET()
                .header("Authorization", "Bearer " + authToken);
    }

    /**
     * Bereitet eine PUT-Request mit Authentifizierung.
     */
    public static HttpRequest.Builder preparePutRequest(String apiUrl, String body, String authToken) throws URISyntaxException {
        return HttpRequest.newBuilder(new URI(BASE_API + apiUrl))
                .PUT(HttpRequest.BodyPublishers.ofString(body))
                .header("Authorization", "Bearer " + authToken);
    }

    /**
     * Bereitet eine POST-Request ohne Authentifizierung.
     */
    public static HttpRequest.Builder preparePostRequest(String apiUrl, String body) throws URISyntaxException {
        return HttpRequest.newBuilder(new URI(BASE_API + apiUrl))
                .POST(HttpRequest.BodyPublishers.ofString(body));
    }
}
