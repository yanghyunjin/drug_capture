package com.example.cameratest;

import java.io.InputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

class ImageDownloader extends AsyncTask<String, Bitmap, Bitmap>{
  ImageView bmImage;
  public ImageDownloader(ImageView bmImage) {
   // TODO Auto-generated constructor stub
   this.bmImage = bmImage;
  }

  @Override
  protected Bitmap doInBackground(String... params) {
   // TODO Auto-generated method stub
   String url = params[0];
   Bitmap mIcon = null;
   try {
    InputStream is = new java.net.URL(url).openStream();
    mIcon = BitmapFactory.decodeStream(is);
   } catch (Exception e) {
    // TODO: handle exception
   }
   return mIcon;
  }
  @Override
  protected void onPostExecute(Bitmap result) {
   // TODO Auto-generated method stub
   //super.onPostExecute(result);
   bmImage.setImageBitmap(result);
  }
 }