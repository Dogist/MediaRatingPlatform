package at.fhtw.mrp.dao.general;

public class DataConflictException extends Exception {
    public DataConflictException(String message) {
        super(message);
    }
}
