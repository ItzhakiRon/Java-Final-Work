package com.example.finalwork;

import javafx.scene.control.Button;
import com.example.finalwork.model.GameBoard;

public class GamePresenter {
    private final GameBoard model;
    private final GameView view;

    public GamePresenter(GameBoard model, GameView view) {
        this.model = model;
        this.view = view;
        initializeGame();
    }

    private void initializeGame() {
        for (int row = 0; row < GameBoard.getRows(); row++) {
            for (int col = 0; col < GameBoard.getColumns(); col++) {
                final int currentCol = col;
                Button cell = view.getCell(row, col);
                cell.setOnAction(e -> handleMove(currentCol));
            }
        }
    }

    private void handleMove(int column) {
        if (model.isColumnFull(column)) {
            return; // Ignore clicks on full columns
        }

        int row = model.findAvailableRow(column);
        if (row != -1 && model.makeMove(column)) {
            // Update the view
            view.updateCell(row, column, model.getCurrentPlayer());

            // Check for win
            if (model.checkWin()) {
                view.setStatus("Player " + model.getCurrentPlayer() + " wins!");
                disableAllButtons();
                return;
            }

            // Switch player
            model.switchPlayer();
            view.setStatus("Player " + model.getCurrentPlayer() + "'s turn");
        }
    }

    private void disableAllButtons() {
        for (int row = 0; row < GameBoard.getRows(); row++) {
            for (int col = 0; col < GameBoard.getColumns(); col++) {
                view.getCell(row, col).setDisable(true);
            }
        }
    }
}