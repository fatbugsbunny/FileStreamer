package com.example.filestreamer;

import java.io.IOException;

public class ClientMain2 {
    public static void main(String[] args) throws IOException, InterruptedException, ClassNotFoundException {
        Client john = new Client("Smith");
        john.connect("192.168.56.1", 1024);
    }
}
