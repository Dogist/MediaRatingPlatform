package at.fhtw.mrp.rest;

import at.fhtw.mrp.dto.MediaEntryInDTO;
import at.fhtw.mrp.dto.MediaEntryOutDTO;
import at.fhtw.mrp.rest.http.ContentType;
import at.fhtw.mrp.rest.http.HttpMethod;
import at.fhtw.mrp.rest.http.HttpStatus;
import at.fhtw.mrp.rest.server.PathParam;
import at.fhtw.mrp.rest.server.QueryParam;
import at.fhtw.mrp.rest.server.REST;
import at.fhtw.mrp.rest.server.Response;
import at.fhtw.mrp.service.MediaService;

import java.util.List;

// TODO Implement
public class MediaRestFacade extends AbstractRestFacade {

    private final MediaService mediaService;

    public MediaRestFacade() {
        super("media");
        mediaService = new MediaService();
    }

    @REST(method = HttpMethod.GET)
    public List<MediaEntryOutDTO> searchMediaEntries(@QueryParam("title") String title,
                                                     @QueryParam("genre") String genre,
                                                     @QueryParam("mediaType") String mediaType,
                                                     @QueryParam("releaseYear") Integer releaseYear,
                                                     @QueryParam("ageRestriction") Integer ageRestriction,
                                                     @QueryParam("rating") Integer rating,
                                                     @QueryParam("sortBy") String sortBy) {
        return mediaService.searchMediaEntries(title, genre, mediaType,
                releaseYear, ageRestriction, rating, sortBy);
    }

    @REST(method = HttpMethod.POST)
    public Response createMediaEntry(MediaEntryInDTO mediaEntry) {
        return new Response(HttpStatus.CREATED, ContentType.JSON, mediaService.createMediaEntry(mediaEntry));
    }

    @REST(path = "{id}", method = HttpMethod.GET)
    public MediaEntryOutDTO getMediaEntry(@PathParam("id") Long mediaId) {
        return mediaService.getMediaEntry(mediaId);
    }

    @REST(path = "{id}", method = HttpMethod.PUT)
    public void updateMediaEntry(@PathParam("id") Long mediaId, MediaEntryInDTO mediaEntry) {
        mediaService.updateMediaEntry(mediaId, mediaEntry);
    }

    @REST(path = "{id}", method = HttpMethod.DELETE)
    public void deleteMediaEntry(@PathParam("id") Long mediaId) {
        mediaService.deleteMediaEntry(mediaId);
    }

    @REST(path = "{id}/rate", method = HttpMethod.POST)
    public Response rateMediaEntry(@PathParam("id") Long mediaId) {
        // TODO Implement
        return null;
    }

    @REST(path = "{id}/favorite", method = HttpMethod.POST)
    public Response favoriteMediaEntry(@PathParam("id") Long mediaId) {
        // TODO Implement
        return null;
    }

    @REST(path = "{id}/favorite", method = HttpMethod.DELETE)
    public Response unfavoriteMediaEntry(@PathParam("id") Long mediaId) {
        // TODO Implement
        return null;
    }
}
