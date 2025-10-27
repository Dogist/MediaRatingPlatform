package at.fhtw.mrp.rest;

import at.fhtw.mrp.entity.UserEntity;
import at.fhtw.mrp.rest.http.HttpMethod;
import at.fhtw.mrp.rest.server.REST;
import at.fhtw.mrp.service.CDI;
import at.fhtw.mrp.service.UserService;

import java.util.List;

public class LeaderboardRestFacade extends AbstractRestFacade {
    private final UserService userService = CDI.INSTANCE.getService(UserService.class);

    public LeaderboardRestFacade() {
        super("leaderboard");
    }

    @REST(method = HttpMethod.GET)
    public List<UserEntity> getLeaderboard() {
        return userService.getUsersByRatingCount();
    }
}
