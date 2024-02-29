package com.example.filestreamer;

import com.zaxxer.hikari.pool.HikariProxyCallableStatement;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientHandler implements Runnable {
    private Socket client;
    private DataInputStream in;
    private DataOutputStream out;
    private HashMap<String, File> fileMap;
    private ExecutorService pool;


    public ClientHandler(Socket socket, HashMap<String, File> fileMap) throws IOException {
        this.fileMap = fileMap;
        client = socket;
        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());
        pool = Executors.newCachedThreadPool();
    }

    @Override
    public void run() {
        try {
            sendFileNamesToClient();
            while (true) {
                SendHandler handler;
                try {
                    System.out.println("FILE NAME RECEIVED");
                    String whatToDo = in.readUTF();


                    if (whatToDo.equals("download")) {
                        handler = new SendHandler(fileMap, true, in.readUTF());
                    } else {
                        handler = new SendHandler(fileMap,false, in.readUTF());
                    }
                    System.out.println("SEND HANDLER CREATED");
                    pool.execute(handler);
                    out.writeUTF(handler.getIpAddress());
                    out.writeInt(handler.getPort());
                    System.out.println("SEND HANDLER CREATED");
                    System.out.println(handler.getIpAddress());
                    System.out.println(handler.getPort());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

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

    private void sendFileNamesToClient() throws IOException {
        for (Map.Entry<String, File> entry : fileMap.entrySet()) {
            out.writeUTF(entry.getKey());
        }
        out.writeUTF("end");
    }
}
