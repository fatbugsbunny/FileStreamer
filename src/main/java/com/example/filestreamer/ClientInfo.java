package com.example.filestreamer;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public record ClientInfo(String name, String ip, int port, List<ClientInfo> knownClients,
                         HashMap<String, SocketInfo> chatConnections) implements Serializable {
    public ClientInfo(Client client) {
        this(client.getName(), client.getIpAddress(), client.getPort(), client.getConnectedClients(), client.getChatConnections());
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
        return Objects.hash(name, ip, port, chatConnections);
    }

}

