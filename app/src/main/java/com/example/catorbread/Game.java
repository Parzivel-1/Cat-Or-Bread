package com.example.catorbread;

public class Game {
    boolean started = false;
    String player1; // bread
    String player2; // cat
    String code;
    Board board;
    int time , ScoreP1 , ScoreP2;

    public Game (String player1 , String player2 , String code , Board board , int time) {
        this.player1 = player1;
        this.player2 = player2;
        this.code = code;
        this.board = board;
        this.time = time;
    }

    public Game () {
    }

    public Board getBoard () {
        return board;
    }

    public void setBoard (Board board) {
        this.board = board;
    }

    public int getTime () {
        return time;
    }

    public void setTime (int time) {
        this.time = time;
    }

    public int getScoreP1 () {
        return ScoreP1;
    }

    public void setScoreP1 (int ScoreP1) {
        this.ScoreP1 = ScoreP1;
    }

    public int getScoreP2 () {
        return ScoreP2;
    }

    public void setScoreP2 (int ScoreP2) {
        this.ScoreP2 = ScoreP2;
    }

    public String getPlayer1 () {
        return player1;
    }

    public void setPlayer1 (String player1) {
        this.player1 = player1;
    }

    public String getPlayer2 () {
        return player2;
    }

    public void setPlayer2 (String player2) {
        this.player2 = player2;
    }

    public String getCode () {
        return code;
    }

    public void setCode (String code) {
        this.code = code;
    }

    public boolean getStarted () {
        return started;
    }

    public void setStarted (boolean started) {
        this.started = started;
    }
}