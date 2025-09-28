package at.fhtw.mrp.rest;

import at.fhtw.mrp.rest.http.HttpMethod;
import at.fhtw.mrp.rest.server.PathParam;
import at.fhtw.mrp.rest.server.REST;
import at.fhtw.mrp.rest.server.Response;

public class LeaderboardRestFacade extends AbstractRestFacade {
    public LeaderboardRestFacade() {
        super("leaderboard");
    }

    @REST(path = "", method = HttpMethod.GET)
    public Response getLeaderboard() {
        // TODO Implement
        return null;
    }
}
