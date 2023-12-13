package com.example.mapquiz;

public class Modelclass {
    String Question;
    String OpA;
    String OpB;
    String OpC;
    String OpD;
    String Correct;

    public Modelclass(String question, String opA, String opB, String opC, String opD, String correct) {
        Question = question;
        OpA = opA;
        OpB = opB;
        OpC = opC;
        OpD = opD;
        Correct = correct;
    }

    public String getQuestion() {
        return Question;
    }

    public void setQuestion(String question) {
        Question = question;
    }

    public String getOpA() {
        return OpA;
    }

    public void setOpA(String opA) {
        OpA = opA;
    }

    public String getOpB() {
        return OpB;
    }

    public void setOpB(String opB) {
        OpB = opB;
    }

    public String getOpC() {
        return OpC;
    }

    public void setOpC(String opC) {
        OpC = opC;
    }

    public String getOpD() {
        return OpD;
    }

    public void setOpD(String opD) {
        OpD = opD;
    }

    public String getCorrect() {
        return Correct;
    }

    public void setCorrect(String correct) {
        Correct = correct;
    }
}
