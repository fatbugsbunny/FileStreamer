package com.example.filestreamer;

import javax.swing.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private ServerSocket serverSocket;
    private ExecutorService pool = Executors.newCachedThreadPool();
    private HashMap<String, File> filesMap;

    public void start() throws IOException {
        serverSocket = new ServerSocket(1024);
        while (true) {
            filesMap = getFilesInFolder();
            Socket client = serverSocket.accept();
            ClientHandler clientHandler = new ClientHandler(client, filesMap);
            pool.execute(clientHandler);
        }
    }

    private HashMap<String, File> getFilesInFolder() {
        HashMap<String, File> map = new HashMap<>();
        File customDirectory = FilePaths.FILE_PATH_SERVER.path.toFile();
        JFileChooser fileChooser = new JFileChooser(customDirectory);

        File[] filesInDirectory = fileChooser.getCurrentDirectory().listFiles();
        for(File file: filesInDirectory){
            map.put(file.getName(), file);
        }

        return map;
    }

}

