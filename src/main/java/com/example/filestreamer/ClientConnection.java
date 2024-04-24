package com.example.filestreamer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class ClientConnection extends SocketConnection {
    public ClientConnection(java.net.Socket socket) throws IOException {
        super(socket);
    }

    public Set<String> getAvailableFiles() throws IOException, ClassNotFoundException {
        out.writeObject(Constants.ServerActions.REQUEST_FILE_LIST);
        return new HashSet<>((ArrayList<String>) in.readObject());
    }

    public SocketInfo initiateChat(ClientInfo clientInfo) throws IOException, ClassNotFoundException {
        out.writeObject(Constants.ServerActions.INITIATE_CHAT);
        out.writeObject(clientInfo);
        out.flush();
        return (SocketInfo) in.readObject();
    }
}
