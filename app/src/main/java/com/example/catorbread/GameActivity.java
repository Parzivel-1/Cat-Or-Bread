package com.example.catorbread;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Random;

import javax.annotation.Nonnull;

public class GameActivity extends AppCompatActivity {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef , myRef2;
    boolean guest = false , gameStart = false;
    Button btnCreateGame;
    TextView tVCode , tVP2 , tVP1 , tVP1S , tVP2S , time;
    Game game;
    Board board;
    int timer;
    ImageButton [][] iB = new ImageButton [3][3];
    Context ctx = this;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        btnCreateGame = findViewById(R.id.btnCreateGame);
        tVCode = findViewById(R.id.codeTV);
        tVP2 = findViewById(R.id.tVP2);
        tVP1 = findViewById(R.id.tVP1);
        tVP1S = findViewById(R.id.P1score);
        tVP2S = findViewById(R.id.P2score);
        time = findViewById(R.id.tVTime);
        tVP1.setText(User.getCurrent());
        timer = 30;
        setSupportActionBar(findViewById(R.id.Toolbar));
        String role = getIntent().getStringExtra("role");
        if (role != null && role.equals("guest")) {
            guest = true;
            btnCreateGame.setVisibility(View.INVISIBLE);
            String code = getIntent().getStringExtra("code");
            game = new Game ();
            game.setCode(code);
            game.update();
            tVP2.setText(User.getCurrent());
            tVP1.setText(game.getPlayer1());
            myRef = database.getReference("Games/" + code);
            myRef.child("player2").setValue(User.getCurrent());
            myRef2 = database.getReference("Games/LEAVE/" + game.getCode());
            waiting();
        }
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        getMenuInflater().inflate(R.menu.game_menu , menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected (@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        int id = item.getItemId();
        if (id == R.id.leave) {
            AlertDialog.Builder builder = new AlertDialog.Builder (ctx);
            builder.setMessage("Are you sure you want to leave the lobby?");
            builder.setCancelable(true);
            builder.setPositiveButton("Yes" , (dialog , which) -> leave());
            builder.setNegativeButton("No" , (dialog , which) -> dialog.cancel());
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
            return true;
        }
        return false;
    }

    public void leave () {
        myRef2.setValue(true);
        if (!gameStart) {
            myRef.removeValue();
            myRef2.removeValue();
        }
        finish();
    }

    public void RunningGame () {
        ValueEventListener running = new ValueEventListener () {
            @Override
            public void onDataChange (@NonNull DataSnapshot dataSnapshot) {
                if (game == null) {
                    return;
                }
                showBoard();
                tVP1S.setText(game.getScoreP1() + "");
                tVP2S.setText(game.getScoreP2() + "");
                time.setText(game.getTime() + "");
            }

            @Override
            public void onCancelled (@NonNull DatabaseError error) {
                Log.w("TAG" , "Failed to read value." , error.toException());
            }
        };

        myRef.addValueEventListener(running);

        ValueEventListener ending = new ValueEventListener () {
            @Override
            public void onDataChange (@NonNull DataSnapshot dataSnapshot) {
                if (game == null) {
                    return;
                } else if (game.getTime() == 0) {
                    myRef.removeEventListener(running);
                    myRef.removeEventListener(this);
                    myRef.removeValue();
                    myRef2.removeValue();
                    endGame();
                } else if (dataSnapshot.getValue() == Boolean.TRUE) {
                    game.stopUpdating();
                    myRef.removeEventListener(running);
                    myRef.removeEventListener(this);
                    myRef.removeValue();
                    myRef2.removeValue();
                    Toast.makeText(ctx , "The other player left the game!" , Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onCancelled (@NonNull DatabaseError error) {
                Log.w("TAG" , "Failed to read value." , error.toException());
            }
        };

        myRef2.addValueEventListener(ending);
    }

    public void endGame () {
        game.stopUpdating();
        game.setTime(-1);
        Intent intent = new Intent (ctx , ScoreBoardActivity.class);
        intent.putExtra("player1" , game.getPlayer1());
        intent.putExtra("player2" , game.getPlayer2());
        intent.putExtra("sP1" , game.getScoreP1());
        intent.putExtra("sP2" , game.getScoreP2());
        intent.putExtra("code" , game.getCode());
        if (guest) {
            intent.putExtra("role" , "guest");
        } else {
            intent.putExtra("role" , "host");
        }
        startActivity(intent);
        finish();
    }

    public void timerFunc () {
        if (guest) {
            return;
        }

        CountDownTimer cdt = new CountDownTimer (timer * 1000 , 1000) {
            public void onTick (long millisUntilFinished) {
                timer--;
                game.setTime(timer);
                myRef.child("time").setValue(timer);
            }

            public void onFinish () {
                if (game == null) {
                    return;
                }
                game.setTime(0);
                myRef.child("time").setValue(timer);
                endGame();
                myRef.removeValue();
            }
        }.start();
    }

    public void startGame () {
        gameStart = true;
        myRef.child("started").setValue(true);
        timerFunc();
        RunningGame();
    }

    public void initBoard () {
        iB[0][0] = findViewById(R.id.btn1);
        iB[0][1] = findViewById(R.id.btn2);
        iB[0][2] = findViewById(R.id.btn3);
        iB[1][0] = findViewById(R.id.btn4);
        iB[1][1] = findViewById(R.id.btn5);
        iB[1][2] = findViewById(R.id.btn6);
        iB[2][0] = findViewById(R.id.btn7);
        iB[2][1] = findViewById(R.id.btn8);
        iB[2][2] = findViewById(R.id.btn9);
        for (int i = 0; i < 3 ; i++) {
            for (int j = 0; j < 3 ; j++) {
                iB[i][j].setVisibility(View.INVISIBLE);
            }
        }

        for (int i = 0 ; i < 3 ; i++) {
            for (int j = 0 ; j < 3 ; j++) {
                iB[i][j].setOnClickListener(view -> {
                    if (game == null || game.getTime() == 0) {
                        return;
                    }
                    String tag = view.getTag().toString();
                    String photo = tag.split("_")[0];
                    int index = Integer.parseInt(tag.split("_")[1]);
                    if (User.getCurrent().equals(game.getPlayer1())) {
                        if (photo.equals("bread")) {
                            myRef.child("scoreP1").setValue(game.getScoreP1() + 1);
                        } else if (photo.equals("cat")) {
                            myRef.child("scoreP1").setValue(game.getScoreP1() - 1);
                        }
                    } else {
                        if (photo.equals("cat")) {
                            myRef.child("scoreP2").setValue(game.getScoreP2() + 1);
                        } else if (photo.equals("bread")) {
                            myRef.child("scoreP2").setValue(game.getScoreP2() - 1);
                        }
                    }
                    Board board = game.getBoard();
                    board.getCells().set(index , "e");
                    board.scan();
                    myRef.child("board").setValue(board);
                });
            }
        }
    }

    public void showBoard () {
        ArrayList <String> cells;
        if (game == null) {
            return;
        }
        cells = game.getBoard().getCells();
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
        Random rnd = new Random ();
        board = new Board ();
        board.init();
        game = new Game (User.getCurrent() , null , (rnd.nextInt(9000) + 1000) + "" , board , timer);
        tVCode.setText(game.getCode());
        myRef = database.getReference("Games/" + game.getCode());
        myRef2 = database.getReference("Games/LEAVE/" + game.getCode());
        initBoard();
        myRef.setValue(game);
        game.update();
        waiting();
    }

    public void waiting () {
        myRef.addValueEventListener(new ValueEventListener () {
            @Override
            public void onDataChange (@NonNull DataSnapshot dataSnapshot) {
                gameStart = game.getStarted();
                if (!guest && game.getPlayer2() != null) {
                    gameStart = true;
                    myRef.removeEventListener(this);
                    tVP1.setText(User.getCurrent());
                    tVP2.setText(game.getPlayer2());
                    btnCreateGame.setText("Start Game");
                    btnCreateGame.setVisibility(View.VISIBLE);
                } else if (!guest && game.getPlayer2() == null) {
                    btnCreateGame.setText("Waiting for player 2 to join the game");
                    btnCreateGame.setVisibility(View.VISIBLE);
                } else if (guest && !gameStart) {
                    btnCreateGame.setText("Waiting for player 1 to start the game");
                    btnCreateGame.setVisibility(View.VISIBLE);
                } else if (guest) {
                    btnCreateGame.setVisibility(View.INVISIBLE);
                    initBoard();
                    RunningGame();
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
            btnCreateGame.setVisibility(View.INVISIBLE);
            createGame();
        } else if (btnCreateGame.getText().toString().equals("Start Game")) {
            btnCreateGame.setVisibility(View.INVISIBLE);
            startGame();
        }
    }
}