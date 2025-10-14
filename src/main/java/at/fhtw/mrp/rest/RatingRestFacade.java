package at.fhtw.mrp.rest;

import at.fhtw.mrp.dto.RatingInDTO;
import at.fhtw.mrp.dto.RatingOutDTO;
import at.fhtw.mrp.rest.http.HttpMethod;
import at.fhtw.mrp.rest.server.PathParam;
import at.fhtw.mrp.rest.server.REST;
import at.fhtw.mrp.service.RatingService;

public class RatingRestFacade extends AbstractRestFacade {
    private final RatingService ratingService = new RatingService();

    public RatingRestFacade() {
        super("ratings");
    }

    @REST(path = "{id}", method = HttpMethod.GET)
    public RatingOutDTO getRating(@PathParam("id") Long ratingId) {
        return ratingService.getRating(ratingId);
    }

    @REST(path = "{id}", method = HttpMethod.PUT)
    public void updateRating(@PathParam("id") Long ratingId, RatingInDTO ratingInDTO) {
        ratingService.updateRating(ratingId, ratingInDTO);
    }

    @REST(path = "{id}", method = HttpMethod.DELETE)
    public void deleteRating(@PathParam("id") Long ratingId) {
        ratingService.deleteRating(ratingId);
    }

    @REST(path = "{id}/unlike", method = HttpMethod.POST)
    public void unlikeRating(@PathParam("id") Long ratingId) {
        ratingService.unlikeRating(ratingId);
    }

    @REST(path = "{id}/like", method = HttpMethod.POST)
    public void likeRating(@PathParam("id") Long ratingId) {
        ratingService.likeRating(ratingId);
    }

    @REST(path = "{id}/confirm", method = HttpMethod.POST)
    public void confirmRating(@PathParam("id") Long ratingId) {
        ratingService.confirmRating(ratingId);
    }
}
