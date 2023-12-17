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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getBestScore() {
        return bestScore;
    }

    public void setBestScore(int bestScore) {
        this.bestScore = bestScore;
    }
}

