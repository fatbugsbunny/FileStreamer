package com.example.filestreamer;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class ClientConnection extends SocketConnection {
    public ClientConnection(Socket socket) throws IOException {
        super(socket);
    }

    public Set<String> getAvailableFiles() throws IOException, ClassNotFoundException {
        out.writeObject(Constants.ServerActions.REQUEST_FILE_LIST);
        return new HashSet<>((ArrayList<String>) in.readObject());
    }

    public SocketInfo initiateChat(String name) throws IOException, ClassNotFoundException {
        out.writeObject(Constants.ServerActions.INITIATE_CHAT);
        out.writeUTF(name);
        out.flush();
        return (SocketInfo) in.readObject();
    }
}
