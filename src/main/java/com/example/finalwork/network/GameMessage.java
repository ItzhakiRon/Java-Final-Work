package com.example.finalwork.network;

import java.io.Serializable;

public class GameMessage implements Serializable {
    private final MessageType type;
    private final String data;

    public GameMessage(MessageType type, String data) {
        this.type = type;
        this.data = data;
    }

    public MessageType getType() {
        return type;
    }

    public String getData() {
        return data;
    }
}