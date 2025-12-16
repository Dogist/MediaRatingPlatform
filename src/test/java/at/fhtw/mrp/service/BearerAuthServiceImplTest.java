package at.fhtw.mrp.service;

import at.fhtw.mrp.dao.UserDao;
import at.fhtw.mrp.dto.UserAuthDTO;
import at.fhtw.mrp.entity.UserEntity;
import at.fhtw.mrp.exceptions.InvalidInputException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class BearerAuthServiceImplTest {

    private static UserDao userDao;
    private static AuthService authService;
    private final static UserAuthDTO validUserAuthDTO = new UserAuthDTO("valid", "valid");

    @BeforeAll
    static void setUp() {
        userDao = Mockito.mock(UserDao.class);
        authService = new BearerAuthServiceImpl(userDao);

        UserEntity validUserEntity = new UserEntity(0L, validUserAuthDTO.username(), HashUtil.generateHashedPassword(validUserAuthDTO.password()), "", "");
        Mockito.when(userDao.getUserByUsername(validUserAuthDTO.username())).thenReturn(validUserEntity);
    }

    @Test
    void loginUser_invalidUser() {
        UserAuthDTO invalidUserAuthDTO = new UserAuthDTO("invalid", "invalid");
        assertThrows(InvalidInputException.class, () -> authService.loginUser(invalidUserAuthDTO), "Validiere einen invaliden Login.");
    }

    @Test
    void checkAuthToken_validToken() {
        String token = authService.loginUser(validUserAuthDTO);

        Mockito.verify(userDao).getUserByUsername(Mockito.anyString());

        assertNotNull(token, "Validiere einen validen Login.");
        assertEquals(authService.checkAuthToken(token), validUserAuthDTO.username(), "Validiere einen korrekten Login und Autorisierung.");
    }

    @Test
    void checkAuthToken_invalidToken() {
        assertNull(authService.checkAuthToken("invalidToken"), "Validiere einen invaliden AuthToken.");
    }
}