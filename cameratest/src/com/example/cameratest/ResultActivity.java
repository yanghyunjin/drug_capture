package com.example.cameratest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;
import java.util.concurrent.ExecutionException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ResultActivity extends Activity {

	private static final int HTTP_STATUS_OK = 200;
	ArrayList<JSONObject> resultList = new ArrayList<JSONObject>();
	InputStream is = null;
	String color, shape, ushape, mark, split;
	String line = null;
	String result = null;
	Intent intent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.result_list);

		color = "연두";
		shape = "타원형";
		ushape = "정제";
		mark = "";
		split = "";
		findViewById(R.id.button1).setOnClickListener(mClickListener);

		System.out.println("GetPillData().execute ?????????.");

	    try {
	      Boolean z = new GetPillData().execute((Void) null).get();
	      System.out.println("dddddddddddddddddddddddz" + z);
	  		LinearLayout topLL = (LinearLayout) findViewById(R.id.dynamicArea);
	  		for (int i = 0; i < resultList.size(); i++) {
	  			
	  			TextView t = new TextView(ResultActivity.this);
	  			t.setLayoutParams(new LinearLayout.LayoutParams(
	  			    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
	  			t.setBackgroundColor(Color.parseColor("#00FFFFFF"));
	  			t.setPadding(20, 10, 10, 10);
	  			t.setText(resultList.get(i).optString("pname"));
	  			topLL.addView(t);
	  		}
      } catch (InterruptedException e) {
	      // TODO Auto-generated catch block
	      e.printStackTrace();
      } catch (ExecutionException e) {
	      // TODO Auto-generated catch block
	      e.printStackTrace();
      }
	 
		
	}
	

	
	/* *******************************************************
	 * GetPillData Class!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	 * *******************************************************/
	class GetPillData extends AsyncTask<Void, Void, Boolean> {
		String[][] parsedData;
		private void postData(String color, String shape, String ushape, String mark,
		    String split) throws ClientProtocolException, IOException {
			System.out.println("????????? ????????????");
			makeHttpPost(color, shape, ushape, mark, split,
			    "http://192.168.43.189:8080/test/parsing/list.do");

		}

		private void makeHttpPost(String color, String shape, String ushape,
		    String mark, String split, String url) {
			// TODO Auto-generated method stub
			System.out.println("url ???????????????.");
			HttpClient httpclient = null;
			HttpParams params;
			
			try {
				httpclient = new DefaultHttpClient();
				params = httpclient.getParams();
				
				HttpConnectionParams.setConnectionTimeout(params, 7000);
				HttpConnectionParams.setSoTimeout(params, 7000);
				
				HttpPost httppost = new HttpPost(url);
				Vector<NameValuePair> nameValue = new Vector<NameValuePair>();
				nameValue.add(new BasicNameValuePair("color", color));
				nameValue.add(new BasicNameValuePair("shape", shape));
				nameValue.add(new BasicNameValuePair("ushape", ushape));
				nameValue.add(new BasicNameValuePair("mark", mark));
				nameValue.add(new BasicNameValuePair("split", split));
				
				httppost.setEntity(makeEntity(nameValue));
				
				System.out.println("...."+httppost.getURI());
				
				// ??????????????? ?????? ????????? ??????.
				HttpResponse response = httpclient.execute(httppost);
				System.out.println(response.toString());
				// ??????????????? ????????? ???????????? ?????? ??????.
				StatusLine status = response.getStatusLine();
				if(status.getStatusCode() != HTTP_STATUS_OK) {
					throw new Exception("Invalid response from server : " + status.toString());
				}

				// ??????????????? ????????? ??????.
				HttpEntity entity = response.getEntity();
				is = entity.getContent();
				System.out.println("??????!"+ is);
				Log.e("pass 1", "connection success ");
			} catch (Exception e) {
				Log.e("Fail 1", e.toString());
				Toast.makeText(getApplicationContext(), "Invalid IP Address",
				    Toast.LENGTH_LONG).show();
			}

			try {
				// ???????????? ??????.
				BufferedReader reader = new BufferedReader(new InputStreamReader(is,
				    "UTF-8"), 8);
				StringBuilder sb = new StringBuilder();
				while ((line = reader.readLine()) != null) {
					sb.append(line + "\n");
				}
				is.close();
				result = sb.toString();
				Log.e("pass 2", "connection success " + result);
				jsonParserList(result);
				
	    	System.out.println("zzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz"+resultList.size());

				
			} catch (Exception e) {
				Log.e("Fail 2", e.toString());
			} finally {
				httpclient.getConnectionManager().shutdown();
				System.out.println("ResultActivity.this.finish()");
				//ResultActivity.this.finish();
			}

		}

		public void jsonParserList(String result) {
			try {
				ArrayList<String> listdata = new ArrayList<String>();
				JSONObject json = new JSONObject(result);

				JSONArray jArr = json.getJSONArray("pillList");
				JSONArray sub;
				Log.e("pass 1.7","jsonParserList()?????????");
				String[] jsonName = { "pno", "pname", "pcompany", "pimg", "pcolor",
				    "pshape", "pushape", "pmarkfront", "pmarkback", "psplitfront",
				    "psplitback", "pingredient", "psafety", "peffect" };

				
				for(int i=0; i<jArr.length(); i++) {
					int idx = 0;
					JSONObject j = (JSONObject)jArr.get(i);
					resultList.add(j);
					Iterator iter = j.keys();
					while(iter.hasNext()) {
						String key = (String)iter.next();
						//System.out.println(j.get(key));
					}
				}
				

			} catch (Exception e) {

			}
			return;
		}

		private HttpEntity makeEntity(Vector<NameValuePair> nameValue) {
	    HttpEntity result = null;
	    try {
	    	result = new UrlEncodedFormEntity(nameValue, "UTF-8");
	    } catch(UnsupportedEncodingException e) {
	    	e.printStackTrace();
	    }
	    return result;
    }

		@Override
	  protected Boolean doInBackground(Void... params) {
	      try {
	        postData(color, shape, ushape, mark, split);
        } catch (ClientProtocolException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
        } catch (IOException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
        }
	      return null;
	  }
	}
	Button.OnClickListener mClickListener = new View.OnClickListener() {
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.button1:
				intent = new Intent(ResultActivity.this, SearchActivity.class); 
				startActivity(intent); 
				break;
			}
		}
	};
	  
}
