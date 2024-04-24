package com.example.filestreamer;

import java.io.*;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public abstract class SocketConnection {
    protected final ExecutorService pool = Executors.newCachedThreadPool();
    protected final java.net.Socket socket;
    protected final ObjectInputStream in;
    protected final ObjectOutputStream out;

    protected SocketConnection(java.net.Socket socket) throws IOException {
        this.socket = socket;
        out = new ObjectOutputStream(socket.getOutputStream());
        System.out.println("ccc");
        in = new ObjectInputStream(socket.getInputStream());
        System.out.println("ddd");
    }

    public void download(File file) throws IOException, ClassNotFoundException {
        try {
            out.writeObject(Constants.ServerActions.DOWNLOAD);
            System.out.println("WRITTEN DOWNLOAD");
            SocketInfo info = ((SocketInfo) in.readObject());
            System.out.println("GOT HANDLER INFo");
            ReceiveHandler receiveHandler = new ReceiveHandler(info.ip(), info.port(), true, file);
            pool.execute(receiveHandler);
        } catch (OptionalDataException e) {
            System.out.println(e.eof + "   " + e.length);
        }
    }

    public void upload(File file) throws IOException, ClassNotFoundException {
        out.writeObject(Constants.ServerActions.UPLOAD);
        SocketInfo info = ((SocketInfo) in.readObject());
        ReceiveHandler receiveHandler = new ReceiveHandler(info.ip(), info.port(), false, file);
        pool.execute(receiveHandler);
    }

    public void addClient(ClientInfo client) throws IOException {
        out.writeObject(Constants.ServerActions.ADD_CLIENT);
        out.writeObject(client);
    }

    public Collection<ClientInfo> getKnownClients() throws IOException, ClassNotFoundException {
        out.writeObject(Constants.ServerActions.GET_KNOWN_CLIENTS);
        return (Collection<ClientInfo>) in.readObject();
    }

    abstract Set<String> getAvailableFiles() throws IOException, ClassNotFoundException;

    protected void sendFileNames() throws IOException, ClassNotFoundException {
        out.writeObject(getAvailableFiles());
    }
}
