package com.example.filestreamer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerMain {
    public static void main(String[] args) throws IOException, InterruptedException, ClassNotFoundException {
        Server server = new Server();
        server.start();
    }
}
