package co.gladminds.bajajcvl.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import co.gladminds.bajajcvl.Common.Common;
import co.gladminds.bajajcvl.R;
import co.gladminds.bajajcvl.adapter.ProductRecycleAdapter;
import co.gladminds.bajajcvl.adapter.Redeemadapter;
import co.gladminds.bajajcvl.interphace.OnResponseListener;
import co.gladminds.bajajcvl.models.Product;
import co.gladminds.bajajcvl.util.OkHttpRequest;
import okhttp3.MediaType;
import okhttp3.Request;

public class ProductCatalogActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private List<Product> productList, productListFinal;
    private ProductRecycleAdapter adapter;
    private String user_id;
    private EditText searchedittext;
    private ImageView searchicon;
    private Animation bottom, top;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_log);

        user_id = Common.getPreferences(getApplicationContext(), "user_id");

        bottom = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bottom_anim);
        top = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.top_anim);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
//        progressBar = (ProgressBar) findViewById(R.id.progressBar);
//        progressBar.setVisibility(View.GONE);

        searchedittext = (EditText) findViewById(R.id.redeemsearchedittext);
        searchicon = (ImageView) findViewById(R.id.productsearch);

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(llm);
        productList = new ArrayList<>();
        productListFinal = new ArrayList<>();
        adapter = new ProductRecycleAdapter(this, productList);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

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

                if (textLength < 2)
                    return;
                //clear the initial data set
                productList.clear();

                for (int i = 0; i < productListFinal.size(); i++) {
                    if (productListFinal.get(i).getProductId().contains(searchString) || productListFinal.get(i).getDescription().contains(searchString)) {
                        productList.add(productListFinal.get(i));
                    }
                }

                adapter.notifyDataSetChanged();
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });


        productlogdetail();

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
                        Gson gson = new Gson();
                        Product[] list = gson.fromJson(productdata, Product[].class);
                        productList.clear();
                        productList.addAll(Arrays.asList(list));


                        Collections.sort(productList, new Comparator<Product>() {
                            public int compare(Product o1, Product o2) {
                                try {
                                    return Integer.parseInt(o1.getPoints()) - Integer.parseInt(o2.getPoints());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    return 0;
                                }
                            }
                        });

                        productListFinal.addAll(productList);

                        adapter.notifyDataSetChanged();

//                        for (int i = 0; i < jsonArray.length(); i++) {
//                            String getvalue = jsonArray.get(i).toString();
//                            JSONObject jsonObject1 = new JSONObject(getvalue);
//                            Product product = new Product();
//
//                            String product_id = jsonObject1.getString("product_id");
//                            String points = jsonObject1.getString("points");
//                            String description = jsonObject1.getString("description");
//                            String image_url = jsonObject1.getString("image_url");
//                            String seekbarper = jsonObject1.getString("point_collect");
//                            String target = jsonObject1.getString("target");
//                            String point_req_no = jsonObject1.getString("point_req_no");
//                            String concat = product_id + "@" + points + "#" + description + "$" + image_url + "%" + seekbarper + "&" + target + "*" + point_req_no;
//
//                            productidlist.add(concat);
//                            productlistone.add(concat);
//
//                        }
//                        redeemadapter = new Redeemadapter(getApplicationContext(), productidlist, ProductLog.this);
//                        listView.setAdapter(redeemadapter);
//                        listView.startAnimation(bottom);

                    } else {

                        Common.Customtoast(ProductCatalogActivity.this, "Please try again");
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
        String url = Common.mainurl + "transaction/productcatlog/" + usertype + "/" + user_id;


        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        Request request = new Request.Builder()
                .url(url)
                .header("Authorization", "Basic " + Base64.encodeToString(data, Base64.NO_WRAP))
                .build();

        Log.e("URL", "" + url);
        okHttpRequest.httpPost(this, request);
    }

}
