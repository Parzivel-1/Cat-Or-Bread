package com.example.catorbread;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.util.Objects;

public class ChangePictureActivity extends AppCompatActivity {
    static final int PICK_IMAGE_REQUEST = 1;
    static final int CAMERA_REQUEST = 2;
    static final int REQUEST_CAMERA_PERMISSION = 100;
    ImageView picture;
    Button backBtn;
    Uri imageUri;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    Context ctx = this;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_picture);
        picture = findViewById(R.id.imageView);
        backBtn = findViewById(R.id.cancel);
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();


        //        ActivityResultLauncher <Intent> cameraLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult () ,
        //                new ActivityResultCallback <ActivityResult> () {


        ActivityResultLauncher <Intent> galleryLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult () , result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        imageUri = result.getData().getData();
                        picture.setImageURI(imageUri);
                        uploadImageToFirebase();
                    }
                });

        ActivityResultLauncher <Intent> cameraLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult () , result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Bitmap bitmap = (Bitmap) Objects.requireNonNull(result.getData().getExtras()).get("data");
                        if (bitmap == null) {
                            return;
                        }
                        imageUri = getImageUri(bitmap);
                        picture.setImageBitmap(bitmap);
                        uploadImageToFirebase();
                    }
                });

        loadProfilePicture(ctx , picture , storageReference , User.getCurrent());
    }

    public void openGallery (View view) {
        Intent intent = new Intent ();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent , PICK_IMAGE_REQUEST);
    }

    public void openCamera (View view) {
        if (ContextCompat.checkSelfPermission(ChangePictureActivity.this , Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ChangePictureActivity.this , new String [] {Manifest.permission.CAMERA} , REQUEST_CAMERA_PERMISSION);
        } else {
            Intent intent = new Intent (MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent , CAMERA_REQUEST);
        }
    }

    @Override
    public void onRequestPermissionsResult (int requestCode , @NonNull String [] permissions , @NonNull int [] grantResults) {
        super.onRequestPermissionsResult(requestCode , permissions , grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera(null);
            } else {
                Toast.makeText(this , "Camera permission is required to use the camera!" , Toast.LENGTH_SHORT).show();
            }
        }
    }

    public Uri getImageUri (Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream ();
        bitmap.compress(Bitmap.CompressFormat.JPEG , 100 , bytes);
        String path = MediaStore.Images.Media.insertImage(getContentResolver() , bitmap , "Title" , null);
        return Uri.parse(path);
    }

    @Override
    public void onActivityResult (int requestCode , int resultCode , @Nullable Intent data) {
        super.onActivityResult(requestCode , resultCode , data);
        if (resultCode == Activity.RESULT_OK) {
            backBtn.setVisibility(View.INVISIBLE);
            if (requestCode == PICK_IMAGE_REQUEST && data != null && data.getData() != null) {
                imageUri = data.getData();
                picture.setImageURI(imageUri);
                uploadImageToFirebase();
            } else if (requestCode == CAMERA_REQUEST && data != null && data.getExtras() != null) {
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                if (bitmap == null) {
                    return;
                }
                imageUri = getImageUri(bitmap);
                picture.setImageURI(imageUri);
                uploadImageToFirebase();
            }
        }
    }

    public void uploadImageToFirebase () {
        if (imageUri != null) {
            StorageReference fileReference = storageReference.child("Images/" + User.getCurrent() + ".jpg");

            //             fileReference.putFile(imageUri)
            //                    .addOnSuccessListener(new OnSuccessListener <UploadTask.TaskSnapshot> () {
            //                        @Override
            //                        public void onSuccess (UploadTask.TaskSnapshot taskSnapshot) {

            fileReference.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        Toast.makeText(ChangePictureActivity.this , "Upload successful" , Toast.LENGTH_SHORT).show();
                        loadProfilePicture(ctx , picture , storageReference , User.getCurrent());
                        backBtn.setVisibility(View.VISIBLE);
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(ChangePictureActivity.this , "Upload failed: " + e.getMessage() , Toast.LENGTH_SHORT).show();
                        loadProfilePicture(ctx , picture , storageReference , User.getCurrent());
                        backBtn.setVisibility(View.VISIBLE);
                    });

            //                     .addOnFailureListener(new OnFailureListener () {
            //                        @Override
            //                        public void onFailure (@NonNull Exception e) {

        }
    }

    public static void loadProfilePicture (Context ctx , ImageView picture , StorageReference storageReference , String user) {
        StorageReference profilePicRef = storageReference.child("Images/" + user + ".jpg");

        //         profilePicRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener <Uri> () {
        //            @Override
        //            public void onSuccess (Uri uri) {

        profilePicRef.getDownloadUrl().addOnSuccessListener(uri -> new DownloadImageTask (picture).execute(uri.toString()))
                .addOnFailureListener(e -> Toast.makeText(ctx , "Failed to load profile picture" , Toast.LENGTH_SHORT).show());


        //         }).addOnFailureListener(new OnFailureListener () {
        //            @Override
        //            public void onFailure (@NonNull Exception e) {


    }

    public void back (View view) {
        finish();
    }
}
