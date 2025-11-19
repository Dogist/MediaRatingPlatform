package at.fhtw.mrp.dto;

import at.fhtw.mrp.exceptions.InvalidInputException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MediaEntryInDTOTest {
    @Test
    void validateCorrectMediaEntryInDTO() {
        MediaEntryInDTO correctMediaEntryInDTO = new MediaEntryInDTO(MediaEntryType.GAME.name(), "title",
                "description", 1998, List.of("genre"), 18);
        assertDoesNotThrow(correctMediaEntryInDTO::validate, "Validiere ein korrektes MediaEntryInDTO");
    }

    @Test
    void validateWrongMediaTypeMediaEntryInDTO() {
        MediaEntryInDTO invalidMediaEntryInDTO1 = new MediaEntryInDTO("FakeMediaType", "title",
                "description", 1998, List.of("genre"), 18);
        assertThrows(InvalidInputException.class, invalidMediaEntryInDTO1::validate, "Validiere ein invalides MediaEntryInDTO");
    }

    @Test
    void validateInvalidReleaseYearMediaEntryInDTO() {
        MediaEntryInDTO invalidMediaEntryInDTO2 = new MediaEntryInDTO(MediaEntryType.GAME.name(), "title",
                "description", -12, List.of("genre"), 18);
        assertThrows(InvalidInputException.class, invalidMediaEntryInDTO2::validate, "Validiere ein invalides MediaEntryInDTO");
    }

    @Test
    void validateInvalidAgeRestrictionMediaEntryInDTO() {
        MediaEntryInDTO invalidMediaEntryInDTO3 = new MediaEntryInDTO(MediaEntryType.GAME.name(), "title",
                "description", 1998, List.of("genre"), -15);
        assertThrows(InvalidInputException.class, invalidMediaEntryInDTO3::validate, "Validiere ein invalides MediaEntryInDTO");
    }
}