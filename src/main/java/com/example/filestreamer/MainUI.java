package com.example.filestreamer;

import javax.swing.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.List;

public class MainUI extends JFrame implements IsUI {
    private JPanel panel1;
    private JTextField fileFilterField;
    private JScrollPane ScrollPane;
    private JList fileList;
    private JLabel ClientName;
    private JButton download;
    private JButton upload;
    private JList clientList;
    private JTextField clientFilterField;
    private JPanel panel;
    private DefaultListModel<ClientInfo> clientListModel;
    private DefaultListModel<String> fileListModel;

    public MainUI(ServerConnection serverConnection, WindowCloseListener closeListener) throws IOException, ClassNotFoundException {
        System.out.println("GET FILE NAMES");
        serverConnection.getAvailableFiles().forEach(fileListModel::addElement);

        System.out.println("GETTING OTHER CLIENTS");
        serverConnection.getKnownClients().forEach(client -> {
            clientListModel.addElement(client);
        });

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
                            Socket client = new Socket(selectedClient.ip(), selectedClient.port());
                            ServerConnection connection = new ServerConnection(client);
                            new FileTransferUI(connection, selectedClient.name());
                        } catch (IOException | ClassNotFoundException j) {
                            j.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    private void createUIComponents() {
        clientListModel = new DefaultListModel<>();
        fileListModel = new DefaultListModel<>();

        clientList = new JList<>(clientListModel);
        fileList = new JList<>(fileListModel);
    }

}