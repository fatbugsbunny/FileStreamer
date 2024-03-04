package com.example.filestreamer;

import javax.swing.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.*;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private ServerSocket serverSocket;
    private ExecutorService pool = Executors.newCachedThreadPool();
//    private ArrayList<Client> clients = new ArrayList<>();

    public void start(ArrayList<Client> clients) throws IOException {
        serverSocket = new ServerSocket(1024);
        System.out.println(getIp());
        while (true) {
            HashMap<String, File> filesMap = getFilesInFolder();
            Socket client = serverSocket.accept();
            ClientHandler clientHandler = new ClientHandler(client, filesMap, clients, true);
            pool.execute(clientHandler);
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
        if(filesInDirectory == null){
            return new HashMap<>();
        }

        for (File file : filesInDirectory) {
            map.put(file.getName(), file);
        }
        return map;
    }

}

