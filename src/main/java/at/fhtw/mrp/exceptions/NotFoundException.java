package at.fhtw.mrp.exceptions;

import at.fhtw.mrp.rest.http.HttpStatus;

public class NotFoundException extends BaseMRPException {
    @Override
    public HttpStatus getStatus() {
        return HttpStatus.NOT_FOUND;
    }

    public NotFoundException(String message) {
        super(message);
    }

}
