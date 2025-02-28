package com.example.tictactoegame;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class PlayerScore {
    private final SimpleStringProperty player;
    private final SimpleIntegerProperty score;

    public PlayerScore(String player, int score) {
        this.player = new SimpleStringProperty(player);
        this.score = new SimpleIntegerProperty(score);
    }

    public String getPlayer() {
        return player.get();
    }

    public void setPlayer(String player) {
        this.player.set(player);
    }

    public int getScore() {
        return score.get();
    }

    public void setScore(int score) {
        this.score.set(score);
    }

    public SimpleStringProperty playerProperty() {
        return player;
    }

    public SimpleIntegerProperty scoreProperty() {
        return score;
    }
}