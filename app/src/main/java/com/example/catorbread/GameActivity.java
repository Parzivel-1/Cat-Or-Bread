package com.example.catorbread;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Random;

public class GameActivity extends AppCompatActivity {
    boolean guest = false;
    boolean gameStart = false;
    Button btnCreateGame;
    TextView tVCode , tVP2 , tVP1 , tVP1S , tVP2S , time;
    Game game;
    Board board;
    int timer = 30;
    ImageButton [][] iB = new ImageButton [3][3];
    boolean waiting = false;


    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        btnCreateGame = findViewById(R.id.btnCreateGame);
        tVCode = findViewById(R.id.codeTV);
        tVP2 = findViewById(R.id.tVP2);
        tVP1 = findViewById(R.id.tVP1);
        time = findViewById(R.id.timeTV);
        tVP1.setText(User.getCurrent());
        setSupportActionBar(findViewById(R.id.Toolbar));
        String code = getIntent().getStringExtra("code");
        if (code != null) {
            guest = true;
            waiting(code);
        }
        initBoard();
    }

    public void updateGameProcess () {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Game/" + game.getCode());
        myRef.addValueEventListener(new ValueEventListener () {
            @Override
            public void onDataChange (@NonNull DataSnapshot dataSnapshot) {
                if (game.getTime() == 0) {
                    endGame();
                }
                game = dataSnapshot.getValue(Game.class);
                if (game == null) {
                    return;
                }
                if (game.getPlayer1().equals(User.getCurrent())) {
                    if (timer != game.getTime()) {
                        game.setTime(timer);
                        updateGame();
                    }
                }
                time.setText(game.getTime() + "");
                tVP1S.setText(game.getScoreP1() + "");
                tVP2S.setText(game.getScoreP2() + "");
                showBoard();
            }

            @Override
            public void onCancelled (@NonNull DatabaseError error) {
                Log.w("Failed to read value.", error.toException());
            }
        });
    }

    public void endGame () {
        Intent intent = new Intent(this , ScoreBoardActivity.class);
        intent.putExtra("player1" , game.getPlayer1());
        intent.putExtra("player2" , game.getPlayer2());
        intent.putExtra("sP1" , game.getScoreP1());
        intent.putExtra("sP2" , game.getScoreP2());
        intent.putExtra("code" , game.getCode());
        startActivity(intent);
    }

    public void timer () {
        if (game.getPlayer2().equals(User.getCurrent())) {
            return;
        }
        game.setTime(30);
        new CountDownTimer (30 * 1000 , 1000) {
            public void onTick (long millisUntilFinished) {
                // time.setText(game.getTime()+"");
                timer--;
                game.setTime(timer);
                updateGame();
            }

            public void onFinish () {
                game.setTime(0);
                updateGame();
                endGame();
            }
        }.start();
    }

    public void updateGame () {
        game.getBoard().scan();
        showBoard();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Game/" + game.getCode());
        myRef.setValue(game);
    }

    public void startGame () {
        gameStart = true;
        tVP1S.setText(game.getScoreP1() + "");
        tVP2S.setText(game.getScoreP2() + "");
        for (int i = 0 ; i < 3 ; i++) {
            for (int j = 0 ; j < 3 ; j++) {
                iB[i][j].setVisibility(View.VISIBLE);
            }
        }
        timer();
        updateGameProcess();
    }

    public void initBoard () {
        board = new Board ();
        int counter = 1;
        for (int i = 0; i < 3 ; i++) {
            for (int j = 0; j < 3 ; j++) {
                int id = getResources().getIdentifier("btn" + counter , "id" , this.getPackageName());
                iB[i][j] = findViewById(id);
                iB[i][j].setVisibility(View.INVISIBLE);
                counter++;
            }
        }

        showBoard();

        for (int i = 0 ; i < 3 ; i++) {
            for (int j = 0 ; j < 3 ; j++) {
                iB[i][j].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick (View view) {
                        String tag = view.getTag().toString();
                        String photo = tag.split("_")[0];
                        int index = Integer.parseInt(tag.split("_")[1]);
                        if (User.getCurrent().equals(game.getPlayer1())) {
                            if (photo.equals("bread")) {
                                game.setScoreP1(game.getScoreP1() + 1);
                            } else if (photo.equals("cat")) {
                                game.setScoreP1(game.getScoreP1() - 1);
                            }
                            game.getBoard().getCells().set(index , "e");
                        } else {
                            if (photo.equals("bread")) {
                                game.setScoreP2(game.getScoreP2() - 1);
                            } else if (photo.equals("cat")) {
                                game.setScoreP2(game.getScoreP2() + 1);
                            }
                            game.getBoard().getCells().set(index , "e");
                        }
                        updateGame();
                    }
                });
            }
        }
    }

    public void showBoard () {
        ArrayList <String> cells;
        if (game == null) {
            cells = board.getCells();
        } else {
            cells = game.getBoard().getCells();
        }
        int counter = 0;
        for (int i = 0 ; i < 3 ; i++) {
            for (int j = 0 ; j < 3 ; j++) {
                if (cells.size() <= counter) {
                    continue;
                }
                if (gameStart) {
                    iB[i][j].setVisibility(View.VISIBLE);
                }
                char tag = cells.get(counter).charAt(0);
                if (tag == 'c') {
                    iB[i][j].setImageResource(R.drawable.tom);
                    iB[i][j].setTag("cat_" + counter);
                } else if (tag == 'b') {
                    iB[i][j].setImageResource(R.drawable.bread);
                    iB[i][j].setTag("bread_" + counter);
                } else {
                    iB[i][j].setVisibility(View.INVISIBLE);
                }
                counter++;
            }
        }
    }

    public void createGame () {
        Random rnd = new Random();
        game = new Game(User.getCurrent() , null , (rnd.nextInt(9000) + 1000) + "" , board);
        tVCode.setText(game.getCode());

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Game/" + game.getCode());
        myRef.setValue(game);

        waiting(game.getCode());
    }

    public void waiting (String code) {
        btnCreateGame.setVisibility(View.INVISIBLE);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Game/" + code);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange (@NonNull DataSnapshot dataSnapshot) {
                Game value = dataSnapshot.getValue(Game.class);
                if (value != null && value.getPlayer2() != null) {
                    game = value;
                    tVP1.setText(value.getPlayer1());
                    tVP2.setText(value.getPlayer2());
                    tVCode.setText(value.getCode());
                    if (value.getTime() > 0 && guest) {
                        startGame();
                    }
                    waiting = false;
                    if (!btnCreateGame.getText().toString().equals("Game Start") && !guest) {
                        btnCreateGame.setText("Start Game");
                        btnCreateGame.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled (@NonNull DatabaseError error) {
                Log.w("TAG" , "Failed to read value." , error.toException());
            }
        });
    }

    public void btnClick (View view) {
        if (btnCreateGame.getText().toString().equals("Create Room")) {
            waiting = true;
            btnCreateGame.setText("");
            createGame();
        } else if (btnCreateGame.getText().toString().equals("Start Game")) {
            btnCreateGame.setVisibility(View.INVISIBLE);
            btnCreateGame.setText("Game Start");
            startGame();
        }
    }
}