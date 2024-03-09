package com.example.filestreamer;

import javax.swing.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.*;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.*;

public class Client implements Runnable, WindowCloseListener {
    private CountDownLatch countDownLatch = new CountDownLatch(1);
    private String ipAddress;
    private int port;
    private LinkedBlockingQueue<Client> knownClients;
    private String name;
    private ExecutorService clientPool = Executors.newCachedThreadPool();
//    private ExecutorService runPool = Executors.newSingleThreadExecutor();

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

        try (ServerSocket serverSocket = new ServerSocket(0)) {

            System.out.println("A Client prints, PORT:" + serverSocket.getLocalPort());
            port = serverSocket.getLocalPort();
            System.out.println(port);
            countDownLatch.countDown();

            while (true) {
                HashMap<String, File> files = getFilesInFolder();
                Socket client = serverSocket.accept();
                //Overload the constructor isntead
                ClientHandler clientHandler = new ClientHandler(client, files, knownClients, false);
                clientPool.execute(clientHandler);
            }
        } catch (IOException e) {
            System.out.println("IOException caught");
            e.printStackTrace();
        }
    }
    public void shutDown(){
        clientPool.shutdown();
    }

    public void connect(String ip, int port) throws IOException, InterruptedException {
        clientPool.execute(this);
        countDownLatch.await();
        Socket mainClient = new Socket(ip, port);
        DataOutputStream out = new DataOutputStream(mainClient.getOutputStream());
        out.writeUTF(name);
        out.writeUTF(ip);
        System.out.println(this.port);
        out.writeInt(this.port);
        new MainUI(mainClient,this);
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