package com.example.tictactoegame;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.TableCell;
import javafx.util.Callback;

public class LeaderboardController {
    @FXML
    private TableView<PlayerScore> leaderboardTable;
    @FXML
    private TableColumn<PlayerScore, String> playerColumn;
    @FXML
    private TableColumn<PlayerScore, Integer> scoreColumn;

    @FXML
    public void initialize() {
        playerColumn.setCellValueFactory(new PropertyValueFactory<>("player"));
        scoreColumn.setCellValueFactory(new PropertyValueFactory<>("score"));

        // Set custom cell factory for score column to change text color to black
        scoreColumn.setCellFactory(new Callback<TableColumn<PlayerScore, Integer>, TableCell<PlayerScore, Integer>>() {
            @Override
            public TableCell<PlayerScore, Integer> call(TableColumn<PlayerScore, Integer> param) {
                return new TableCell<PlayerScore, Integer>() {
                    @Override
                    protected void updateItem(Integer item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setText(null);
                        } else {
                            setText(item.toString());
                            setStyle("-fx-text-fill: black;");
                        }
                    }
                };
            }
        });

        // Add sample data
        leaderboardTable.getItems().add(new PlayerScore("Player1", 100));
        leaderboardTable.getItems().add(new PlayerScore("Player2", 80));
    }
}