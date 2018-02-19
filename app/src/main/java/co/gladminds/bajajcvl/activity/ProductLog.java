package co.gladminds.bajajcvl.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import co.gladminds.bajajcvl.Common.Common;
import co.gladminds.bajajcvl.R;
import co.gladminds.bajajcvl.adapter.Redeemadapter;
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

public class ProductLog extends AppCompatActivity {
    private ListView listView;
    private EditText searchedittext;
    private List imagelist, imagelistone, namelist, namelistone, deslist, deslistone, productidlist, productlistone,
            barcodelist, barcodelistone, seekbarlist, seekbarlistone, targetlist, targetlistone;
    SharedPreferences.Editor editor;
    SharedPreferences sharedpreferences;
    private ImageView countiamge, searchicon;
    private int i;
    private String user_id;
    private StringBuilder sb;
    private Animation bottom, top;
    private Redeemadapter redeemadapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_log);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        user_id = Common.getPreferences(getApplicationContext(), "user_id");
        //Toast.makeText(getApplicationContext(),""+user_id,Toast.LENGTH_SHORT).show();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        bottom = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bottom_anim);
        top = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.top_anim);
        listView = (ListView) findViewById(R.id.redeemlistview);
        searchedittext = (EditText) findViewById(R.id.redeemsearchedittext);
        searchicon = (ImageView) findViewById(R.id.productsearch);

        imagelist = new ArrayList();
        imagelistone = new ArrayList();
        namelist = new ArrayList();
        namelistone = new ArrayList();
        deslist = new ArrayList();
        deslistone = new ArrayList();
        productidlist = new ArrayList();
        productlistone = new ArrayList();
        seekbarlist = new ArrayList();
        seekbarlistone = new ArrayList();
        targetlist = new ArrayList();
        targetlistone = new ArrayList();
        targetlist.clear();
        imagelist.clear();
        seekbarlist.clear();
        namelist.clear();
        deslist.clear();
        productidlist.clear();
        productlistone.clear();
        targetlistone.clear();
        imagelistone.clear();
        seekbarlistone.clear();
        namelistone.clear();
        deslistone.clear();
        productlogdetail();

        searchicon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchedittext.setVisibility(View.VISIBLE);
                searchedittext.startAnimation(top);

            }
        });
        searchedittext.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //get the text in the EditText
                String searchString = searchedittext.getText().toString().replace(" ", "");
                int textLength = searchString.length();

                //clear the initial data set
                productidlist.clear();

                for (int i = 0; i < productlistone.size(); i++) {
                    String playerName = productlistone.get(i).toString();
                    String replacestring = "#";
                    String descrip = playerName.substring(playerName.indexOf("#"), playerName.indexOf("$"))
                            .replace("#", "").replace(" ", "").toLowerCase();
                    String descripone = playerName.substring(playerName.indexOf("#"), playerName.indexOf("$"))
                            .replace("#", "").replace(" ", "").toLowerCase();
                    playerName = playerName.substring(0, playerName.indexOf("@")).toLowerCase().replace(" ", "");
                    String playerNameone = productlistone.get(i).toString().substring(0, productlistone.get(i).toString().indexOf("@"))
                            .toUpperCase().replace(" ", "");

                    if (textLength <= descrip.length()) {
                        //compare the String in EditText with Names in the ArrayList
                        if (playerName.contains(searchString) || playerNameone.contains(searchString)
                                || descrip.contains(searchString) || descripone.contains(searchString))
                            productidlist.add(productlistone.get(i));

                    }
                }

                redeemadapter.notifyDataSetChanged();
            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {


            }

            public void afterTextChanged(Editable s) {


            }
        });


    }

    private void productlogdetail() {

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
                            String product_id = jsonObject1.getString("product_id");
                            String points = jsonObject1.getString("points");
                            String description = jsonObject1.getString("description");
                            String image_url = jsonObject1.getString("image_url");
                            String seekbarper = jsonObject1.getString("point_collect");
                            String target = jsonObject1.getString("target");
                            String point_req_no = jsonObject1.getString("point_req_no");
                            String concat = product_id + "@" + points + "#" + description + "$" + image_url + "%" + seekbarper + "&" + target + "*" + point_req_no;

                            productidlist.add(concat);
                            productlistone.add(concat);

                        }
                        redeemadapter = new Redeemadapter(getApplicationContext(), productidlist, ProductLog.this);
                        listView.setAdapter(redeemadapter);
                        listView.startAnimation(bottom);

                    } else {

                        Common.Customtoast(ProductLog.this, "Please try again");
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

        String usertype = Common.getPreferences(getApplicationContext(), "user_type");
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        Request request = new Request.Builder()
                .url(Common.mainurl + "transaction/productcatlog/" + usertype + "/" + user_id)
                .header("Authorization", "Basic " + Base64.encodeToString(data, Base64.NO_WRAP))
                .build();

        okHttpRequest.httpPost(ProductLog.this, request);
    }

    @Override
    public void onResume() {
        super.onResume();
        //listView.startAnimation(bottom);
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
        LayoutInflater inflator = (LayoutInflater) ProductLog.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

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
                            Common.Customtoast(ProductLog.this, jsonObject.optString("message"));
                        } else {
                            Common.Customtoast(ProductLog.this, "Please try again");
                        }
                    } else {

                        Common.Customtoast(ProductLog.this, "Please try again");
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
        okHttpRequest.httpPost(ProductLog.this, request);
    }


}
