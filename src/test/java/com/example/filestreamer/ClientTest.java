package com.example.filestreamer;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ClientTest {
    @Test
    void clientAwaitsConnections() {
        Client client = new Client("ann");
        assertThrows(IOException.class, client::run);
    }

    @Test
    void clientConnectsToServer() throws IOException, InterruptedException, ClassNotFoundException {
        Server server = new Server();
        server.start();
        Client client = new Client("Jake");
        assertDoesNotThrow(() -> client.connect("192.168.1.2", 1024));
    }

    @Test
    void clientCanConnectToClient() throws IOException, InterruptedException, ClassNotFoundException {
        Client jake = new Client("Jake");
        Client paul = new Client("Paul");

        jake.run();
        paul.connect(jake.getIpAddress(), jake.getPort());
    }
}