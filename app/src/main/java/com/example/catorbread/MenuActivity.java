package com.example.catorbread;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import javax.annotation.Nonnull;

public class MenuActivity extends AppCompatActivity {
    Context ctx = this;
    TextView username;
    boolean flag;

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
        finish();
    }

    public void createGame (View view) {
        Intent intent = new Intent(this , GameActivity.class);
        startActivity(intent);
        finish();
    }

    public void clickLogOut (View view) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Users/" + User.getCurrent());
        flag = false;
        myRef.addValueEventListener(new ValueEventListener () {
            @Override
            public void onDataChange (@Nonnull DataSnapshot dataSnapshot) {
                if (flag) {
                    return;
                }
                flag = true;
                myRef.getRef().child("connected").setValue(false);
                Intent intent = new Intent(ctx , MainActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onCancelled (@Nonnull DatabaseError error) {
                Log.w("TAG" , "Failed to read value." , error.toException());
            }
        });
    }
}