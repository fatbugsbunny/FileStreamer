package com.example.filestreamer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class SpecialSocketHandler implements Runnable {
    private DataOutputStream out;
    private DataInputStream in;

    public SpecialSocketHandler(Socket server, Socket client) throws IOException {
        out = new DataOutputStream(server.getOutputStream());
        in = new DataInputStream(client.getInputStream());
    }

    @Override
    public void run() {
        try {
            out.writeUTF(in.readUTF());
            out.writeUTF(in.readUTF());
            out.writeInt(in.readInt());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
