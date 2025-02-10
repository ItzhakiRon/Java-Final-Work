package com.example.finalwork;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import com.example.finalwork.network.*;
import com.example.finalwork.model.GameBoard;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


public class LoginPresenter {
    private final LoginView loginView;
    private final Stage primaryStage;
    private final GameClient gameClient;
    private String playerName;
    private String currentRoom;

    public LoginPresenter(LoginView loginView, Stage primaryStage) {
        this.loginView = loginView;
        this.primaryStage = primaryStage;
        this.gameClient = new GameClient();

        initializeClient();
        initializeLoginHandlers();
        setupAutoRefresh();
    }

    private void initializeClient() {
        try {
            gameClient.connect();
            gameClient.setMessageHandler(this::handleServerMessage);
            // Initial room list refresh
            refreshRoomList();
        } catch (IOException e) {
            showError("Connection Error", "Unable to connect to game server: " + e.getMessage());
        }
    }

    private void initializeLoginHandlers() {
        // Create Room button handler
        loginView.getCreateRoomButton().setOnAction(e -> {
            if (validateInputs()) {
                createRoom();
            }
        });

        // Join Room button handler
        loginView.getJoinRoomButton().setOnAction(e -> {
            if (validateInputs() && validateRoomSelection()) {
                joinRoom();
            }
        });

        // Refresh button handler
        loginView.getRefreshRoomsButton().setOnAction(e -> refreshRoomList());
    }

    private void setupAutoRefresh() {
        // Automatically refresh the room list every 5 seconds
        Thread autoRefreshThread = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(5000);
                    Platform.runLater(this::refreshRoomList);
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        autoRefreshThread.setDaemon(true);
        autoRefreshThread.start();
    }

    private boolean validateInputs() {
        String name = loginView.getPlayerNameField().getText().trim();
        if (name.isEmpty()) {
            showError("Input Error", "Please enter player name");
            return false;
        }
        playerName = name;
        return true;
    }

    private boolean validateRoomSelection() {
        if (loginView.getSelectedRoom() == null) {
            showError("Selection Error", "Please select a game room from the list");
            return false;
        }
        return true;
    }

    private void createRoom() {
        String roomName = loginView.getRoomNameField().getText().trim();
        if (roomName.isEmpty()) {
            showError("Input Error", "Please enter a room name");
            return;
        }

        try {
            GameMessage createMessage = new GameMessage(MessageType.CREATE_ROOM,
                    String.format("%s:%s", roomName, playerName));
            gameClient.sendMessage(createMessage);
        } catch (IOException e) {
            showError("Communication Error", "Unable to create new game room: " + e.getMessage());
        }
    }

    private void joinRoom() {
        String selectedRoom = loginView.getSelectedRoom();
        try {
            GameMessage joinMessage = new GameMessage(MessageType.JOIN_ROOM,
                    String.format("%s:%s", selectedRoom, playerName));
            gameClient.sendMessage(joinMessage);
        } catch (IOException e) {
            showError("Communication Error", "Unable to join game room: " + e.getMessage());
        }
    }

    private void refreshRoomList() {
        try {
            gameClient.sendMessage(new GameMessage(MessageType.GET_ROOMS, ""));
        } catch (IOException e) {
            showError("Communication Error", "Unable to refresh room list: " + e.getMessage());
        }
    }

    private void handleServerMessage(GameMessage message) {
        Platform.runLater(() -> {
            switch (message.getType()) {
                case ROOM_CREATED:
                    handleRoomCreated(message.getData());
                    break;
                case ROOM_JOINED:
                    handleRoomJoined(message.getData());
                    break;
                case ROOM_LIST:
                    handleRoomList(message.getData());
                    break;
                case ERROR:
                    showError("Server Error", message.getData());
                    break;
                case GAME_UPDATE:
                    handleGameUpdate(message.getData());
                    break;
            }
        });
    }

    private void handleRoomCreated(String roomData) {
        String[] parts = roomData.split(":");
        currentRoom = parts[0];
        startGame(true); // Start as first player (red)
    }

    private void handleRoomJoined(String roomData) {
        String[] parts = roomData.split(":");
        currentRoom = parts[0];
        startGame(false); // Start as second player (yellow)
    }

    private void handleRoomList(String roomsData) {
        if (roomsData != null && !roomsData.isEmpty()) {
            List<String> rooms = Arrays.asList(roomsData.split(","));
            loginView.setRoomList(FXCollections.observableArrayList(rooms));
        } else {
            loginView.setRoomList(FXCollections.observableArrayList());
        }
    }

    private void handleGameUpdate(String gameData) {
        // This will be handled by GamePresenter
        // Game state updates will be processed there
    }

    private void startGame(boolean isFirstPlayer) {
        GameBoard model = new GameBoard();
        GameView view = new GameView();
        GamePresenter gamePresenter = new GamePresenter(model, view, gameClient, currentRoom, playerName, isFirstPlayer);

        Scene gameScene = new Scene(view);
        primaryStage.setTitle(String.format("Connect Four - Room: %s - Player: %s", currentRoom, playerName));
        primaryStage.setScene(gameScene);
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Cleanup method to be called when closing the application
     * or switching to game view
     */
    public void cleanup() {
        // Stop the auto-refresh thread if needed
        // Additional cleanup can be added here
    }
}