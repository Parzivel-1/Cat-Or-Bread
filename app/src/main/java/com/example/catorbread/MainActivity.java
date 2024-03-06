package com.example.catorbread;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
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
        intent.putExtra("code" , "login");
        startActivity(intent);
        finish();
    }

    public void clickSignUp (View view) {
        Intent intent = new Intent(this , SignUpActivity.class);
        intent.putExtra("code" , "signup");
        startActivity(intent);
        finish();
    }

    public void clickExit (View view) {
        // pop an alert dialog to confirm exit
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to exit?");
        builder.setCancelable(true);
        builder.setPositiveButton("Yes" , (dialog , which) -> quit());
        builder.setNegativeButton("No" , (dialog , which) -> dialog.cancel());
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void quit() {
        finish();
        System.exit(0);
    }
}