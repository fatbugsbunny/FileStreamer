package com.example.filestreamer;

import java.io.IOException;

public class ClientMain {
    public static void main(String[] args) throws IOException, InterruptedException, ClassNotFoundException {
        Client client = new Client("Paul");
        client.connect("192.168.56.1", 1024);
    }
}