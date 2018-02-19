package co.gladminds.bajajcvl.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import co.gladminds.bajajcvl.Common.Common;
import co.gladminds.bajajcvl.interphace.OnResponseListener;
import co.gladminds.bajajcvl.util.OkHttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

public class Contact_Us extends AppCompatActivity implements View.OnClickListener {

    private TextView callone, calltwo, email, phonetext, emailtext,
            productenquiries, hrenquries, emailone;
    private ImageView callimage, emailimage, countiamge;
    public static final int MY_PERMISSIONS_REQUEST_WRITE_CALENDAR = 123;
    private Animation left, top, bottom, right;
    SharedPreferences.Editor editor;
    SharedPreferences sharedpreferences;
    private List barcodelist;
    private int i;
    private StringBuilder sb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(co.gladminds.bajajcvl.R.layout.activity_contactus);
        Toolbar toolbar = (Toolbar) findViewById(co.gladminds.bajajcvl.R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });
        right = AnimationUtils.loadAnimation(getApplicationContext(), co.gladminds.bajajcvl.R.anim.right_anim);
        left = AnimationUtils.loadAnimation(getApplicationContext(), co.gladminds.bajajcvl.R.anim.left_animation);
        top = AnimationUtils.loadAnimation(getApplicationContext(), co.gladminds.bajajcvl.R.anim.top_anim);
        bottom = AnimationUtils.loadAnimation(getApplicationContext(), co.gladminds.bajajcvl.R.anim.bottom_anim);

        callone = (TextView) findViewById(co.gladminds.bajajcvl.R.id.callfirst);
        calltwo = (TextView) findViewById(co.gladminds.bajajcvl.R.id.callsecond);
        email = (TextView) findViewById(co.gladminds.bajajcvl.R.id.emailid);
//        visittext = (TextView) findViewById(co.gladminds.bajajcvl.R.id.visitus);
        phonetext = (TextView) findViewById(co.gladminds.bajajcvl.R.id.phone);
        emailtext = (TextView) findViewById(co.gladminds.bajajcvl.R.id.email);
//        address = (TextView) findViewById(co.gladminds.bajajcvl.R.id.visitustext);
//        addressone = (TextView) findViewById(co.gladminds.bajajcvl.R.id.visitustextone);
        productenquiries = (TextView) findViewById(co.gladminds.bajajcvl.R.id.productenquiries);

        hrenquries = (TextView) findViewById(co.gladminds.bajajcvl.R.id.hrenquiries);

        emailone = (TextView) findViewById(co.gladminds.bajajcvl.R.id.emailtext);
