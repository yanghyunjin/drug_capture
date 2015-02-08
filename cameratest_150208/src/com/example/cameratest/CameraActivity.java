package com.example.cameratest;

import com.example.cameratest.R;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

public class CameraActivity extends Activity {
	
	boolean aniF, capF = false;
	
	private Preview mPreview;
	private Camera mCamera;
	Context context;
	ImageView scanBarImageView;
	ImageView runnynoseImageView;
	AnimationDrawable scanBarAnimation;
	AnimationDrawable runnynoseAnimation;
	CustomAnimationDrawableNew animationTimer;
	int Color;
	int Shape;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
		
		context = this;
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
	
		setContentView(R.layout.activity_camera);

		// Create an instance of Camera
		mCamera = getCameraInstance();

		// Create our Preview view and set it as the content of our activity.
		mPreview = new Preview(this, mCamera);
		final FrameLayout frameLayout = (FrameLayout) findViewById(R.id.camera_preview);
		frameLayout.addView(mPreview);

		mPreview.setOnClickListener(new Preview.OnClickListener() {
			public void onClick(View v) {
				mCamera.autoFocus(mAutoFocus);
			}

		});

		ImageView background = new ImageView(this);
		background.setImageResource(R.drawable.background_withtext);
		background.setLayoutParams(new LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT));
		
		background.setAlpha(200);
		frameLayout.addView(background);

		
		scanBarImageView = new ImageView(this);
		scanBarImageView.setBackgroundResource(R.anim.scan_animation);
		frameLayout.addView(scanBarImageView);
		ViewGroup.MarginLayoutParams scanbar_margin = new ViewGroup.MarginLayoutParams(scanBarImageView.getLayoutParams());
		scanbar_margin.setMargins(150, 725, 145, 340);
		scanBarImageView.setLayoutParams(new FrameLayout.LayoutParams(scanbar_margin));
		
		scanBarAnimation = (AnimationDrawable) scanBarImageView.getBackground();
		scanBarAnimation.setOneShot(true);
		
		animationTimer = new CustomAnimationDrawableNew(scanBarAnimation) {
            @Override
            void onAnimationEnd() {
            	aniF = true;
            	if(capF) {
        				goFinish();
        			}
            }
        };


		runnynoseImageView = new ImageView(this);
		runnynoseImageView.setBackgroundResource(R.anim.nose_animation);
		frameLayout.addView(runnynoseImageView);
		runnynoseImageView.setAlpha(200);
		ViewGroup.MarginLayoutParams margin2 = new ViewGroup.MarginLayoutParams(runnynoseImageView.getLayoutParams());
		margin2.setMargins(400, 450, 240, 660);
		runnynoseImageView.setLayoutParams(new FrameLayout.LayoutParams(margin2));
		runnynoseImageView.setOnClickListener(imageViewClickListener);
		
		runnynoseAnimation = (AnimationDrawable) runnynoseImageView.getBackground();
		runnynoseAnimation.setOneShot(true);
		

	}

	public void logSensorData(View view) {
		Toast.makeText(getApplicationContext(), "Logged", Toast.LENGTH_SHORT)
				.show();
	}

	@Override
	protected void onPause() {
		super.onPause();
		releaseCamera();
		runnynoseAnimation.stop();
		scanBarAnimation.stop();
		animationTimer.stop();
	}

	/** A safe way to get an instance of the Camera object. */
	public static Camera getCameraInstance() {
		Camera c = null;
		try {
			c = Camera.open();
		} catch (Exception e) {
			// Camera is not available (in use or does not exist)
		}
		return c; // returns null if camera is unavailable
	}
	private void goFinish() {
		System.out.println("zzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz");
		Intent intent = new Intent(this,FragmentTab1.class);
		intent.putExtra("results", mPreview.getResults());
		setResult(1,intent);
		finish();
  }
	
	private void releaseCamera() {
		if (mCamera != null) {
			mCamera.release(); // release the camera for other applications
			mCamera = null;
		}
	}

	AutoFocusCallback mAutoFocus = new AutoFocusCallback() {
		public void onAutoFocus(boolean success, Camera camera) {
		}

	};

	private class CaptureThread extends Thread {

		@Override
		public void run() {
			int count = 0;
			while (count < 3) {
				String mFileName = "_" + count + ".jpg";
				mPreview.getPicture(context, mFileName);
				count++;

				try {
					Thread.sleep(2000);
				} catch (InterruptedException exception) {
					exception.printStackTrace();
				}
			}
			capF = true;
			if(aniF) {
				goFinish();
			}
			
		}
	}
	
	ImageView.OnClickListener imageViewClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			animationTimer.stop();
			animationTimer.start();

			scanBarAnimation.stop();
			scanBarAnimation.start();
			
			runnynoseAnimation.stop();
			runnynoseAnimation.start();
			
			CaptureThread captureThread = new CaptureThread();
			captureThread.start();

		}
	}; 
}
