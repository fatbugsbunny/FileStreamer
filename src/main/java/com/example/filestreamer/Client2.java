package com.example.filestreamer;

import java.io.IOException;

public class Client2 {
    public static void main(String[] args) throws IOException, InterruptedException {
        Client john = new Client("Smith");
        john.connect("192.168.56.1", 1024);
    }
}