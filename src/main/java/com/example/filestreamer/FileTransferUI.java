package com.example.filestreamer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FileTransferUI extends JFrame{

    private ArrayList<String> files;
    private JPanel panel1;
    private JTextField filterField;
    private JButton download;
    private JList<String> list;
    private JLabel ClientName;
    private JScrollPane ScrollPane;
    private JButton upload;
    private DefaultListModel<String> listModel;
    private ExecutorService pool = Executors.newCachedThreadPool();
    private Socket socket;
    private DataOutputStream out;
    private DataInputStream in;

    public FileTransferUI(Socket socket, String name) throws IOException{
        this.socket = socket;
        out = new DataOutputStream(socket.getOutputStream());
        in = new DataInputStream(socket.getInputStream());

        ClientName.setText(name);

        while (true) {
            String fileName = in.readUTF();
            if (fileName.equals("end")) {
                break;
            }
            listModel.addElement(fileName);
        }

        setContentPane(panel1);
        setTitle("File selector");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setVisible(true);
        setSize(400, 400);

        download.addActionListener(e -> {
            List<String> selectedFiles = list.getSelectedValuesList();
            String directory = getDirectory();
            for (String fileName : selectedFiles) {
                File file = new File(directory + fileName);
                download(file);
            }
        });

        filterField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                filterList();
            }
        });

        upload.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                List<File> filesToSend = getFilesToSend();
                if (filesToSend == null) {
                    return;
                }

                for (File file : filesToSend) {
                    upload(file);
                }
            }
        });
    }

    private void createUIComponents() {

        listModel = new DefaultListModel<>();
        list = new JList<>(listModel);

    }

    private void filterList() {
        String filterText = filterField.getText().toLowerCase();
        DefaultListModel<String> filteredModel = new DefaultListModel<>();
        for (int i = 0; i < listModel.size(); i++) {
            String item = listModel.getElementAt(i);
            if (item.toLowerCase().contains(filterText)) {
                filteredModel.addElement(item);
            }
        }
        list.setModel(filteredModel);
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

}

