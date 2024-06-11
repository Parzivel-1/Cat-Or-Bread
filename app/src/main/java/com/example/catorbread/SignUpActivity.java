package com.example.catorbread;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import javax.annotation.Nonnull;

public class SignUpActivity extends AppCompatActivity {
    EditText eTPassword , eTUsername;
    Context ctx = this;
    boolean flag;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        eTUsername = findViewById(R.id.username);
        eTPassword = findViewById(R.id.password);
        setSupportActionBar(findViewById(R.id.Toolbar));
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        getMenuInflater().inflate(R.menu.log_in_menu , menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected (@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        int id = item.getItemId();
        if (id == R.id.back) {
            back();
            return true;
        } else if (id == R.id.exit) {
            finish();
            System.exit(0);
            return true;
        }
        return false;
    }

    public void clickCntn (View view) {
        String username = eTUsername.getText().toString();
        String password = eTPassword.getText().toString();
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(ctx , "Please fill all the fields." , Toast.LENGTH_SHORT).show();
            return;
        }
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
                    back();
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

    public void back () {
        Intent intent = new Intent(this , StartActivity.class);
        startActivity(intent);
        finish();
    }
}