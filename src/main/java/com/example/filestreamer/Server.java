package com.example.filestreamer;

import javax.swing.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class Server {
    public static void main(String[] args) {

        Socket socket;
        ServerSocket serverSocket = null;
        DataInputStream inputStream;
        DataOutputStream outputStream;

        try {
            serverSocket = new ServerSocket(8008);
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (true) {
            try {
                socket = serverSocket.accept();
                inputStream = new DataInputStream(socket.getInputStream());
                outputStream = new DataOutputStream(socket.getOutputStream());

                while (true) {
                    String response = inputStream.readUTF();
                    response = response.trim().toLowerCase();
                    System.out.println("Client:" + response);

                    if (response.equals("end")) {
                        System.out.println("Connection terminated");
                        break;
                    }

                    switch (response) {
                        case "download" -> {
                            outputStream.writeUTF(getFilesInFolder().toString());
                            String filesToSend = getStringWithoutParentheses(inputStream.readUTF());
                            String[] filesToSendArray = filesToSend.split(", ");
                            if (filesToSend.isEmpty()) {
                                break;
                            }
                            FileManager.sendFile(outputStream, filesToSendArray);
                            System.out.println("all files sent");
                        }
                        case "upload" -> {
                            outputStream.writeUTF(getFilesInFolder().toString());
                            String selectedFiles = getStringWithoutParentheses(inputStream.readUTF());
                            String[] selectedFilesArray = selectedFiles.split(", ");
                            for (String fileName : selectedFilesArray) {
                                File file = new File(FilePaths.FILE_PATH_SERVER.path + fileName);
                                FileManager.receiveFile(inputStream, file);
                            }
                        }
                        default -> {
                            String msg = "Message received ";
                            outputStream.writeUTF(msg);
                        }
                    }
                }
                socket.close();
                outputStream.close();
                inputStream.close();

            } catch (IOException e) {
                System.out.println("Error occurred ");
                e.printStackTrace();
            }
        }
    }


    private static String getStringWithoutParentheses(String response) {
        response = response.replace("[", "");
        response = response.replace("]", "");
        return response;
    }

    public static ArrayList<String> getFilesInFolder() {
        File customDirectory = new File(FilePaths.FILE_PATH_SERVER.path);
        JFileChooser fileChooser = new JFileChooser(customDirectory);

        String[] filesInDirectory = fileChooser.getCurrentDirectory().list();
        ArrayList<String> fileList = new ArrayList<>(Arrays.asList(filesInDirectory));

        return fileList;
    }

}
