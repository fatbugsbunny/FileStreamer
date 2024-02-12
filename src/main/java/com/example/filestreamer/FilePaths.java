package com.example.filestreamer;

import java.io.File;

public enum FilePaths {
    FILE_PATH_SERVER(System.getProperty("user.home") + File.separator + "Desktop" + File.separator + "Files" + File.separator),
    FILE_PATH_CLIENT(System.getProperty("user.home") + File.separator + "Desktop" + File.separator + "Downloaded Files" + File.separator);

    final String path;
    FilePaths(String path) {
        this.path = path;
    }
}
