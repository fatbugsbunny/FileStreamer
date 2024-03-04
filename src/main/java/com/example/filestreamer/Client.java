package com.example.filestreamer;

import javax.swing.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.*;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Client implements Runnable {
    private String ipAddress;
    private int port;
    private ArrayList<Client> knownClients;
    private String name;
    private ExecutorService pool = Executors.newCachedThreadPool();
    private DataInputStream in;

    public Client(String name) {
        ipAddress = getIp();
        this.name = name;
        knownClients = new ArrayList<>();
    }

    public Client(String name, String ip) {
        this(name);
        ipAddress = ip;
    }

    public Client(String name, String ip, int port) {
        this(name, ip);
        this.port = port;
    }

    @Override
    public void run() {

        try (ServerSocket serverSocket = new ServerSocket(0)) {

            System.out.println("PORT:" + serverSocket.getLocalPort());
            port = serverSocket.getLocalPort();
            while (true) {
                HashMap<String, File> files = getFilesInFolder();
                Socket client = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(client, files, knownClients, false);
                pool.execute(clientHandler);
            }
        } catch (IOException e) {
            System.out.println("IOException caught");
            e.printStackTrace();
        }
    }

    public void connect(String ip, int port) throws IOException {
        Socket mainClient = new Socket(ip, port);
        new MainUI(mainClient);
    }


    private HashMap<String, File> getFilesInFolder() {
        HashMap<String, File> map = new HashMap<>();
        File customDirectory = FilePaths.FILE_PATH_CLIENT.path.toFile();
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

    private String getIp() {
        InetAddress localhost;
        try {
            localhost = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        return localhost.getHostAddress();
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public int getPort() {
        return port;
    }

    public ArrayList<Client> getKnownClients() {
        return knownClients;
    }

    public ArrayList<Client> getOtherClients(ArrayList<Client> clients) {

        for (Client client : clients) {
            for (Client knownClients : knownClients) {
                if (!(client.getIp().equals(knownClients.getIp()))) {
                    clients.add(knownClients);
                }
            }
        }
        return clients;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}