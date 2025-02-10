package com.example.finalwork.network;

import java.util.*;
import com.example.finalwork.model.GameBoard;

public class GameRoom {
    private final String roomName;
    private final GameBoard gameBoard;
    private final Set<String> players;
    private static final int MAX_PLAYERS = 2;

    public GameRoom(String roomName) {
        this.roomName = roomName;
        this.gameBoard = new GameBoard();
        this.players = new HashSet<>();
    }

    public boolean canJoin() {
        return players.size() < MAX_PLAYERS;
    }

    public void addPlayer(String playerName) {
        if (canJoin()) {
            players.add(playerName);
        }
    }

    public String getRoomName() {
        return roomName;
    }

    public GameBoard getGameBoard() {
        return gameBoard;
    }

    public Set<String> getPlayers() {
        return Collections.unmodifiableSet(players);
    }
}