package com.example.filestreamer;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.Collections;
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
    private DefaultListModel<Client> clientListModel;
    private DefaultListModel<String> fileListModel;
    private WindowCloseListener closeListener;
    private ExecutorService pool = Executors.newCachedThreadPool();
    private Socket socket;
    private DataOutputStream out;
    private DataInputStream in;

    public MainUI(Socket socket,WindowCloseListener closeListener) throws IOException {
        this.closeListener = closeListener;
        this.socket = socket;
        out = new DataOutputStream(socket.getOutputStream());
        in = new DataInputStream(socket.getInputStream());


        System.out.println("GET FILE NAMES");
        while (true) {
            String fileName = in.readUTF();
            if (fileName.equals("end")) {
                break;
            }
            fileListModel.addElement(fileName);
        }

        System.out.println("GETTING OTHER CLIENTS");
        while (true) {

            String name = in.readUTF();
            if (name.equals("end")) {
                break;
            }

            String ip = in.readUTF();
            System.out.println("PORT UI");
            int port = in.readInt();
            System.out.println(port);
            Client client = new Client(name, ip, port);
            clientListModel.addElement(client);
        }

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
                download(file);
            }
        });

        upload.addActionListener(e -> {
            List<File> filesToSend = getFilesToSend();
            if (filesToSend == null) {
                return;
            }

            for (File file : filesToSend) {
                upload(file);
            }
        });

        fileFilterField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                filterFileList();
            }
        });

        clientFilterField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                filterClientList();
            }
        });
        clientList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int index = clientList.locationToIndex(e.getPoint());
                    if (index >= 0) {
                        Client selectedClient = clientListModel.getElementAt(index);
                        try {
                            Socket client = new Socket(selectedClient.getIpAddress(), selectedClient.getPort());
                            new FileTransferUI(client, selectedClient.getName());
                        } catch (IOException j) {
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

    private void download(File file) {
        ReceiveHandler handler;
        try {
            out.writeUTF("download");
            System.out.println("FILE NAME SENT");
            out.writeUTF(file.getName());
            String ip = in.readUTF();
            System.out.println(ip);
            int port = in.readInt();
            System.out.println(port);
            handler = new ReceiveHandler(ip, port, true, file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        pool.execute(handler);
    }

    private void upload(File file) {
        ReceiveHandler handler;
        try {
            out.writeUTF("upload");
            System.out.println("FILE NAME SENT");
            // out.writeUTF(file.getName()); HERE IF IT DOESNT WORK
            String ip = in.readUTF();
            System.out.println(ip);
            int port = in.readInt();
            System.out.println(port);
            handler = new ReceiveHandler(ip, port, false, file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        pool.execute(handler);
    }

    private void filterFileList() {
        filter(fileFilterField, fileListModel, fileList);
    }

    private void filterClientList() {
        filterClient(clientFilterField, clientListModel, clientList);
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

    private void filterClient(JTextField fileFilterField, DefaultListModel<Client> fileListModel, JList<Client> clientList) {
        String filterText = fileFilterField.getText().toLowerCase();
        DefaultListModel<Client> filteredModel = new DefaultListModel<>();
        for (int i = 0; i < fileListModel.size(); i++) {
            Client client = fileListModel.getElementAt(i);
            if (client.getName().toLowerCase().contains(filterText)) {
                filteredModel.addElement(client);
            }
        }
        clientList.setModel(filteredModel);
    }

    private String getDirectory() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Select a Directory");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int returnVal = chooser.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            return chooser.getSelectedFile().getAbsolutePath() + File.separator;
        }
        return "";
    }

    private List<File> getFilesToSend() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Choose your files");
        chooser.setMultiSelectionEnabled(true);
        int returnVal = chooser.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            return Arrays.asList(chooser.getSelectedFiles());
        }
        return Collections.emptyList();
    }
}