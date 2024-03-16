//package com.example.filestreamer;
//
//import java.io.*;
//import java.net.Socket;
//import java.util.*;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.concurrent.LinkedBlockingQueue;
//
//public class ClientHandler implements Runnable {
//    private Socket clientSocket;
//
//    public ClientHandler(Socket socket){
//        this.clientSocket = socket;
//    }
//    @Override
//    public void run() {
//        try {
//            var clientInput = new ObjectInputStream(clientSocket.getInputStream());
//            var clientOutput = new ObjectOutputStream(clientSocket.getOutputStream());
//            while (true) {
//                switch ((Constants.ServerActions) clientInput.readObject()) {
//                    case REQUEST_FILE_LIST -> {
//                        System.out.println("REQUEST FILE LIST");
//                        clientOutput.writeObject(new ArrayList(getFilesInFolder().keySet()));//Keyset is not serializable
//                    }
//                    case ADD_CLIENT -> {
////                                ClientInfo info = (ClientInfo) clientInput.readObject();
////                                Client client = new Client(info.name(), info.ip(), info.port());
////                                System.out.println("PORT IN SERVER " + info.port());
//                        clients.put((Client) clientInput.readObject());
//                    }
//                    case GET_KNOWN_CLIENTS -> {
//                        clientOutput.writeObject(List.of(clients.toArray()));// List is serialiable for classes that support it not Client class for some reason
//                    }
//                    case DOWNLOAD -> {
//
//                        FileManager.sendFile(new DataOutputStream(clientSocket.getOutputStream()), getFilesInFolder().get((String) clientInput.readObject()));
//                    }
//                    default -> {
//                        throw new IllegalStateException("Unexpected value");
//                    }
//                }
//            }
//        } catch (IOException | ClassNotFoundException | InterruptedException e) {
//            throw new RuntimeException(e);
//        }
//    }
//}
