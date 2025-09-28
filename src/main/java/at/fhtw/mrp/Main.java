package at.fhtw.mrp;

import at.fhtw.mrp.rest.server.Server;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try {
            new Server().start();
            System.out.println("Server started on port 8080");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
