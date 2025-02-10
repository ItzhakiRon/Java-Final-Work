package com.example.finalwork;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.collections.ObservableList;

public class LoginView extends VBox {
    private final TextField playerNameField;
    private final TextField portField;
    private final TextField roomNameField;
    private final ListView<String> roomList;
    private final Button createRoomButton;
    private final Button joinRoomButton;
    private final Button refreshRoomsButton;

    public LoginView() {
        setSpacing(10);
        setPadding(new Insets(20));
        setAlignment(Pos.CENTER);

        // Player name input
        Label nameLabel = new Label("Player Name:");
        playerNameField = new TextField();

        // Server port input
        Label portLabel = new Label("Server Port:");
        portField = new TextField();

        // Room name input for creating new rooms
        Label roomLabel = new Label("Room Name:");
        roomNameField = new TextField();

        // Room list
        Label roomListLabel = new Label("Available Rooms:");
        roomList = new ListView<>();
        roomList.setPrefHeight(200);

        // Buttons
        createRoomButton = new Button("Create Room");
        joinRoomButton = new Button("Join Selected Room");
        refreshRoomsButton = new Button("Refresh Room List");

        getChildren().addAll(
                nameLabel, playerNameField,
                portLabel, portField,
                roomLabel, roomNameField,
                roomListLabel, roomList,
                createRoomButton, joinRoomButton, refreshRoomsButton
        );
    }

    // Getters
    public TextField getPlayerNameField() { return playerNameField; }
    public TextField getPortField() { return portField; }
    public TextField getRoomNameField() { return roomNameField; }
    public String getSelectedRoom() { return roomList.getSelectionModel().getSelectedItem(); }
    public Button getCreateRoomButton() { return createRoomButton; }
    public Button getJoinRoomButton() { return joinRoomButton; }
    public Button getRefreshRoomsButton() { return refreshRoomsButton; }

    public void setRoomList(ObservableList<String> rooms) {
        roomList.setItems(rooms);
    }
}