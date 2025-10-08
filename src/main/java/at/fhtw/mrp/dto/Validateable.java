package at.fhtw.mrp.dto;

import at.fhtw.mrp.exceptions.InvalidInputException;

/**
 * Interface um ein DTO-Validierbar zu deklarieren.
 */
public interface Validateable {

    void validate() throws InvalidInputException;
}
