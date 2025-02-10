package com.example.finalwork;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.collections.ObservableList;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class LoginView extends VBox {
    private final TextField playerNameField;
    private final TextField roomNameField;
    private final Button createRoomButton;
    private final Button joinRoomButton;
    private final ListView<String> roomList;
    private final Button refreshRoomsButton;
    private final Label titleLabel;

    public LoginView() {
        // Basic setup
        setSpacing(15);
        setPadding(new Insets(30));
        setAlignment(Pos.CENTER);
        setStyle("-fx-background-color: #f0f0f0;");

        // Title
        titleLabel = new Label("Connect Four - Game Lobby");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titleLabel.setStyle("-fx-text-fill: #2c3e50;");

        // Player name section
        Label nameLabel = new Label("Player Name:");
        nameLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        playerNameField = new TextField();
        playerNameField.setPromptText("Enter your name");
        playerNameField.setPrefWidth(250);
        styleTextField(playerNameField);

        // Room creation section
        Label roomLabel = new Label("Room Name:");
        roomLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        roomNameField = new TextField();
        roomNameField.setPromptText("Enter room name");
        roomNameField.setPrefWidth(250);
        styleTextField(roomNameField);

        createRoomButton = new Button("Create New Room");
        styleButton(createRoomButton);

        // Available rooms section
        Label availableRoomsLabel = new Label("Available Rooms:");
        availableRoomsLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));

        roomList = new ListView<>();
        roomList.setPrefHeight(200);
        roomList.setPrefWidth(250);
        styleListView(roomList);

        // Buttons
        refreshRoomsButton = new Button("Refresh Room List");
        joinRoomButton = new Button("Join Selected Room");

        styleButton(refreshRoomsButton);
        styleButton(joinRoomButton);

        // Create button container
        HBox buttonContainer = new HBox(10);
        buttonContainer.setAlignment(Pos.CENTER);
        buttonContainer.getChildren().addAll(refreshRoomsButton, joinRoomButton);

        // Add all components to the VBox
        getChildren().addAll(
                titleLabel,
                createSeparator(),
                nameLabel,
                playerNameField,
                roomLabel,
                roomNameField,
                createRoomButton,
                createSeparator(),
                availableRoomsLabel,
                roomList,
                buttonContainer
        );
    }

    private void styleTextField(TextField textField) {
        textField.setStyle(
                "-fx-background-radius: 5;" +
                        "-fx-border-radius: 5;" +
                        "-fx-border-color: #bdc3c7;" +
                        "-fx-border-width: 1;" +
                        "-fx-padding: 8;"
        );
    }

    private void styleButton(Button button) {
        button.setStyle(
                "-fx-background-color: #3498db;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 14px;" +
                        "-fx-padding: 10 20;" +
                        "-fx-background-radius: 5;" +
                        "-fx-cursor: hand;"
        );
        button.setPrefWidth(200);

        // Hover effect
        button.setOnMouseEntered(e ->
                button.setStyle(button.getStyle() + "-fx-background-color: #2980b9;")
        );
        button.setOnMouseExited(e ->
                button.setStyle(button.getStyle() + "-fx-background-color: #3498db;")
        );
    }

    private void styleListView(ListView<String> listView) {
        listView.setStyle(
                "-fx-background-radius: 5;" +
                        "-fx-border-radius: 5;" +
                        "-fx-border-color: #bdc3c7;" +
                        "-fx-border-width: 1;"
        );
    }

    private Separator createSeparator() {
        Separator separator = new Separator();
        separator.setPadding(new Insets(10, 0, 10, 0));
        return separator;
    }

    // Getters
    public TextField getPlayerNameField() {
        return playerNameField;
    }

    public TextField getRoomNameField() {
        return roomNameField;
    }

    public Button getCreateRoomButton() {
        return createRoomButton;
    }

    public Button getJoinRoomButton() {
        return joinRoomButton;
    }

    public Button getRefreshRoomsButton() {
        return refreshRoomsButton;
    }

    public String getSelectedRoom() {
        return roomList.getSelectionModel().getSelectedItem();
    }

    public void setRoomList(ObservableList<String> rooms) {
        roomList.setItems(rooms);
    }

    // Method to update the title (if needed)
    public void setTitle(String title) {
        titleLabel.setText(title);
    }
}