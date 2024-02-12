package com.example.filestreamer;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class ClientUI {

    private CountDownLatch latch = new CountDownLatch(1);
    private ArrayList<String> selectedFiles = new ArrayList<>();
    private List<String> files;

    private JTextField filterField;
    private JButton button;
    private JFrame frame;
    private JTable fileTable;
    private JPanel panel;

    public ClientUI(List<String> files) {
        this.files = new ArrayList<>(files);
    }

    public void start() {

        SwingUtilities.invokeLater(this::createUI);

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void createUI() {
        frame = new JFrame("File selection");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        Object[] columnName = {"Documents"};
        Object[][] dataArray = new Object[files.size()][1];
        for (int i = 0; i < files.size(); i++) {
            dataArray[i][0] = files.get(i);
        }

        DefaultTableModel model = new DefaultTableModel(dataArray, columnName);
        fileTable = new JTable(model);

        TableRowSorter<DefaultTableModel> rowSorter = new TableRowSorter<>(model);
        fileTable.setRowSorter(rowSorter);

        fileTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        button = new JButton("Download");
        setSelectedFiles();

        filterField = new JTextField();
        enableFilteringByTextField(rowSorter);

        panel = new JPanel(new BorderLayout());
        addComponentsToPanel();

        setupFrame(panel);
    }

    private void addComponentsToPanel() {
        panel.add(new JScrollPane(fileTable), BorderLayout.CENTER);
        panel.add(button, BorderLayout.SOUTH);
        panel.add(filterField, BorderLayout.NORTH);
        panel.setSize(fileTable.getPreferredSize());
    }

    private void setSelectedFiles() {
        button.addActionListener(e -> {
            int[] selectedRows = fileTable.getSelectedRows();
            for (int row : selectedRows) {
                String selected = (String) fileTable.getValueAt(row, 0);
                selectedFiles.add(selected);
            }
            frame.dispose();
            latch.countDown();
        });
    }

    private void enableFilteringByTextField(TableRowSorter<DefaultTableModel> rowSorter) {
        filterField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                filterTable(rowSorter, filterField);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filterTable(rowSorter, filterField);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filterTable(rowSorter, filterField);
            }
        });
    }

    private void setupFrame(JPanel panel) {
        frame.add(panel);
        frame.setSize(panel.getPreferredSize());
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public String getSelectedFiles() {
        return selectedFiles.toString();
    }

    private void filterTable(TableRowSorter rowSorter, JTextField filterField) {
        RowFilter<DefaultTableModel, Object> rowFilter;
        try {
            // Check if any column contains the entered text as a substring (case-insensitive)
            rowFilter = RowFilter.regexFilter("(?i).*" + filterField.getText() + ".*");
        } catch (java.util.regex.PatternSyntaxException ex) {
            return;
        }
        rowSorter.setRowFilter(rowFilter);
    }
}