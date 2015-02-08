package com.example.cameratest;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class DetailActivity extends Activity {

	Intent intent;
	JSONObject item;
	ImageView dImg;
	TextView name, company, color, split, shape, ushape, mark, safety, ingredient, effect;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detail_activity);

		intent = getIntent();

		try {
			item = new JSONObject(getIntent().getStringExtra("item"));
			dImg = (ImageView)findViewById(R.id.DetailDrugImg);
			new BitmapResizing(dImg).execute(item.optString("pimg"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		name = (TextView)findViewById(R.id.detail_name);
		company = (TextView)findViewById(R.id.detail_company);
		color = (TextView)findViewById(R.id.detail_color);
		split = (TextView)findViewById(R.id.detail_split);
		shape = (TextView)findViewById(R.id.detail_shape);
		ushape = (TextView)findViewById(R.id.detail_ushape);
		mark = (TextView)findViewById(R.id.detail_mark);
		safety = (TextView)findViewById(R.id.d_safety);
		ingredient = (TextView)findViewById(R.id.d_ingredient);
		effect = (TextView)findViewById(R.id.d_effect);
		
		name.setText(item.optString("pname"));
		company.setText(item.optString("pcompany"));
		color.setText(item.optString("pcolor"));
		
		String frontSplit="", backSplit="", frontMark="", backMark="";
		if(item.optString("psplitfront") != null)
			frontSplit = item.optString("pfrontsplit");
		if(item.optString("psplitback") != null)
			backSplit = item.optString("psplitback");
		if(item.optString("pmarkfront") != null)
			frontMark = item.optString("pmarkfront");
		if(item.optString("pmarkback") != null)
			backMark = item.optString("pmarkback");
		
		split.setText(frontSplit+backSplit);
		mark.setText(frontMark+backMark);
		
		shape.setText(item.optString("pshape"));
		ushape.setText(item.optString("pushape"));
		
		safety.setText(item.optString("psafety"));
		ingredient.setText(item.optString("pingredient"));
		effect.setText(item.optString("peffect"));
	}



}