//        visitimage = (ImageView) findViewById(co.gladminds.bajajcvl.R.id.visitlogo);
        callimage = (ImageView) findViewById(co.gladminds.bajajcvl.R.id.calllogo);
        emailimage = (ImageView) findViewById(co.gladminds.bajajcvl.R.id.msglogo);

        callone.setOnClickListener(this);
        calltwo.setOnClickListener(this);
        email.setOnClickListener(this);
    }

    public void onClick(View v) {

        switch (v.getId()) {

            case co.gladminds.bajajcvl.R.id.callfirst:
                if (isPermissionGranted()) {
                    call_action(callone.getText().toString());
                }
                break;

            case co.gladminds.bajajcvl.R.id.callsecond:
                if (isPermissionGranted()) {
                    call_action(calltwo.getText().toString());
                }
                break;

            case co.gladminds.bajajcvl.R.id.emailid:
                //Toast.makeText(getApplicationContext(),"click",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("plain/text");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{email.getText().toString()});
                intent.putExtra(Intent.EXTRA_SUBJECT, "");
                intent.putExtra(Intent.EXTRA_TEXT, "");
                startActivity(Intent.createChooser(intent, ""));
                break;
        }
    }

    public void call_action(String num) {

        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + num));
        startActivity(callIntent);
    }


    public boolean isPermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.CALL_PHONE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v("TAG", "Permission is granted");
                return true;
            } else {

                Log.v("TAG", "Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v("TAG", "Permission is granted");
            return true;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {

            case 1: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Toast.makeText(getApplicationContext(), "Permission granted", Toast.LENGTH_SHORT).show();
                    Common.Customtoast(Contact_Us.this, "Permission granted");
                    call_action(callone.getText().toString());
                } else {
                    Common.Customtoast(Contact_Us.this, "Permission denied");
                    //Toast.makeText(getApplicationContext(), "Permission denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public void onResume() {
        super.onResume();

//        visitimage.startAnimation(top);
        callimage.startAnimation(left);
        emailimage.startAnimation(bottom);
//        visittext.startAnimation(left);
        phonetext.startAnimation(right);
        emailtext.startAnimation(left);
        AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
        AlphaAnimation fadeOut = new AlphaAnimation(1.0f, 0.0f);
//        address.startAnimation(fadeIn);
//        addressone.startAnimation(fadeIn);
        productenquiries.startAnimation(fadeIn);
        callone.startAnimation(fadeIn);
        hrenquries.startAnimation(fadeIn);
        calltwo.startAnimation(fadeIn);
        emailone.startAnimation(fadeIn);
        email.startAnimation(fadeIn);
        fadeIn.setDuration(3000);
        fadeIn.setFillAfter(true);
        fadeOut.setDuration(1200);
        fadeOut.setFillAfter(true);
        fadeOut.setStartOffset(4200 + fadeIn.getStartOffset());
        countiamge = (ImageView) findViewById(co.gladminds.bajajcvl.R.id.scancount);
        sharedpreferences = getSharedPreferences("bajaj_pref", Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();
        barcodelist = new ArrayList();
        barcodelist.clear();
        try {
            Set<String> set = sharedpreferences.getStringSet("countbarcode", null);
            barcodelist.addAll(set);
        } catch (Throwable ex) {
        }
        if (barcodelist.size() == 1) {
            countiamge.setImageResource(co.gladminds.bajajcvl.R.drawable.cartone);
        } else if (barcodelist.size() == 2) {
            countiamge.setImageResource(co.gladminds.bajajcvl.R.drawable.carttwo);
        } else if (barcodelist.size() == 3) {
            countiamge.setImageResource(co.gladminds.bajajcvl.R.drawable.cartthree);
        } else if (barcodelist.size() == 4) {
            countiamge.setImageResource(co.gladminds.bajajcvl.R.drawable.cartfour);
        } else if (barcodelist.size() == 5) {
            countiamge.setImageResource(co.gladminds.bajajcvl.R.drawable.cartfive);
        } else if (barcodelist.size() == 6) {
            countiamge.setImageResource(co.gladminds.bajajcvl.R.drawable.cartsix);
        } else if (barcodelist.size() == 7) {
            countiamge.setImageResource(co.gladminds.bajajcvl.R.drawable.cartseven);
        } else if (barcodelist.size() == 8) {
            countiamge.setImageResource(co.gladminds.bajajcvl.R.drawable.carteight);
        } else if (barcodelist.size() == 9) {
            countiamge.setImageResource(co.gladminds.bajajcvl.R.drawable.cartnine);
        } else if (barcodelist.size() == 10) {
            countiamge.setImageResource(co.gladminds.bajajcvl.R.drawable.cartten);
        } else {
            countiamge.setImageResource(co.gladminds.bajajcvl.R.drawable.cart);
        }
        countiamge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (barcodelist.size() > 0) {
                    showDailog();
                }
            }
        });


    }


    Dialog dialogA;

    public void showDailog() {
        dialogA = new Dialog(this);
        dialogA.setCancelable(true);
        dialogA.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogA.setContentView(co.gladminds.bajajcvl.R.layout.barcode_layout);

        final LinearLayout linearLayout = (LinearLayout) dialogA.findViewById(co.gladminds.bajajcvl.R.id.barcodelinear);
        RelativeLayout send = (RelativeLayout) dialogA.findViewById(co.gladminds.bajajcvl.R.id.sendlayout);
        RelativeLayout close = (RelativeLayout) dialogA.findViewById(co.gladminds.bajajcvl.R.id.closelayout);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogA.dismiss();
            }
        });
        LayoutInflater inflator = (LayoutInflater) Contact_Us.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        for (i = 0; i < barcodelist.size(); i++) {
            final View item = inflator.inflate(co.gladminds.bajajcvl.R.layout.barcode_view, null);
            String getvalue = "" + barcodelist.get(i);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(10, 10, 10, 10);
            item.setLayoutParams(layoutParams);
            TextView barcodevalue = (TextView) item.findViewById(co.gladminds.bajajcvl.R.id.barcodetext);
            final TextView cross = (TextView) item.findViewById(co.gladminds.bajajcvl.R.id.barcodecross);
            cross.setTag(i);
            cross.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = 0;
                    if (cross.getTag() instanceof Integer) {
                        position = (Integer) cross.getTag();
                        if (position >= barcodelist.size()) {
                            position = barcodelist.size() - 1;
                        }
                    }
                    //Toast.makeText(getApplicationContext(),""+position,Toast.LENGTH_SHORT).show();
                    try {
                        barcodelist.remove(position);
                        linearLayout.removeViewAt(position);
                    } catch (Throwable ex) {
                    }
                    Set<String> setTestId = new HashSet<String>();
                    setTestId.addAll(barcodelist);
                    editor.putStringSet("countbarcode", setTestId);
                    editor.commit();

                    if (barcodelist.size() == 1) {
                        countiamge.setImageResource(co.gladminds.bajajcvl.R.drawable.cartone);
                    } else if (barcodelist.size() == 2) {
                        countiamge.setImageResource(co.gladminds.bajajcvl.R.drawable.carttwo);
                    } else if (barcodelist.size() == 3) {
                        countiamge.setImageResource(co.gladminds.bajajcvl.R.drawable.cartthree);
                    } else if (barcodelist.size() == 4) {
                        countiamge.setImageResource(co.gladminds.bajajcvl.R.drawable.cartfour);
                    } else if (barcodelist.size() == 5) {
                        countiamge.setImageResource(co.gladminds.bajajcvl.R.drawable.cartfive);
                    } else if (barcodelist.size() == 6) {
                        countiamge.setImageResource(co.gladminds.bajajcvl.R.drawable.cartsix);
                    } else if (barcodelist.size() == 7) {
                        countiamge.setImageResource(co.gladminds.bajajcvl.R.drawable.cartseven);
                    } else if (barcodelist.size() == 8) {
                        countiamge.setImageResource(co.gladminds.bajajcvl.R.drawable.carteight);
                    } else if (barcodelist.size() == 9) {
                        countiamge.setImageResource(co.gladminds.bajajcvl.R.drawable.cartnine);
                    } else if (barcodelist.size() == 10) {
                        countiamge.setImageResource(co.gladminds.bajajcvl.R.drawable.cartten);
                    } else {
                        countiamge.setImageResource(co.gladminds.bajajcvl.R.drawable.cart);
                        dialogA.dismiss();
                    }
                }
            });

            send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sendbarcode();

                }
            });
            barcodevalue.setText(getvalue);
            linearLayout.addView(item);
        }

        dialogA.show();
    }

    private void sendbarcode() {

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
                        if (jsonObject.optBoolean("status") == true) {
                            dialogA.dismiss();
                            barcodelist.clear();
                            Set<String> setTestId = new HashSet<String>();
                            setTestId.addAll(barcodelist);
                            editor.putStringSet("countbarcode", setTestId);
                            editor.commit();
                            countiamge.setImageResource(co.gladminds.bajajcvl.R.drawable.cart);
                            Common.Customtoast(Contact_Us.this, jsonObject.optString("message"));
                        } else {
                            Common.Customtoast(Contact_Us.this, "Please try again");
                        }
                    } else {

                        Common.Customtoast(Contact_Us.this, "Please try again");
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
        sb = new StringBuilder();
        for (int i = 0; i < barcodelist.size(); i++) {
            String getvalue = "" + barcodelist.get(i);
            sb.append(getvalue + ",");
        }
        sb.deleteCharAt(sb.length() - 1);
        String upccode = sb.toString();
        String userid = Common.getPreferences(getApplicationContext(), "mobile_no");
        String jsonstring = "{\"mobile_no\":\"" + userid + "\",\"upc\":\"" + upccode + "\"}";
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        okhttp3.RequestBody body = RequestBody.create(JSON, jsonstring);
        Request request = new Request.Builder()
                .url(Common.mainurl + "transaction/pointcollect")
                .header("Authorization", "Basic " + Base64.encodeToString(data, Base64.NO_WRAP))
                .post(body)
                .build();
        okHttpRequest.httpPost(Contact_Us.this, request);
    }


}