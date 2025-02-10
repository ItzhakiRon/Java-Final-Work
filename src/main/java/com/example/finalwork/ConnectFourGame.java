package com.example.finalwork;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.example.finalwork.model.GameBoard;

public class ConnectFourGame extends Application {
    @Override
    public void start(Stage primaryStage) {
        GameBoard model = new GameBoard();
        GameView view = new GameView();
        GamePresenter presenter = new GamePresenter(model, view);

        Scene scene = new Scene(view);
        primaryStage.setTitle("Connect Four");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}