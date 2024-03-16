package com.example.filestreamer;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class Server implements HasLocalFiles {
    private final ExecutorService pool = Executors.newCachedThreadPool();
    private final LinkedBlockingQueue<ClientInfo> clients = new LinkedBlockingQueue<>();

    public void start() throws IOException {
        ServerSocket serverSocket = new ServerSocket(1024);
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
                                clients.put((ClientInfo) clientInput.readObject());
                            }
                            case GET_KNOWN_CLIENTS -> {
                                clientOutput.writeObject(List.of(clients.toArray()));
                            }
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

    public HashMap<String, File> getFilesInFolder() {
        File customDirectory = FilePaths.FILE_PATH_SERVER.path.toFile();
        HashMap<String, File> map = getFilesInFolder(customDirectory);
        return map;
    }

}