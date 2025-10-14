package at.fhtw.mrp.service;

import at.fhtw.mrp.exceptions.InvalidInputException;

public class ValidationUtil {

    public static void validateEntityId(Long id, String entityType) throws InvalidInputException {
        if (id == null || id < 1) {
            throw new InvalidInputException("Diese " + entityType + "-ID ist ungültig.");
        }
    }
}
