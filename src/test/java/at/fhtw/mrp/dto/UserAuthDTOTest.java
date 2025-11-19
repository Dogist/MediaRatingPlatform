package at.fhtw.mrp.dto;

import at.fhtw.mrp.exceptions.InvalidInputException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserAuthDTOTest {

    @Test
    void validateCorrectUserAuthDTO() {
        UserAuthDTO correctUserAuthDTO = new UserAuthDTO("username", "password");
        assertDoesNotThrow(correctUserAuthDTO::validate, "Validiere ein korrektes UserAuthDTO");
    }

    @Test
    void validateMissingUsernameUserAuthDTO() {
        UserAuthDTO invalidUserAuthDTO1 = new UserAuthDTO("", "password");
        assertThrows(InvalidInputException.class, invalidUserAuthDTO1::validate, "Validiere ein invalides UserAuthDTO");
    }

    @Test
    void validateMissingPasswordUserAuthDTO() {
        UserAuthDTO invalidUserAuthDTO2 = new UserAuthDTO("username", "");
        assertThrows(InvalidInputException.class, invalidUserAuthDTO2::validate, "Validiere ein invalides UserAuthDTO");
    }

    @Test
    void validateEmptyUserAuthDTO() {
        UserAuthDTO invalidUserAuthDTO3 = new UserAuthDTO("", "");
        assertThrows(InvalidInputException.class, invalidUserAuthDTO3::validate, "Validiere ein invalides UserAuthDTO");
    }
}