package at.fhtw.mrp.exceptions;

import at.fhtw.mrp.rest.http.HttpStatus;

/**
 * Basis Exception in MRP, damit alle
 * Custom-Exceptions auf einmal behandelt werden können.
 */
public abstract class BaseMRPException extends RuntimeException {

    public abstract HttpStatus getStatus();

    public BaseMRPException(String message) {
        super(message);
    }
}
