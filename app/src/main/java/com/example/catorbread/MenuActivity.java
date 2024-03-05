package com.example.catorbread;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MenuActivity extends AppCompatActivity {
    TextView username;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        username = findViewById(R.id.username);
        username.setText(User.getCurrent());
    }

    public void joinGame (View view) {
        Intent intent = new Intent(this , JoinActivity.class);
        startActivity(intent);
    }

    public void createGame (View view) {
        Intent intent = new Intent(this , GameActivity.class);
        startActivity(intent);
    }

    public void clickBack (View view) {
        Intent intent = new Intent(this , MainActivity.class);
        startActivity(intent);
    }
}