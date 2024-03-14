package com.example.filestreamer;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ServerConnection extends SocketConnection {
    public ServerConnection(Socket socket) throws IOException {
        super(socket);
    }

    public Set<String> getAvailableFiles() throws IOException, ClassNotFoundException {
        out.writeObject(Constants.ServerActions.REQUEST_FILE_LIST);
        return new HashSet<>((ArrayList<String>) in.readObject());
    }

    public void download(String filename) throws IOException, ClassNotFoundException {
        out.writeObject(Constants.ServerActions.DOWNLOAD);
        out.writeObject(filename);
    }

    @Override
    public void run() {
//        try {
//            sendFileNames();
//            informAboutOtherClients();
//            while (true) {
//                SendHandler handler;
//
//                System.out.println("FILE NAME RECEIVED");
//                String whatToDo = in.readUTF();
//
//
//                if (whatToDo.equals("download")) {
//                    handler = new SendHandler(getAvailableFiles(), true, true);
//                } else {
//                    handler = new SendHandler(getAvailableFiles(), false, true);
//                }
//                System.out.println("SEND HANDLER CREATED");
//                pool.execute(handler);
//                out.writeUTF(handler.getIpAddress());
//                out.writeInt(handler.getPort());
//                System.out.println("SEND HANDLER CREATED");
//                System.out.println(handler.getIpAddress());
//                System.out.println(handler.getPort());
//            }
//        } catch (IOException | ClassNotFoundException e) {
//            throw new RuntimeException(e);
//        } finally {
//            try {
//                in.close();
//                out.close();
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        }
    }

    public void addClient(ClientInfo clientInfo) throws IOException {
        out.writeObject(Constants.ServerActions.ADD_CLIENT);
        out.writeObject(clientInfo);
//        out.writeObject(STREAM_END);
    }

    public List<ClientInfo> getKnownClients() throws IOException, ClassNotFoundException {
        out.writeObject(Constants.ServerActions.GET_KNOWN_CLIENTS);
        return (List<ClientInfo>) in.readObject();
    }
}
