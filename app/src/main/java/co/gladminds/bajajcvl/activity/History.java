package co.gladminds.bajajcvl.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
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
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import co.gladminds.bajajcvl.Common.Common;
import co.gladminds.bajajcvl.adapter.HistoryAdapter;
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

public class History extends AppCompatActivity implements AbsListView.OnScrollListener{
    private Animation bottom,top;
    private ListView historylistview;
    private ImageView countiamge;
    SharedPreferences.Editor editor;
    SharedPreferences sharedpreferences;
    private List barcodelist;
    private int i,previousTotal=0,currentPage=0,visibleThreshold=5;
    private StringBuilder sb;
    private HistoryAdapter historyAdapter;
    private EditText searchedittext;
    private ImageView searchimageview;
    private List historylst,historylstone;
    private View footer;
    private boolean loading = true,scrollChecker=false;
    String upc_count="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(co.gladminds.bajajcvl.R.layout.activity_history);
        Toolbar toolbar = (Toolbar) findViewById(co.gladminds.bajajcvl.R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        historylistview = (ListView) findViewById(co.gladminds.bajajcvl.R.id.historylistview);
        searchedittext = (EditText) findViewById(co.gladminds.bajajcvl.R.id.historysearchedittext);
        searchimageview = (ImageView) findViewById(co.gladminds.bajajcvl.R.id.productsearch);
        top = AnimationUtils.loadAnimation(getApplicationContext(), co.gladminds.bajajcvl.R.anim.top_anim);
        bottom = AnimationUtils.loadAnimation(getApplicationContext(), co.gladminds.bajajcvl.R.anim.bottom_anim);
        historylst = new ArrayList();
        historylstone = new ArrayList();
        historylst.clear();
        historylstone.clear();
        footer = ((LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(co.gladminds.bajajcvl.R.layout.loading_layout, null, false);
        historylistview.setOnScrollListener(this);
        producthistory("0");

        searchimageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchedittext.setVisibility(View.VISIBLE);
                searchedittext.startAnimation(top);

            }
        });
        searchedittext.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //get the text in the EditText
                String searchString=searchedittext.getText().toString();
                int textLength=searchString.length();

                //clear the initial data set
                historylst.clear();

                for(int i=0;i<historylstone.size();i++)
                {
                    String playerName=historylstone.get(i).toString();
                    String part = playerName.substring(playerName.indexOf("&") + 1).toLowerCase();
                    String partone = playerName.substring(playerName.indexOf("&") + 1).toUpperCase();
                    playerName = playerName.substring(0, playerName.indexOf("@")).toLowerCase();
                   String playerNameone = historylstone.get(i).toString().substring(0, historylstone.get(i).toString().indexOf("@")).toUpperCase();


                    if(textLength<=playerName.length()){
                        //compare the String in EditText with Names in the ArrayList
                        if(playerName.contains(searchString)||playerNameone.contains(searchString)
                                || part.contains(searchString)||partone.contains(searchString))
                            historylst.add(historylstone.get(i));

                    }
                }

                historyAdapter.notifyDataSetChanged();
            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {


            }

