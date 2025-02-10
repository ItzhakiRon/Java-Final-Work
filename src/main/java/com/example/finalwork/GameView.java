package com.example.finalwork;


import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class GameView extends VBox {
    private final Button[][] cells;
    private final Label statusLabel;
    private final GridPane gameGrid;

    public GameView() {
        cells = new Button[6][7];
        gameGrid = new GridPane();
        gameGrid.setPadding(new Insets(10));
        gameGrid.setHgap(5);
        gameGrid.setVgap(5);

        // Initialize the game grid
        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 7; col++) {
                Button cell = createCell();
                cells[row][col] = cell;
                gameGrid.add(cell, col, row);
            }
        }

        statusLabel = new Label("Player 1's turn");
        statusLabel.setStyle("-fx-font-size: 20px; -fx-padding: 10px;");

        getChildren().addAll(statusLabel, gameGrid);
    }

    private Button createCell() {
        Button cell = new Button();
        cell.setMinSize(60, 60);
        cell.setMaxSize(60, 60);

        Circle circle = new Circle(25);
        circle.setFill(Color.WHITE);
        circle.setStroke(Color.BLACK);

        cell.setGraphic(circle);
        return cell;
    }

    public Button getCell(int row, int col) {
        return cells[row][col];
    }

    public void updateCell(int row, int col, int player) {
        Circle circle = (Circle) cells[row][col].getGraphic();
        circle.setFill(player == 1 ? Color.RED : Color.YELLOW);
    }

    public void setStatus(String message) {
        statusLabel.setText(message);
    }
}