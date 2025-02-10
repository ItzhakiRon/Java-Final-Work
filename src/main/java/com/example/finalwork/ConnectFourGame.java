package com.example.finalwork;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.example.finalwork.network.GameServer;
import java.io.IOException;

public class ConnectFourGame extends Application {
    private GameServer server;

    @Override
    public void start(Stage primaryStage) {
        try {
            // Start the server
            server = new GameServer();
            server.start();

            // Get the dynamically assigned port
            int serverPort = server.getPort();

            // Create and show the login view
            LoginView loginView = new LoginView();
            // Pass the server port to the LoginPresenter
            new LoginPresenter(loginView, primaryStage, serverPort);

            // Set up the initial scene with login view
            Scene scene = new Scene(loginView, 600, 800);
            primaryStage.setTitle("Connect Four - Game Lobby");
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (IOException e) {
            System.err.println("Error starting server: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
        // Clean up resources when the application closes
        if (server != null) {
            try {
                // First notify any connected clients about shutdown
                for (String roomName : server.getAvailableRooms()) {
                    server.removeRoom(roomName);
                }

                // Then stop the server
                server.stop();

                // Allow a brief moment for connections to close gracefully
                Thread.sleep(100);

            } catch (Exception e) {
                System.err.println("Error during shutdown: " + e.getMessage());
                e.printStackTrace();
            } finally {
                // Ensure server is marked as stopped even if an error occurred
                server = null;
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}