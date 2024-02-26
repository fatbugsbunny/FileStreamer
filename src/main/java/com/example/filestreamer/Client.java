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

public class Client {
    private String ipAddress;
    private int port;
    private HashMap<String, File> files = getFilesInFolder();
    private ArrayList<Client> knownClients;
    private String name;
    private ExecutorService pool = Executors.newCachedThreadPool();

    public Client(String name) {
        ipAddress = getIp();
        this.name = name;
    }


    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(0);) {

            port = serverSocket.getLocalPort();
            while (true) {
                Socket client = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(client, files);
                pool.execute(clientHandler);
            }
        } catch (IOException e) {
            System.out.println("IOException caught");
            e.printStackTrace();
        }
    }

    public void connect(String ip, int port) throws IOException {

            Socket client = new Socket(ip, port);
            DataInputStream in = new DataInputStream(client.getInputStream());
            List<String> files = new ArrayList<>();
        System.out.println("PRINTING IN CONNECT METHOD");

            while (true) {//method gets called here too so not everything gets loaded
                String name = in.readUTF();
                if(name.equals("end")){
                    break;
                }
                files.add(name);
                //System.out.println(in.readUTF());
            }
            new MainUI(client, files);
//        ServerHandler serverHandler = new ServerHandler(client);
//        pool.execute(serverHandler);
//        } catch (IOException e){
//
//        }

    }

    private HashMap<String, File> getFilesInFolder() {
        HashMap<String, File> map = new HashMap<>();
        File customDirectory = FilePaths.FILE_PATH_SERVER.path.toFile();
        JFileChooser fileChooser = new JFileChooser(customDirectory);

        File[] filesInDirectory = fileChooser.getCurrentDirectory().listFiles();
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

    public String getName() {
        return name;
    }
}