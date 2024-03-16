package com.example.filestreamer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Set;

import static com.example.filestreamer.Constants.STREAM_END;

public abstract class SocketConnection {

    protected final Socket socket;
    protected final ObjectInputStream in;
    protected final ObjectOutputStream out;

    protected SocketConnection(Socket socket) throws IOException {
        this.socket = socket;
        out = new ObjectOutputStream(socket.getOutputStream());
        System.out.println("ccc");
        in = new ObjectInputStream(socket.getInputStream());
        System.out.println("ddd");
    }

    abstract Set<String> getAvailableFiles() throws IOException, ClassNotFoundException;

    protected void sendFileNames() throws IOException, ClassNotFoundException {
        out.writeObject(getAvailableFiles());
        out.writeObject(STREAM_END);
    }
}
