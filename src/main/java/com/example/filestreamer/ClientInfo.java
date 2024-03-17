package com.example.filestreamer;

import java.io.Serializable;
import java.util.Objects;

public record ClientInfo(String name, String ip, int port) implements Serializable {
    public ClientInfo(Client client) {
        this(client.getName(), client.getIpAddress(), client.getPort());
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        ClientInfo other = (ClientInfo) obj;
        return name.equals(other.name) && ip == other.ip && port == other.port;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, ip, port);
    }

}

