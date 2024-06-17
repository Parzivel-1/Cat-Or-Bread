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

public class StartActivity extends AppCompatActivity {
    Context ctx = this;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        setSupportActionBar(findViewById(R.id.Toolbar));
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        getMenuInflater().inflate(R.menu.start_menu , menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected (@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        int id = item.getItemId();
        if (id == R.id.exit) {
            AlertDialog.Builder builder = new AlertDialog.Builder (ctx);
            builder.setMessage("Are you sure you want to exit?");
            builder.setCancelable(true);
            builder.setPositiveButton("Yes" , (dialog , which) -> quit());
            builder.setNegativeButton("No" , (dialog , which) -> dialog.cancel());
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
            return true;
        }
        return false;
    }

    public void clickLogIn (View view) {
        Intent intent = new Intent (ctx , LogInActivity.class);
        intent.putExtra("code" , "login");
        startActivity(intent);
    }

    public void clickSignUp (View view) {
        Intent intent = new Intent (ctx , SignUpActivity.class);
        intent.putExtra("code" , "signup");
        startActivity(intent);
    }

    public void quit () {
        finish();
        System.exit(0);
    }
}