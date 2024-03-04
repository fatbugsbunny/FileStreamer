package com.example.filestreamer;

import java.io.File;
import java.nio.file.Path;

public enum FilePaths {
    FILE_PATH_SERVER(System.getProperty("user.home") + File.separator + "Desktop" + File.separator + "Files" + File.separator),
    FILE_PATH_CLIENT(System.getProperty("user.home") + File.separator + "Desktop" + File.separator + "Client files" + File.separator);

    final Path path;
    FilePaths(String path) {
        this.path = Path.of(path);
    }
}
