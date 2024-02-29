package com.example.filestreamer;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class ClientTest {
    @Test
    void clientAwaitsConnections(){
        Client client = new Client("ann");
        assertThrows(IOException.class, client::start);
    }

    @Test
    void clientConnectsToServer() throws IOException {
        Server server = new Server();
        server.start();
        Client client = new Client("Jake");
        assertDoesNotThrow(() -> client.connect("192.168.1.2", 1024));
    }

    @Test
    void clientCanConnectToClient() throws IOException{
        Client jake = new Client("Jake");
        Client paul = new Client("Paul");

        jake.start();
        paul.connect(jake.getIpAddress(),jake.getPort());
    }
}