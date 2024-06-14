package com.example.catorbread;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import javax.annotation.Nonnull;

public class ChangePasswordActivity extends AppCompatActivity {
    EditText currentPassword;
    EditText newPassword;
    EditText confirmPassword;
    Context ctx = this;
    boolean flag;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        currentPassword = findViewById(R.id.currentPassword);
        newPassword = findViewById(R.id.newPassword);
        confirmPassword = findViewById(R.id.confirmPassword);
    }

    public void changePassword (View view) {
        currentPassword = findViewById(R.id.currentPassword);
        String password = currentPassword.getText().toString();
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
                User value = dataSnapshot.getValue(User.class);
                if (value != null && value.getPassword().equals(password)) {
                    if (newPassword.getText().toString().equals(confirmPassword.getText().toString())) {
                        myRef.child("password").setValue(newPassword.getText().toString());
                        Toast.makeText(ctx , "Password changed successfully!" , Toast.LENGTH_SHORT).show();
                        SharedPreferences sharedPreferences = getSharedPreferences("user" , MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("password" , password);
                        editor.apply();
                        back(view);
                    } else {
                        Toast.makeText(ctx , "Passwords do not match." , Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ctx , "Incorrect password!" , Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled (@NonNull DatabaseError error) {
                Log.w("TAG" , "Failed to read value." , error.toException());
            }
        });
    }

    public void back (View view) {
        finish();
    }
}