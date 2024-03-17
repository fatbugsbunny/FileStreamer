package com.example.filestreamer;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerConnection extends SocketConnection {
    private final ExecutorService pool = Executors.newCachedThreadPool();

    public ServerConnection(Socket socket) throws IOException {
        super(socket);
    }

    public Set<String> getAvailableFiles() throws IOException, ClassNotFoundException {
        out.writeObject(Constants.ServerActions.REQUEST_FILE_LIST);
        return new HashSet<>((ArrayList<String>) in.readObject());
    }

    public void download(File file) throws IOException, ClassNotFoundException {
        out.writeObject(Constants.ServerActions.DOWNLOAD);
        HandlerInfo info = ((HandlerInfo) in.readObject());
        ReceiveHandler receiveHandler = new ReceiveHandler(info.ip(), info.port(), true, file);
        pool.execute(receiveHandler);
    }

    public void upload(File file) throws IOException, ClassNotFoundException {
        out.writeObject(Constants.ServerActions.UPLOAD);
        HandlerInfo info = ((HandlerInfo) in.readObject());
        ReceiveHandler receiveHandler = new ReceiveHandler(info.ip(), info.port(), false, file);
        pool.execute(receiveHandler);
    }

    public void addClient(ClientInfo client) throws IOException {
        out.writeObject(Constants.ServerActions.ADD_CLIENT);
        out.writeObject(client);
    }

    public List<ClientInfo> getKnownClients() throws IOException, ClassNotFoundException {
        out.writeObject(Constants.ServerActions.GET_KNOWN_CLIENTS);
        return (List<ClientInfo>) in.readObject();
    }
}
