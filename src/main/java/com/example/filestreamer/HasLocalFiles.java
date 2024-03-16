package com.example.filestreamer;

import javax.swing.*;
import java.io.File;
import java.util.HashMap;

public interface HasLocalFiles {
    default HashMap<String, File> getFilesInFolder(File customDirectory) {
        HashMap<String, File> map = new HashMap<>();

        JFileChooser fileChooser = new JFileChooser(customDirectory);

        File[] filesInDirectory = fileChooser.getCurrentDirectory().listFiles();
        if (filesInDirectory == null) {
            return new HashMap<>();
        }

        for (File file : filesInDirectory) {
            map.put(file.getName(), file);
        }
        return map;
    }
}
