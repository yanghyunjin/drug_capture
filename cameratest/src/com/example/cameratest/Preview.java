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
import android.graphics.Matrix;
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
	private int[] pixels = null;
	private int[] srcImage = null;
	private int imageFormat;
	private int width;
	private int height;
	String filePath;
	String nameFile;

	Handler mHandler = new Handler(Looper.getMainLooper());

	static {
		System.loadLibrary("imgeffects");
	}

	private static native void getFloodFilledBitmap(int width, int height,
			int[] srcImage, int[] pixels);

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
				bitmap = cropBitmap(bitmap, 50, 310, 390, 210);

				srcImage = new int[bitmap.getWidth() * bitmap.getHeight()];
				pixels = new int[bitmap.getWidth() * bitmap.getHeight()];
				int cnt = 0;

				for (int j = 0; j < bitmap.getHeight(); j++) {
					for (int i = 0; i < bitmap.getWidth(); i++) {
						srcImage[cnt++] = bitmap.getPixel(i, j);
					}
				}

				getFloodFilledBitmap(bitmap.getWidth(),bitmap.getHeight(),srcImage,pixels);

				Bitmap opencvBitmap = null;
				opencvBitmap = Bitmap.createBitmap(bitmap.getWidth(),bitmap.getHeight(), Bitmap.Config.RGB_565);
				opencvBitmap.setPixels(pixels, 0, bitmap.getWidth(), 0, 0,bitmap.getWidth(), bitmap.getHeight());
				fileOutputStream = new FileOutputStream(filePath);
				BufferedOutputStream bos = new BufferedOutputStream(fileOutputStream);
				opencvBitmap.compress(CompressFormat.JPEG, 100, bos);

				// opencvBitmap = rotate(opencvBitmap, 90);

				// bitmap.compress(CompressFormat.JPEG, 100, bos);

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
