package com.example.filestreamer;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

public record ClientInfo(String id, String name, String ip, int port,
                         Map<String, ClientInfo> knownClients) implements Serializable {
    public ClientInfo(Client client) {
        this(client.getId(), client.getName(), client.getIpAddress(), client.getPort(), client.getConnectedClients());
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

