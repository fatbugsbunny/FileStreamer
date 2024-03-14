package com.example.filestreamer;

public enum Constants {
    STREAM_END;

    public enum ServerActions {
        REQUEST_FILE_LIST,
        ADD_CLIENT,
        GET_KNOWN_CLIENTS,
        DOWNLOAD
    }
}
