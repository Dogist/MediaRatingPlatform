package at.fhtw.mrp.rest;

import at.fhtw.mrp.rest.http.HttpMethod;
import at.fhtw.mrp.rest.server.PathParam;
import at.fhtw.mrp.rest.server.REST;
import at.fhtw.mrp.rest.server.Response;

public class RatingRestFacade extends AbstractRestFacade {
    public RatingRestFacade() {
        super("ratings");
    }

    @REST(path = "{id}", method = HttpMethod.PUT)
    public Response updateRating(@PathParam("id") String ratingId) {
        // TODO Implement
        return null;
    }

    @REST(path = "{id}/like", method = HttpMethod.POST)
    public Response likeRating(@PathParam("id") String ratingId) {
        // TODO Implement
        return null;
    }

    @REST(path = "{id}/confirm", method = HttpMethod.POST)
    public Response confirmRating(@PathParam("id") String ratingId) {
        // TODO Implement
        return null;
    }
}
