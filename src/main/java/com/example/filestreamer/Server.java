package com.example.filestreamer;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.*;

public class Server implements HasLocalFiles {
    private final ExecutorService pool = Executors.newCachedThreadPool();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(0);

    private final LinkedBlockingQueue<ClientInfo> clients = new LinkedBlockingQueue<>();

    public void start() throws IOException {
        ServerSocket serverSocket = new ServerSocket(1024);
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
                                clients.put(clientInfo);
                                scheduler.scheduleAtFixedRate(() -> {
                                    try {
                                        Socket socket = new Socket();
                                        socket.connect(new InetSocketAddress(clientInfo.ip(), clientInfo.port()), 1000); // Timeout set to 1 second
                                        System.out.println("Server socket is still accepting connections.");
                                        socket.close();
                                    } catch (IOException e) {
                                        System.out.println("Removed " + clients.remove(clientInfo));
                                        System.err.println("Error: " + e.getMessage());
                                        throw new RuntimeException();
                                    }
                                }, 0, 5, TimeUnit.SECONDS);
                            }

                            case GET_KNOWN_CLIENTS -> clientOutput.writeObject(List.of(clients.toArray()));

                            case DOWNLOAD -> {
                                SendHandler sendHandler = new SendHandler(getFilesInFolder(), true, true);
                                pool.execute(sendHandler);
                                clientOutput.writeObject(new HandlerInfo(sendHandler));
                            }
                            case UPLOAD -> {
                                SendHandler sendHandler = new SendHandler(getFilesInFolder(), false, true);
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
    }

    public HashMap<String, File> getFilesInFolder() {
        File customDirectory = FilePaths.FILE_PATH_SERVER.path.toFile();
        return getFilesInFolder(customDirectory);
    }

}