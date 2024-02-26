package com.example.filestreamer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class FileTransferUI extends JFrame {
    private ArrayList<String> files;
    private JPanel panel1;
    private JTextField filterField;
    private JButton download;
    private JList<String> list;
    private JLabel ClientName;
    private JScrollPane ScrollPane;
    private JButton upload;
    private DefaultListModel<String> listModel;

    public FileTransferUI() {
        setContentPane(panel1);
        setTitle("File selector");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
        setSize(400, 400);
        download.addActionListener(e -> {
            ArrayList<String> selectedFiles = (ArrayList) list.getSelectedValuesList();
            System.out.println(selectedFiles);
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

            }
        });
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here

        files = new ArrayList<>();
        listModel = new DefaultListModel<>();
        for (int i = 0; i < 50; i++) {
            files.add("sssss");
            listModel.addElement("Jake");
        }

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
}

