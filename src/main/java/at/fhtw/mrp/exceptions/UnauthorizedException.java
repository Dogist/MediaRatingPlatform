package at.fhtw.mrp.exceptions;

import at.fhtw.mrp.rest.http.HttpStatus;

public class UnauthorizedException extends BaseMRPException {
    @Override
    public HttpStatus getStatus() {
        return HttpStatus.UNAUTHORIZED;
    }

    public UnauthorizedException(String message) {
        super(message);
    }

}
