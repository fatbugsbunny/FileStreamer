package com.example.filestreamer;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class ClientConnection extends SocketConnection implements Runnable {
    public ClientConnection(Socket socket) throws IOException {
        super(socket);
    }

    public Set<String> getAvailableFiles() {
        HashMap<String, File> map = new HashMap<>();
        File customDirectory = FilePaths.FILE_PATH_CLIENT.path.toFile();
        JFileChooser fileChooser = new JFileChooser(customDirectory);

        File[] filesInDirectory = fileChooser.getCurrentDirectory().listFiles();
        if (filesInDirectory == null) {
            return new HashSet<>();
        }
        for (File file : filesInDirectory) {
            map.put(file.getName(), file);
        }
        return map.keySet();
    }

    public HashMap<String, File> getAvailableFilesAsMap() {
        HashMap<String, File> map = new HashMap<>();
        File customDirectory = FilePaths.FILE_PATH_CLIENT.path.toFile();
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

    @Override
    public void run() {
        try {
            sendFileNames();
//            while (true) {
//                SendHandler handler;
//
//                System.out.println("FILE NAME RECEIVED");
//                String whatToDo = in.readUTF();
//
//                if (whatToDo.equals("download")) {
//                    handler = new SendHandler(getAvailableFilesAsMap(), true, false);
//                } else {
//                    handler = new SendHandler(getAvailableFilesAsMap(), false, false);
//                }
//                System.out.println("SEND HANDLER CREATED");
////                pool.execute(handler);
//                out.writeUTF(handler.getIpAddress());
//                out.writeInt(handler.getPort());
//                System.out.println("SEND HANDLER CREATED");
//                System.out.println(handler.getIpAddress());
//                System.out.println(handler.getPort());
//            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                in.close();
                out.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
