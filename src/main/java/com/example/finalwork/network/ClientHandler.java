package com.example.finalwork.network;

import java.io.*;
import java.net.*;

public class ClientHandler extends Thread {
    private final Socket clientSocket;
    private final GameServer gameServer;
    private final ObjectOutputStream out;
    private final ObjectInputStream in;
    private boolean running;

    public ClientHandler(Socket socket, GameServer server) throws IOException {
        this.clientSocket = socket;
        this.gameServer = server;
        this.out = new ObjectOutputStream(socket.getOutputStream());
        this.in = new ObjectInputStream(socket.getInputStream());
        this.running = true;
    }

    @Override
    public void run() {
        try {
            while (running) {
                GameMessage message = (GameMessage) in.readObject();
                handleMessage(message);
            }
        } catch (IOException | ClassNotFoundException e) {
            try {
                closeConnection();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } finally {
            gameServer.unregisterClient(this);
        }
    }

    private void handleMessage(GameMessage message) throws IOException {
        switch (message.getType()) {
            case CREATE_ROOM:
                String[] createData = message.getData().split(":");
                String newRoomName = createData[0];
                GameRoom newRoom = gameServer.createGameRoom(newRoomName);
                sendMessage(new GameMessage(MessageType.ROOM_CREATED, newRoom.getRoomName()));
                break;

            case JOIN_ROOM:
                String[] joinData = message.getData().split(":");
                GameRoom room = gameServer.joinGameRoom(joinData[0], joinData[1]);
                if (room != null) {
                    sendMessage(new GameMessage(MessageType.ROOM_JOINED, room.getRoomName()));
                } else {
                    sendMessage(new GameMessage(MessageType.ERROR, "Room full or not found"));
                }
                break;

            case GET_ROOMS:
                sendRoomList();
                break;

            case GAME_UPDATE:
                gameServer.broadcastToAllClients(message);
                break;
        }
    }

    private void sendRoomList() throws IOException {
        String roomListData = gameServer.getAvailableRooms().stream()
                .map(room -> room + ":" + (gameServer.isRoomJoinable(room) ? "open" : "full"))
                .collect(java.util.stream.Collectors.joining(","));
        sendMessage(new GameMessage(MessageType.ROOM_LIST, roomListData));
    }

    public void sendMessage(GameMessage message) throws IOException {
        synchronized (out) {
            out.writeObject(message);
            out.flush();
        }
    }

    public void closeConnection() throws IOException {
        running = false;
        if (clientSocket != null && !clientSocket.isClosed()) {
            clientSocket.close();
        }
    }
}