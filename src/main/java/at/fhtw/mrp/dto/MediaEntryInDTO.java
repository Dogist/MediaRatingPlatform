package at.fhtw.mrp.dto;

import at.fhtw.mrp.exceptions.InvalidInputException;

import java.util.List;

public record MediaEntryInDTO(
        String mediaType,
        String title,
        String description,
        Integer releaseYear,
        List<String> genres,
        Integer ageRestriction) implements Validateable {
    @Override
    public void validate() throws InvalidInputException {
        try {
            MediaEntryType.parse(mediaType);
        } catch (IllegalArgumentException e) {
            throw new InvalidInputException("Der angegebene MediaTyp ist invalid.");
        }
        if (ageRestriction < 0) {
            throw new InvalidInputException("Der angegebene Altersbeschränkung ist invalid.");
        }
        if (releaseYear < 0) {
            throw new InvalidInputException("Das angegebene Veröffentlichungsjahr ist invalid.");
        }
    }
}
