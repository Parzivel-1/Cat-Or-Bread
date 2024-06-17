package com.example.catorbread;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

import javax.annotation.Nonnull;

public class SplashActivity extends AppCompatActivity {
    String user = "" , password = "";
    TextToSpeech textToSpeech;
    Context ctx = this;
    boolean flag;
    boolean logged;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        //         textToSpeech = new TextToSpeech (ctx , new TextToSpeech.OnInitListener () {
        //            @Override
        //            public void onInit (int i) {


        textToSpeech = new TextToSpeech (ctx , i -> {
            if (i == TextToSpeech.SUCCESS) {
                textToSpeech.setLanguage(Locale.US);
                int res = textToSpeech.speak("CAT OR BREAD" , TextToSpeech.QUEUE_FLUSH , null , ctx.hashCode() + "");
                if (res == TextToSpeech.ERROR) {
                    Log.e("TTS" , "Error in converting Text to Speech!");
                }
            } else {
                Log.e("TTS" , "Initialization failed!");
            }
        });

        Thread thread = new Thread () {
            public void run () {
                try {
                    if (getSharedPreferences("user" , MODE_PRIVATE).getBoolean("save" , true)) {
                        user = getSharedPreferences("user" , MODE_PRIVATE).getString("username" , "");
                        password = getSharedPreferences("user" , MODE_PRIVATE).getString("password" , "");
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
                                if (value != null && value.getPassword() != null && value.getPassword().equals(password)) {
                                    User.setCurrent(value.getUsername());
                                    logged = true;
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
                    if (logged) {
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