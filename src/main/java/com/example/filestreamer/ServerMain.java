package com.example.filestreamer;

import java.io.IOException;

public class ServerMain {
    public static void main(String[] args) {
        Server server = new Server();
        try {
            server.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
