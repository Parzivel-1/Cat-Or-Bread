package com.example.catorbread;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    Context ctx = this;
    TextView username;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        username = findViewById(R.id.username);
        username.setText(User.getCurrent());
        setSupportActionBar(findViewById(R.id.Toolbar));
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu , menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected (@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        int id = item.getItemId();
        if (id == R.id.settings) {
            Intent intent = new Intent (ctx , SettingsActivity.class);
            startActivity(intent);
            finish();
            return true;
        } else if (id == R.id.logout) {
            logOut(false);
            return true;
        } else if (id == R.id.exit) {
            AlertDialog.Builder builder = new AlertDialog.Builder (this);
            builder.setMessage("Are you sure you want to exit?");
            builder.setCancelable(true);
            builder.setPositiveButton("Yes" , (dialog , which) -> logOut(true));
            builder.setNegativeButton("No" , (dialog , which) -> dialog.cancel());
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
            return true;
        }
        return false;
    }

    public void createGame (View view) {
        Intent intent = new Intent (ctx , GameActivity.class);
        intent.putExtra("role" , "host");
        startActivity(intent);
    }

    public void joinGame (View view) {
        Intent intent = new Intent (ctx , JoinActivity.class);
        startActivity(intent);
    }

    public void viewProfile (View view) {
        Intent intent = new Intent (this , ProfileActivity.class);
        startActivity(intent);
    }

    public void logOut (boolean close) {
        getSharedPreferences("user" , MODE_PRIVATE).edit().putBoolean("save" , false).apply();
        getSharedPreferences("user" , MODE_PRIVATE).edit().putString("password" , "").apply();
        User.setCurrent("");
        if (close) {
            finish();
            System.exit(0);
        } else {
            Intent intent = new Intent (ctx , StartActivity.class);
            startActivity(intent);
            finish();
        }
    }
}