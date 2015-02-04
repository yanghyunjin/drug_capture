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
				// startActivityForResult(intent, 0);
				// startActivityForResult(intent, 0);
				// 넘겨주는 페이지에서는 Intent intent = new Intent();
				// intent.putExtra("key", value), setResult(RESULT_CODE, intent);
				// finish();
				break;
			case R.id.button2:
				intent = new Intent(MainActivity.this, SearchActivity.class); 
				startActivity(intent); 
				break;
			
			
			case R.id.button3:
				intent = new Intent(MainActivity.this, history.class); 
				startActivity(intent); 
				break;
			}
		}
	};
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	
			if(resultCode == 1) {
				Intent intent = new Intent(MainActivity.this, ResultActivity.class);
				startActivity(intent);
			}

	}
	/* protected void onActivityResult(int requestConde, int resultCode, Intent data) {
	 * 			super.onActvityResult(requestCode, resultCode, data);
	 * 			switch(resultcode) {
	 * 					case 1 : 
	 * 					String key = dta.getStringExtra("key");
	 *  			//// 신호 받아 실행할 작업 ///
	 *    		  break;
	 *    	}
	 * }
	*/		
	
	
}
