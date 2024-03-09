package com.example.filestreamer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SpecialSocket {
    private static ExecutorService pool = Executors.newCachedThreadPool();
    public static void main(String[] args) throws IOException {
        ServerSocket specialSocket = new ServerSocket(1025);
        System.out.println(specialSocket.getLocalPort());
        System.out.println(specialSocket.getInetAddress().toString());
            System.out.println("BEFORE");
        Socket server = specialSocket.accept();
            System.out.println("AFTER SERVER");
        while(true){
        Socket client = specialSocket.accept();
        SpecialSocketHandler handler = new SpecialSocketHandler(server,client);
        pool.execute(handler);
        }
    }
}
