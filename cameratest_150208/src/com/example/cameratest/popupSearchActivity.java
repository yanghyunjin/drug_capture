package com.example.cameratest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

public class popupSearchActivity extends Activity {
	
	Intent intent;
	String color="", shape="", ushape="", mark="", split="";
	EditText input;
	@SuppressWarnings("deprecation")
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.popup_activity_search);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,
		    WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
		input = (EditText)findViewById(R.id.popup_mark);
		intent = getIntent();

		if(intent.getExtras().containsKey("color")) {
			color = intent.getExtras().getString("color");
		}
		if(intent.getExtras().containsKey("shape")) {
			shape = intent.getExtras().getString("shape");
		}
		if(intent.getExtras().containsKey("ushape")) {
			ushape = intent.getExtras().getString("ushape");
		}
		if(intent.getExtras().containsKey("mark")) {
			mark = intent.getExtras().getString("mark");
		}
		if(intent.getExtras().containsKey("split")) {
			split = intent.getExtras().getString("split");
		}
		
		
		input.setText(mark);

		setSpinner(R.id.popup_ushape,R.array.spinnerArray1,R.array.spinnerArray1,android.R.layout.simple_spinner_dropdown_item,ushape);
		setSpinner(R.id.popup_shape,R.array.spinnerArray2,R.array.spinnerArray2,android.R.layout.simple_spinner_dropdown_item,shape);
		setSpinner(R.id.popup_color,R.array.spinnerArray3,R.array.spinnerArray3,android.R.layout.simple_spinner_dropdown_item,color);
		setSpinner(R.id.popup_split,R.array.spinnerArray4,R.array.spinnerArray4,android.R.layout.simple_spinner_dropdown_item,split);
		
		findViewById(R.id.popup_btn).setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				System.out.println(input.getText().toString());
				input.setText(input.getText());
				System.out.println(input.getText());
				process();
				finish();
			}

			private void process() {
	      Intent resultIntent = new Intent(popupSearchActivity.this, FragmentTabs.class);
	      resultIntent.putExtra("mark", input.getText().toString());
	      resultIntent.putExtra("color", color);
	      resultIntent.putExtra("shape", shape);
	      resultIntent.putExtra("ushape", ushape);
	      resultIntent.putExtra("split",split);
	      setResult(2, resultIntent);
      }
		});

		getSpinner(R.id.popup_ushape).setOnItemSelectedListener(
		    new OnItemSelectedListener() {

			    @Override
			    public void onItemSelected(AdapterView<?> parent, View view,
			        int position, long id) {
				    printChecked(view, position);

			    }

			    @Override
			    public void onNothingSelected(AdapterView<?> parent) {
				    // TODO Auto-generated method stub

			    }

		    });

		getSpinner(R.id.popup_shape).setOnItemSelectedListener(
		    new OnItemSelectedListener() {

			    @Override
			    public void onItemSelected(AdapterView<?> parent, View view,
			        int position, long id) {
				    printChecked(view, position);

			    }

			    @Override
			    public void onNothingSelected(AdapterView<?> parent) {
				    // TODO Auto-generated method stub

			    }

		    });

		getSpinner(R.id.popup_color).setOnItemSelectedListener(
		    new OnItemSelectedListener() {

			    @Override
			    public void onItemSelected(AdapterView<?> parent, View view,
			        int position, long id) {
			    	System.out.println("color position : "+position);
				    printChecked(view, position);

			    }

			    @Override
			    public void onNothingSelected(AdapterView<?> parent) {
				    // TODO Auto-generated method stub

			    }

		    });
		getSpinner(R.id.popup_split).setOnItemSelectedListener(
		    new OnItemSelectedListener() {

			    @Override
			    public void onItemSelected(AdapterView<?> parent, View view,
			        int position, long id) {
				    printChecked(view, position);

			    }

			    @Override
			    public void onNothingSelected(AdapterView<?> parent) {
				    // TODO Auto-generated method stub

			    }

		    });

	}


	public void setSpinner(int objId, int optionLabelId, int optionId,
	    int listStyle, String defaultVal) {
		String[] optionLavala = getResources().getStringArray(optionLabelId);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
		    android.R.layout.simple_spinner_item, optionLavala);
		if (listStyle > -1) {
			adapter.setDropDownViewResource(listStyle);
			Spinner obj = (Spinner) findViewById(objId);
			obj.setAdapter(adapter);
			if (defaultVal != null) {
				String[] optiona = getResources().getStringArray(optionId);
				int thei = 0;
				for (int a = 0; a < optiona.length; a++) {
					if (defaultVal.equals(optiona[a])) {
						thei = a;
						break;
					}
				}
				obj.setSelection(adapter.getPosition(optionLavala[thei]));
			} else {
				obj.setSelection(adapter.getPosition(defaultVal));
			}
		}

	}

	public void printChecked(View v, int position) {
		Spinner sp1 = (Spinner) findViewById(R.id.popup_ushape);
		Spinner sp2 = (Spinner) findViewById(R.id.popup_shape);
		Spinner sp3 = (Spinner) findViewById(R.id.popup_color);
		Spinner sp4 = (Spinner) findViewById(R.id.popup_split);

		if (sp1.getSelectedItemPosition() >= 0) {
			String resultText = (String) sp1.getAdapter().getItem(
			    sp1.getSelectedItemPosition());
			ushape = resultText;
		}
		if (sp2.getSelectedItemPosition() >= 0) {
			String resultText = (String) sp2.getAdapter().getItem(
			    sp2.getSelectedItemPosition());
			shape = resultText;
		}
		if (sp3.getSelectedItemPosition() >= 0) {
			String resultText = (String) sp3.getAdapter().getItem(
			    sp3.getSelectedItemPosition());
			color = resultText;
		}
		if (sp4.getSelectedItemPosition() >= 0) {
			String resultText = (String) sp4.getAdapter().getItem(
			    sp4.getSelectedItemPosition());
			split = resultText;
		}

	}
	
	public Spinner getSpinner(int objId){
		return (Spinner) findViewById(objId);
	}

	public String getSpinnerVal(int objId){
		return getSpinnerVal(objId, getResources().getStringArray(objId));
	}


  public String getSpinnerVal(int objId, String[] optiona) {
		String rtn = "";
		Spinner sp = ((Spinner) findViewById(objId));
		if (sp != null) {
			int selectedIndex = sp.getSelectedItemPosition();
			if (optiona == null) {
				rtn = "" + selectedIndex;
			} else {
				if (optiona.length > selectedIndex) {
					rtn = optiona[selectedIndex];
				}
			}
		}
		System.out.println("rtn : " +rtn);
		return rtn;
	}

}
