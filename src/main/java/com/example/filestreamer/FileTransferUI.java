package com.example.filestreamer;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class FileTransferUI extends JFrame implements IsUI {
    private JPanel panel1;
    private JTextField filterField;
    private JButton download;
    private JList<String> list;
    private JLabel ClientName;
    private JScrollPane ScrollPane;
    private JButton upload;
    private DefaultListModel<String> listModel;

    public FileTransferUI(ServerConnection serverConnection, String name) throws IOException, ClassNotFoundException {
        ClientName.setText(name);

        serverConnection.getAvailableFiles().forEach(listModel::addElement);

        setContentPane(panel1);
        setTitle("File selector");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setVisible(true);
        setSize(400, 400);

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
    }

    private void createUIComponents() {
        listModel = new DefaultListModel<>();
        list = new JList<>(listModel);
    }

}