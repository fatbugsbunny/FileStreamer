package com.example.filestreamer;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AnotherClient {
    public static void main(String[] args) throws IOException {
        ArrayList<Client> clients = new ArrayList<>();

        ExecutorService pool = Executors.newCachedThreadPool();
        Client john = new Client("John");
        Client paul = new Client("Paul");
        Client smith = new Client("Smith");
        Client dave = new Client("Dave");

        pool.execute(john);
        pool.execute(paul);
        pool.execute(smith);
        pool.execute(dave);

        clients.add(john);
        clients.add(paul);
        clients.add(smith);
        clients.add(dave);

        Server server = new Server();
        server.start(clients);
    }
}
