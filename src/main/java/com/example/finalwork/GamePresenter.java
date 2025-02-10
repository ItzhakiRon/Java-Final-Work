package com.example.finalwork;

import com.example.finalwork.model.GameBoard;
import com.example.finalwork.network.GameClient;
import com.example.finalwork.network.GameMessage;
import com.example.finalwork.network.MessageType;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;
import java.io.IOException;

public class GamePresenter {
    private final GameBoard model;
    private final GameView view;
    private final GameClient gameClient;
    private final String roomName;
    private final String playerName;
    private final boolean isFirstPlayer;
    private boolean isMyTurn;

    public GamePresenter(GameBoard model, GameView view, GameClient gameClient,
                         String roomName, String playerName, boolean isFirstPlayer) {
        this.model = model;
        this.view = view;
        this.gameClient = gameClient;
        this.roomName = roomName;
        this.playerName = playerName;
        this.isFirstPlayer = isFirstPlayer;
        this.isMyTurn = isFirstPlayer; // First player starts the game

        initializeGame();
        setupNetworkHandler();
    }

    private void initializeGame() {
        // Set initial game status
        updateGameStatus();

        // Initialize click handlers for all cells
        for (int row = 0; row < GameBoard.getRows(); row++) {
            for (int col = 0; col < GameBoard.getColumns(); col++) {
                final int currentCol = col;
                Button cell = view.getCell(row, col);
                cell.setOnAction(e -> handleMove(currentCol));
            }
        }
    }

    private void setupNetworkHandler() {
        gameClient.setMessageHandler(message -> {
            if (message.getType() == MessageType.GAME_UPDATE) {
                Platform.runLater(() -> handleGameUpdate(message.getData()));
            }
        });
    }

    private void handleMove(int column) {
        if (!isMyTurn) {
            showMessage("Not your turn", "Please wait for your opponent's move");
            return;
        }

        if (model.isColumnFull(column)) {
            showMessage("Invalid Move", "This column is full");
            return;
        }

        // Find the lowest empty row in the selected column
        int row = model.findAvailableRow(column);
        if (row != -1 && model.makeMove(column)) {
            // Update the view
            view.updateCell(row, column, model.getCurrentPlayer());

            // Send move to other player
            try {
                String moveData = String.format("%s:%d:%d", roomName, row, column);
                gameClient.sendMessage(new GameMessage(MessageType.MAKE_MOVE, moveData));
            } catch (IOException e) {
                showMessage("Network Error", "Failed to send move to opponent");
                return;
            }

            // Check for win
            if (model.checkWin()) {
                view.setStatus("You won!");
                disableAllCells();
                return;
            }

            // Switch turns
            isMyTurn = false;
            model.switchPlayer();
            updateGameStatus();
        }
    }

    private void handleGameUpdate(String gameData) {
        String[] parts = gameData.split(":");
        if (parts.length >= 3) {
            int row = Integer.parseInt(parts[1]);
            int col = Integer.parseInt(parts[2]);

            // Update the model and view
            model.makeMove(col);
            view.updateCell(row, col, model.getCurrentPlayer());

            // Check for win
            if (model.checkWin()) {
                view.setStatus("Opponent won!");
                disableAllCells();
                return;
            }

            // Switch turns
            isMyTurn = true;
            model.switchPlayer();
            updateGameStatus();
        }
    }

    private void updateGameStatus() {
        String status = isMyTurn ? "Your turn" : "Opponent's turn";
        view.setStatus(String.format("Player: %s (%s) - %s",
                playerName,
                isFirstPlayer ? "Red" : "Yellow",
                status));
    }

    private void disableAllCells() {
        for (int row = 0; row < GameBoard.getRows(); row++) {
            for (int col = 0; col < GameBoard.getColumns(); col++) {
                view.getCell(row, col).setDisable(true);
            }
        }
    }

    private void showMessage(String title, String content) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(content);
            alert.showAndWait();
        });
    }
}