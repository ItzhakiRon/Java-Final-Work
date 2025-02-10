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

            // Create and show the login view
            LoginView loginView = new LoginView();
            new LoginPresenter(loginView, primaryStage);

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
            server.stop();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}