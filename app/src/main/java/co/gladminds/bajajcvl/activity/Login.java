package co.gladminds.bajajcvl.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;

import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import co.gladminds.bajajcvl.Common.Common;
import co.gladminds.bajajcvl.interphace.OnResponseListener;
import co.gladminds.bajajcvl.util.OkHttpRequest;

import org.json.JSONException;
import org.json.JSONObject;


import java.io.UnsupportedEncodingException;

import okhttp3.MediaType;
import okhttp3.Request;

public class Login extends AppCompatActivity {
    private Button getotp;
    private EditText logineditText;
    private RadioGroup radioGroup;
    private RadioButton retailorradioButton, memberRadiobutton;
    private String mobilevalue, radiobuttonvalue = "1";
    private TextView verifyOtp;
    EditText mobileNo, otp;
    private String otpvalue = "", token = "";
    private ProgressBar otprogressbar;
    private SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(co.gladminds.bajajcvl.R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(co.gladminds.bajajcvl.R.id.toolbar);
        setSupportActionBar(toolbar);
        getotp = (Button) findViewById(co.gladminds.bajajcvl.R.id.getOtp);
        logineditText = (EditText) findViewById(co.gladminds.bajajcvl.R.id.entermobileNo);
        radioGroup = (RadioGroup) findViewById(co.gladminds.bajajcvl.R.id.radiogrp);
        retailorradioButton = (RadioButton) findViewById(co.gladminds.bajajcvl.R.id.retailorradio);
        memberRadiobutton = (RadioButton) findViewById(co.gladminds.bajajcvl.R.id.memberradio);
        sharedpreferences = getSharedPreferences("loyalty_pref_token", Context.MODE_PRIVATE);
        token = sharedpreferences.getString("regId", null);
        getotp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!logineditText.getText().toString().equalsIgnoreCase("")) {
                    attemptLogin();
                } else {
                    logineditText.setError("Please enter valid mobile no");
                }
            }
        });

        retailorradioButton.setChecked(true);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int checkid) {
                if (checkid == co.gladminds.bajajcvl.R.id.retailorradio) {
                    radiobuttonvalue = "1";
                    Common.Customtoast(Login.this, "retailor");
                } else {
                    radiobuttonvalue = "2";
                    Common.Customtoast(Login.this, "member");
                }
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        otpvalue = Common.getPreferences(getApplicationContext(), "otpvalue");
        if (TextUtils.isEmpty(otpvalue)) {
            showDailog();
        }

    }

    private void attemptLogin() {
        mobilevalue = logineditText.getText().toString();
        OkHttpRequest okHttpRequest = new OkHttpRequest();
        okHttpRequest.setOnResponseListener(new OnResponseListener() {
            @Override
            public void onResponse(String result) {
                // login.setVisibility(View.VISIBLE);
                // progressBar.setVisibility(View.GONE);
                Log.e("RESULT", result);
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(result);
                    if (result != null) {
                        boolean status = jsonObject.optBoolean("status");
                        if (status == true) {
                            String user_code = jsonObject.optString("user_code");
                            String user_id = jsonObject.optString("user_id");
                            String user_type = jsonObject.optString("user_type");
                            String mobile_no = jsonObject.optString("mobile_no");
                            otpvalue = jsonObject.optString("otp");
                            Common.SetPreferences(getApplicationContext(), "user_code", user_code);
                            Common.SetPreferences(getApplicationContext(), "user_id", user_id);
                            Common.SetPreferences(getApplicationContext(), "user_type", user_type);
                            Common.SetPreferences(getApplicationContext(), "mobile_no", mobile_no);
                            Common.SetPreferences(getApplicationContext(), "otpvalue", otpvalue);
                            showDailog();
                        } else {
                            Common.Customtoast(Login.this, jsonObject.optString("message"));
                        }

                    } else {

                        Common.Customtoast(Login.this, "Please try again");
                    }
                } catch (JSONException e) {
                    //FirebaseCrash.report(new Exception(e.toString()));
                    e.printStackTrace();
                }

            }
        });
        String userCredentials = "bajajcvl:gm1361";
        byte[] data = null;
        try {
            data = userCredentials.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }


        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        Request request = new Request.Builder()
                .url(Common.mainurl + "user/login/" + mobilevalue + "/" + radiobuttonvalue + "/" + token)
                .header("Authorization", "Basic " + Base64.encodeToString(data, Base64.NO_WRAP))
                .build();

        okHttpRequest.httpPost(Login.this, request);
    }


    Dialog dialogA;

    public void showDailog() {
        dialogA = new Dialog(this);
        dialogA.setCancelable(false);
        dialogA.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogA.setContentView(co.gladminds.bajajcvl.R.layout.otp);
        verifyOtp = (TextView) dialogA.findViewById(co.gladminds.bajajcvl.R.id.verify);
        TextView cancel = (TextView) dialogA.findViewById(co.gladminds.bajajcvl.R.id.cancel);
        TextView resend = (TextView) dialogA.findViewById(co.gladminds.bajajcvl.R.id.resend);
        otp = (EditText) dialogA.findViewById(co.gladminds.bajajcvl.R.id.otp);
        otprogressbar = (ProgressBar) dialogA.findViewById(co.gladminds.bajajcvl.R.id.otpprogressbar);
        otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                otprogressbar.setVisibility(View.INVISIBLE);
            }
        });
        verifyOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (otp.getText().toString().equalsIgnoreCase(otpvalue)) {
                    Common.SetPreferences(getApplicationContext(), "login", "true");
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();

                    overridePendingTransition(co.gladminds.bajajcvl.R.anim.enter, co.gladminds.bajajcvl.R.anim.exit);
                } else {
                    Common.Customtoast(Login.this, "please enter valid otp");
                }

            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogA.dismiss();
            }
        });
        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogin();
            }
        });
        dialogA.setCancelable(false);
        dialogA.show();
    }

}










