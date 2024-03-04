package com.example.filestreamer;

import java.io.IOException;

public class ClientMain {
    public static void main(String[] args) throws IOException {
        Client client = new Client("John");
        client.connect("192.168.56.1", 1024);
    }
}

