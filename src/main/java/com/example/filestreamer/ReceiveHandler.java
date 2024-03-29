package com.example.filestreamer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;

public class ReceiveHandler implements Runnable {
    private final File file;
    private final boolean whatTodo;
    private final int port;
    private final String ip;

    public ReceiveHandler(String ip, int port, Boolean whatToDo, File file) {
        this.whatTodo = whatToDo;
        this.file = file;
        this.port = port;
        this.ip = ip;
    }

    @Override
    public void run() {
        System.out.println("BEFORE SOCKET INITIALIZATION");
        System.out.println(ip);
        System.out.println(port);
        try (Socket receiver = new Socket(ip, port)) {
            DataInputStream in = new DataInputStream(receiver.getInputStream());
            DataOutputStream out = new DataOutputStream(receiver.getOutputStream());
            out.writeUTF(file.getName());
            if (whatTodo) {
                System.out.println("FILE HANDLER");
                FileManager.receiveFile(in, file);
            } else {
                System.out.println("FILE HANDLER");
                FileManager.sendFile(out, file);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}