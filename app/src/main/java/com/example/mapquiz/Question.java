package com.example.mapquiz;

import java.util.Random;

public class Question {

    private Country OpA;
    private Country OpB;
    private Country OpC;
    private Country OpD;
    public Country getRandomOption() {
        Random random = new Random();
        int randomNumber = random.nextInt(4);
        switch (randomNumber) {
            case 0:
                return OpA;
            case 1:
                return OpB;
            case 2:
                return OpC;
            case 3:
                return OpD;
            default:
                return null;
        }
    }
    public Country getOpA() {
        return OpA;
    }

    public void setOpA(Country opA) {
        OpA = opA;
    }

    public Country getOpB() {
        return OpB;
    }

    public void setOpB(Country opB) {
        OpB = opB;
    }

    public Country getOpC() {
        return OpC;
    }

    public void setOpC(Country opC) {
        OpC = opC;
    }

    public Country getOpD() {
        return OpD;
    }

    public void setOpD(Country opD) {
        OpD = opD;
    }
}