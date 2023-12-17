package com.example.mapquiz;

public class User {
    public String name;
    public String country;
    public String email;
    public int bestScore;

    public User() {
        // Constructor vacío requerido para Firebase
    }

    public User(String name, String country, String email, int bestScore) {
        this.name = name;
        this.country = country;
        this.email = email;
        this.bestScore = bestScore;
    }
}

