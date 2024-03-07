package com.example.catorbread;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import javax.annotation.Nonnull;

public class MainActivity extends AppCompatActivity {
    Context ctx = this;
    TextView username;
    boolean flag;

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
            Intent intent = new Intent(this , SettingsActivity.class);
            startActivity(intent);
            finish();
            return true;
        } else if (id == R.id.logout) {
            logout(false);
            return true;
        } else if (id == R.id.exit) {
            logout(true);
            return true;
        }
        return false;
    }

    public void joinGame (View view) {
        Intent intent = new Intent(this , JoinActivity.class);
        startActivity(intent);
        finish();
    }

    public void createGame (View view) {
        Intent intent = new Intent(this , GameActivity.class);
        startActivity(intent);
        finish();
    }

    public void logout (boolean close) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Users/" + User.getCurrent());
        flag = false;
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange (@Nonnull DataSnapshot dataSnapshot) {
                if (flag) {
                    return;
                }
                flag = true;
                myRef.getRef().child("connected").setValue(false);
                if (close) {
                    finish();
                    System.exit(0);
                } else {
                    Intent intent = new Intent(ctx , StartActivity.class);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onCancelled (@Nonnull DatabaseError error) {
                Log.w("TAG", "Failed to read value.", error.toException());
            }
        });
    }
}