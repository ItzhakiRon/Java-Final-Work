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
    private final Set<ClientHandler> clients;
    private boolean running;

    public GameServer() throws IOException {
        serverSocket = new ServerSocket(PORT);
        gameRooms = new ConcurrentHashMap<>();
        clients = Collections.synchronizedSet(new HashSet<>());
        running = true;
    }

    public void start() {
        new Thread(() -> {
            while (running) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    ClientHandler handler = new ClientHandler(clientSocket, this);
                    registerClient(handler);
                    handler.start();
                } catch (IOException e) {
                    if (running) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public void registerClient(ClientHandler client) {
        clients.add(client);
    }

    public void unregisterClient(ClientHandler client) {
        clients.remove(client);
    }

    public GameRoom createGameRoom(String roomName) {
        GameRoom room = new GameRoom(roomName);
        gameRooms.put(roomName, room);
        broadcastRoomListUpdate();
        return room;
    }

    public GameRoom joinGameRoom(String roomName, String playerName) {
        GameRoom room = gameRooms.get(roomName);
        if (room != null && room.canJoin()) {
            room.addPlayer(playerName);
            broadcastRoomListUpdate();
            return room;
        }
        return null;
    }

    public List<String> getAvailableRooms() {
        return new ArrayList<>(gameRooms.keySet());
    }

    public boolean isRoomJoinable(String roomName) {
        GameRoom room = gameRooms.get(roomName);
        return room != null && room.canJoin();
    }

    public void broadcastRoomListUpdate() {
        String roomListData = gameRooms.entrySet().stream()
                .map(entry -> entry.getKey() + ":" + (entry.getValue().canJoin() ? "open" : "full"))
                .collect(Collectors.joining(","));
        broadcastToAllClients(new GameMessage(MessageType.ROOM_LIST, roomListData));
    }

    public void broadcastToAllClients(GameMessage message) {
        synchronized(clients) {
            for (ClientHandler client : clients) {
                try {
                    client.sendMessage(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void stop() {
        running = false;
        synchronized(clients) {
            for (ClientHandler client : new ArrayList<>(clients)) {
                try {
                    client.closeConnection();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void removeRoom(String roomName) {
        gameRooms.remove(roomName);
        broadcastRoomListUpdate();
    }

    public GameRoom getRoom(String roomName) {
        return gameRooms.get(roomName);
    }

    public int getPort() {
        return serverSocket.getLocalPort();
    }
}