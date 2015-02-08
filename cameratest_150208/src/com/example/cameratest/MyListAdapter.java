package com.example.cameratest;

import java.util.ArrayList;

import org.json.JSONObject;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MyListAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<JSONObject> list;
	
	public MyListAdapter(Context context, ArrayList<JSONObject> list) {
		this.context = context;
		this.list = list;
	}

	@Override
  public int getCount() {
	  // TODO Auto-generated method stub
	  return list.size();
  }

	@Override
  public Object getItem(int position) {
	  // TODO Auto-generated method stub
	  return list.get(position);
  }

	@Override
  public long getItemId(int position) {
	  // TODO Auto-generated method stub
	  return 0;
  }
	
	// 행의 index, 행 전체를 나타내는 뷰, 어댑터를 가지는 부모 뷰(ListView)
	@Override
  public View getView(int position, View convertView, ViewGroup parent) {
		ImageTextView imgTxtView;
		
	  if(convertView == null) { // 항목뷰가 한번도 보여진 적 없는 경우 레이아웃을 자바객체로 변환
	  	System.out.println("getView : convertView Null" + position);
	  	imgTxtView = new ImageTextView(context, list.get(position));
	  } else {
	  	imgTxtView = (ImageTextView)convertView;
	  	System.out.println("getView :" + convertView.getId()+" "+ position);
	  }
	 
	  JSONObject info = (JSONObject)list.get(position);
	  String safety = info.optString("psafety");
	  
	  System.out.println(position + " : " +safety);
	  if(safety.equals("A")) 
	  	imgTxtView.setIcon(context.getResources().getDrawable(R.drawable.a));
	  if(safety.equals("B")) 
	  	imgTxtView.setIcon(context.getResources().getDrawable(R.drawable.b));
	  if(safety.equals("C")) 
	  	imgTxtView.setIcon(context.getResources().getDrawable(R.drawable.c));
	  if(safety.equals("D")) 
	  	imgTxtView.setIcon(context.getResources().getDrawable(R.drawable.d));
	  if(safety.equals("X")) 
	  	imgTxtView.setIcon(context.getResources().getDrawable(R.drawable.x));
	  if(safety.equals("없음"))
	  	imgTxtView.setIcon(null);
	  
	  new ImageDownloader(imgTxtView.wImg).execute(info.optString("pimg"));
	  imgTxtView.setText(info.optString("pname"));
  	
		return imgTxtView;
  }

	class ImageTextView extends LinearLayout {
		private ImageView wImg;
		private TextView txt;
		private ImageView aImg;

		public ImageTextView(Context context , JSONObject jsonObject) {
			super(context);
			LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	  	inflater.inflate(R.layout.result_row, this, true);
	  	wImg = (ImageView)findViewById(R.id.result_img);
	  	txt = (TextView)findViewById(R.id.result_txt);
	  	txt.setText(jsonObject.optString("pname"));

	  	aImg = (ImageView)findViewById(R.id.result_arrow);
	  	
		}	

		public ImageView getwImg() {
			return wImg;
		}


		public TextView getTxt() {
			return txt;
		}


		public ImageView getaImg() {
			return aImg;
		}

		
		public void setText(String data) {
			txt.setText(data);
		}
		
		public void setIcon(Drawable icon) {
			if(icon!=null) {
			icon.setBounds( 0, -15, 50, 35 );
			txt.setCompoundDrawables( null, null, icon, null );
			txt.setCompoundDrawablePadding(20);
			} else {
				txt.setCompoundDrawables(null, null, null, null);
			}

		}

	}

	
}





	
