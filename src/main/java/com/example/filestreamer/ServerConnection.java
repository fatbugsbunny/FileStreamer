package com.example.filestreamer;

import java.io.IOException;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerConnection extends SocketConnection {
    private final String name;
    private final ExecutorService pool = Executors.newCachedThreadPool();

    public ServerConnection(Socket socket, String name) throws IOException {
        super(socket);
        this.name = name;
    }

    public HashMap<String, SocketInfo> getChatConnections(String name) throws IOException, ClassNotFoundException {
        out.writeObject(Constants.ServerActions.GET_CHAT_CONNECTIONS);
        System.out.println("NAME");
        out.writeUTF(name);
        out.flush();
        System.out.println("WRITTEN NAME");

        HashMap<String, SocketInfo> map = (HashMap<String, SocketInfo>) in.readObject();
        System.out.println("MAPS MAPS" + map);
        return map;
    }

    public Set<String> getAvailableFiles() throws IOException, ClassNotFoundException {
        out.writeObject(Constants.ServerActions.REQUEST_FILE_LIST);
        return new HashSet<>((ArrayList<String>) in.readObject());
    }

    public String getName() {
        return name;
    }

    public List<ClientInfo> getClientsClients(String name) throws IOException, ClassNotFoundException {
        out.writeObject(Constants.ServerActions.GET_CLIENTS_CLIENTS);
        out.writeUTF(name);
        out.flush();
        List list = (List) in.readObject();
        System.out.println("FOUND");
        System.out.println(list);
        return list;
    }

    public void close() throws IOException {
        out.writeObject(Constants.ServerActions.CLOSE);
        out.close();
        in.close();
    }
}
