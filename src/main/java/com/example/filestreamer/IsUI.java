package com.example.filestreamer;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public interface IsUI {
    default void download(ServerConnection serverConnection, File file) {
        try {
            serverConnection.download(file);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    default void upload(ServerConnection serverConnection, File file) {
        try {
            serverConnection.upload(file);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    default String getDirectory() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Select a Directory");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int returnVal = chooser.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            return chooser.getSelectedFile().getAbsolutePath() + File.separator;
        }
        return "";
    }

    default List<File> getFilesToSend() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Choose your files");
        chooser.setMultiSelectionEnabled(true);
        int returnVal = chooser.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            return Arrays.asList(chooser.getSelectedFiles());
        }
        return Collections.emptyList();
    }

    default void filter(JTextField fileFilterField, DefaultListModel fileListModel, JList<String> fileList) {
        String filterText = fileFilterField.getText().toLowerCase();
        DefaultListModel filteredModel = new DefaultListModel<>();
        for (int i = 0; i < fileListModel.size(); i++) {
            Object object = fileListModel.getElementAt(i);
            if (object.toString().toLowerCase().contains(filterText)) {
                filteredModel.addElement(object);
            }
        }
        fileList.setModel(filteredModel);
    }


}
