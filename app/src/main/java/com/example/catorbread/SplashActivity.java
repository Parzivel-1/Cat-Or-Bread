package com.example.catorbread;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import javax.annotation.Nonnull;

public class SplashActivity extends AppCompatActivity {
    Context ctx = this;
    boolean flag;
    boolean loggedin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Thread thread = new Thread() {
            public void run () {
                try {
                    if (getSharedPreferences("user" , MODE_PRIVATE).getBoolean("save" , true)) {
                        String user = getSharedPreferences("user" , MODE_PRIVATE).getString("username" , "");
                        String password = getSharedPreferences("user" , MODE_PRIVATE).getString("password" , "");
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference myRef = database.getReference("Users/" + user);
                        flag = false;
                        myRef.addValueEventListener (new ValueEventListener() {
                            @Override
                            public void onDataChange (@Nonnull DataSnapshot dataSnapshot) {
                                if (flag) {
                                    return;
                                }
                                flag = true;
                                User value = dataSnapshot.getValue(User.class);
                                if (value != null && value.getPassword().equals(password)) {
                                    User.setCurrent(value.getUsername());
                                    loggedin = true;
                                }
                            }

                            @Override
                            public void onCancelled (@NonNull DatabaseError error) {
                                Log.w("TAG" , "Failed to read value." , error.toException());
                            }
                        });
                    }
                    synchronized (this) {
                        wait(3000);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    Intent intent = new Intent (ctx , StartActivity.class);
                    if (loggedin) {
                        intent = new Intent (ctx , MainActivity.class);
                    }
                    startActivity(intent);
                    finish();
                }

            }
        };
        thread.start();
    }
}