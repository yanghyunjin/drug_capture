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



import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class FragmentTab1 extends Fragment implements OnItemClickListener {
	
	private static final int HTTP_STATUS_OK = 200;
	MyListAdapter adapter;
	ArrayList<JSONObject> resultList = new ArrayList<JSONObject>();
	InputStream is = null;
	String color, shape, ushape, mark, split;
	String line = null;
	String result = null;
	ListView listview;
	View v;
	Context context; 

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		context=inflater.getContext();
		v = inflater.inflate(R.layout.result_list, container, false);
		System.out.println("1들어왔다!!!!");
		Intent intent = new Intent(getActivity(), CameraActivity.class);
		startActivityForResult(intent, 1);
		
		System.out.println("22들어왔다!!!!");
		

		return v;
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		color = shape = ushape = mark = "";
		split="������";
		int result[]=new int [2];
		
		if (resultCode == 1) {
			System.out.println("333들어왔다!!!!");
			result = data.getExtras().getIntArray("results");
			createResultView(result);
		}
		else if(resultCode == 2) {
			if(data.getExtras().containsKey("color")) {
				color = data.getExtras().getString("color");
			}
			if(data.getExtras().containsKey("shape")) {
				shape = data.getExtras().getString("shape");
			}
			if(data.getExtras().containsKey("ushape")) {
				ushape = data.getExtras().getString("ushape");
			}
			if(data.getExtras().containsKey("mark")) {
				mark = data.getExtras().getString("mark");
			}
			if(data.getExtras().containsKey("split")) {
				split = data.getExtras().getString("split");
			}
			System.out.println("color: " + color + ", " + "shape : " + shape + " , " + "ushape : " + ushape + " , "+ "mark : " + mark + " , "+ "split : " + split);
			try {
        new GetPillData().execute((Void) null).get();
        listview = null;
        listview = (ListView)v.findViewById(R.id.listView);
    		listview.setOnItemClickListener(this);
				System.out.println("resultList.size()-> " + resultList.size());
				adapter = new MyListAdapter(context, resultList);
				listview.setAdapter(adapter);
    } catch (InterruptedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    } catch (ExecutionException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    }
		}
			
	}

	private void createResultView(int results[]) {

		
		if(results[0]==0){
			shape = "";
		}
		else if(results[0]==1){
			shape = "원형";
		}
		else if(results[0]==2){
			shape = "타원형";
		}
		
		if(results[1]==0){
			color = "하양";
		}
		else if(results[1]==1){
			color = "검정";
		}
		else if(results[1]==2){
			color = "파랑";
		}
		else if(results[1]==3){
			color = "초록";
		}
		else if(results[1]==4){
			color = "빨강";
		}
		else if(results[1]==5){
			color = "노랑";
		}
		
		
		ushape = "정제";
		mark = "";
		split = "";

		System.out.println("GetPillData().execute ���������.");


		try {
			int databaseResult = new GetPillData().execute((Void) null).get();

			System.out.println(databaseResult);
			// databaseResult : 0->db��� ������������ ������ ������. 1->db��� ������������ 20��� ������. 2->db��� ������������ 20��� ������.

			switch (databaseResult) {
			case 0:
				break;
			case 1:
				listview = null;
				listview = (ListView) v.findViewById(R.id.listView);
				listview.setOnItemClickListener(this);
				System.out.println(resultList.size());
				adapter = new MyListAdapter(context, resultList);
				listview.setAdapter(adapter);
				break;
			case 2:
				DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
			    @Override
			    public void onClick(DialogInterface dialog, int which) {
			        Intent intent;
							switch (which){
			        case DialogInterface.BUTTON_POSITIVE:
			            System.out.println("��������������� ������");
			            intent = new Intent(context, popupSearchActivity.class); 
			            if(!color.equals("")) {
			            	intent.putExtra("color", color);
			            }
			            if(!shape.equals("")) {
			            	intent.putExtra("shape", shape);
			            }
			            if(!ushape.equals("")) {
			            	intent.putExtra("ushape", ushape);
			            }
			            if(!mark.equals("")) {
			            	intent.putExtra("mark", mark);
			            }
			            if(!split.equals("")) {
			            	intent.putExtra("split", split);
			            }
			    				startActivityForResult(intent, 2);
			    		
			            break;

			        case DialogInterface.BUTTON_NEGATIVE:
			        	System.out.println("������������ ������");
			        	//finish();
			          break;
			        }
			    }
			};

			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setMessage("��������� ������ ������ ������ ������������.\n ������������ ��������� ������������������������?").setPositiveButton("Yes", dialogClickListener)
			    .setNegativeButton("No", dialogClickListener).show();
				break;
			}
			
			

      } catch (InterruptedException e) {
	      // TODO Auto-generated catch block
	      e.printStackTrace();
      } catch (ExecutionException e) {
	      // TODO Auto-generated catch block
	      e.printStackTrace();
      }
		
		
	}


	@Override
  public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
	  // TODO Auto-generated method stub
	  JSONObject item = (JSONObject)parent.getItemAtPosition(position);
	  
	  Log.i("ResultActivity->onItemClick()", "---------------------------------------------------------");
	  Log.i("ResultActivity->onItemClick()", "position is " + position);
	  Log.i("ResultActivity->onItemClick()", "JSONObject" + item);
	  Log.i("ResultActivity->onItemClick()", "---------------------------------------------------------");
	  Intent detailIntent = new Intent(getActivity(), DetailActivity.class); 
	  detailIntent.putExtra("item", item.toString());
	  startActivity(detailIntent);
  }
	
	/* *******************************************************
	 * GetPillData Class!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	 * ******************************************************
	 */
	class GetPillData extends AsyncTask<Void, Void, Integer> {
		String[][] parsedData;

		private int postData(String color, String shape, String ushape,
		    String mark, String split) throws ClientProtocolException, IOException {
			System.out.println("��������� ������������");
			return makeHttpPost(color, shape, ushape, mark, split,
			    "http://192.168.43.189:8080/test/parsing/list.do");
			// ��� ip : 192.168.43.189
		}

		private int makeHttpPost(String color, String shape, String ushape,
		    String mark, String split, String url) {
			// TODO Auto-generated method stub
			System.out.println("url ���������������.");
			HttpClient httpclient = null;
			HttpParams params;

			try {
				httpclient = new DefaultHttpClient();
				params = httpclient.getParams();

				HttpConnectionParams.setConnectionTimeout(params, 2000);
				HttpConnectionParams.setSoTimeout(params, 2000);

				HttpPost httppost = new HttpPost(url);
				Vector<NameValuePair> nameValue = new Vector<NameValuePair>();
				nameValue.add(new BasicNameValuePair("color", color));
				nameValue.add(new BasicNameValuePair("shape", shape));
				nameValue.add(new BasicNameValuePair("ushape", ushape));
				nameValue.add(new BasicNameValuePair("mark", mark));
				nameValue.add(new BasicNameValuePair("split", split));

				httppost.setEntity(makeEntity(nameValue));

				System.out.println("...." + httppost.getURI());

				// ��������������� ������ ��������� ������.
				HttpResponse response = httpclient.execute(httppost);
				System.out.println(response.toString());
				// ��������������� ��������� ������������ ������ ������.
				StatusLine status = response.getStatusLine();
				if (status.getStatusCode() != HTTP_STATUS_OK) {
					throw new Exception("Invalid response from server : "
					    + status.toString());
				}

				// ��������������� ��������� ������.
				HttpEntity entity = response.getEntity();
				is = entity.getContent();
				System.out.println("������!" + is);
				Log.e("pass 1", "connection success ");
			} catch (Exception e) {
				getActivity().runOnUiThread(new Runnable(){
					public void run() {
					   Toast.makeText(getActivity().getApplicationContext(), "Server not conneted!",Toast.LENGTH_LONG).show();
					}
					});
				Log.e("Fail 1", e.toString());		
				//finish();
			}

			try {
				// ������������ ������.
				BufferedReader reader = new BufferedReader(new InputStreamReader(is,
				    "UTF-8"), 8);
				StringBuilder sb = new StringBuilder();
				while ((line = reader.readLine()) != null) {
					sb.append(line + "\n");
				}
				is.close();
				result = sb.toString();
				Log.e("pass 2", "connection success " + result);
				if (result.equals("too long\n")) {
					System.out.println("list ������ ������������������������������������������������������20���������������");
					return 2;
				} else {
					jsonParserList(result);
					return 1;
				}

			} catch (Exception e) {
				Log.e("Fail 2", e.toString());
				getActivity().runOnUiThread(new Runnable(){
					public void run() {
					   Toast.makeText(getActivity().getApplicationContext(), "Server not conneted!",Toast.LENGTH_LONG).show();
					}
					});
				//finish();
			} finally {
				httpclient.getConnectionManager().shutdown();
				System.out.println("ResultActivity.this.finish()");
				// ResultActivity.this.finish();
			}
			return 0;

		}

		public void jsonParserList(String result) {
			try {
				ArrayList<String> listdata = new ArrayList<String>();
				JSONObject json = new JSONObject(result);

				JSONArray jArr = json.getJSONArray("pillList");
				JSONArray sub;
				Log.e("pass 1.7", "jsonParserList()���������");
				String[] jsonName = { "pno", "pname", "pcompany", "pimg", "pcolor",
				    "pshape", "pushape", "pmarkfront", "pmarkback", "psplitfront",
				    "psplitback", "pingredient", "psafety", "peffect" };

				for (int i = 0; i < jArr.length(); i++) {
					int idx = 0;
					System.out.println(jArr.get(i).toString());
					JSONObject j = (JSONObject) jArr.get(i);
					resultList.add(j);
					Iterator iter = j.keys();
					while (iter.hasNext()) {
						String key = (String) iter.next();
						// System.out.println(j.get(key));
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
			return;
		}

		private HttpEntity makeEntity(Vector<NameValuePair> nameValue) {
			HttpEntity result = null;
			try {
				result = new UrlEncodedFormEntity(nameValue, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			return result;
		}

		@Override
		protected Integer doInBackground(Void... params) {
			int postResult = 0;
			try {
				postResult = postData(color, shape, ushape, mark, split);
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				//finish();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				//finish();
			}
			return postResult;
		}
	}

}

