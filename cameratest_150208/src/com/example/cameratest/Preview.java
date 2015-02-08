package com.example.cameratest;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.Size;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class Preview extends SurfaceView implements SurfaceHolder.Callback {

	private static final String TAG = "Camera Preview";
	private SurfaceHolder mHolder;
	private Camera mCamera;
	private byte[] FrameData = null;
	private int[] srcImage = null;
	private int[] results = null;
	public int[] getResults() {
		return results;
	}

	public void setResults(int[] results) {
		this.results = results;
	}

	private int imageFormat;
	private int width;
	private int height;
	String filePath;
	String collection_filePath;
	String nameFile;
	
	
	Handler mHandler = new Handler(Looper.getMainLooper());

	static {
		System.loadLibrary("imgeffects");
	}

	private static native void getFloodFilledBitmap(int width, int height,
			int[] srcImage,int[] results);

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
			Log.d(TAG,"Error setting camera preview : " + exception.getMessage());
		}
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		// mCamera.stopPreview();
		// mCamera.release();
		// mCamera = null;
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
		Parameters parameters;
		parameters = mCamera.getParameters();
		Size size = parameters.getPreviewSize();
		this.height = size.height;
		this.width = size.width;
		mCamera.setParameters(parameters);
		mCamera.startPreview();

	}

	public void getPicture(final Context context, final String mFileName) {
		mCamera.takePicture(null, null, new PictureCallback() {

			@Override
			public void onPictureTaken(byte[] data, Camera camera) {

				long time = System.currentTimeMillis();
				SimpleDateFormat sdf = new SimpleDateFormat("ddHHmmss");

				Date dd = new Date(time);
				String strTime = sdf.format(dd);
				nameFile = strTime;

				String extr = Environment.getExternalStorageDirectory().toString();

				File sdImageMainDirectory = new File(extr + "/Pictures/");
				sdImageMainDirectory.mkdir();

				filePath = sdImageMainDirectory.toString() + "/" + nameFile + mFileName;
				collection_filePath = sdImageMainDirectory.toString() + "/drug_collection.jpg";

				FrameData = data;
				mHandler.post(DoImageProcessing);

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

	private Runnable DoImageProcessing = new Runnable() {
		public void run() {
			try {

				FileOutputStream fileOutputStream = null;

				Bitmap bitmap = null;
				bitmap = BitmapFactory.decodeByteArray(FrameData, 0,FrameData.length);
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inSampleSize = 1;
				bitmap = rotate(bitmap, 90);
				//bitmap = cropBitmap(bitmap, 50, 310, 390, 210);
				

				srcImage = new int[bitmap.getWidth() * bitmap.getHeight()];
				results = new int[2]; 
				
				int cnt=0;
				for (int j = 0; j < bitmap.getHeight(); j++) {
					for (int i = 0; i < bitmap.getWidth(); i++) {
						srcImage[cnt++] = bitmap.getPixel(i, j);
					}
				}
				
				fileOutputStream = new FileOutputStream(filePath);
				BufferedOutputStream bos = new BufferedOutputStream(fileOutputStream);
				

				getFloodFilledBitmap(bitmap.getWidth(),bitmap.getHeight(),srcImage,results);
				
				if(results[0]==0){
					Log.d(TAG,"Results : 알약검출실패");
				}
				else if(results[0]==1){
					Log.d(TAG,"Results : 원");
				}
				else if(results[0]==2){
					Log.d(TAG,"Results : 타원 / 장방");
				}
				
				if(results[1]==0){
					Log.d(TAG,"Results : 흰색/회색");
				}
				else if(results[1]==1){
					Log.d(TAG,"Results : 검정");
				}
				else if(results[1]==2){
					Log.d(TAG,"Results : 파랑");
				}
				else if(results[1]==3){
					Log.d(TAG,"Results : 초록");
				}
				else if(results[1]==4){
					Log.d(TAG,"Results : 빨강");
				}
				else if(results[1]==5){
					Log.d(TAG,"Results : 노랑");
				}

				
//				opencvBitmap.compress(CompressFormat.JPEG, 100, bos);

			

				bitmap.compress(CompressFormat.JPEG, 100, bos);

				fileOutputStream.flush();
				fileOutputStream.close();
				bos.flush();
				bos.close();

			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	};
	

}
