package com.example.filestreamer;

import java.io.Serializable;
import java.net.Socket;

public record SocketInfo(String ip, int port) implements Serializable {
    public SocketInfo(SendHandler sendHandler) {
        this(sendHandler.getIpAddress(), sendHandler.getPort());
    }

    public SocketInfo(Socket socket) {
        this(socket.getLocalAddress().toString(), socket.getPort());
    }
}
