package com.example.catorbread;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignUpActivity extends AppCompatActivity {
    EditText eTPassword , eTUsername;
    Context ctx = this;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        eTUsername = findViewById(R.id.username);
        eTPassword = findViewById(R.id.password);
    }

    public void clickCntn (View view) {
        String username = eTUsername.getText().toString();
        String password = eTPassword.getText().toString();
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(ctx , "Please fill all the fields." , Toast.LENGTH_SHORT).show();
            return;
        }
        User user = new User(username , password);
        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Users/" + user.getUsername());

        myRef.setValue(user);
        // Read from the database
        myRef.addValueEventListener(new ValueEventListener () {
            @Override
            public void onDataChange (DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                User value = dataSnapshot.getValue(User.class);
                if (value != null && value.getPassword().equals(user.getPassword())) {
                    Toast.makeText(ctx , "Success!" , Toast.LENGTH_SHORT).show();
                    clickBack(view);
                } else {
                    Toast.makeText(ctx , "Failed!" , Toast.LENGTH_SHORT).show();
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