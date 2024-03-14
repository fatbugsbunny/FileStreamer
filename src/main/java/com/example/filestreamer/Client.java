package com.example.filestreamer;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class Client implements Runnable, WindowCloseListener, HasLocalFiles, Serializable {
    transient final ExecutorService clientPool = Executors.newCachedThreadPool();
    transient final CountDownLatch countDownLatch = new CountDownLatch(1);
    private final LinkedBlockingQueue<Client> knownClients;
    private final String name;
    private String ipAddress;
    private int port;

    public Client(String name) {
        ipAddress = getIp();
        this.name = name;
        knownClients = new LinkedBlockingQueue<>();
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
        try (ServerSocket openSocket = new ServerSocket(0)) {

            System.out.println("A Client prints, PORT:" + openSocket.getLocalPort());
            port = openSocket.getLocalPort();
            System.out.println(port);
            countDownLatch.countDown();

            Socket clientSocket = openSocket.accept();
            while (true) {
                HashMap<String, File> files = getFilesInFolder();
                //Overload the constructor isntead
                ServerConnection clientHandler = new ServerConnection(clientSocket);
                clientPool.execute(clientHandler);
            }
        } catch (IOException e) {
            System.out.println("IOException caught");
            e.printStackTrace();
        }
    }

    public void shutDown() {
        clientPool.shutdown();
    }

    public void connect(String ip, int port) throws IOException, InterruptedException, ClassNotFoundException {
        clientPool.execute(this);
        countDownLatch.await();
        Socket serverSocket = new Socket(ip, port);
        ServerConnection serverConnection = new ServerConnection(serverSocket);

        serverConnection.addClient(new ClientInfo(name, ip, port));
        System.out.println(this.port);

        new MainUI(serverConnection, this);
    }

    public HashMap<String, File> getFilesInFolder() {
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

    public LinkedBlockingQueue<Client> getKnownClients() {
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

    @Override
    public void onWindowClosed() {
        // This method will be called when the window is closed
        shutDown();
    }
}