package co.gladminds.bajajcvl.util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;


import co.gladminds.bajajcvl.interphace.OnConnectedListener;
import co.gladminds.bajajcvl.interphace.OnResponseListener;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OkHttpRequestNoPd {
	
	private final int RETRY_COUNT = 5;
	private int intial = 0;

	private OnResponseListener onResponseListener;
	private boolean showDialog = true;

	public synchronized void httpPost(final Activity act, final Request request) {
		
		NetworkConnection network = new NetworkConnection();
		network.setOnConnectedListener(new OnConnectedListener() {
			@Override
			public void httpConnect() {
				new GetData(act, request).execute();
			}
		});
		network.isOnline(act);
	}

	public synchronized void httpPost(final Activity act, final Request request, boolean showDialog) {

		this.showDialog = showDialog;
		NetworkConnection network = new NetworkConnection();
		network.setOnConnectedListener(new OnConnectedListener() {
			@Override
			public void httpConnect() {
				new GetData(act, request).execute();
			}
		});
		network.isOnline(act);
	}
	
	class GetData extends AsyncTask<String, Void, String> {
		
		Activity act;
		Request request;

		ProgressDialog pd;

		public GetData(Activity act, Request request) {
			// TODO Auto-generated constructor stub
			this.act = act;
			this.request = request;
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (showDialog) {
				//No Progress bar or animation

			}
		}
		
		@Override
		protected String doInBackground(String... params) {
			
			String result = "";
			try {
				intial++;
				OkHttpClient client = new OkHttpClient();
				Response response = client.newCall(request).execute();
				result = response.body().string();
			} catch (Exception e) {
				//FirebaseCrash.report(new Exception(e.toString()));
				e.printStackTrace();
			}
			return result;
		}
		
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			Log.e("TAG", "res-"+result);
			try {
				if (result.length() <= 0 && intial < RETRY_COUNT) {
					new GetData(act, request).execute();
				} else {
					if (onResponseListener != null) 
						onResponseListener.onResponse(result);
				}
			} catch (Exception e) {
				//FirebaseCrash.report(new Exception(e.toString()));
				e.printStackTrace();
				// TODO: handle exception
				if (intial < RETRY_COUNT) {
					new GetData(act, request).execute();
				}
			} finally {
				if (pd != null)
					pd.dismiss();
			}
		}
	}
	
	public void setOnResponseListener(OnResponseListener onResponseListener) {
		
		this.onResponseListener = onResponseListener;

	}
}
