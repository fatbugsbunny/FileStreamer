package com.example.filestreamer;

import java.io.IOException;

public class ClientMain {
    public static void main(String[] args) throws IOException {
        Client client = new Client("John");
        client.connect("192.168.1.3", 1024);
    }
}

