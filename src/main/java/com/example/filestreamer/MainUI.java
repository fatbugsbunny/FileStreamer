package com.example.filestreamer;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainUI extends JFrame {
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
    private DefaultListModel<String> clientListModel;
    private DefaultListModel<String> fileListModel;
    private ExecutorService pool = Executors.newCachedThreadPool();
    private Socket socket;

    public MainUI(Socket socket, List<String> fileNames) throws IOException {
        this.socket = socket;

        for (String name : fileNames) {
            fileListModel.addElement(name);
        }

        setContentPane(panel);
        setTitle("File selector");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
        setSize(400, 400);

        download.addActionListener(e -> {
            List<String> selectedFiles = fileList.getSelectedValuesList();
            String directory = getDirectory();
            for (String fileName : selectedFiles) {
                File file = new File(directory + fileName);
                    download(file);
            }
        });

        fileFilterField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                filterFileList();
            }
        });

        upload.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });

        clientFilterField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                filterClientList();
            }
        });
    }

    private void download(File file) {
        FileHandler handler;
        try {

            handler = new FileHandler(true, socket, file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        pool.execute(handler);
    }

    private void createUIComponents() {
        clientListModel = new DefaultListModel<>();
        fileListModel = new DefaultListModel<>();

        clientList = new JList<>(clientListModel);

        fileList = new JList<>(fileListModel);
    }


    private void filterFileList() {
        filter(fileFilterField, fileListModel, fileList);
    }

    private void filterClientList() {
        filter(clientFilterField, clientListModel, clientList);
    }

    private void filter(JTextField fileFilterField, DefaultListModel<String> fileListModel, JList<String> fileList) {
        String filterText = fileFilterField.getText().toLowerCase();
        DefaultListModel<String> filteredModel = new DefaultListModel<>();
        for (int i = 0; i < fileListModel.size(); i++) {
            String item = fileListModel.getElementAt(i);
            if (item.toLowerCase().contains(filterText)) {
                filteredModel.addElement(item);
            }
        }
        fileList.setModel(filteredModel);
    }

    private String getDirectory() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Select a Directory");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int returnVal = chooser.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            return chooser.getSelectedFile().getAbsolutePath() + File.separator;
        }
        return"";
    }
}
