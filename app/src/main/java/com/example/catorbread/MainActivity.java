package com.example.catorbread;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void clickLogIn (View view) {
        Intent intent = new Intent(this , LogInActivity.class);
        startActivity(intent);
    }

    public void clickSignUp (View view) {
        Intent intent = new Intent(this , SignUpActivity.class);
        startActivity(intent);
    }
}