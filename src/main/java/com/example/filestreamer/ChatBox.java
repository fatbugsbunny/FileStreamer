package com.example.filestreamer;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatBox extends JFrame {
    private final ExecutorService pool = Executors.newSingleThreadExecutor();
    private JPanel panel;
    private JTextArea chatArea;
    private JTextField message;
    private JButton sendButton;
    private JLabel client;

    public ChatBox(Socket socket, String name) throws IOException {
        client.setText(name);

        setContentPane(panel);
        setTitle("File selector");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setVisible(true);
        setSize(400, 400);

        PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        pool.execute(() -> {
            while (true) {
                try {
                    chatArea.append(name + ": " + reader.readLine() + "\n");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        sendButton.addActionListener(e -> {
            chatArea.append("You: " + message.getText() + "\n");
            writer.println(message.getText());
        });
    }
}
