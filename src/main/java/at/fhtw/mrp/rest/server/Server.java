package at.fhtw.mrp.rest.server;

import at.fhtw.mrp.rest.*;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

public class Server {
    public void start() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 10);

        List<AbstractRestFacade> facades = new ArrayList<>();
        facades.add(new UserRestFacade());
        facades.add(new MediaRestFacade());
        facades.add(new RatingRestFacade());
        facades.add(new LeaderboardRestFacade());

        facades.forEach(facade ->
                server.createContext(facade.getBasePath(), facade));

        server.start();
    }
}
