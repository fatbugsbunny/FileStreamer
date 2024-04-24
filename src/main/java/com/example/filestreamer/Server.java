package com.example.filestreamer;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.HashMap;

public class Server extends AbstractServer implements HasLocalFiles {
    public void start(int serverSocketPort) throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(serverSocketPort)) {
            port = serverSocket.getLocalPort();
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

                                case GET_KNOWN_CLIENTS -> clientOutput.writeObject(onlineClients.values());

                                case DOWNLOAD -> {
                                    SendHandler sendHandler = new SendHandler(getFilesInFolder(), true, true);
                                    pool.execute(sendHandler);
                                    clientOutput.writeObject(new SocketInfo(sendHandler));
                                }
                                case UPLOAD -> {
                                    SendHandler sendHandler = new SendHandler(getFilesInFolder(), false, true);
                                    pool.execute(sendHandler);
                                    clientOutput.writeObject(new SocketInfo(sendHandler));
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

    public HashMap<String, File> getFilesInFolder() {
        File customDirectory = FilePaths.FILE_PATH_SERVER.path.toFile();
        return getFilesInFolder(customDirectory);
    }

}