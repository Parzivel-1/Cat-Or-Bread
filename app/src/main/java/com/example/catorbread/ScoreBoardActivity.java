package com.example.catorbread;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class ScoreBoardActivity extends AppCompatActivity {
    TextView tVP1Name,tVP2Name,tVP1Score,tVP2Sore,tVCode;
    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_board);
        initViews();
    }
    public void initViews(){
        tVCode=findViewById(R.id.code);
        tVP1Name=findViewById(R.id.P1Name);
        tVP1Score=findViewById(R.id.P1Score);
        tVP2Name=findViewById(R.id.P2Name);
        tVP2Sore=findViewById(R.id.P2Score);
        button=findViewById(R.id.button);

        Intent intent=getIntent();
        tVP1Name.setText(intent.getStringExtra("player1"));
        tVP2Name.setText(intent.getStringExtra("player2"));
        tVP1Score.setText(intent.getIntExtra("sP1",0)+"");
        tVP2Sore.setText(intent.getIntExtra("sP2",0)+"");
        tVCode.setText(intent.getStringExtra("code"));
    }
}