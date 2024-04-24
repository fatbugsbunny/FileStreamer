package com.example.filestreamer;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientToClientUI extends JFrame implements IsUI {
    private final ExecutorService pool = Executors.newSingleThreadExecutor();
    private JPanel panel1;
    private JTextField filterField;
    private JButton download;
    private JList<String> list;
    private JLabel clientName;
    private JScrollPane ScrollPane;
    private JButton upload;
    private JTextArea chatArea;
    private JTextField message;
    private JButton sendButton;
    private JTextArea chatBox;
    private DefaultListModel<String> listModel;

    public ClientToClientUI(ClientConnection clientConnection, String connectClientName, ClientInfo ourClient) throws IOException, ClassNotFoundException {
        clientName.setText(connectClientName);

        clientConnection.getAvailableFiles().forEach(listModel::addElement);

        setContentPane(panel1);
        setTitle("File selector");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setVisible(true);
        setSize(400, 400);

        System.out.println("INITIATING CHAT");
        SocketInfo chatServer = clientConnection.initiateChat(ourClient);
        Socket socket = new Socket(chatServer.ip(), chatServer.port());
        System.out.println("CHAT INITIATEd");

        PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        System.out.println("PRINTERS");
        pool.execute(() -> {
            while (true) {
                try {
                    chatArea.append(connectClientName + ": " + reader.readLine() + "\n");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        sendButton.addActionListener(e -> {
            chatArea.append("You: " + message.getText() + "\n");
            writer.println(message.getText());
        });


        filterField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                filter(filterField, listModel, list);
            }
        });

        download.addActionListener(e -> {
            List<String> selectedFiles = list.getSelectedValuesList();
            String directory = getDirectory();
            for (String fileName : selectedFiles) {
                File file = new File(directory + fileName);
                download(clientConnection, file);
            }
        });

        upload.addActionListener(e -> {
            List<File> filesToSend = getFilesToSend();
            if (filesToSend == null) {
                return;
            }

            for (File file : filesToSend) {
                upload(clientConnection, file);
            }
        });
    }

    private void createUIComponents() {
        listModel = new DefaultListModel<>();
        list = new JList<>(listModel);
    }

}