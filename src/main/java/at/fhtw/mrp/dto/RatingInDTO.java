package at.fhtw.mrp.dto;

import at.fhtw.mrp.exceptions.InvalidInputException;

public record RatingInDTO(Short stars, String comment) implements Validateable {
    @Override
    public void validate() throws InvalidInputException {
        if (stars == null || stars < 1 || stars > 5) {
            throw new InvalidInputException("Dieses Rating ist invalide.");
        }
    }
}
