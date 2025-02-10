package com.example.finalwork.network;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class GameServer {
    private static final int PORT = 5000;
    private final ServerSocket serverSocket;
    private final Map<String, GameRoom> gameRooms;
    private boolean running;

    public GameServer() throws IOException {
        serverSocket = new ServerSocket(PORT);
        gameRooms = new ConcurrentHashMap<>();
        running = true;
    }

    public void start() {
        new Thread(() -> {
            while (running) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    new ClientHandler(clientSocket, this).start();
                } catch (IOException e) {
                    if (running) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public GameRoom createGameRoom(String roomName) {
        GameRoom room = new GameRoom(roomName);
        gameRooms.put(roomName, room);
        return room;
    }

    public GameRoom joinGameRoom(String roomName, String playerName) {
        GameRoom room = gameRooms.get(roomName);
        if (room != null && room.canJoin()) {
            room.addPlayer(playerName);
            return room;
        }
        return null;
    }

    // Fixed version of getAvailableRooms()
    public List<String> getAvailableRooms() {
        return gameRooms.values().stream()
                .filter(GameRoom::canJoin)
                .map(GameRoom::getRoomName)
                .collect(Collectors.toList());
    }

    public void stop() {
        running = false;
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void removeRoom(String roomName) {
        gameRooms.remove(roomName);
    }

    public GameRoom getRoom(String roomName) {
        return gameRooms.get(roomName);
    }
}