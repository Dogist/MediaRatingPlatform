package at.fhtw.mrp.dto;

import at.fhtw.mrp.exceptions.InvalidInputException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RatingInDTOTest {

    @Test
    void validateCorrectRatingInDTO() {
        RatingInDTO correctRatingInDTO = new RatingInDTO((short) 3, "comment");
        assertDoesNotThrow(correctRatingInDTO::validate, "Validiere ein korrektes RatingInDTO");
    }

    @Test
    void validateNoStarsRatingInDTO() {
        RatingInDTO invalidRatingInDTO1 = new RatingInDTO(null, "comment");
        assertThrows(InvalidInputException.class, invalidRatingInDTO1::validate, "Validiere ein invalides null-Stars RatingInDTO");
    }

    @Test
    void validateNegativStarsRatingInDTO() {
        RatingInDTO invalidRatingInDTO2 = new RatingInDTO((short) -1, "comment");
        assertThrows(InvalidInputException.class, invalidRatingInDTO2::validate, "Validiere ein invalides negativ-Stars RatingInDTO");
    }

    @Test
    void validateTooManyStarsRatingInDTO() {
        RatingInDTO invalidRatingInDTO3 = new RatingInDTO((short) 6, "comment");
        assertThrows(InvalidInputException.class, invalidRatingInDTO3::validate, "Validiere ein invalides zu groß-Stars RatingInDTO");
    }
}