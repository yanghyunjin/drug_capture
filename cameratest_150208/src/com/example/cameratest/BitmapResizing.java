package com.example.cameratest;

import java.io.InputStream;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.widget.ImageView;

class BitmapResizing extends AsyncTask<String, Void, Bitmap> {
	ImageView bmImage;

	public BitmapResizing(ImageView bmImage) {
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
			mIcon = Bitmap.createScaledBitmap(mIcon, mIcon.getWidth() * 2,
			    mIcon.getHeight() * 2, true);
			mIcon = getRoundedCorverBitmap(mIcon);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return mIcon;
	}

	private Bitmap getRoundedCorverBitmap(Bitmap mIcon) {
		Bitmap output = Bitmap.createBitmap(mIcon.getWidth(), mIcon.getHeight(),
		    Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, mIcon.getWidth(), mIcon.getHeight());
		final RectF rectF = new RectF(rect);
		final float roundPx = 40;

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(mIcon, rect, rect, paint);

		return output;
	}

	@Override
	protected void onPostExecute(Bitmap result) {
		// TODO Auto-generated method stub
		// super.onPostExecute(result);
		bmImage.setImageBitmap(result);
	}
}