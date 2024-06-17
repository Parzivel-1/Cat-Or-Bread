package com.example.catorbread;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.Locale;

public class ScoreBoardActivity extends AppCompatActivity {
    TextView tVP1Name , tVP2Name , tVP1Score , tVP2Sore , tVCode;
    ImageView P1Image , P2Image;
    Button button;
    TextToSpeech textToSpeech;
    String role;
    boolean flag;
    Context ctx = this;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_board);
        tVCode = findViewById(R.id.code);
        tVP1Name = findViewById(R.id.P1Name);
        tVP1Score = findViewById(R.id.P1Score);
        P1Image = findViewById(R.id.P1Image);
        tVP2Name = findViewById(R.id.P2Name);
        tVP2Sore = findViewById(R.id.P2Score);
        P2Image = findViewById(R.id.P2Image);
        button = findViewById(R.id.button);
        Intent intent = getIntent();
        tVP1Name.setText(intent.getStringExtra("player1"));
        tVP2Name.setText(intent.getStringExtra("player2"));
        tVP1Score.setText(intent.getIntExtra("sP1" , 0) + "");
        tVP2Sore.setText(intent.getIntExtra("sP2" , 0) + "");
        tVCode.setText(intent.getStringExtra("code"));
        role = intent.getStringExtra("role");
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
                User value = dataSnapshot.getValue(User.class);
                if (role.equals("host") && value != null) {
                    myRef.child("score").setValue(value.getScore() + intent.getIntExtra("sP1" , 0));
                } else if (role.equals("guest") && value != null) {
                    myRef.child("score").setValue(value.getScore() + intent.getIntExtra("sP2" , 0));
                }
            }

            @Override
            public void onCancelled (@NonNull DatabaseError error) {
                Log.w("TAG" , "Failed to read value." , error.toException());
            }
        });

        ChangePictureActivity.loadProfilePicture(ctx , P1Image , FirebaseStorage.getInstance().getReference() , intent.getStringExtra("player1"));
        ChangePictureActivity.loadProfilePicture(ctx , P2Image , FirebaseStorage.getInstance().getReference() , intent.getStringExtra("player2"));

        String winner;
        if (intent.getIntExtra("sP1" , 0) > intent.getIntExtra("sP2" , 0)) {
            winner = tVP1Name.getText().toString() + " wins!";
        } else if (intent.getIntExtra("sP1" , 0) < intent.getIntExtra("sP2" , 0)) {
            winner = tVP2Name.getText().toString() + " wins!";
        } else {
            winner = "It's a tie!";
        }


        //        textToSpeech = new TextToSpeech (ctx , new TextToSpeech.OnInitListener () {
        //            @Override
        //            public void onInit (int i) {




        textToSpeech = new TextToSpeech (ctx , i -> {
            if (i == TextToSpeech.SUCCESS) {
                textToSpeech.setLanguage(Locale.US);
                int res = textToSpeech.speak(winner , TextToSpeech.QUEUE_FLUSH , null , ctx.hashCode() + "");
                if (res == TextToSpeech.ERROR) {
                    Log.e("TTS" , "Error in converting Text to Speech!");
                }
            } else {
                Log.e("TTS" , "Initialization failed!");
            }
        });
    }

    public void cntn (View view) {
        finish();
    }
}