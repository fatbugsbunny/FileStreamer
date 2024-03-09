package com.example.filestreamer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class ClientHandler implements Runnable {
    private DataInputStream in;
    private DataOutputStream out;
    private HashMap<String, File> fileMap;
    private ExecutorService pool;
    private LinkedBlockingQueue<Client> clients;
    private Boolean serverConnection;

    public ClientHandler(Socket socket, HashMap<String, File> fileMap, LinkedBlockingQueue<Client> clients, Boolean serverConnection) throws IOException {
        this.clients = clients;
        this.fileMap = fileMap;
        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());
        pool = Executors.newCachedThreadPool();
        this.serverConnection = serverConnection;
    }

    @Override
    public void run() {
        try {
            sendFileNamesToClient();
            if (serverConnection) {
                informAboutOtherClients();
            }
            while (true) {
                SendHandler handler;

                System.out.println("FILE NAME RECEIVED");
                String whatToDo = in.readUTF();


                if (whatToDo.equals("download")) {
                    handler = new SendHandler(fileMap, true,serverConnection);
                } else {
                    handler = new SendHandler(fileMap, false,serverConnection);
                }
                System.out.println("SEND HANDLER CREATED");
                pool.execute(handler);
                out.writeUTF(handler.getIpAddress());
                out.writeInt(handler.getPort());
                System.out.println("SEND HANDLER CREATED");
                System.out.println(handler.getIpAddress());
                System.out.println(handler.getPort());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                in.close();
                out.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void informAboutOtherClients() throws IOException {
        for (Client client : clients) {
            out.writeUTF(client.getName());
            out.writeUTF(client.getIpAddress());
            out.writeInt(client.getPort());
        }
        out.writeUTF("end");
    }

    private void sendFileNamesToClient() throws IOException {
        for (Map.Entry<String, File> entry : fileMap.entrySet()) {
            out.writeUTF(entry.getKey());
        }
        out.writeUTF("end");
    }
}
