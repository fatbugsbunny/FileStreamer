package com.example.filestreamer;

import java.io.*;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

public class FileManager {
    static void sendFile(DataOutputStream outputStream, File file) throws IOException {
        System.out.println("FILE before try");

        try (FileChannel inChannel = new FileInputStream(file).getChannel();
             WritableByteChannel writableByteChannel = Channels.newChannel(outputStream)) {
            // inChannel.transferTo(0, inChannel.size(), outChannel); // original -- apparently has trouble copying large files on Windows
            // magic number for Windows, (64Mb - 32Kb)
            int maxCount = (64 * 1024 * 1024) - (32 * 1024);
            long size = inChannel.size();
            long position = 0;
            System.out.println("FILE SEnding");
            while (position < size) {
                position += inChannel.transferTo(position, maxCount, writableByteChannel);
            }
            System.out.println("FILE SENT");
        }
    }

    public static void receiveFile(DataInputStream inputStream, File receivedFile) throws IOException {
        try (FileOutputStream fileOutputStream = new FileOutputStream(receivedFile)) {
            ReadableByteChannel readableByteChannel;
            FileChannel fileChannel;
            readableByteChannel = Channels.newChannel(inputStream);
            fileChannel = fileOutputStream.getChannel();
            System.out.println("FILE GONNNA receive");
            fileChannel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
            System.out.println("FILE RECEIVED");
            fileChannel.close();
            readableByteChannel.close();
        }
    }
}
