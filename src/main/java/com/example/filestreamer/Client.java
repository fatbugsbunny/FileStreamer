package com.example.filestreamer;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.CountDownLatch;

public class Client extends AbstractServer implements Runnable, WindowCloseListener, HasLocalFiles {

    private final CountDownLatch countDownLatch = new CountDownLatch(1);
    private final String id = UUID.randomUUID().toString();
    private final String name;
    private final String ipAddress;

    public Client(String name) {
        ipAddress = getIp();
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void start() throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(0)) {
            port = serverSocket.getLocalPort();
            countDownLatch.countDown();
            while (true) {
                java.net.Socket clientSocket = serverSocket.accept();
                pool.execute(() -> {
                    try {
                        var clientInput = new ObjectInputStream(clientSocket.getInputStream());
                        var clientOutput = new ObjectOutputStream(clientSocket.getOutputStream());

                        while (true) {
                            switch ((Constants.ServerActions) clientInput.readObject()) {
                                case REQUEST_FILE_LIST ->
                                        clientOutput.writeObject(new ArrayList(getFilesInFolder().keySet()));//Key set is not serializable

                                case ADD_CLIENT -> {
                                    ClientInfo clientInfo = (ClientInfo) clientInput.readObject();
                                    onlineClients.put(clientInfo.id(), clientInfo);
                                    checkIfClientIsOnline(clientInfo);
                                }

                                case GET_KNOWN_CLIENTS -> clientOutput.writeObject(List.of(onlineClients.values()));

                                case DOWNLOAD -> {
                                    System.out.println("DOWNLOAD REACHED");
                                    SendHandler sendHandler = new SendHandler(getFilesInFolder(), true, true);
                                    pool.execute(sendHandler);
                                    System.out.println("HANDLER EXECUTED");
                                    clientOutput.writeObject(new SocketInfo(sendHandler));
                                    System.out.println("OBJ WRITTEN");
                                }

                                case UPLOAD -> {
                                    SendHandler sendHandler = new SendHandler(getFilesInFolder(), false, true);
                                    pool.execute(sendHandler);
                                    clientOutput.writeObject(new SocketInfo(sendHandler));
                                }

                                case INITIATE_CHAT -> {
                                    System.out.println("INITIATING CHAT IN CLIENTS NOW");
                                    ClientInfo clientInfo = (ClientInfo) clientInput.readObject();

                                    ServerSocket chatServer = new ServerSocket(0);
                                    InetAddress localHost = InetAddress.getLocalHost();
                                    clientOutput.writeObject(new SocketInfo(localHost.getHostAddress(), chatServer.getLocalPort()));
                                    new ChatBox(chatServer.accept(), clientInfo.name());
                                }

                                default -> throw new IllegalStateException("Unexpected value");
                            }
                        }
                    } catch (IOException | ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }

                });

            }
        }
    }

    @Override
    public void run() {
        try {
            start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public void shutDown() {
        pool.shutdown();
    }

    public void connect(String ip, int port) throws IOException, InterruptedException, ClassNotFoundException {
        pool.execute(this);
        countDownLatch.await();
        java.net.Socket serverSocket = new java.net.Socket(ip, port);
        ServerConnection serverConnection = new ServerConnection(serverSocket, name);

        serverConnection.addClient(new ClientInfo(this));
        System.out.println(this.port);

        new MainUI(serverConnection, this, this);
    }

    public HashMap<String, File> getFilesInFolder() {
        File customDirectory = FilePaths.FILE_PATH_CLIENT.path.toFile();
        return getFilesInFolder(customDirectory);
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

    public Map<String, ClientInfo> getConnectedClients() {
        return onlineClients;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public int getPort() {
        return port;
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
        shutDown();
    }
}