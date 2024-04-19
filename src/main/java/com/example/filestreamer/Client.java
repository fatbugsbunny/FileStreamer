package com.example.filestreamer;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class Client extends AbstractServer implements Runnable, WindowCloseListener, HasLocalFiles {

    private final CountDownLatch countDownLatch = new CountDownLatch(1);
    private final String name;
    private final String ipAddress;
    private final ConcurrentHashMap<String, SocketInfo> chatConnections = new ConcurrentHashMap<>();

    public Client(String name) {
        ipAddress = getIp();
        this.name = name;
    }

    public void start() throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(0)) {
            port = serverSocket.getLocalPort();
            countDownLatch.countDown();
            while (true) {
                Socket clientSocket = serverSocket.accept();
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
                                    onlineClients.put(clientInfo);
                                    //checkIfClientIsOnline(clientInfo);
                                }

                                case GET_KNOWN_CLIENTS -> clientOutput.writeObject(List.of(onlineClients.toArray()));

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
                                    String name = clientInput.readUTF();
                                    ServerSocket chatServer = new ServerSocket(0);
                                    InetAddress localHost = InetAddress.getLocalHost();
                                    clientOutput.writeObject(new SocketInfo(localHost.getHostAddress(), chatServer.getLocalPort()));

                                    Socket chatConnection = chatServer.accept();
                                    chatConnections.put(name, new SocketInfo(chatConnection));
                                    System.out.println(name + "KEYSET" + chatConnections.keySet());
                                    System.out.println("CHAT CONNECTIONS IN CLINT" + chatConnections.values());

                                    Socket socket = new Socket("192.168.56.1", 1024);
                                    ServerConnection serverConnection = new ServerConnection(socket, this.name);
                                    serverConnection.addClient(new ClientInfo(this));
                                    System.out.println("CHAT CONNECTIONS IN CLINT" + chatConnections.values());
                                }

                                default -> throw new IllegalStateException("Unexpected value");
                            }
                        }
                    } catch (IOException | ClassNotFoundException | InterruptedException e) {
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

    public HashMap<String, SocketInfo> getChatConnections() {
        return new HashMap<>(chatConnections);
    }

    public void shutDown() {
        pool.shutdown();
    }

    public void connect(String ip, int port) throws IOException, InterruptedException, ClassNotFoundException {
        pool.execute(this);
        countDownLatch.await();
        Socket serverSocket = new Socket(ip, port);
        ServerConnection serverConnection = new ServerConnection(serverSocket, name);

        serverConnection.addClient(new ClientInfo(this));
        System.out.println(this.port);

        new MainUI(serverConnection, this, this);
    }

    public void checkIfClientIsOnline(ClientInfo clientInfo) {
        scheduler.scheduleAtFixedRate(() -> {
            try {
                Socket socket = new Socket();
                socket.connect(new InetSocketAddress(clientInfo.ip(), clientInfo.port()), 1000); // Timeout set to 1 second
                System.out.println("Server socket is still accepting connections.");
                socket.close();
            } catch (IOException e) {
                System.out.println("Removed " + onlineClients.remove(clientInfo));
                System.out.println("Removed from map" + chatConnections.remove(clientInfo.name()));
                System.err.println("Error: " + e.getMessage());
                throw new RuntimeException();
            }
        }, 0, 5, TimeUnit.SECONDS);
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

    public ArrayList<ClientInfo> getConnectedClients() {
        return new ArrayList<>(onlineClients);
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