package com.example.filestreamer;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

public abstract class AbstractServer {
    protected final ExecutorService pool = Executors.newCachedThreadPool();
    protected final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(0);
    protected final Map<String, ClientInfo> onlineClients = new ConcurrentHashMap<>();
    protected int port = 0;

    public void checkIfClientIsOnline(ClientInfo clientInfo) {
        scheduler.scheduleAtFixedRate(() -> {
            try {
                Socket socket = new Socket();
                socket.connect(new InetSocketAddress(clientInfo.ip(), clientInfo.port()), 1000); // Timeout set to 1 second
                System.out.println("Server socket is still accepting connections.");
                socket.close();
            } catch (IOException e) {
                System.out.println("Removed " + onlineClients.remove(clientInfo.id()));
                System.err.println("Error: " + e.getMessage());
                throw new RuntimeException();
            }
        }, 0, 5, TimeUnit.SECONDS);
    }

    abstract HashMap<String, File> getFilesInFolder();
}
