package at.fhtw.mrp.rest.server;

import at.fhtw.mrp.rest.http.ContentType;
import at.fhtw.mrp.rest.http.HttpStatus;

public record Response(int status, String contentType, String content) {

    public Response(HttpStatus status, ContentType contentType, String content) {
        this(status.code, contentType.type, content);
    }

}