package com.example.filestreamer;

import javax.swing.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.*;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

public class Server {
    private ServerSocket serverSocket;
    private ExecutorService pool = Executors.newCachedThreadPool();
    private LinkedBlockingQueue<Client> clients = new LinkedBlockingQueue<>();

    public void start(ArrayList<Client> givenClients) throws IOException, InterruptedException {
        serverSocket = new ServerSocket(1024);
        clients.addAll(givenClients);
        while (true) {
            HashMap<String, File> filesMap = getFilesInFolder();
            Socket client = serverSocket.accept();
            ClientHandler clientHandler = new ClientHandler(client, filesMap, clients, true);
            pool.execute(clientHandler);
            DataInputStream in = new DataInputStream(client.getInputStream());
            String name = in.readUTF();
            String ip = in.readUTF();
            int port = in.readInt();
            System.out.println("PORT IN SERVER " + port);
            Client john = new Client(name,ip,port);
            clients.put(john);
        }
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

    private HashMap<String, File> getFilesInFolder() {
        HashMap<String, File> map = new HashMap<>();
        File customDirectory = FilePaths.FILE_PATH_SERVER.path.toFile();
        JFileChooser fileChooser = new JFileChooser(customDirectory);

        File[] filesInDirectory = fileChooser.getCurrentDirectory().listFiles();
        if (filesInDirectory == null) {
            return new HashMap<>();
        }

        for (File file : filesInDirectory) {
            map.put(file.getName(), file);
        }
        return map;
    }

}