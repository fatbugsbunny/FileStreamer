package com.example.filestreamer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;

public class ReceiveHandler implements Runnable {
    private DataInputStream in;
    private DataOutputStream out;
    private File file;
    private boolean whatTodo;
    private int port;
    private String ip;

    public ReceiveHandler(String ip, int port, Boolean whatToDo, File file) throws IOException {
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
            in = new DataInputStream(receiver.getInputStream());
            out = new DataOutputStream(receiver.getOutputStream());
            out.writeUTF(file.getName());
            if (whatTodo) {
                System.out.println("FILE HANDLER");
                System.out.println(file.getName());
                FileManager.receiveFile(in, file);
            } else {
                System.out.println("FILE HANDLER");
                System.out.println(file.getName());
                FileManager.sendFile(out, file);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
