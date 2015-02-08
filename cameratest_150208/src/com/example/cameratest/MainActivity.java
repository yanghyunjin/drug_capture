package com.example.cameratest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {
	Intent intent;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
			
			setContentView(R.layout.activity_main);
			findViewById(R.id.button1).setOnClickListener(mClickListener);
			findViewById(R.id.button2).setOnClickListener(mClickListener);
			findViewById(R.id.button3).setOnClickListener(mClickListener);
			
	}
	Button.OnClickListener mClickListener = new View.OnClickListener() {
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.button1:
				intent = new Intent(MainActivity.this, CameraActivity.class); 
				startActivityForResult(intent, 1);
				break;
			case R.id.button2:
				intent = new Intent(MainActivity.this, SearchActivity.class); 
				startActivity(intent); 
				break;
			
			
			case R.id.button3:
				intent = new Intent(MainActivity.this, FragmentTabs.class); 
				startActivity(intent); 
				break;
			}
		}
	};
	
	
	
	
}
