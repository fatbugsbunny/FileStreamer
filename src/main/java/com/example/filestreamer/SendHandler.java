package com.example.filestreamer;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;

public class SendHandler implements Runnable, Serializable {
    private final boolean whatTodo;
    private final boolean serverConnection;
    private final ServerSocket serverSocket = new ServerSocket(0);
    private final String ip;
    private final int port;
    private final HashMap<String, File> fileMap;

    public SendHandler(HashMap<String, File> fileMap, Boolean whatToDo, Boolean serverConnection) throws IOException {
        this.whatTodo = whatToDo;
        this.serverConnection = serverConnection;
        port = serverSocket.getLocalPort();
        ip = getIp();
        this.fileMap = fileMap;
    }

    @Override
    public void run() {
        try (Socket receiver = serverSocket.accept()) {
            System.out.println("SERVER" + port);
            System.out.println("SOCKET CREATED");
            DataInputStream in = new DataInputStream(receiver.getInputStream());
            DataOutputStream out = new DataOutputStream(receiver.getOutputStream());
            String name = in.readUTF();
            if (whatTodo) {
                FileManager.sendFile(out, fileMap.get(name));
            } else {
                File file = createEmptyFile(name);
                FileManager.receiveFile(in, file);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private File createEmptyFile(String name) {
        File file;
        if (serverConnection) {
            file = new File(FilePaths.FILE_PATH_SERVER.path.toString() + File.separator + name);
        } else {
            file = new File(FilePaths.FILE_PATH_CLIENT.path.toString() + File.separator + name);
        }
        return file;
    }

    public int getPort() {
        return port;
    }

    public String getIpAddress() {
        return ip;
    }

    private String getIp() {
        InetAddress localhost;
        try {
            localhost = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        return localhost.getHostAddress();
    }
}