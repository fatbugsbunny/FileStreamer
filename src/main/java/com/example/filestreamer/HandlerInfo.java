package com.example.filestreamer;

import java.io.Serializable;

public record HandlerInfo(String ip, int port) implements Serializable {
    public HandlerInfo(SendHandler sendHandler) {
        this(sendHandler.getIpAddress(), sendHandler.getPort());
    }
}
