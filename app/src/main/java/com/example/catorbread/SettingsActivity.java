package com.example.catorbread;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class SettingsActivity extends AppCompatActivity {
    Context ctx = this;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }

    public void clickBack (View view) {
        Intent intent = new Intent(this , MainActivity.class);
        startActivity(intent);
        finish();
    }
}