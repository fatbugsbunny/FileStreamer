package com.example.filestreamer;

import javax.swing.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainUI extends JFrame implements IsUI {
    private final ExecutorService pool = Executors.newCachedThreadPool();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(0);
    private JPanel panel1;
    private JTextField fileFilterField;
    private JScrollPane scrollPane;
    private JList fileList;
    private JLabel clientName;
    private JButton download;
    private JButton upload;
    private JList clientList;
    private JTextField clientFilterField;
    private JPanel panel;
    private JTextArea chatArea;
    private JButton button1;
    private JList connectedClientsList;
    private JTextArea chatBox;
    private JButton sendButton;
    private JTextField message;
    private HashMap<String, Chat> chats;
    private DefaultListModel<ClientInfo> clientListModel;
    private DefaultListModel<String> fileListModel;
    private DefaultListModel<String> connectedClientsListModel;
    private String chattingClient;

    public MainUI(ServerConnection serverConnection, WindowCloseListener closeListener, Client client) throws IOException, ClassNotFoundException {
        System.out.println("GET FILE NAMES");
        serverConnection.getAvailableFiles().forEach(fileListModel::addElement);

        System.out.println("GETTING OTHER CLIENTS");
        serverConnection.getKnownClients().forEach(clientInfo -> {
            clientListModel.addElement(clientInfo);
        });

        scheduler.scheduleAtFixedRate(() -> {
            try {
                System.out.println("AAAAAa");
                ServerConnection serverConnection1 = new ServerConnection(new Socket("192.168.56.1", 1024), client.getName());
                System.out.println("AAAAA");
                System.out.println(client.getName());
                HashMap<String, SocketInfo> chatConnections = serverConnection1.getChatConnections(client.getName());//stuck, DONT FORGET TO FLUSH FFS
                System.out.println(chatConnections);
                //System.out.println(chatConnections.keySet() + "  ??  " + chatConnections.values());
//                for (Map.Entry<String, SocketInfo> entry : chatConnections.entrySet()) {
//                    if (!connectedClientsListModel.contains(entry.getKey())) {
//                        connectedClientsListModel.addElement(entry.getKey());
//                    }
//                }
//                createChats(client.getName(), chatConnections);
//                serverConnection1.close();
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }, 0, 5, TimeUnit.SECONDS);

        setContentPane(panel);
        setTitle("File selector");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
        setSize(400, 400);


        this.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {
            }

            @Override
            public void windowClosing(WindowEvent e) {
            }

            @Override
            public void windowClosed(WindowEvent e) {
                if (closeListener != null) {
                    closeListener.onWindowClosed();
                }
            }

            @Override
            public void windowIconified(WindowEvent e) {
            }

            @Override
            public void windowDeiconified(WindowEvent e) {
            }

            @Override
            public void windowActivated(WindowEvent e) {
            }

            @Override
            public void windowDeactivated(WindowEvent e) {
            }
        });

        download.addActionListener(e -> {
            List<String> selectedFiles = fileList.getSelectedValuesList();
            String directory = getDirectory();
            for (String fileName : selectedFiles) {
                File file = new File(directory + fileName);
                download(serverConnection, file);
            }
        });

        upload.addActionListener(e -> {
            List<File> filesToSend = getFilesToSend();
            if (filesToSend == null) {
                return;
            }

            for (File file : filesToSend) {
                upload(serverConnection, file);
            }
        });

        fileFilterField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                filter(fileFilterField, fileListModel, fileList);
            }
        });

        clientFilterField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                filter(clientFilterField, clientListModel, clientList);
            }
        });

        clientList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int index = clientList.locationToIndex(e.getPoint());
                    if (index >= 0) {
                        ClientInfo selectedClient = clientListModel.getElementAt(index);
                        try {
                            System.out.println("SOCKET");
                            Socket clientSocket = new Socket(selectedClient.ip(), selectedClient.port());
                            ClientConnection connection = new ClientConnection(clientSocket);
                            connection.addClient(new ClientInfo(client));
                            System.out.println("made it ast connection");
                            new ClientToClientUI(connection, selectedClient.name(), client.getName());
                        } catch (IOException | ClassNotFoundException j) {
                            j.printStackTrace();
                        }
                    }
                }
            }
        });


        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chats.get(chattingClient).send(message.getText());
            }
        });

        connectedClientsList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    int index = connectedClientsList.locationToIndex(e.getPoint());
                    if (index >= 0) {
                        chattingClient = connectedClientsListModel.getElementAt(index);
                        chatArea = chats.get(chattingClient).getChatArea();
                    }
                }
            }
        });
    }

    private void createChats(String name, Map<String, SocketInfo> chatConnections) {
        for (Map.Entry<String, SocketInfo> entry : chatConnections.entrySet()) {
            if (chatConnections.containsKey(entry.getKey())) {
                return;
            }
            try {
                Socket socket = new Socket(entry.getValue().ip(), entry.getValue().port());
                System.out.println("ERROR");
                Chat chat = new Chat(name, socket);
                chats.put(entry.getKey(), chat);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }


    private void createUIComponents() {
        clientListModel = new DefaultListModel<>();
        fileListModel = new DefaultListModel<>();
        connectedClientsListModel = new DefaultListModel<>();

        connectedClientsList = new JList<>(connectedClientsListModel);
        clientList = new JList<>(clientListModel);
        fileList = new JList<>(fileListModel);
    }

}