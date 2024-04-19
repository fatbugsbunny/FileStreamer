package com.example.filestreamer;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Chat {
    private final JTextArea chatArea;
    private final PrintWriter writer;
    private final BufferedReader reader;
    private final String name;

    private final ExecutorService pool = Executors.newSingleThreadExecutor();

    public Chat(String name, Socket socket) throws IOException {
        this.name = name;
        writer = new PrintWriter(socket.getOutputStream(), true);
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.chatArea = new JTextArea();
    }

    public void read() {
        pool.execute(() -> {
            try {
                chatArea.append(name + ": " + reader.readLine() + "\n");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void send(String message) {
        chatArea.append("You: " + message + "\n");
        writer.println(message);
    }

    public JTextArea getChatArea() {
        return chatArea;
    }

    public PrintWriter getWriter() {
        return writer;
    }
}
