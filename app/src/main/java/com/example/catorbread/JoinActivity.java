package com.example.catorbread;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import javax.annotation.Nonnull;

public class JoinActivity extends AppCompatActivity {
    EditText eTCode;
    Context ctx = this;
    String code;
    boolean flag;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);
        eTCode = findViewById(R.id.eTCode);
    }

    public void joinGame (View view) {
        code = eTCode.getText().toString();
        if (code.isEmpty()) {
            Toast.makeText(ctx , "Please enter a game code." , Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Games/" + code);
        flag = false;
        myRef.addListenerForSingleValueEvent(new ValueEventListener () {
            @Override
            public void onDataChange (@Nonnull DataSnapshot dataSnapshot) {
                if (flag) {
                    return;
                }
                flag = true;
                Game value = dataSnapshot.getValue(Game.class);
                if (value == null) {
                    Toast.makeText(JoinActivity.this , "Game not found." , Toast.LENGTH_SHORT).show();
                    return;
                } else if (value.getPlayer2() != null) {
                    Toast.makeText(JoinActivity.this , "Game is full." , Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent (ctx , GameActivity.class);
                intent.putExtra("role" , "guest");
                intent.putExtra("code" , code);
                startActivity(intent);
                finish();
            }

            @Override
            public void onCancelled (@Nonnull  DatabaseError error) {
                Log.w("TAG" , "Failed to read value." , error.toException());
            }
        });
    }

    public void back (View view) {
        finish();
    }
}