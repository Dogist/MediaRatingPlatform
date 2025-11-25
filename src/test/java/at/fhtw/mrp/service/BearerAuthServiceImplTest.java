package at.fhtw.mrp.service;

import at.fhtw.mrp.dao.UserDao;
import at.fhtw.mrp.dto.UserAuthDTO;
import at.fhtw.mrp.entity.UserEntity;
import at.fhtw.mrp.exceptions.InvalidInputException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class BearerAuthServiceImplTest {

    private AuthService authService;
    private final UserAuthDTO validUserAuthDTO = new UserAuthDTO("valid", "valid");
    private final UserAuthDTO invalidUserAuthDTO = new UserAuthDTO("invalid", "invalid");

    @BeforeEach
    void setUp() {
        UserDao userDao = Mockito.mock(UserDao.class);
        authService = new BearerAuthServiceImpl(userDao);

        UserEntity validUserEntity = new UserEntity(0L, "valid", HashUtil.generateHashedPassword("valid"), "", "");
        Mockito.when(userDao.getUserByUsername(validUserAuthDTO.username())).thenReturn(validUserEntity);
    }

    @Test
    void loginUser_validUser() {
        assertNotNull(authService.loginUser(validUserAuthDTO), "Validiere einen validen Login.");
    }

    @Test
    void loginUser_invalidUser() {
        assertThrows(InvalidInputException.class, () -> authService.loginUser(invalidUserAuthDTO), "Validiere einen invaliden Login.");
    }

    @Test
    void checkAuthToken_validToken() {
        String token = authService.loginUser(validUserAuthDTO);
        assertEquals(authService.checkAuthToken(token), validUserAuthDTO.username(), "Validiere einen korrekten Login und Autorisierung.");
    }

    @Test
    void checkAuthToken_invalidToken() {
        assertNull(authService.checkAuthToken("invalidToken"), "Validiere einen invaliden AuthToken.");
    }
}