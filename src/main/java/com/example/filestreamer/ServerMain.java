package com.example.filestreamer;

import java.io.IOException;


public class ServerMain {
    public static void main(String[] args) throws IOException {
        Server server = new Server();
        server.start(1024);
    }
}
