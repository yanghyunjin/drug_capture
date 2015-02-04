package com.example.cameratest;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;



public class Preview extends SurfaceView implements SurfaceHolder.Callback {

	
	private static final String TAG = "Camera Preview";
	private SurfaceHolder mHolder;
	private Camera mCamera;
	private byte[] FrameData = null;
	private int[] pixels = null;

	
	static {
		System.loadLibrary("imgeffects");
	}

	private static native void getFloodFilledBitmap(int width, int height,
			byte[] NV21FrameData, int[] pixels, int order[], int r_l[]);
	
	
	
	public Preview(Context context, Camera camera) {
		super(context);
		mCamera = camera;
		mHolder = getHolder();
		mHolder.addCallback(this);
		mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	public void surfaceCreated(SurfaceHolder holder) {
		try {
			mCamera.setPreviewDisplay(holder);
			mCamera.setDisplayOrientation(90);
			mCamera.startPreview();
		} catch (IOException exception) {
			Log.d(TAG,
					"Error setting camera preview : " + exception.getMessage());
		}
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		// mCamera.stopPreview();
		// mCamera.release();
		// mCamera = null;
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
		if (mHolder.getSurface() == null) {
			// preview surface does not exist
			return;
		}

		// stop preview before making changes
		try {
			mCamera.stopPreview();
		} catch (Exception e) {
			// ignore: tried to stop a non-existent preview
		}

		// set preview size and make any resize, rotate or
		// reformatting changes here

		// start preview with new settings
		try {
			mCamera.setPreviewDisplay(mHolder);
			mCamera.startPreview();

		} catch (Exception e) {
			Log.d(TAG, "Error starting camera preview: " + e.getMessage());
		}
	}

	public void getPicture(final Context context, final String mFileName) {
		mCamera.takePicture(null, null, new PictureCallback() {

			@Override
			public void onPictureTaken(byte[] data, Camera camera) {

				String filePath;
				long time = System.currentTimeMillis();
				SimpleDateFormat sdf = new SimpleDateFormat("ddHHmmss");

				Date dd = new Date(time);
				String strTime = sdf.format(dd);
				FileOutputStream out;

				String extr = Environment.getExternalStorageDirectory()
						.toString();

				File sdImageMainDirectory = new File(extr + "/Pictures/");
				sdImageMainDirectory.mkdir();
				ByteArrayInputStream stream = new ByteArrayInputStream(data);

				// ????????????????????????????????????
				FileOutputStream fileOutputStream = null;
				String nameFile = strTime;
				Bitmap bitmap = null;
				filePath = sdImageMainDirectory.toString() + "/" + nameFile
						+ mFileName;
				try {

					FrameData=data;	
					bitmap = BitmapFactory
							.decodeByteArray(data, 0, data.length);
					BitmapFactory.Options options = new BitmapFactory.Options();
					options.inSampleSize = 1;

					fileOutputStream = new FileOutputStream(filePath);
					BufferedOutputStream bos = new BufferedOutputStream(
							fileOutputStream);

					bitmap = rotate(bitmap, 90);
					
					int order[] = { 0, 0 };
					int r_l[] = { 0 };
					
					
					//getFloodFilledBitmap(bitmap.getWidth(),bitmap.getHeight(),FrameData,pixels, order, r_l);

					// bitmap = cropBitmap(bitmap,50,310,390,210);

					bitmap.compress(CompressFormat.JPEG, 100, bos);
					//bitmap.compress(CompressFormat.JPEG, 100, bos);

					fileOutputStream.flush();
					fileOutputStream.close();
					bos.flush();
					bos.close();

				} catch (Exception e) {
					e.printStackTrace();
				}
				mCamera.startPreview();
			}
		});

	}

	public Bitmap rotate(Bitmap bitmap, int degrees) {
		if (degrees != 0 && bitmap != null) {
			Matrix m = new Matrix();
			m.setRotate(degrees, (float) bitmap.getWidth() / 2,
					(float) bitmap.getHeight() / 2);

			try {
				Bitmap converted = Bitmap.createBitmap(bitmap, 0, 0,
						bitmap.getWidth(), bitmap.getHeight(), m, true);
				if (bitmap != converted) {
					bitmap.recycle();
					bitmap = converted;
				}
			} catch (OutOfMemoryError ex) {
			
			}
		}
		return bitmap;
	}

	public static Bitmap cropBitmap(Bitmap src, int x, int y, int w, int h) {
		if (src == null)
			return null;

		int width = src.getWidth();
		int height = src.getHeight();

		if (width < w && height < h)
			return src;

		int cw = w; // crop width
		int ch = h; // crop height

		if (w > width)
			cw = width;

		if (h > height)
			ch = height;

		return Bitmap.createBitmap(src, x, y, cw, ch);
	}



}