            public void afterTextChanged(Editable s) {


            }
        });


    }

      private void producthistory(String page) {

        OkHttpRequest okHttpRequest = new OkHttpRequest();
        okHttpRequest.setOnResponseListener(new OnResponseListener() {
            @Override
            public void onResponse(String result) {
                // login.setVisibility(View.VISIBLE);
                // progressBar.setVisibility(View.GONE);
                Log.e("RESULT",result);
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(result);
                    if (result!=null) {
                         upc_count = jsonObject.getString("upc_count");
                        String productdata = jsonObject.getString("upc_history");
                        JSONArray jsonArray = new JSONArray(productdata);

                        for(int i=0;i<jsonArray.length();i++){
                            String getvalue = jsonArray.get(i).toString();
                            JSONObject jsonObject1 = new JSONObject(getvalue);
                            String transaction_id = jsonObject1.getString("transaction_id");
                            String created_date = jsonObject1.getString("created_date");
                            String upc = jsonObject1.getString("upc");
                            String total_points = jsonObject1.getString("total_points");
                            String description = jsonObject1.getString("description");
                            String category = jsonObject1.getString("category");
                            String part_number = jsonObject1.getString("part_number");
                            String concat = upc+"@"+created_date+"#"+total_points+"$"+description+"%"+category+"&"+part_number;
                            historylst.add(concat);
                            historylstone.add(concat);

                        }
                        Parcelable state = historylistview.onSaveInstanceState();
                       historyAdapter = new HistoryAdapter(getApplicationContext(),historylst,History.this);
                        historylistview.setAdapter(historyAdapter);
                        historylistview.startAnimation(bottom);
                        historylistview.onRestoreInstanceState(state);

                        try
                        {
                            if(historyAdapter!=null)
                            {
                                int adapterCount = historyAdapter.getCount();
                                int countallInteger = Integer.parseInt(upc_count);

                                if(adapterCount==countallInteger)
                                {
                                    historylistview.removeFooterView(footer);
                                    //checkFooterViewRemovedOrNot = true;
                                }
                            }
                            if (upc_count != null) {

                                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("allApplication", upc_count).commit();
                            }
                        }
                        catch(NullPointerException EE)
                        {

                        }

                    }else {

                        Common.Customtoast(History.this,"Please try again");
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

        String usertype = Common.getPreferences(getApplicationContext(),"user_type");
        String userid = Common.getPreferences(getApplicationContext(),"user_id");
        String jsonstring = "{\"user_type\":\""+usertype+"\",\"user_id\":\""+userid+"\",\"page\":\"" + page + "\"}";
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        okhttp3.RequestBody body = RequestBody.create(JSON, jsonstring);

        Request request = new Request.Builder()
                .url(Common.mainurl+"transaction/upchistory")
                .post(body)
                .header("Authorization", "Basic "+ Base64.encodeToString(data, Base64.NO_WRAP))
                .build();

        okHttpRequest.httpPost(History.this, request);
    }


    @Override
    public void onResume(){
        super.onResume();
        historylistview.startAnimation(bottom);
        countiamge = (ImageView) findViewById(co.gladminds.bajajcvl.R.id.scancount);
        sharedpreferences = getSharedPreferences("bajaj_pref", Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();
        barcodelist = new ArrayList();
        barcodelist.clear();
        try{
            Set<String> set = sharedpreferences.getStringSet("countbarcode", null);
            barcodelist.addAll(set);}catch (Throwable ex){}
        if(barcodelist.size() == 1){
            countiamge.setImageResource(co.gladminds.bajajcvl.R.drawable.cartone);
        }else if(barcodelist.size() == 2){
            countiamge.setImageResource(co.gladminds.bajajcvl.R.drawable.carttwo);
        }else if(barcodelist.size() == 3){
            countiamge.setImageResource(co.gladminds.bajajcvl.R.drawable.cartthree);
        }else if(barcodelist.size() == 4){
            countiamge.setImageResource(co.gladminds.bajajcvl.R.drawable.cartfour);
        }else if(barcodelist.size() == 5){
            countiamge.setImageResource(co.gladminds.bajajcvl.R.drawable.cartfive);
        }else if(barcodelist.size() == 6){
            countiamge.setImageResource(co.gladminds.bajajcvl.R.drawable.cartsix);
        }else if(barcodelist.size() == 7){
            countiamge.setImageResource(co.gladminds.bajajcvl.R.drawable.cartseven);
        }else if(barcodelist.size() == 8){
            countiamge.setImageResource(co.gladminds.bajajcvl.R.drawable.carteight);
        }else if(barcodelist.size() == 9){
            countiamge.setImageResource(co.gladminds.bajajcvl.R.drawable.cartnine);
        }else if(barcodelist.size() == 10){
            countiamge.setImageResource(co.gladminds.bajajcvl.R.drawable.cartten);
        }

        else{
            countiamge.setImageResource(co.gladminds.bajajcvl.R.drawable.cart);
        }
        countiamge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(barcodelist.size()>0){
                    showDailog();}
            }
        });
    }

    Dialog dialogA;
    public void showDailog(){
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
        LayoutInflater inflator = (LayoutInflater) History.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        for( i=0;i<barcodelist.size();i++){
            final View item = inflator.inflate(co.gladminds.bajajcvl.R.layout.barcode_view, null);
            String getvalue = ""+barcodelist.get(i);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(10, 10, 10, 10);
            item.setLayoutParams(layoutParams);
            TextView barcodevalue = (TextView)item.findViewById(co.gladminds.bajajcvl.R.id.barcodetext);
            final TextView cross = (TextView)item.findViewById(co.gladminds.bajajcvl.R.id.barcodecross);
            cross.setTag(i);
            cross.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = 0;
                    if (cross.getTag() instanceof Integer) {
                        position = (Integer) cross.getTag();
                        if(position >= barcodelist.size()){
                            position = barcodelist.size()-1;
                        }
                    }
                    //Toast.makeText(getApplicationContext(),""+position,Toast.LENGTH_SHORT).show();
                    try{
                        barcodelist.remove(position);
                        linearLayout.removeViewAt(position);
                    }catch (Throwable ex){}
                    Set<String> setTestId = new HashSet<String>();
                    setTestId.addAll(barcodelist);
                    editor.putStringSet("countbarcode", setTestId);
                    editor.commit();

                    if(barcodelist.size() == 1){
                        countiamge.setImageResource(co.gladminds.bajajcvl.R.drawable.cartone);
                    }else if(barcodelist.size() == 2){
                        countiamge.setImageResource(co.gladminds.bajajcvl.R.drawable.carttwo);
                    }else if(barcodelist.size() == 3){
                        countiamge.setImageResource(co.gladminds.bajajcvl.R.drawable.cartthree);
                    }else if(barcodelist.size() == 4){
                        countiamge.setImageResource(co.gladminds.bajajcvl.R.drawable.cartfour);
                    }else if(barcodelist.size() == 5){
                        countiamge.setImageResource(co.gladminds.bajajcvl.R.drawable.cartfive);
                    }else if(barcodelist.size() == 6){
                        countiamge.setImageResource(co.gladminds.bajajcvl.R.drawable.cartsix);
                    }else if(barcodelist.size() == 7){
                        countiamge.setImageResource(co.gladminds.bajajcvl.R.drawable.cartseven);
                    }else if(barcodelist.size() == 8){
                        countiamge.setImageResource(co.gladminds.bajajcvl.R.drawable.carteight);
                    }else if(barcodelist.size() == 9){
                        countiamge.setImageResource(co.gladminds.bajajcvl.R.drawable.cartnine);
                    }else if(barcodelist.size() == 10){
                        countiamge.setImageResource(co.gladminds.bajajcvl.R.drawable.cartten);
                    }

                    else{
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
                Log.e("RESULT",result);
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(result);
                    if (result!=null) {
                        if(jsonObject.optBoolean("status") == true){
                            dialogA.dismiss();
                            barcodelist.clear();
                            Set<String> setTestId = new HashSet<String>();
                            setTestId.addAll(barcodelist);
                            editor.putStringSet("countbarcode", setTestId);
                            editor.commit();
                            countiamge.setImageResource(co.gladminds.bajajcvl.R.drawable.cart);
                            Common.Customtoast(History.this,jsonObject.optString("message"));
                        }else{
                            Common.Customtoast(History.this,"Please try again");
                        }
                    }else {

                        Common.Customtoast(History.this,"Please try again");
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
        for(int i=0;i<barcodelist.size();i++){
            String getvalue = ""+barcodelist.get(i);
            sb.append(getvalue+",");
        }
        sb.deleteCharAt(sb.length()-1);
        String upccode = sb.toString();
        String userid = Common.getPreferences(getApplicationContext(),"mobile_no");
        String jsonstring = "{\"mobile_no\":\""+userid+"\",\"upc\":\""+upccode+"\"}";
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        okhttp3.RequestBody body = RequestBody.create(JSON, jsonstring);
        Request request = new Request.Builder()
                .url(Common.mainurl+"transaction/pointcollect")
                .header("Authorization", "Basic "+ Base64.encodeToString(data, Base64.NO_WRAP))
                .post(body)
                .build();
        okHttpRequest.httpPost(History.this, request);
    }
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
        if (loading) {
            if (totalItemCount > previousTotal && scrollChecker == true) {
                loading = false;
                previousTotal = totalItemCount;
                currentPage++;
                scrollChecker=false;
            }
        }
        if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold) && scrollChecker == true) {
            // I load the next page of gigs using a background task,
            // but you can call any function here.
            Toast.makeText(getApplicationContext(),"in this",Toast.LENGTH_SHORT).show();
            producthistory(""+currentPage + 1);
            loading = true;
            scrollChecker = false;
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {

            scrollChecker = true;

        }
    }

}
