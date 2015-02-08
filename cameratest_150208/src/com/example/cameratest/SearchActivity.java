package com.example.cameratest;

import com.example.cameratest.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class SearchActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
			
			setContentView(R.layout.activity_search);
			
			String[] optionLavala=getResources().getStringArray(R.array.spinnerArray1);
			ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,optionLavala);
			Spinner obj = (Spinner)findViewById(R.id.Spinner01);
			obj.setAdapter(adapter);
			
			setSpinner(R.id.Spinner02,R.array.spinnerArray2,android.R.layout.simple_spinner_dropdown_item);
			setSpinner(R.id.Spinner03,R.array.spinnerArray3,android.R.layout.simple_spinner_dropdown_item);
			setSpinner(R.id.Spinner04,R.array.spinnerArray4,android.R.layout.simple_spinner_dropdown_item);
			
			getSpinner(R.id.Spinner01).setOnItemSelectedListener(new OnItemSelectedListener(){

				@Override
				public void onItemSelected(AdapterView<?> parent, View view,
						int position, long id) {
					printChecked(view,position);
					
				}

				@Override
				public void onNothingSelected(AdapterView<?> parent) {
					// TODO Auto-generated method stub
					
				}
				
			});
			
			getSpinner(R.id.Spinner02).setOnItemSelectedListener(new OnItemSelectedListener(){

				@Override
				public void onItemSelected(AdapterView<?> parent, View view,
						int position, long id) {
					printChecked(view,position);
					
				}

				@Override
				public void onNothingSelected(AdapterView<?> parent) {
					// TODO Auto-generated method stub
					
				}
				
			});
			
			getSpinner(R.id.Spinner03).setOnItemSelectedListener(new OnItemSelectedListener(){

				@Override
				public void onItemSelected(AdapterView<?> parent, View view,
						int position, long id) {
					printChecked(view,position);
					
				}

				@Override
				public void onNothingSelected(AdapterView<?> parent) {
					// TODO Auto-generated method stub
					
				}
				
			});
			getSpinner(R.id.Spinner04).setOnItemSelectedListener(new OnItemSelectedListener(){

				@Override
				public void onItemSelected(AdapterView<?> parent, View view,
						int position, long id) {
					printChecked(view,position);
					
				}

				@Override
				public void onNothingSelected(AdapterView<?> parent) {
					// TODO Auto-generated method stub
					
				}
				
			});
			
			
	}
	
	public void setSpinner(int objId,int optionLabelId,int listStyle){
		setSpinner(objId, optionLabelId,-1,listStyle,null);
	}
	public void setSpinner(int objId,int optionLabelId,int optionId, int listStyle, String defaultVal){
		String[] optionLavala = getResources().getStringArray(optionLabelId);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,optionLavala);
		if(listStyle > -1){
			adapter.setDropDownViewResource(listStyle);
			
			Spinner obj = (Spinner)findViewById(objId);
			obj.setAdapter(adapter);
			if(defaultVal != null){
				String[] optiona=getResources().getStringArray(optionId);
				int thei=0;
				for(int a=0;a<optiona.length;a++){
					if(defaultVal.equals(optiona[a])){
						thei=a;
						break;
					}
				}
				obj.setSelection(adapter.getPosition(optionLavala[thei]));
			}
			else{
				obj.setSelection(adapter.getPosition(defaultVal));
			}
		}
		
	}
	public void printChecked(View v,int position){
		Spinner sp1=(Spinner)findViewById(R.id.Spinner01);
		Spinner sp2=(Spinner)findViewById(R.id.Spinner02);
		Spinner sp3=(Spinner)findViewById(R.id.Spinner03);
		Spinner sp4=(Spinner)findViewById(R.id.Spinner04);
		String resultText="";
		
		if(sp1.getSelectedItemPosition()>0){
			resultText=(String)sp1.getAdapter().getItem(sp1.getSelectedItemPosition());
		}
		if(sp2.getSelectedItemPosition()>0){
			if(!"".equals(resultText))
				resultText+=",";
			resultText+=(String)sp2.getAdapter().getItem(sp2.getSelectedItemPosition());
		}
		if(sp3.getSelectedItemPosition()>0){
			if(!"".equals(resultText))
				resultText+=",";
			resultText+=(String)sp3.getAdapter().getItem(sp3.getSelectedItemPosition());
		}
		if(sp4.getSelectedItemPosition()>0){
			if(!"".equals(resultText))
				resultText+=",";
			resultText+=(String)sp4.getAdapter().getItem(sp4.getSelectedItemPosition());
		}
		
	}
	
	public Spinner getSpinner(int objId){
		return (Spinner) findViewById(objId);
	}
	public String getSpinnerVal(int objId){
		return getSpinneVal(objId,null);
	}
	private String getSpinneVal(int objId, String[] optiona){
		String rtn="";
		Spinner sp=((Spinner)findViewById(objId));
		if(sp!=null){
			int selectedIndex = sp.getSelectedItemPosition();
			if(optiona==null){
				rtn=""+selectedIndex;
			}
			else{
				if(optiona.length>selectedIndex){
					rtn=optiona[selectedIndex];
				}
			}
		}
		return rtn;
	}
}
