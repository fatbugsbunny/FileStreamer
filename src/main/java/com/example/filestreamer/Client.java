package com.example.filestreamer;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.*;

public class Client implements Runnable, WindowCloseListener, HasLocalFiles {
    private final ExecutorService pool = Executors.newCachedThreadPool();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(0);
    private final CountDownLatch countDownLatch = new CountDownLatch(1);
    private final LinkedBlockingQueue<ClientInfo> knownClients;
    private final String name;
    private final String ipAddress;
    private int port;

    public Client(String name) {
        ipAddress = getIp();
        this.name = name;
        knownClients = new LinkedBlockingQueue<>();
    }

    @Override
    public void run() {
        try (ServerSocket openSocket = new ServerSocket(0)) {

            System.out.println("A Client prints, PORT:" + openSocket.getLocalPort());
            port = openSocket.getLocalPort();
            System.out.println(port);
            countDownLatch.countDown();
            while (true) {
                Socket clientSocket = openSocket.accept();
                pool.execute(() -> {
                    System.out.println("Thread started --client--");
                    try {
                        var clientInput = new ObjectInputStream(clientSocket.getInputStream());
                        var clientOutput = new ObjectOutputStream(clientSocket.getOutputStream());
                        while (true) {
                            switch ((Constants.ServerActions) clientInput.readObject()) {
                                case REQUEST_FILE_LIST ->
                                        clientOutput.writeObject(new ArrayList(getFilesInFolder().keySet()));//Key set is not serializable

                                case ADD_CLIENT -> {
                                    ClientInfo clientInfo = (ClientInfo) clientInput.readObject();
                                    knownClients.put(clientInfo);
                                    removeWhenOffline(clientInfo);
                                }

                                case GET_KNOWN_CLIENTS -> clientOutput.writeObject(List.of(knownClients.toArray()));

                                case DOWNLOAD -> {
                                    SendHandler sendHandler = new SendHandler(getFilesInFolder(), true, false);
                                    pool.execute(sendHandler);
                                    clientOutput.writeObject(new HandlerInfo(sendHandler));
                                }

                                case UPLOAD -> {
                                    SendHandler sendHandler = new SendHandler(getFilesInFolder(), false, false);
                                    pool.execute(sendHandler);
                                    clientOutput.writeObject(new HandlerInfo(sendHandler));
                                }

                                default -> throw new IllegalStateException("Unexpected value");
                            }
                        }
                    } catch (IOException | ClassNotFoundException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void shutDown() {
        pool.shutdown();
    }

    private void removeWhenOffline(ClientInfo clientInfo) {
        scheduler.scheduleAtFixedRate(() -> {
            try {
                Socket socket = new Socket();
                socket.connect(new InetSocketAddress(clientInfo.ip(), clientInfo.port()), 1000); // Timeout set to 1 second
                System.out.println("Server socket is still accepting connections.");
                socket.close();
            } catch (IOException e) {
                System.out.println("Removed " + knownClients.remove(clientInfo));
                System.err.println("Error: " + e.getMessage());
                throw new RuntimeException();
            }
        }, 0, 5, TimeUnit.SECONDS);
    }

    public void connect(String ip, int port) throws IOException, InterruptedException, ClassNotFoundException {
        pool.execute(this);
        countDownLatch.await();
        Socket serverSocket = new Socket(ip, port);
        ServerConnection serverConnection = new ServerConnection(serverSocket);

        serverConnection.addClient(new ClientInfo(this));
        System.out.println(this.port);

        new MainUI(serverConnection, this);
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
        // This method will be called when the window is closed
        shutDown();
    }
}