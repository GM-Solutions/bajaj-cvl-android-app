package co.gladminds.bajajcvl.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import co.gladminds.bajajcvl.Common.Common;
import co.gladminds.bajajcvl.R;
import co.gladminds.bajajcvl.adapter.FailedAdapter;
import co.gladminds.bajajcvl.adapter.UPCRecycleAdapter;
import co.gladminds.bajajcvl.interphace.OnResponseListener;
import co.gladminds.bajajcvl.util.OkHttpRequest;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.firebase.crash.FirebaseCrash;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

public class Productscan extends AppCompatActivity {
    //private ZXingScannerView mScannerView;
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    private TextView scanbutton, detecttextview, offlineenterupc, scanbuttonone;
    public static Dialog faileddialog;
    public static ImageView countiamge, addmanual, failedcart;
    public static List barcodelist, failedbarcodelist;
    private EditText barcodeedittext;
    SharedPreferences.Editor editor;
    SharedPreferences sharedpreferences;
    int i, j, countofimages;
    private StringBuilder sb, detectedText, appendstring;
    private List Imagelist;
    private TextView progressBar;
    private Animation bouncing;
    private boolean isFormatting;
    private boolean deletingHyphen;
    private int hyphenStart;
    private boolean deletingBackward;
    private int lastCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_productscan);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //mScannerView.stopCamera();
                finish();
            }
        });


        bouncing = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bouncing);
        sharedpreferences = getSharedPreferences("bajaj_pref", Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();
        checkAndRequestPermissions();


        scanbutton = (TextView) findViewById(R.id.newscanbutton);
        scanbuttonone = (TextView) findViewById(R.id.newscanbuttonone);
        progressBar = (TextView) findViewById(R.id.scanprogressbar);
//        detecttextview = (TextView) findViewById(R.id.detectedtext);
        offlineenterupc = (TextView) findViewById(R.id.offlineentercode);
        countiamge = (ImageView) findViewById(R.id.scancount);
        failedcart = (ImageView) findViewById(R.id.failedcart);
        addmanual = (ImageView) findViewById(R.id.addimage);
        new LongOperation("true").execute();
      /* mScannerView = (ZXingScannerView) findViewById(R.id.scanView);
        mScannerView.setResultHandler(Productscan.this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();*/
        detectedText = new StringBuilder();
        // appendstring = new StringBuilder();
        try {
            Imagelist = getIntent().getStringArrayListExtra("imagelist");

            if (Imagelist.size() > 0) {
                Runnable r = new Runnable() {
                    @Override
                    public void run() {
                        new LongOperation("false").execute();
                        for (int i = 0; i < Imagelist.size(); i++) {

                            // Thread.sleep(1000);
                            try {
                                Bitmap mBitmap = MediaStore.Images.Media.getBitmap(Productscan.this.getContentResolver(), Uri.parse("file://" + Imagelist.get(i).toString()));
                                // Toast.makeText(getApplicationContext(),""+Uri.parse("file://"+Imagelist.get(i).toString()),Toast.LENGTH_SHORT).show();
                                Log.e("my image is", "" + Uri.parse("file://" + Imagelist.get(i).toString()));
                                countofimages = i;
                                inspectFromBitmap(mBitmap);
                            } catch (Throwable e) {
                                //FirebaseCrash.report(new Exception(e.toString()));
                                Toast.makeText(getApplicationContext(), "" + e.toString(), Toast.LENGTH_SHORT).show();
                            }

                        }
                        //new Handler().postDelayed(this, 1000);
                    }
                };

                new Handler().postDelayed(r, 1000);

            }
        } catch (Throwable ex) {
            //FirebaseCrash.report(new Exception(ex.toString()));
        }
        barcodelist = new ArrayList();
        failedbarcodelist = new ArrayList();
        barcodelist.clear();
        failedbarcodelist.clear();
        try {

            Set<String> set = sharedpreferences.getStringSet("failedcountbarcode", null);
            failedbarcodelist.addAll(set);
        } catch (Throwable ex) {
        }
        if (failedbarcodelist.size() > 0) {
            failedcart.setVisibility(View.VISIBLE);
        } else {
            failedcart.setVisibility(View.INVISIBLE);
        }
        try {
            barcodelist.clear();
            Set<String> set = sharedpreferences.getStringSet("countbarcode", null);
            barcodelist.addAll(set);
        } catch (Throwable ex) {
            //FirebaseCrash.report(new Exception(ex.toString()));
        }
        if (barcodelist.size() == 1) {
            countiamge.setImageResource(R.drawable.cartone);
        } else if (barcodelist.size() == 2) {
            countiamge.setImageResource(R.drawable.carttwo);
        } else if (barcodelist.size() == 3) {
            countiamge.setImageResource(R.drawable.cartthree);
        } else if (barcodelist.size() == 4) {
            countiamge.setImageResource(R.drawable.cartfour);
        } else if (barcodelist.size() == 5) {
            countiamge.setImageResource(R.drawable.cartfive);
        } else if (barcodelist.size() == 6) {
            countiamge.setImageResource(R.drawable.cartsix);
        } else if (barcodelist.size() == 7) {
            countiamge.setImageResource(R.drawable.cartseven);
        } else if (barcodelist.size() == 8) {
            countiamge.setImageResource(R.drawable.carteight);
        } else if (barcodelist.size() == 9) {
            countiamge.setImageResource(R.drawable.cartnine);
        } else if (barcodelist.size() == 10) {
            countiamge.setImageResource(R.drawable.cartten);
        } else {
            countiamge.setImageResource(R.drawable.cart);
        }

        countiamge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (barcodelist.size() > 0) {
                    showDailog();
                }
            }
        });

        failedcart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (failedbarcodelist.size() > 0) {
                    showDailogfailedupc();
                }
            }
        });

        addmanual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (barcodelist.size() < 10) {
                    showDailogmanual();
                } else {
                    Common.Customtoast(Productscan.this, "you can save 10 products at a time");
                }
            }
        });

        offlineenterupc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                offlineenterupc.startAnimation(bouncing);
                if (barcodelist.size() < 10) {
                    showDailogmanual();
                } else {
                    Common.Customtoast(Productscan.this, "you can save 10 products at a time");
                }
            }
        });
        scanbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanbutton.startAnimation(bouncing);
                if (isPermissionGranted()) {
                    Intent intent = new Intent(getApplicationContext(), Multi_photo.class);
                    finish();
                    startActivity(intent);
                }
            }
        });


        (findViewById(R.id.tvScanUpc)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanActivity();
            }
        });

        (findViewById(R.id.scannerpic)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanActivity();
            }
        });


    }

    private void scanActivity() {
        Intent intent = new Intent(getApplicationContext(), CameraActivity.class);
        intent.putExtra(UPCScanActivity.AutoFocus, true);
        intent.putExtra(UPCScanActivity.UseFlash, true);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            barcodelist.clear();
            Set<String> set = sharedpreferences.getStringSet("countbarcode", null);
            barcodelist.addAll(set);
        } catch (Throwable ex) {
            //FirebaseCrash.report(new Exception(ex.toString()));
        }
        if (barcodelist.size() == 1) {
            countiamge.setImageResource(R.drawable.cartone);
        } else if (barcodelist.size() == 2) {
            countiamge.setImageResource(R.drawable.carttwo);
        } else if (barcodelist.size() == 3) {
            countiamge.setImageResource(R.drawable.cartthree);
        } else if (barcodelist.size() == 4) {
            countiamge.setImageResource(R.drawable.cartfour);
        } else if (barcodelist.size() == 5) {
            countiamge.setImageResource(R.drawable.cartfive);
        } else if (barcodelist.size() == 6) {
            countiamge.setImageResource(R.drawable.cartsix);
        } else if (barcodelist.size() == 7) {
            countiamge.setImageResource(R.drawable.cartseven);
        } else if (barcodelist.size() == 8) {
            countiamge.setImageResource(R.drawable.carteight);
        } else if (barcodelist.size() == 9) {
            countiamge.setImageResource(R.drawable.cartnine);
        } else if (barcodelist.size() == 10) {
            countiamge.setImageResource(R.drawable.cartten);
        } else {
            countiamge.setImageResource(R.drawable.cart);
        }

    }

    private class LongOperation extends AsyncTask<String, Void, String> {
        String checklaod;

        public LongOperation(String showLoading) {
            super();
            checklaod = showLoading;
            // do stuff
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                if (checklaod.equalsIgnoreCase("true") && Imagelist.size() > 0) {
                    progressBar.setVisibility(View.VISIBLE);
                    // scanbuttonone.setVisibility(View.VISIBLE);
                    scanbutton.setVisibility(View.GONE);


                } else {
                    progressBar.setVisibility(View.INVISIBLE);

                }
            } catch (Throwable ex) {
                // FirebaseCrash.report(new Exception(ex.toString()));
            }

            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }


    private boolean checkAndRequestPermissions() {

        int locationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);

        List<String> listPermissionsNeeded = new ArrayList<>();
        if (locationPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);


            return false;
        }
        return true;
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // mScannerView.stopCamera();
        this.finish();
    }

    Dialog dialogA;

    public void showDailog() {
        dialogA = new Dialog(this);
        dialogA.setCancelable(true);
        dialogA.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogA.setContentView(R.layout.barcode_layout);

        final LinearLayout linearLayout = (LinearLayout) dialogA.findViewById(R.id.barcodelinear);
        RelativeLayout send = (RelativeLayout) dialogA.findViewById(R.id.sendlayout);
        RelativeLayout close = (RelativeLayout) dialogA.findViewById(R.id.closelayout);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogA.dismiss();
            }
        });
        LayoutInflater inflator = (LayoutInflater) Productscan.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        for (i = 0; i < barcodelist.size(); i++) {
            final View item = inflator.inflate(R.layout.barcode_view, null);
            String getvalue = "" + barcodelist.get(i);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(10, 10, 10, 10);
            item.setLayoutParams(layoutParams);
            TextView barcodevalue = (TextView) item.findViewById(R.id.barcodetext);
            final TextView cross = (TextView) item.findViewById(R.id.barcodecross);
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

                    // Toast.makeText(getApplicationContext(),""+position,Toast.LENGTH_SHORT).show();
                    try {
                        barcodelist.remove(position);
                        linearLayout.removeViewAt(position);
                    } catch (Throwable ex) {
                        //FirebaseCrash.report(new Exception(ex.toString()));
                    }
                    Set<String> setTestId = new HashSet<String>();
                    setTestId.addAll(barcodelist);
                    editor.putStringSet("countbarcode", setTestId);
                    editor.commit();

                    if (barcodelist.size() == 1) {
                        countiamge.setImageResource(R.drawable.cartone);
                    } else if (barcodelist.size() == 2) {
                        countiamge.setImageResource(R.drawable.carttwo);
                    } else if (barcodelist.size() == 3) {
                        countiamge.setImageResource(R.drawable.cartthree);
                    } else if (barcodelist.size() == 4) {
                        countiamge.setImageResource(R.drawable.cartfour);
                    } else if (barcodelist.size() == 5) {
                        countiamge.setImageResource(R.drawable.cartfive);
                    } else if (barcodelist.size() == 6) {
                        countiamge.setImageResource(R.drawable.cartsix);
                    } else if (barcodelist.size() == 7) {
                        countiamge.setImageResource(R.drawable.cartseven);
                    } else if (barcodelist.size() == 8) {
                        countiamge.setImageResource(R.drawable.carteight);
                    } else if (barcodelist.size() == 9) {
                        countiamge.setImageResource(R.drawable.cartnine);
                    } else if (barcodelist.size() == 10) {
                        countiamge.setImageResource(R.drawable.cartten);
                    } else {
                        countiamge.setImageResource(R.drawable.cart);
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

// failed upc dialog box


    public void showDailogfailedupc() {

        faileddialog = new Dialog(this);
        faileddialog.setCancelable(true);
        faileddialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        faileddialog.setContentView(R.layout.failedlayout);
        ListView failedlistview = (ListView) faileddialog.findViewById(R.id.failedlistview);
        Button varifyall = (Button) faileddialog.findViewById(R.id.varifyallbutton);
        TextView crossbutton = (TextView) faileddialog.findViewById(R.id.crossfailedupcdialog);
        FailedAdapter adapter = new FailedAdapter(getApplicationContext(), failedbarcodelist, faileddialog, Productscan.this);
        failedlistview.setAdapter(adapter);
        crossbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                faileddialog.dismiss();
            }
        });
        faileddialog.show();
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
                            countiamge.setImageResource(R.drawable.cart);
                            Common.Customtoast(Productscan.this, jsonObject.optString("message"));
                        } else {
                            Common.Customtoast(Productscan.this, "Please try again");
                        }
                    } else {

                        Common.Customtoast(Productscan.this, "Please try again");
                    }
                } catch (JSONException e) {
                    FirebaseCrash.report(new Exception(e.toString()));
                    e.printStackTrace();
                }

            }
        });
        String userCredentials = "bajajcvl:gm1361";
        byte[] data = null;
        try {
            data = userCredentials.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e1) {
            FirebaseCrash.report(new Exception(e1.toString()));
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
        okHttpRequest.httpPost(Productscan.this, request);
    }


    public void Upcvarify(final String upccode, String source) {
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
                            JSONArray jsonArray = jsonObject.getJSONArray("upc");
                            int count = Integer.parseInt(jsonObject.optString("count"));
                            appendstring = new StringBuilder();
                            for (int i = 0; i < count; i++) {
                                final JSONObject jb = jsonArray.getJSONObject(i);
                                String upc = jb.optString("upc");
                                String status_used = jb.optString("status_used");
                                String message = jb.optString("message");
                                if (status_used.equalsIgnoreCase("no")) {
                                    if (barcodelist.contains(upc)) {
                                        appendstring.append(upc + " Already in cart" + "\n");
                                        //Common.Customtoast(Productscan.this,"Already in cart");
                                    } else {
                                        barcodelist.add(upc);
                                        //Common.Customtoast(Productscan.this,message);
                                        appendstring.append(message);
                                    }

                                    Set<String> setTestId = new HashSet<String>();
                                    setTestId.addAll(barcodelist);
                                    barcodelist.clear();
                                    barcodelist.addAll(setTestId);
                                    editor.putStringSet("countbarcode", setTestId);
                                    editor.commit();
                                    if (barcodelist.size() == 1) {
                                        countiamge.setImageResource(R.drawable.cartone);
                                    } else if (barcodelist.size() == 2) {
                                        countiamge.setImageResource(R.drawable.carttwo);
                                    } else if (barcodelist.size() == 3) {
                                        countiamge.setImageResource(R.drawable.cartthree);
                                    } else if (barcodelist.size() == 4) {
                                        countiamge.setImageResource(R.drawable.cartfour);
                                    } else if (barcodelist.size() == 5) {
                                        countiamge.setImageResource(R.drawable.cartfive);
                                    } else if (barcodelist.size() == 6) {
                                        countiamge.setImageResource(R.drawable.cartsix);
                                    } else if (barcodelist.size() == 7) {
                                        countiamge.setImageResource(R.drawable.cartseven);
                                    } else if (barcodelist.size() == 8) {
                                        countiamge.setImageResource(R.drawable.carteight);
                                    } else if (barcodelist.size() == 9) {
                                        countiamge.setImageResource(R.drawable.cartnine);
                                    } else if (barcodelist.size() == 10) {
                                        countiamge.setImageResource(R.drawable.cartten);
                                    } else {
                                        countiamge.setImageResource(R.drawable.cart);
                                    }

                                } else if (status_used.equalsIgnoreCase("yes")) {
                                    //Common.Customtoast(Productscan.this,message);
                                    appendstring.append(upc + " " + message);
                                } else {
                                    //Common.Customtoast(Productscan.this,message);
                                    appendstring.append(upc + " " + message);
                                    failedbarcodelist.add(upc);
                                    Set<String> setTestId = new HashSet<String>();
                                    setTestId.addAll(failedbarcodelist);
                                    failedbarcodelist.clear();
                                    failedbarcodelist.addAll(setTestId);
                                    editor.putStringSet("failedcountbarcode", setTestId);
                                    editor.commit();

                                    if (failedbarcodelist.size() > 0) {
                                        failedcart.setVisibility(View.VISIBLE);
                                    }
                                }

                            }
                            Common.Customtoast(Productscan.this, appendstring.toString());
                            //scanbuttonone.setVisibility(View.INVISIBLE);
                            scanbutton.setVisibility(View.VISIBLE);


                            //textView.setVisibility(View.VISIBLE);
                            //*  scanbutton.setVisibility(View.VISIBLE);


                        } else {
                            Common.Customtoast(Productscan.this, "Please try again");


                            scanbutton.setVisibility(View.VISIBLE);

                        }
                    } else {

                        Common.Customtoast(Productscan.this, "Please try again");

                    }
                } catch (JSONException e) {
                    FirebaseCrash.report(new Exception(e.toString()));
                    e.printStackTrace();
                }

            }
        });
        String userCredentials = "bajajcvl:gm1361";
        byte[] data = null;
        try {
            data = userCredentials.getBytes("UTF-8");
            Log.e("my data is", Base64.encodeToString(data, Base64.NO_WRAP));

        } catch (UnsupportedEncodingException e1) {
            FirebaseCrash.report(new Exception(e1.toString()));
            e1.printStackTrace();
        }
        String finalupccode = upccode.replace("\n", " ");
        Log.e("detect text is", finalupccode);
        String usertype = Common.getPreferences(getApplicationContext(), "user_type");
        String jsonstring = "{\"upc\":\"" + finalupccode + "\",\"user_type\":\"" + usertype + "\",\"source\":\"" + source + "\"}";
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        okhttp3.RequestBody body = RequestBody.create(JSON, jsonstring);
        Request request = new Request.Builder()
                .url(Common.mainurl + "transaction/upcverifywithpicture")
                .header("Authorization", "Basic " + Base64.encodeToString(data, Base64.NO_WRAP))
                .post(body)
                .build();
        okHttpRequest.httpPost(Productscan.this, request);


    }


    Dialog dialog;

    public void showDailogmanual() {
        dialog = new Dialog(this);
        dialog.setCancelable(false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.addmanualview);
        TextView save = (TextView) dialog.findViewById(R.id.savebarcode);
        TextView cancel = (TextView) dialog.findViewById(R.id.cancel);

        barcodeedittext = (EditText) dialog.findViewById(R.id.barcodenumber);
        barcodeedittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable text) {

                if (lastCount > text.length()) {
                    lastCount = text.length();
                    return;
                }
                if (isFormatting)
                    return;

                isFormatting = true;

                // If deleting hyphen, also delete character before or after it
                if (deletingHyphen && hyphenStart > 0) {
                    if (deletingBackward) {
                        if (hyphenStart - 1 < text.length()) {
                            text.delete(hyphenStart - 1, hyphenStart);
                        }
                    } else if (hyphenStart < text.length()) {
                        text.delete(hyphenStart, hyphenStart + 1);
                    }
                }


                if (text.length() == 8 || text.length() == 17 || text.length() == 26 || text.length() == 35 || text.length() == 44
                        || text.length() == 53 || text.length() == 62 || text.length() == 71 || text.length() == 80) {
                    text.append('-');
                }
                lastCount = text.length();

                isFormatting = false;
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (isFormatting)
                    return;

                // Make sure user is deleting one char, without a selection
                final int selStart = Selection.getSelectionStart(s);
                final int selEnd = Selection.getSelectionEnd(s);
                if (s.length() > 1 // Can delete another character
                        && count == 1 // Deleting only one character
                        && after == 0 // Deleting
                        && s.charAt(start) == '-' // a hyphen
                        && selStart == selEnd) { // no selection
                    deletingHyphen = true;
                    hyphenStart = start;
                    // Check if the user is deleting forward or backward
                    if (selStart == start + 1) {
                        deletingBackward = true;
                    } else {
                        deletingBackward = false;
                    }
                } else {
                    deletingHyphen = false;
                }
            }
        });

        /*barcodeedittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub
            }
            @Override
            public void afterTextChanged(Editable text) {
                // TODO Auto-generated method stub
                if (text.length() == 8) {
                    text.append(',');
                }else if(text.length() == 17){
                    text.append(',');
                }else if(text.length() == 26){
                    text.append(',');
                }else if(text.length() == 35){
                    text.append(',');
                }else if(text.length() == 44){
                    text.append(',');
                }else if(text.length() == 53){
                    text.append(',');
                }else if(text.length() == 62){
                    text.append(',');
                }else if(text.length() == 71){
                    text.append(',');
                }else if(text.length() == 80){
                    text.append(',');
                }
            }
        });*/

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!barcodeedittext.getText().toString().equalsIgnoreCase("")) {
                    if (barcodelist.size() < 10) {
                        Upcvarify(barcodeedittext.getText().toString().replaceAll("-", ","), "2");
                    } else {
                        Common.Customtoast(Productscan.this, "you can scan only 10 products");
                    }
                        /*if(Upcvarify(barcodeedittext.getText().toString())) {
                            barcodelist.add(barcodeedittext.getText().toString());
                            Set<String> setTestId = new HashSet<String>();
                            setTestId.addAll(barcodelist);
                            barcodelist.clear();
                            barcodelist.addAll(setTestId);
                            editor.putStringSet("countbarcode", setTestId);
                            editor.commit();
                            if (barcodelist.size() == 1) {
                                countiamge.setImageResource(R.drawable.cartone);
                            } else if (barcodelist.size() == 2) {
                                countiamge.setImageResource(R.drawable.carttwo);
                            } else if (barcodelist.size() == 3) {
                                countiamge.setImageResource(R.drawable.cartthree);
                            } else if (barcodelist.size() == 4) {
                                countiamge.setImageResource(R.drawable.cartfour);
                            } else if (barcodelist.size() == 5) {
                                countiamge.setImageResource(R.drawable.cartfive);
                            } else if (barcodelist.size() == 6) {
                                countiamge.setImageResource(R.drawable.cartsix);
                            } else if (barcodelist.size() == 7) {
                                countiamge.setImageResource(R.drawable.cartseven);
                            } else if (barcodelist.size() == 8) {
                                countiamge.setImageResource(R.drawable.carteight);
                            } else if (barcodelist.size() == 9) {
                                countiamge.setImageResource(R.drawable.cartnine);
                            } else if (barcodelist.size() == 10) {
                                countiamge.setImageResource(R.drawable.cartten);
                            } else {
                                countiamge.setImageResource(R.drawable.cart);
                            }
                        }
                    } else {
                        Common.Customtoast(Productscan.this, "you can scan only 10 products");

                    }*/
                } else {
                    Common.Customtoast(Productscan.this, "please enter your product barcode");
                }
                dialog.dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public boolean isPermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v("TAG", "Permission is granted");

                return true;
            } else {

                Log.v("TAG", "Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
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
                    Toast.makeText(getApplicationContext(), "Permission granted", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), Multi_photo.class);
                    finish();
                    startActivity(intent);

                } else {

                    Toast.makeText(getApplicationContext(), "Permission denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private void inspectFromBitmap(Bitmap bitmap) {
        // Toast.makeText(getApplicationContext(),"hello in this",Toast.LENGTH_SHORT).show();
        TextRecognizer textRecognizer = new TextRecognizer.Builder(this).build();
        try {
            if (!textRecognizer.isOperational()) {
                new AlertDialog.
                        Builder(this).
                        setMessage("Text recognizer could not be set up on your device").show();
                return;
            }

            Frame frame = new Frame.Builder().setBitmap(bitmap).build();
            SparseArray<TextBlock> origTextBlocks = textRecognizer.detect(frame);
            List<TextBlock> textBlocks = new ArrayList<>();
            for (int i = 0; i < origTextBlocks.size(); i++) {
                TextBlock textBlock = origTextBlocks.valueAt(i);
                textBlocks.add(textBlock);
            }
            Collections.sort(textBlocks, new Comparator<TextBlock>() {
                @Override
                public int compare(TextBlock o1, TextBlock o2) {
                    int diffOfTops = o1.getBoundingBox().top - o2.getBoundingBox().top;
                    int diffOfLefts = o1.getBoundingBox().left - o2.getBoundingBox().left;
                    if (diffOfTops != 0) {
                        return diffOfTops;
                    }
                    return diffOfLefts;
                }
            });
//  StringBuilder detectedText = new StringBuilder();
            for (TextBlock textBlock : textBlocks) {
                if (textBlock != null && textBlock.getValue() != null) {
                    detectedText.append(textBlock.getValue());
                    detectedText.append("\n");
                }
            }

            //Toast.makeText(getApplicationContext(),"hi "+detectedText,Toast.LENGTH_SHORT).show();
//            detecttextview.setText(detectedText);
            if (countofimages == Imagelist.size() - 1) {
                Upcvarify(detectedText.toString(), "1");
            }

        } finally {
            textRecognizer.release();
        }
    }


}
