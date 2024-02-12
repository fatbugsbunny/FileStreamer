package com.example.filestreamer;

import java.io.*;

public class FileManager {
    static void sendFile(DataOutputStream outputStream, String[] filesArray) throws IOException {
        for (String fileName : filesArray) {
            File file = new File(FilePaths.FILE_PATH_SERVER.path + fileName);
            send(outputStream, file);
        }
    }

    static void sendFile(DataOutputStream outputStream, File fileToSend) throws IOException {
        send(outputStream, fileToSend);
    }
    private static void send(DataOutputStream outputStream, File file) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(file);
        BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);

        byte[] buffer = new byte[4096];
        int bytesRead;

        while ((bytesRead = fileInputStream.read(buffer)) != -1) {
            outputStream.writeInt(bytesRead);
            outputStream.write(buffer, 0, bytesRead);
        }


        outputStream.writeInt(-1);

        outputStream.flush();
        System.out.println("File sent.");
        fileInputStream.close();
        bufferedInputStream.close();
    }


    static void receiveFile(DataInputStream inputStream, String[] filesArray) throws IOException {
        for (String fileName : filesArray) {
            File file = new File(FilePaths.FILE_PATH_CLIENT.path + fileName);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            receive(inputStream,fileOutputStream);
        }
    }

    static void receiveFile(DataInputStream inputStream, File receivedFile) throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream(receivedFile);
        receive(inputStream,fileOutputStream);
    }

    private static void receive(DataInputStream inputStream,FileOutputStream fileOutputStream) throws IOException {
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);


        byte[] buffer = new byte[4096];
        int bytesRead;

        while ((bytesRead = inputStream.readInt()) != -1) {
            if (bytesRead == -1) {
                break;
            }
            inputStream.readFully(buffer, 0, bytesRead); // Read file content
            fileOutputStream.write(buffer, 0, bytesRead);
        }

        bufferedOutputStream.close();
        fileOutputStream.close();
    }
}
