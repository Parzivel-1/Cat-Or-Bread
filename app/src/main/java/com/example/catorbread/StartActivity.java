package com.example.catorbread;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import javax.annotation.Nonnull;

public class StartActivity extends AppCompatActivity {
    Context ctx = this;
    boolean flag;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        setSupportActionBar(findViewById(R.id.Toolbar));
        if (getSharedPreferences("user" , MODE_PRIVATE).getBoolean("save" , true)) {
            String user = getSharedPreferences("user" , MODE_PRIVATE).getString("username" , "");
            String password = getSharedPreferences("user" , MODE_PRIVATE).getString("password" , "");
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("Users/" + user);
            flag = false;
            myRef.addValueEventListener (new ValueEventListener () {
                @Override
                public void onDataChange (@Nonnull DataSnapshot dataSnapshot) {
                    if (flag) {
                        return;
                    }
                    flag = true;
                    User value = dataSnapshot.getValue(User.class);
                    if (value != null && value.getPassword().equals(password)) {
                        User.setCurrent(value.getUsername());
                        Intent intent = new Intent(ctx , MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }

                @Override
                public void onCancelled (@NonNull DatabaseError error) {
                    Log.w("TAG" , "Failed to read value." , error.toException());
                }
            });
        }
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
            AlertDialog.Builder builder = new AlertDialog.Builder (this);
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
        Intent intent = new Intent (this , LogInActivity.class);
        intent.putExtra("code" , "login");
        startActivity(intent);
    }

    public void clickSignUp (View view) {
        Intent intent = new Intent (this , SignUpActivity.class);
        intent.putExtra("code" , "signup");
        startActivity(intent);
    }

    public void quit () {
        finish();
        System.exit(0);
    }
}