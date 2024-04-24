package com.example.filestreamer;

import java.io.Serializable;

public record SocketInfo(String ip, int port) implements Serializable {
    public SocketInfo(SendHandler sendHandler) {
        this(sendHandler.getIpAddress(), sendHandler.getPort());
    }

    public SocketInfo(java.net.Socket socket) {
        this(socket.getLocalAddress().toString(), socket.getPort());
    }
}
