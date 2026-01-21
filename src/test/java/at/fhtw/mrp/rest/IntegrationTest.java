package at.fhtw.mrp.rest;

import at.fhtw.mrp.dao.general.DatabaseManager;
import at.fhtw.mrp.dto.MediaEntryInDTO;
import at.fhtw.mrp.dto.UserProfileUpdateDTO;
import at.fhtw.mrp.rest.server.Server;
import at.fhtw.mrp.util.AbstractDBTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.junit.jupiter.api.*;
import org.junit.jupiter.migrationsupport.rules.EnableRuleMigrationSupport;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

@EnableRuleMigrationSupport
public class IntegrationTest extends AbstractDBTest {

    public static final String BASE_API = "http://localhost:8080/api/";
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static Server server;
    private static DatabaseManager databaseManager;

    @BeforeAll
    public static void setUp() throws IOException {
        databaseManager = AbstractDBTest.setupMockCDI();

        server = new Server();
        server.start();
    }

    @BeforeEach
    public void setup() {
        super.setupMockConnection(databaseManager);
    }

    @AfterAll
    public static void teardown() {
        server.stop();
    }

    @Test
    void generalUserIntegrationTest() throws IOException, InterruptedException, URISyntaxException {
        try (HttpClient httpClient = HttpClient.newHttpClient()) {

            String authToken = registerAndLoginUser(httpClient);

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

    @Test
    void generalMediaEntryIntegrationTest() throws IOException, InterruptedException, URISyntaxException {
        try (HttpClient httpClient = HttpClient.newHttpClient()) {

            String authToken = registerAndLoginUser(httpClient);

            HttpResponse<String> createMedia = httpClient.send(preparePostRequest("media", """
                            {
                              "title": "Inception",
                              "description": "Sci-fi thriller",
                              "mediaType": "movie",
                              "releaseYear": 2010,
                              "genres": [
                                "sci-fi",
                                "thriller"
                              ],
                              "ageRestriction": 12
                            }
                            """, authToken).build(),
                    HttpResponse.BodyHandlers.ofString());
            String media = createMedia.body();

            Assertions.assertEquals(201, createMedia.statusCode(), "Create MediaEntry failed.");
            String expectedInitialMedia = "{\"id\":1,\"mediaType\":\"MOVIE\",\"title\":\"Inception\",\"description\":\"Sci-fi thriller\",\"releaseYear\":2010,\"genres\":[\"sci-fi\",\"thriller\"],\"ageRestriction\":12,\"creator\":\"user1\",\"score\":null}";
            Assertions.assertEquals(expectedInitialMedia, media, "Correct MediaEntry returned.");

            HttpResponse<String> updateMediaResponse = httpClient.send(preparePutRequest("media/1",
                    MAPPER.writeValueAsString(new MediaEntryInDTO("MOVIE", "InceptionTest",
                            "Test Description", 2008, List.of("action", "thriller"), 18)),
                    authToken).build(), HttpResponse.BodyHandlers.ofString());

            Assertions.assertEquals(204, updateMediaResponse.statusCode(), "Update MediaEntry failed.");

            HttpResponse<String> searchMediaEntryResponse = httpClient.send(prepareGetRequest("media?title=inception&genre=action&mediaType=movie&releaseYear=2008&ageRestriction=18&sortBy=title", authToken).build(),
                    HttpResponse.BodyHandlers.ofString());
            String searchedMediaEntry = searchMediaEntryResponse.body();

            Assertions.assertEquals(200, searchMediaEntryResponse.statusCode(), "Search MediaEntry failed.");
            String expectedUpdatedUserProfile = "[{\"id\":1,\"mediaType\":\"MOVIE\",\"title\":\"InceptionTest\",\"description\":\"Test Description\",\"releaseYear\":2008,\"genres\":[\"action\",\"thriller\"],\"ageRestriction\":18,\"creator\":\"user1\",\"score\":null}]";
            Assertions.assertEquals(expectedUpdatedUserProfile, searchedMediaEntry, "Correct Updated MediaEntry returned.");
        }
    }

    private static @NonNull String registerAndLoginUser(HttpClient httpClient) throws IOException, InterruptedException, URISyntaxException {
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

        return authToken;
    }

    /**
     * Bereitet eine GET-Request mit Authentifizierung.
     */
    private static HttpRequest.Builder prepareGetRequest(String apiUrl, String authToken) throws URISyntaxException {
        return HttpRequest.newBuilder(new URI(BASE_API + apiUrl)).GET()
                .header("Authorization", "Bearer " + authToken);
    }

    /**
     * Bereitet eine PUT-Request mit Authentifizierung.
     */
    private static HttpRequest.Builder preparePutRequest(String apiUrl, String body, String authToken) throws URISyntaxException {
        return HttpRequest.newBuilder(new URI(BASE_API + apiUrl))
                .PUT(HttpRequest.BodyPublishers.ofString(body))
                .header("Authorization", "Bearer " + authToken);
    }

    /**
     * Bereitet eine POST-Request mit Authentifizierung.
     */
    private static HttpRequest.Builder preparePostRequest(String apiUrl, String body, String authToken) throws URISyntaxException {
        return preparePostRequest(apiUrl, body)
                .header("Authorization", "Bearer " + authToken);
    }

    /**
     * Bereitet eine POST-Request ohne Authentifizierung.
     */
    private static HttpRequest.Builder preparePostRequest(String apiUrl, String body) throws URISyntaxException {
        return HttpRequest.newBuilder(new URI(BASE_API + apiUrl))
                .POST(HttpRequest.BodyPublishers.ofString(body));
    }
}
