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

public class JoinActivity extends AppCompatActivity {
    EditText eTCode;
    Context ctx = this;
    String code;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);
        eTCode = findViewById(R.id.eTCode);
    }

    public void joinGame (View view) {
        code = eTCode.getText().toString();

        if (code.isEmpty()) {
            Toast.makeText(ctx , "Please enter a game code" , Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Game/" + code);

        // Read from the database
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                Game value = dataSnapshot.getValue(Game.class);
                if (value==null || value.getPlayer2() != null){
                    Toast.makeText(JoinActivity.this , "Game not found", Toast.LENGTH_SHORT).show();
                    return;
                }
                value.setPlayer2(User.getCurrent());
                myRef.setValue(value);
                Intent intent = new Intent(ctx , GameActivity.class);
                intent.putExtra("code" , code);
                startActivity(intent);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("TAG", "Failed to read value.", error.toException());
            }
        });
    }

    public void clickBack (View view) {
        Intent intent = new Intent(this , MenuActivity.class);
        startActivity(intent);
    }
}