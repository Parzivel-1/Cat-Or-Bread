package com.example.catorbread;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.InputStream;

public class DownloadImageTask extends AsyncTask <String , Void , Bitmap> {
    ImageView imageView;

    public DownloadImageTask (ImageView imageView) {
        this.imageView = imageView;
    }

    public Bitmap doInBackground (String ... urls) {
        String imageUrl = urls[0];
        Bitmap bitmap = null;
        try {
            InputStream in = new java.net.URL (imageUrl).openStream();
            bitmap = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public void onPostExecute (Bitmap result) {
        if (result != null) {
            imageView.setImageBitmap(result);
        }
    }
}