package com.example.filestreamer;

import javax.swing.*;
import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class Server implements HasLocalFiles {
    private final ExecutorService pool = Executors.newCachedThreadPool();
    private final LinkedBlockingQueue<Client> clients = new LinkedBlockingQueue<>();
    private ServerSocket serverSocket;

    public void start() throws IOException, InterruptedException, ClassNotFoundException {
        serverSocket = new ServerSocket(1024);
        while (true) {
            Socket clientSocket = serverSocket.accept();

            pool.execute(() -> {
                System.out.println("bbb");
                try {
                    var clientInput = new ObjectInputStream(clientSocket.getInputStream());
                    var clientOutput = new ObjectOutputStream(clientSocket.getOutputStream());
                    while (true) {
                        switch ((Constants.ServerActions) clientInput.readObject()) {
                            case REQUEST_FILE_LIST -> {
                                System.out.println("REQUEST FILE LIST");
                                clientOutput.writeObject(new ArrayList(getFilesInFolder().keySet()));//Keyset is not serializable
                            }
                            case ADD_CLIENT -> {
                                ClientInfo info = (ClientInfo) clientInput.readObject();
                                Client client = new Client(info.name(), info.ip(), info.port());
                                System.out.println("PORT IN SERVER " + info.port());
                                clients.put(client);
                            }
                            case GET_KNOWN_CLIENTS -> {
                                clientOutput.writeObject(List.of(clients.toArray()));// List is serialiable for classes that support it not Client class for some reason
                            }
                            case DOWNLOAD -> {
                                FileManager.sendFile(new DataOutputStream(clientSocket.getOutputStream()), getFilesInFolder().get((String) clientInput.readObject()));
                            }
                            default -> {
                                throw new IllegalStateException("Unexpected value");
                            }
                        }
                    }
                } catch (IOException | ClassNotFoundException | InterruptedException e) {
                    throw new RuntimeException(e);
                }

            });
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

    public HashMap<String, File> getFilesInFolder() {
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