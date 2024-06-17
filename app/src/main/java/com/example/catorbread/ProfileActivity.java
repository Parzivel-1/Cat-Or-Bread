package com.example.catorbread;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.content.Context;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import javax.annotation.Nonnull;

public class ProfileActivity extends AppCompatActivity {
    TextView tVscore , tVusername;
    ImageView iVprofile;
    Context ctx = this;
    boolean flag;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        tVscore = findViewById(R.id.score);
        tVusername = findViewById(R.id.username);
        iVprofile = findViewById(R.id.imageView);
        showProfile();
    }

    public void showProfile () {
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
                tVusername.setText(User.getCurrent());
                if (value == null) {
                    return;
                }
                tVscore.setText(value.getScore() + "");
            }

            @Override
            public void onCancelled (@NonNull DatabaseError error) {
                Log.w("TAG" , "Failed to read value." , error.toException());
            }
        });

        ChangePictureActivity.loadProfilePicture(ctx , iVprofile , FirebaseStorage.getInstance().getReference() , User.getCurrent());
    }

    public void back (View view) {
        finish();
    }
}