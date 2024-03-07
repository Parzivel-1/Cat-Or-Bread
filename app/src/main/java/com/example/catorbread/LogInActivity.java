package com.example.catorbread;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import javax.annotation.Nonnull;

public class LogInActivity extends AppCompatActivity {
    EditText eTUsername , eTPassword;
    Context ctx = this;
    boolean flag;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        eTPassword = findViewById(R.id.password);
        eTUsername = findViewById(R.id.username);
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
            Intent intent = new Intent(this , StartActivity.class);
            startActivity(intent);
            finish();
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
            Toast.makeText(this , "Please fill all the fields." , Toast.LENGTH_SHORT).show();
            return;
        }
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Users/" + username);
        flag = false;
        myRef.addValueEventListener(new ValueEventListener () {
            @Override
            public void onDataChange (@Nonnull DataSnapshot dataSnapshot) {
                if (flag) {
                    return;
                }
                flag = true;
                User value = dataSnapshot.getValue(User.class);
                if (value.getConnected()) {
                    Toast.makeText(ctx , "User already logged in!" , Toast.LENGTH_SHORT).show();
                    return;
                }
                if (value != null && value.getPassword().equals(password)) {
                    User.setCurrent(value.getUsername());
                    myRef.getRef().child("connected").setValue(true);
                    Intent intent = new Intent(ctx , MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(ctx , "Wrong username or password!" , Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled (@Nonnull DatabaseError error) {
                Log.w("TAG" , "Failed to read value." , error.toException());
            }
        });
    }
}