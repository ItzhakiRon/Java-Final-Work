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
    private final int serverPort;
    private volatile boolean autoRefreshActive;

    public LoginPresenter(LoginView loginView, Stage primaryStage, int serverPort) {
        this.loginView = loginView;
        this.primaryStage = primaryStage;
        this.serverPort = serverPort;
        this.gameClient = new GameClient(serverPort);
        this.autoRefreshActive = true;

        if (serverPort > 0) {
            loginView.getPortField().setText(String.valueOf(serverPort));
            loginView.getPortField().setEditable(false);
        }

        initializeClient();
        initializeLoginHandlers();
        setupAutoRefresh();
    }

    private void initializeClient() {
        try {
            gameClient.connect();
            gameClient.setMessageHandler(this::handleServerMessage);
            refreshRoomList();
        } catch (IOException e) {
            showError("Connection Error", "Unable to connect to game server: " + e.getMessage());
        }
    }

    private void initializeLoginHandlers() {
        loginView.getCreateRoomButton().setOnAction(e -> {
            if (validateInputs()) {
                createRoom();
            }
        });

        loginView.getJoinRoomButton().setOnAction(e -> {
            if (validateInputs() && validateRoomSelection()) {
                joinRoom();
            }
        });

        loginView.getRefreshRoomsButton().setOnAction(e -> refreshRoomList());
    }

    private void setupAutoRefresh() {
        Thread autoRefreshThread = new Thread(() -> {
            while (autoRefreshActive) {
                try {
                    Thread.sleep(5000);
                    if (autoRefreshActive) {
                        Platform.runLater(this::refreshRoomList);
                    }
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

        if (loginView.getPortField().isEditable()) {
            String portText = loginView.getPortField().getText().trim();
            try {
                int port = Integer.parseInt(portText);
                if (port <= 0 || port > 65535) {
                    showError("Input Error", "Please enter a valid port number (1-65535)");
                    return false;
                }
            } catch (NumberFormatException e) {
                showError("Input Error", "Please enter a valid port number");
                return false;
            }
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
            gameClient.sendMessage(new GameMessage(MessageType.CREATE_ROOM,
                    String.format("%s:%s", roomName, playerName)));
        } catch (IOException e) {
            showError("Communication Error", "Unable to create new game room: " + e.getMessage());
        }
    }

    private void joinRoom() {
        String selectedRoom = loginView.getSelectedRoom();
        if (selectedRoom.contains(" (Full)")) {
            showError("Room Error", "This room is already full");
            return;
        }

        try {
            selectedRoom = selectedRoom.replace(" (Full)", "").trim();
            gameClient.sendMessage(new GameMessage(MessageType.JOIN_ROOM,
                    String.format("%s:%s", selectedRoom, playerName)));
        } catch (IOException e) {
            showError("Communication Error", "Unable to join game room: " + e.getMessage());
        }
    }

    private void refreshRoomList() {
        if (gameClient != null) {
            try {
                gameClient.sendMessage(new GameMessage(MessageType.GET_ROOMS, ""));
            } catch (IOException e) {
                Platform.runLater(() ->
                        showError("Communication Error", "Unable to refresh room list: " + e.getMessage())
                );
            }
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
            }
        });
    }

    private void handleRoomCreated(String roomData) {
        currentRoom = roomData;
        startGame(true);
    }

    private void handleRoomJoined(String roomData) {
        currentRoom = roomData;
        startGame(false);
    }

    private void handleRoomList(String roomsData) {
        if (roomsData != null && !roomsData.isEmpty()) {
            List<String> roomInfo = Arrays.asList(roomsData.split(","));
            List<String> displayRooms = roomInfo.stream()
                    .map(info -> {
                        String[] parts = info.split(":");
                        String roomName = parts[0];
                        String status = parts.length > 1 ? parts[1] : "unknown";
                        return roomName + (status.equals("full") ? " (Full)" : "");
                    })
                    .collect(Collectors.toList());

            loginView.setRoomList(FXCollections.observableArrayList(displayRooms));
        } else {
            loginView.setRoomList(FXCollections.observableArrayList());
        }
    }

    private void startGame(boolean isFirstPlayer) {
        GameBoard model = new GameBoard();
        GameView view = new GameView();
        GamePresenter gamePresenter = new GamePresenter(model, view, gameClient,
                currentRoom, playerName, isFirstPlayer);

        Scene gameScene = new Scene(view);
        primaryStage.setTitle(String.format("Connect Four - Room: %s - Player: %s",
                currentRoom, playerName));
        primaryStage.setScene(gameScene);
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void cleanup() {
        autoRefreshActive = false;
        if (gameClient != null) {
            gameClient.disconnect();
        }
    }
}