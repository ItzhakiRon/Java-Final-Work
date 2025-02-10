package com.example.finalwork.network;

import java.io.*;
import java.net.*;
import java.util.function.Consumer;

public class GameClient {
    private static final String SERVER_HOST = "localhost";
    private final int serverPort;

    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private Consumer<GameMessage> messageHandler;

    public GameClient(int port) {
        this.serverPort = port;
    }

    public void connect() throws IOException {
        socket = new Socket(SERVER_HOST, serverPort);
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());

        // Start message listener thread
        new Thread(this::listenForMessages).start();
    }

    public void setMessageHandler(Consumer<GameMessage> handler) {
        this.messageHandler = handler;
    }

    public void sendMessage(GameMessage message) throws IOException {
        out.writeObject(message);
        out.flush();
    }

    private void listenForMessages() {
        try {
            while (true) {
                GameMessage message = (GameMessage) in.readObject();
                if (messageHandler != null) {
                    messageHandler.accept(message);
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}