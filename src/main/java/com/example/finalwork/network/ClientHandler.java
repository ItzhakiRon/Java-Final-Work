package com.example.finalwork.network;

import java.io.*;
import java.net.*;

public class ClientHandler extends Thread {
    private final Socket clientSocket;
    private final GameServer gameServer;
    private final ObjectOutputStream out;
    private final ObjectInputStream in;

    public ClientHandler(Socket socket, GameServer server) throws IOException {
        this.clientSocket = socket;
        this.gameServer = server;
        this.out = new ObjectOutputStream(socket.getOutputStream());
        this.in = new ObjectInputStream(socket.getInputStream());
    }

    @Override
    public void run() {
        try {
            while (true) {
                GameMessage message = (GameMessage) in.readObject();
                handleMessage(message);
            }
        } catch (IOException | ClassNotFoundException e) {
            try {
                clientSocket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void handleMessage(GameMessage message) throws IOException {
        switch (message.getType()) {
            case CREATE_ROOM:
                GameRoom newRoom = gameServer.createGameRoom(message.getData());
                sendMessage(new GameMessage(MessageType.ROOM_CREATED, newRoom.getRoomName()));
                break;
            case JOIN_ROOM:
                String[] data = message.getData().split(":");
                GameRoom room = gameServer.joinGameRoom(data[0], data[1]);
                if (room != null) {
                    sendMessage(new GameMessage(MessageType.ROOM_JOINED, room.getRoomName()));
                } else {
                    sendMessage(new GameMessage(MessageType.ERROR, "Room full or not found"));
                }
                break;
            case GET_ROOMS:
                sendMessage(new GameMessage(MessageType.ROOM_LIST,
                        String.join(",", gameServer.getAvailableRooms())));
                break;
        }
    }

    private void sendMessage(GameMessage message) throws IOException {
        out.writeObject(message);
        out.flush();
    }
}