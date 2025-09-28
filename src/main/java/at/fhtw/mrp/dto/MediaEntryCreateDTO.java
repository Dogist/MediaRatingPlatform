package at.fhtw.mrp.dto;

import java.util.List;

public record MediaEntryCreateDTO(
        String mediaType,
        String title,
        String description,
        Integer releaseYear,
        List<String> genres,
        Integer ageRestriction) {
}
