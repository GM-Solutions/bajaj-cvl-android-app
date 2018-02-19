package co.gladminds.bajajcvl.util;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

import co.gladminds.bajajcvl.interphace.OnConnectedListener;
import co.gladminds.bajajcvl.interphace.OnResponseListener;
import com.google.firebase.crash.FirebaseCrash;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OkHttpRequest {

    private final int RETRY_COUNT = 5;
    private int intial = 0;
    public Dialog dialog;
    public static Context mContext = null;

    private OnResponseListener onResponseListener;
    private boolean showDialog = true;

    public synchronized void httpPost(final Activity act, final Request request) {
        mContext = act;
        URLConstants.context = act;

        NetworkConnection network = new NetworkConnection();
        network.setOnConnectedListener(new OnConnectedListener() {
            @Override
            public void httpConnect() {
                if (URLConstants.isNetworkAvailable()) {
                    new GetData(act, request).execute();
                } else {
                    Toast.makeText(act, "Please Try Again...", Toast.LENGTH_SHORT).show();
                }

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
                /*pd = new ProgressDialog(act);
                pd.setCanceledOnTouchOutside(false);
				pd.setMessage("Loading...");
				pd.show();*/
                dialog = new Dialog(act);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(co.gladminds.bajajcvl.R.layout.custom);
                ImageView image = (ImageView) dialog.findViewById(co.gladminds.bajajcvl.R.id.image);
                GlideDrawableImageViewTarget imageViewTarget = new GlideDrawableImageViewTarget(image);
                Glide.with(act).load(co.gladminds.bajajcvl.R.drawable.progress3).into(imageViewTarget);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                dialog.show();
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

                e.printStackTrace();
                FirebaseCrash.report(new Exception(e.toString()));
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.e("TAG", "res-" + result);
            try {
                if (result.length() <= 0 && intial < RETRY_COUNT) {
                    new GetData(act, request).execute();
                } else {
                    if (onResponseListener != null) {
                        onResponseListener.onResponse(result);
                    } else {
                        final Dialog serverConnErrDialog = new Dialog(mContext);
                        serverConnErrDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        //serverConnErrDialog.setTitle(context.getResources().getString(R.string.alert_conn_err_title));
                        serverConnErrDialog.setContentView(co.gladminds.bajajcvl.R.layout.layout_unexpected_error_dialog);
                        serverConnErrDialog.setCancelable(false);
                        ((TextView) serverConnErrDialog.findViewById(co.gladminds.bajajcvl.R.id.txtMessage)).setText(mContext.getString(
                                co.gladminds.bajajcvl.R.string.servererror));
                        final Button buttontryConn = (Button) serverConnErrDialog
                                .findViewById(co.gladminds.bajajcvl.R.id.btnSerErrTrytoConnect);
                        final Button buttonSupportEmail = (Button) serverConnErrDialog
                                .findViewById(co.gladminds.bajajcvl.R.id.btnSupportEmail);
                        buttontryConn.setText("Ok");
                        buttonSupportEmail.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                            }
                        });

                        buttontryConn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // TODO Auto-generated
                                if (URLConstants.isNetworkAvailable()) {
                                    //moveOn();
                                    serverConnErrDialog.dismiss();
                                }
                            }
                        });
                        serverConnErrDialog.show();
                    }
                }
            } catch (Exception e) {
                FirebaseCrash.report(new Exception(e.toString()));
                e.printStackTrace();
                // TODO: handle exception
                if (intial < RETRY_COUNT) {
                    new GetData(act, request).execute();
                }
            } finally {
                if (pd != null)
                    pd.dismiss();
                if (dialog != null && dialog.isShowing())
                    dialog.dismiss();
            }
        }
    }

    public void setOnResponseListener(OnResponseListener onResponseListener) {
        this.onResponseListener = onResponseListener;

    }
}
