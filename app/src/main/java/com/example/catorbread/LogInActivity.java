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

public class LogInActivity extends AppCompatActivity {
    EditText eTUsername , eTPassword;
    Context ctx = this;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        // ctx = this;
        eTPassword = findViewById(R.id.password);
        eTUsername = findViewById(R.id.username);
    }

    public void clickCntn (View view) {
        String username = eTUsername.getText().toString();
        String password = eTPassword.getText().toString();
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this , "Please fill all the fields." , Toast.LENGTH_SHORT).show();
            return;
        }
        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Users/" + username);
        // Read from the database
        myRef.addValueEventListener(new ValueEventListener () {
            @Override
            public void onDataChange (DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                User value = dataSnapshot.getValue(User.class);
                if (value != null && value.getPassword().equals(password)) {
                    User.setCurrent(value.getUsername());
                    Intent intent = new Intent(ctx , MenuActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(ctx , "Wrong!" , Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled (DatabaseError error) {
                Log.w("TAG" , "Failed to read value." , error.toException());
            }
        });
    }

    public void clickBack (View view) {
        Intent intent = new Intent(this , MainActivity.class);
        startActivity(intent);
    }
}