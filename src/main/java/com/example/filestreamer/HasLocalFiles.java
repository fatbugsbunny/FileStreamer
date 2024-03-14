package com.example.filestreamer;

import java.io.File;
import java.util.HashMap;

public interface HasLocalFiles {
    HashMap<String, File> getFilesInFolder();
}
