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
    boolean guest = false , gameStart = false;
    Button btnCreateGame;
    TextView tVCode , tVP2 , tVP1 , tVP1S , tVP2S , time;
    Game game;
    Board board;
    int timer;
    ImageButton [][] iB = new ImageButton [3][3];
    boolean waiting = true;
    boolean flag , flag2;
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
        initBoard();
        setSupportActionBar(findViewById(R.id.Toolbar));
        String role = getIntent().getStringExtra("role");
        if (role != null && role.equals("guest")) {
            guest = true;
            btnCreateGame.setVisibility(View.INVISIBLE);
            String code = getIntent().getStringExtra("code");
            waiting(code);
            game = new Game ();
            game.setCode(code);
            game.setTime(timer);
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
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Games/");
        myRef.child("LEAVE").child(game.getCode()).setValue(true);
        if (tVP2.getText().toString().isEmpty()) {
            myRef = database.getReference("Games/" + game.getCode());
            myRef.removeValue();
            DatabaseReference myRef2 = database.getReference("Games/");
            myRef2.child("LEAVE").child(game.getCode()).removeValue();
        }
        finish();
    }

    public void RunningGame () {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Games/" + game.getCode());
        ValueEventListener running = new ValueEventListener () {
            @Override
            public void onDataChange (@NonNull DataSnapshot dataSnapshot) {
                game = dataSnapshot.getValue(Game.class);
                if (game == null || !game.getStarted()) {
                    return;
                } else if (!guest && game.getTime() == 0) {
                    myRef.child("DONE").setValue(true);
                    endGame(game);
                    return;
                } else if (guest && dataSnapshot.child("DONE").getValue() == Boolean.TRUE) {
                    myRef.removeValue();
                    endGame(game);
                    return;
                } else if (!guest && timer != game.getTime()) {
                    game.setTime(timer);
                    updateGame();
                }
                game.getBoard().scan();
                showBoard();
                time.setText(game.getTime() + "");
                tVP1S.setText(game.getScoreP1() + "");
                tVP2S.setText(game.getScoreP2() + "");
                if (game.getPlayer1() != null && game.getPlayer2() != null) {
                    tVP1.setText(game.getPlayer1());
                    tVP2.setText(game.getPlayer2());
                }
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
                if (dataSnapshot.child("LEAVE").child(game.getCode()).getValue() == Boolean.TRUE) {
                    myRef.removeEventListener(running);
                    myRef.removeEventListener(this);
                    myRef.removeValue();
                    Toast.makeText(ctx , "The other player left the game!" , Toast.LENGTH_SHORT).show();
                    DatabaseReference myRef2 = database.getReference("Games/");
                    myRef2.child("LEAVE").child(game.getCode()).removeValue();
                    finish();
                }
            }

            @Override
            public void onCancelled (@NonNull DatabaseError error) {
                Log.w("TAG" , "Failed to read value." , error.toException());
            }
        };
    }

    public void endGame (Game finalGame) {
        if (flag2) {
            return;
        }
        flag2 = true;
        Intent intent = new Intent (ctx , ScoreBoardActivity.class);
        intent.putExtra("player1" , finalGame.getPlayer1());
        intent.putExtra("player2" , finalGame.getPlayer2());
        intent.putExtra("sP1" , finalGame.getScoreP1());
        intent.putExtra("sP2" , finalGame.getScoreP2());
        intent.putExtra("code" , finalGame.getCode());
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
        game.setTime(timer);
        new CountDownTimer (timer * 1000 , 1000) {
            public void onTick (long millisUntilFinished) {
                timer--;
                game.setTime(timer);
                updateGame();
            }

            public void onFinish () {
                if (game == null) {
                    return;
                }
                game.setTime(0);
                updateGame();
            }
        }.start();
    }

    public void startGame () {
        gameStart = true;
        game.setStarted(true);
        updateGame();
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



                //                 iB[i][j].setOnClickListener(new View.OnClickListener () {
                //                    @Override
                //                    public void onClick (View view) {




                iB[i][j].setOnClickListener(view -> {
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference("Games/" + game.getCode());
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
        Random rnd = new Random ();
        board = new Board ();
        board.init();
        game = new Game (User.getCurrent() , null , (rnd.nextInt(9000) + 1000) + "" , board , timer);
        tVCode.setText(game.getCode());

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Games/" + game.getCode());
        flag = false;
        myRef.addValueEventListener(new ValueEventListener () {
            @Override
            public void onDataChange (@Nonnull DataSnapshot dataSnapshot) {
                if (flag) {
                    return;
                }
                flag = true;
                Game value = dataSnapshot.getValue(Game.class);
                if (value != null) {
                    createGame();
                } else {
                    updateGame();
                    waiting(game.getCode());
                }
            }

            @Override
            public void onCancelled (@Nonnull DatabaseError error) {
                Log.w("TAG" , "Failed to read value." , error.toException());
            }
        });
    }

    public void waiting (String code) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Games/" + code);
        myRef.addValueEventListener(new ValueEventListener () {
            @Override
            public void onDataChange (@NonNull DataSnapshot dataSnapshot) {
                if (!waiting || dataSnapshot.child("DONE").getValue() == Boolean.TRUE || gameStart) {
                    return;
                }
                Game value = dataSnapshot.getValue(Game.class);
                if (value == null) {
                    return;
                }
                if (guest && value.getPlayer2() == null) {
                    myRef.child("player2").setValue(User.getCurrent());
                }
                if (!guest && value.getPlayer2() == null) {
                    game = value;
                    myRef.setValue(game);
                }
                if (tVP1.getText().toString().isEmpty()) {
                    tVP1.setText(value.getPlayer1());
                }
                if (tVP2.getText().toString().isEmpty()) {
                    tVP2.setText(value.getPlayer2());
                }
                tVCode.setText(value.getCode());
                gameStart = value.getStarted();
                if (!guest && value.getPlayer2() != null) {
                    waiting = false;
                    btnCreateGame.setText("Start Game");
                    btnCreateGame.setVisibility(View.VISIBLE);
                } else if (!guest && value.getPlayer2() == null) {
                    btnCreateGame.setText("Waiting for player 2 to join the game");
                    btnCreateGame.setVisibility(View.VISIBLE);
                } else if (guest && !gameStart) {
                    btnCreateGame.setText("Waiting for player 1 to start the game");
                    btnCreateGame.setVisibility(View.VISIBLE);
                } else if (guest) {
                    btnCreateGame.setVisibility(View.INVISIBLE);
                    RunningGame();
                }
            }

            @Override
            public void onCancelled (@NonNull DatabaseError error) {
                Log.w("TAG" , "Failed to read value." , error.toException());
            }
        });
    }

    public void updateGame () {
        if (game != null) {
            game.getBoard().scan();
            showBoard();
            time.setText(game.getTime() + "");
        }
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Games/" + game.getCode());
        myRef.setValue(game);
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