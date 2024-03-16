package com.example.filestreamer;

import java.io.Serializable;

public record ClientInfo(String name, String ip, int port) implements Serializable {
    public ClientInfo(Client client) {
        this(client.getName(), client.getIpAddress(), client.getPort());
    }

    @Override
    public String toString() {
        return name;
    }
}

