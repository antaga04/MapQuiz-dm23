package com.example.mapquiz;

public class Score {
    public String game;
    public int score;
    public String date;

    public Score() {
        // Constructor vacío requerido para Firebase
    }

    public Score(String game, int score, String date) {
        this.game = game;
        this.score = score;
        this.date = date;
    }

    public String getGame() {
        return game;
    }

    public void setGame(String game) {
        this.game = game;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
