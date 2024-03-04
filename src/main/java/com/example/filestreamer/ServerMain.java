package com.example.filestreamer;

import java.io.IOException;
import java.util.ArrayList;

public class ServerMain {
    public static void main(String[] args) {
        ArrayList<Client> clients = new ArrayList<>();
        clients.add(new Client("paul"));
        Server server = new Server();
        try {
            server.start(clients);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
