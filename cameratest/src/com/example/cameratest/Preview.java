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
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
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
	private int imageFormat;
	String filePath;
	String nameFile;

	Handler mHandler = new Handler(Looper.getMainLooper());
	
	static {
		System.loadLibrary("imgeffects");
	}

	private static native void getFloodFilledBitmap(int width, int height,
			byte[] NV21FrameData, int[] pixels);
	
	
	
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
		Parameters parameters;

		parameters = mCamera.getParameters();
		// Set the camera preview size
		parameters.setPreviewSize(w, h);

		imageFormat = parameters.getPreviewFormat();

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

				String extr = Environment.getExternalStorageDirectory()
						.toString();

				File sdImageMainDirectory = new File(extr + "/Pictures/");
				sdImageMainDirectory.mkdir();
				
				filePath = sdImageMainDirectory.toString() + "/" + nameFile
						+ mFileName;
				if (imageFormat == ImageFormat.NV21) {
					// We only accept the NV21(YUV420) format.
					FrameData=data;
					mHandler.post(DoImageProcessing);
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

	static public void decodeYUV420SP(int[] rgb, byte[] yuv420sp, int width, int height) {
	    final int frameSize = width * height;
	    
	    for (int j = 0, yp = 0; j < height; j++) {
	        int uvp = frameSize + (j >> 1) * width, u = 0, v = 0;
	        for (int i = 0; i < width; i++, yp++) {
	            int y = (0xff & ((int) yuv420sp[yp])) - 16;
	            if (y < 0) y = 0;
	            if ((i & 1) == 0) {
	                v = (0xff & yuv420sp[uvp++]) - 128;
	                u = (0xff & yuv420sp[uvp++]) - 128;
	            }
	            
	            int y1192 = 1192 * y;
	            int r = (y1192 + 1634 * v);
	            int g = (y1192 - 833 * v - 400 * u);
	            int b = (y1192 + 2066 * u);
	            
	            if (r < 0) r = 0; else if (r > 262143) r = 262143;
	            if (g < 0) g = 0; else if (g > 262143) g = 262143;
	            if (b < 0) b = 0; else if (b > 262143) b = 262143;
	            
	            rgb[yp] = 0xff000000 | ((r << 6) & 0xff0000) | ((g >> 2) & 0xff00) | ((b >> 10) & 0xff);
	        }
	    }
	}

	static public void encodeYUV420SP_original(byte[] yuv420sp, int[] rgba, int width, int height)
	{
	    final int frameSize = width * height;
	    
	    int[] U, V;
	    U = new int[frameSize];
	    V = new int[frameSize];
	    
	    int r, g, b, y, u, v;
	    for (int j = 0; j < height; j++)
	    {
	        int index = width * j;
	        for (int i = 0; i < width; i++)
	        {
	            r = (rgba[index] & 0xff000000) >> 24;
	            g = (rgba[index] & 0xff0000) >> 16;
	            b = (rgba[index] & 0xff00) >> 8;
	            
	            // rgb to yuv
	            y = (66 * r + 129 * g + 25 * b + 128) >> 8 + 16;
	            u = (-38 * r - 74 * g + 112 * b + 128) >> 8 + 128;
	            v = (112 * r - 94 * g - 18 * b + 128) >> 8 + 128;
	            
	            // clip y
	            yuv420sp[index++] = (byte) ((y < 0) ? 0 : ((y > 255) ? 255 : y));
	            U[index] = u;
	            V[index++] = v;
	        }
	    }
	}
	
	private Runnable DoImageProcessing = new Runnable() {
		public void run() {
			try {
				
				ByteArrayInputStream stream = new ByteArrayInputStream(FrameData);
				FileOutputStream fileOutputStream = null;
				
				Bitmap bitmap = null;
				Bitmap opencvBitmap = null;
				
				
				
				bitmap = BitmapFactory
						.decodeByteArray(FrameData, 0, FrameData.length);
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inSampleSize = 1;

				fileOutputStream = new FileOutputStream(filePath);
				BufferedOutputStream bos = new BufferedOutputStream(fileOutputStream);
				
				pixels = new int[bitmap.getWidth() * bitmap.getHeight()];

				bitmap = rotate(bitmap, 90);
				getFloodFilledBitmap(bitmap.getWidth(),bitmap.getHeight(),FrameData,pixels);
				
				opencvBitmap = Bitmap.createBitmap(bitmap.getWidth() , bitmap.getHeight(), Bitmap.Config.ARGB_8888);
				
				opencvBitmap.setPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
				
				
				//opencvBitmap = rotate(opencvBitmap, 90);
				// bitmap = cropBitmap(bitmap,50,310,390,210);
				//bitmap.compress(CompressFormat.JPEG, 100, bos);
				opencvBitmap.compress(CompressFormat.JPEG, 100, bos);

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
