package co.gladminds.bajajcvl.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import co.gladminds.bajajcvl.Common.Common;
import co.gladminds.bajajcvl.R;
import co.gladminds.bajajcvl.adapter.GiftAdapter;
import co.gladminds.bajajcvl.interphace.OnResponseListener;
import co.gladminds.bajajcvl.util.OkHttpRequest;

import org.json.JSONArray;
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

public class Mygifts extends AppCompatActivity {
    private ImageView countiamge;
    SharedPreferences.Editor editor;
    SharedPreferences sharedpreferences;
    private List barcodelist;
    private int i;
    private StringBuilder sb;
    private ListView giftlistview;
    private Spinner itemstatusSpinner;
    private List transactionidlist, productidlist, productdeslist, productimagelist, productpointlist,
            productpricelist, productstatuslist, datelist, statuslist;
    private GiftAdapter giftAdapter;
    private LinearLayout llNoGift;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mygifts);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        llNoGift = (LinearLayout) findViewById(R.id.llNoGift);
        giftlistview = (ListView) findViewById(R.id.giftlistview);
        itemstatusSpinner = (Spinner) findViewById(R.id.statusspinner);
        transactionidlist = new ArrayList();
        productidlist = new ArrayList();
        productdeslist = new ArrayList();
        productimagelist = new ArrayList();
        productpointlist = new ArrayList();
        productpricelist = new ArrayList();
        productstatuslist = new ArrayList();
        statuslist = new ArrayList();
        statuslist.clear();
        datelist = new ArrayList();

        productidlist.clear();
        productdeslist.clear();
        productimagelist.clear();
        productpointlist.clear();
        productpricelist.clear();
        productstatuslist.clear();
        transactionidlist.clear();
        datelist.clear();
        statuslist.add("All");
        statuslist.add("Open");
        statuslist.add("Shipped");
        statuslist.add("Accepted");
        statuslist.add("Approved");
        statuslist.add("Rejected");
        statuslist.add("Delivered");
        ArrayAdapter adb = new ArrayAdapter(getApplicationContext(), R.layout.chk1, statuslist);
        itemstatusSpinner.setAdapter(adb);
        itemstatusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String value = adapterView.getItemAtPosition(i).toString().replace(" ", "");
                transactionidlist.clear();
                productidlist.clear();
                productpointlist.clear();
                productdeslist.clear();
                productimagelist.clear();
                productpricelist.clear();
                productstatuslist.clear();
                datelist.clear();

                productlogdetail(value);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        productlogdetail("all");
    }

    @Override
    public void onResume() {
        super.onResume();
        countiamge = (ImageView) findViewById(R.id.scancount);
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
        LayoutInflater inflator = (LayoutInflater) Mygifts.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

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

    private void productlogdetail(String status) {

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
                        String productdata = jsonObject.getString("product_data");
                        JSONArray jsonArray = new JSONArray(productdata);


                        for (int i = 0; i < jsonArray.length(); i++) {
                            String getvalue = jsonArray.get(i).toString();
                            JSONObject jsonObject1 = new JSONObject(getvalue);
                            String transaction_id = jsonObject1.getString("transaction_id");
                            String product_id = jsonObject1.getString("product_id");
                            String points = jsonObject1.getString("points");
                            String description = jsonObject1.getString("description");
                            String image_url = jsonObject1.getString("image_url");
                            String price = jsonObject1.getString("price");
                            String delivered = jsonObject1.getString("giftstatus");
                            String delivery_date = jsonObject1.getString("delivery_date");
                            String shipped_date = jsonObject1.getString("shipped_date");
                            String expected_delivery_date = jsonObject1.getString("expected_delivery_date");
                            String concat = delivery_date + "@" + shipped_date + "#" + expected_delivery_date;
                            transactionidlist.add(transaction_id);
                            productidlist.add(product_id);
                            productpointlist.add(points);
                            productdeslist.add(description);
                            productimagelist.add(image_url);
                            productpricelist.add(price);
                            productstatuslist.add(delivered);
                            datelist.add(concat);


                        }

                        giftAdapter = new GiftAdapter(getApplicationContext(), transactionidlist, productidlist, productpointlist,
                                productdeslist, productimagelist, productpricelist, productstatuslist, datelist, Mygifts.this);

                        giftlistview.setAdapter(giftAdapter);
                        llNoGift.setVisibility(datelist.isEmpty() ? View.VISIBLE : View.GONE);
                        //giftlistview.startAnimation(bottom);

                    } else {
                        llNoGift.setVisibility(View.VISIBLE);
                        Common.Customtoast(Mygifts.this, "Please try again");
                    }
                } catch (JSONException e) {
                    llNoGift.setVisibility(View.VISIBLE);
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
        String userid = Common.getPreferences(getApplicationContext(), "user_id");
        String user_type = Common.getPreferences(getApplicationContext(), "user_type");
        //Toast.makeText(getApplicationContext(),""+userid+" "+user_type,Toast.LENGTH_SHORT).show();
        String jsonstring = "{\"user_id\":\"" + "" + userid + "\", \"user_type\":\"" + user_type + "\",\"status\":\"" + status + "\"}";
        String usertype = Common.getPreferences(getApplicationContext(), "user_type");
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        okhttp3.RequestBody body = RequestBody.create(JSON, jsonstring);
        Request request = new Request.Builder()
                .url(Common.mainurl + "transaction/giftreqlist")
                .header("Authorization", "Basic " + Base64.encodeToString(data, Base64.NO_WRAP))
                .post(body)
                .build();

        okHttpRequest.httpPost(Mygifts.this, request);
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
                            Common.Customtoast(Mygifts.this, jsonObject.optString("message"));
                        } else {
                            Common.Customtoast(Mygifts.this, "Please try again");
                        }
                    } else {

                        Common.Customtoast(Mygifts.this, "Please try again");
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
        okHttpRequest.httpPost(Mygifts.this, request);
    }


}
