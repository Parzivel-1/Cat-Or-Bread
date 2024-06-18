package com.example.catorbread;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Game {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef;
    ValueEventListener updating;
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
        myRef = database.getReference("Games/" + code);
    }

    public Game () {
    }

    public Board getBoard () {
        return board;
    }

    public void update () {
        updating = new ValueEventListener() {
            @Override
            public void onDataChange (@NonNull DataSnapshot dataSnapshot) {
                Game game = dataSnapshot.getValue(Game.class);
                if (game == null) {
                    return;
                }
                setBoard(game.getBoard());
                setTime(game.getTime());
                setScoreP1(game.getScoreP1());
                setScoreP2(game.getScoreP2());
                setPlayer1(game.getPlayer1());
                setPlayer2(game.getPlayer2());
                setStarted(game.getStarted());
            }

            @Override
            public void onCancelled (@NonNull DatabaseError error) {
                Log.w("TAG" , "Failed to read value." , error.toException());
            }
        };

        myRef.addValueEventListener(updating);
    }

    public void stopUpdating () {
        myRef.removeEventListener(updating);
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
        myRef = database.getReference("Games/" + code);
    }

    public boolean getStarted () {
        return started;
    }

    public void setStarted (boolean started) {
        this.started = started;
    }
}