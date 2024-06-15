package com.example.catorbread;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.Firebase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ScoreBoardActivity extends AppCompatActivity {
    TextView tVP1Name , tVP2Name , tVP1Score , tVP2Sore , tVCode;
    Button button;
    boolean flag;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_board);
        tVCode = findViewById(R.id.code);
        tVP1Name = findViewById(R.id.P1Name);
        tVP1Score = findViewById(R.id.P1Score);
        tVP2Name = findViewById(R.id.P2Name);
        tVP2Sore = findViewById(R.id.P2Score);
        button = findViewById(R.id.button);
        Intent intent = getIntent();
        tVP1Name.setText(intent.getStringExtra("player1"));
        tVP2Name.setText(intent.getStringExtra("player2"));
        tVP1Score.setText(intent.getIntExtra("sP1" , 0) + "");
        tVP2Sore.setText(intent.getIntExtra("sP2" , 0) + "");
        tVCode.setText(intent.getStringExtra("code"));
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Users/" + User.getCurrent());
        flag = false;
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange (@NonNull DataSnapshot dataSnapshot) {
                if (flag) {
                    return;
                }
                flag = true;
                User user = dataSnapshot.getValue(User.class);
                if (user == null) {
                    return;
                } else if (tVP1Name.getText().toString().equals(user.getUsername())) {
                    myRef.child("score").setValue(user.getScore() + intent.getIntExtra("sP1" , 0));
                } else if (tVP2Name.getText().toString().equals(user.getUsername())) {
                    myRef.child("score").setValue(user.getScore() + intent.getIntExtra("sP2" , 0));
                }
            }

            @Override
            public void onCancelled (@NonNull DatabaseError error) {
                Log.w("TAG" , "Failed to read value." , error.toException());
            }
        });
    }

    public void cntn (View view) {
        finish();
    }
}