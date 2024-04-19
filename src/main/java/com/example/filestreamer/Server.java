package com.example.filestreamer;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class Server extends AbstractServer implements HasLocalFiles {
    public void start(int serverSocketPort) throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(serverSocketPort)) {
            port = serverSocket.getLocalPort();
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
                                    Iterator<ClientInfo> iterator = onlineClients.iterator();
                                    while (true) {
                                        ClientInfo info;
                                        if (iterator.hasNext()) {
                                            info = iterator.next();
                                        } else {
                                            info = null;
                                        }
                                        if (info != null && info.equals(clientInfo)) {
                                            System.out.println(onlineClients.remove(info));
                                        }
                                        onlineClients.put(clientInfo);
                                        break;
                                    }
                                    //checkIfClientIsOnline(clientInfo);
                                }

                                case GET_KNOWN_CLIENTS -> clientOutput.writeObject(List.of(onlineClients.toArray()));

                                case GET_CLIENTS_CLIENTS -> {
                                    System.out.println("GET CLIENTS CLIENTS");
                                    String clientName = clientInput.readUTF();
                                    Iterator<ClientInfo> iterator = onlineClients.iterator();
                                    while (iterator.hasNext()) {
                                        ClientInfo info = iterator.next();
                                        if (info.name().equals(clientName)) {
                                            clientOutput.writeObject(info.knownClients());
                                        }
                                    }
                                    System.out.println("FOUND");
                                }

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
                                case GET_CHAT_CONNECTIONS -> {
                                    System.out.println("GET CHAT CONNECTIONS");
                                    String clientName = clientInput.readUTF();
                                    System.out.println("Read name" + clientName);
                                    Iterator<ClientInfo> iterator = onlineClients.iterator();
                                    while (iterator.hasNext()) {
                                        ClientInfo info = iterator.next();
                                        System.out.println("LOOPING");
                                        if (info.name().equals(clientName)) {
                                            System.out.println("FOUND");
                                            clientOutput.writeObject(info.chatConnections());
                                            clientOutput.flush();
                                            //clientOutput.writeObject(new ArrayList<>(map.values()));
                                        }
                                    }
                                }
//                                case CLOSE -> {
//                                    clientInput.close();
//                                    clientOutput.close();
//                                }
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

    public HashMap<String, File> getFilesInFolder() {
        File customDirectory = FilePaths.FILE_PATH_SERVER.path.toFile();
        return getFilesInFolder(customDirectory);
    }

}