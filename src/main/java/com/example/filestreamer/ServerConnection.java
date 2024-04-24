package com.example.filestreamer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerConnection extends SocketConnection {
    private final String name;
    private final ExecutorService pool = Executors.newCachedThreadPool();

    public ServerConnection(java.net.Socket socket, String name) throws IOException {
        super(socket);
        this.name = name;
    }

    public Set<String> getAvailableFiles() throws IOException, ClassNotFoundException {
        out.writeObject(Constants.ServerActions.REQUEST_FILE_LIST);
        return new HashSet<>((ArrayList<String>) in.readObject());
    }

    public String getName() {
        return name;
    }

}
