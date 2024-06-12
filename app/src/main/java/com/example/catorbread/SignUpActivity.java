package com.example.catorbread;

import android.app.AlertDialog;
import android.content.Context;
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

import javax.annotation.Nonnull;

public class SignUpActivity extends AppCompatActivity {
    EditText eTUsername , eTPassword , eTConfirmPassword;
    Context ctx = this;
    boolean flag;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        eTUsername = findViewById(R.id.username);
        eTPassword = findViewById(R.id.password);
        eTConfirmPassword = findViewById(R.id.confirmPassword);
    }

    public void signUp (View view) {
        String username = eTUsername.getText().toString();
        String password = eTPassword.getText().toString();
        String confirmPassword = eTConfirmPassword.getText().toString();
        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(ctx , "Please fill all the fields." , Toast.LENGTH_SHORT).show();
            return;
        } else if (!password.equals(confirmPassword)) {
            Toast.makeText(ctx , "Passwords do not match." , Toast.LENGTH_SHORT).show();
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder (this);
        builder.setMessage("You're about to create an account named:\n" + username + "\n\nAre you sure?");
        builder.setCancelable(true);
        builder.setPositiveButton("Yes" , (dialog , which) -> createUser(username , password));
        builder.setNegativeButton("No" , (dialog , which) -> dialog.cancel());
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void createUser (String username , String password) {
        User user = new User (username , password , 0);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Users/" + user.getUsername());
        flag = false;
        myRef.addValueEventListener(new ValueEventListener () {
            @Override
            public void onDataChange (@Nonnull DataSnapshot dataSnapshot) {
                if (flag) {
                    return;
                }
                flag = true;
                User value = dataSnapshot.getValue(User.class);
                if (value == null) {
                    myRef.setValue(user);
                    Toast.makeText(ctx , "User created!" , Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(ctx , "Username already exists!" , Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled (@Nonnull DatabaseError error) {
                Log.w("TAG" , "Failed to read value." , error.toException());
            }
        });
    }

    public void back (View view) {
        finish();
    }
}