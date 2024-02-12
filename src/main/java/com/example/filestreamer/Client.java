package com.example.filestreamer;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.net.Socket;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Client {

    public static void main(String[] args) {
        Socket socket = null;
        DataInputStream inputStream = null;
        DataOutputStream outputStream = null;
        Scanner sc = new Scanner(System.in);
        try {
            socket = new Socket("192.168.1.3", 8008);
            inputStream = new DataInputStream(socket.getInputStream());
            outputStream = new DataOutputStream(socket.getOutputStream());

            while (true) {
                String msg = sc.nextLine();
                sendToServer(outputStream, msg);

                if (msg.equals("end")) {
                    break;
                }

                String response;
                String[] fileNames;

                switch (msg) {
                    case "download" -> {
                        response = inputStream.readUTF();
                        fileNames = getFileNames(response);
                        ClientUI clientUI = new ClientUI(Arrays.asList(fileNames));
                        clientUI.start();
                        String selectedFiles = clientUI.getSelectedFiles();
                        sendToServer(outputStream, selectedFiles);
                        selectedFiles = getStringWithoutParentheses(selectedFiles);
                        String[] selectedFilesArray = selectedFiles.split(", ");
                        if (selectedFiles.isEmpty()) {
                            break;
                        }
                        FileManager.receiveFile(inputStream, selectedFilesArray);
                        System.out.println("All files received");
                    }
                    case "upload" -> {
                        response = inputStream.readUTF();
                        fileNames = getFileNames(response);
                        String pathsAndFileNames = getFileNamesAndPaths();
                        System.out.println(pathsAndFileNames);
                        if (pathsAndFileNames.isEmpty()) {
                            break;
                        }
                        pathsAndFileNames = getStringWithoutParentheses(pathsAndFileNames);
                        String[] array = pathsAndFileNames.split("\n");
                        String[] filesToUploadArray = array[0].split(", ");
                        String[] filesToUploadPathsArray = array[1].split(", ");
                        ArrayList<String> existingFiles = returnExistingFilesIfThereAreAny(fileNames, filesToUploadArray);
                        if (!existingFiles.isEmpty()) {
                            System.out.println("File exists:" + existingFiles);
                            break;
                        }
                        sendToServer(outputStream, Arrays.asList(filesToUploadArray).toString());
                        for (String filePath : filesToUploadPathsArray) {
                            System.out.println(filePath);
                            File file = new File(filePath);
                            FileManager.sendFile(outputStream, file);
                        }
                    }
                    default -> {
                        response = inputStream.readUTF();
                        System.out.println("Server: " + response);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error occured");
            e.printStackTrace();
        } finally {
            try {
                if (socket != null) {
                    socket.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static ArrayList<String> returnExistingFilesIfThereAreAny(String[] fileNames, String[] filesToUpload) {
        ArrayList<String> existingFiles = new ArrayList<>();
        for (String fileToUpload : filesToUpload) {
            for (String fileName : fileNames) {
                if (fileToUpload.equals(fileName)) {
                    existingFiles.add(fileToUpload);
                }
            }
        }
        return existingFiles;
    }

    private static String[] getFileNames(String response) {
        response = getStringWithoutParentheses(response);
        String[] fileStrings = response.split(", ");
        return fileStrings;
    }

    private static String getStringWithoutParentheses(String response) {
        response = response.replace("[", "");
        response = response.replace("]", "");
        return response;
    }

    private static String getFileNamesAndPaths() {
        System.out.println("Method called");
        ArrayList<String> selectedFileNames = new ArrayList<>();
        ArrayList<String> selectedFilePaths = new ArrayList<>();

        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("File type", "png", "jpg", "exe", "txt");

        fileChooser.setFileFilter(filter);
        fileChooser.setMultiSelectionEnabled(true);
        System.out.println("window not opened");
        int returnValue = fileChooser.showOpenDialog(null);
        System.out.println("window opened");
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File[] selectedFiles = fileChooser.getSelectedFiles();

            for (File selectedFile : selectedFiles) {
                selectedFileNames.add(selectedFile.getName());
            }

            for (File selectedFile : selectedFiles) {
                selectedFilePaths.add(selectedFile.getAbsolutePath());
            }
        }
        return selectedFileNames+ "\n" + selectedFilePaths;
    }


    private static void sendToServer(DataOutputStream writer, String msg) throws IOException {
        writer.writeUTF(msg);
    }
}