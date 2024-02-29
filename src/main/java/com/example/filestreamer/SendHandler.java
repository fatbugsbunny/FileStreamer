package com.example.filestreamer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;

public class SendHandler implements Runnable {
    private DataInputStream in;
    private DataOutputStream out;
    private String file;
    private boolean whatTodo;
    private ServerSocket serverSocket = new ServerSocket(0);
    private String ip;
    private int port;
    private HashMap<String, File> fileMap;

    public SendHandler(HashMap<String, File> fileMap, Boolean whatToDo, String file) throws IOException {
        this.whatTodo = whatToDo;
        this.file = file;
        port = serverSocket.getLocalPort();
        ip = getIp();
        this.fileMap = fileMap;
    }

    @Override
    public void run() {
        try (Socket receiver = serverSocket.accept()) {
            System.out.println("SERVER" + port);
            System.out.println("SOCKET CREATED");
            in = new DataInputStream(receiver.getInputStream());
            out = new DataOutputStream(receiver.getOutputStream());
            String name = in.readUTF();
            if (whatTodo) {
                FileManager.sendFile(out, fileMap.get(name));
            } else {
                File file = new File(FilePaths.FILE_PATH_SERVER.path.toString() + File.separator + name);
                FileManager.receiveFile(in, file);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
