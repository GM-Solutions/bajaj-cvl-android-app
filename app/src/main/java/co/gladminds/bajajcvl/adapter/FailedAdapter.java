package co.gladminds.bajajcvl.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import co.gladminds.bajajcvl.Common.Common;
import co.gladminds.bajajcvl.activity.Productscan;
import co.gladminds.bajajcvl.interphace.OnResponseListener;
import co.gladminds.bajajcvl.util.OkHttpRequest;
import com.google.firebase.crash.FirebaseCrash;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by vikram on 11/28/2017.
 */

public class FailedAdapter extends BaseAdapter {
    Context context;
    LayoutInflater inflater;
    List productlist,productlistone;
    Activity parentActivity;
    private Animation upanim,downanim;
    private int count=0;
    SharedPreferences.Editor editor;
    SharedPreferences sharedpreferences;
    private StringBuilder appendstring,checkedupc;
    private Dialog faileddialog;
    private Button varifyallbutton;
    public FailedAdapter(Context con, List productlist,Dialog faileddialog,Activity parentActivity) {
        // TODO Auto-generated constructor stub
        this.productlist = productlist;
        this.productlistone = productlist;
        this.faileddialog = faileddialog;
        this.context = con;
        this.parentActivity = parentActivity;
        inflater = LayoutInflater.from(con);
        // this.acp=a;

    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return productlist.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return productlist.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(final int position, View view, ViewGroup arg2)
    {
        final ViewHolder holder;
        if (view == null) {
            view = inflater.inflate(co.gladminds.bajajcvl.R.layout.failedupc_layout,null);
            holder = createViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        varifyallbutton = (Button)faileddialog.findViewById(co.gladminds.bajajcvl.R.id.varifyallbutton);
        checkedupc = new StringBuilder();
        sharedpreferences = context.getSharedPreferences("bajaj_pref", Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();
        upanim = AnimationUtils.loadAnimation(context, co.gladminds.bajajcvl.R.anim.slideup);
        downanim = AnimationUtils.loadAnimation(context, co.gladminds.bajajcvl.R.anim.slidedown);
        holder.failedtextview.setText(productlist.get(position).toString());
        holder.upcedittext.setText(productlist.get(position).toString());
         holder.editbutton.setTag(position);
          holder.editbutton.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View view) {
               holder.savebutton.setVisibility(View.VISIBLE);
               holder.editbutton.setVisibility(View.INVISIBLE);
               holder.upcedittext.setVisibility(View.VISIBLE);
               holder.failedtextview.setVisibility(View.INVISIBLE);
              }
          });
          holder.savebutton.setTag(position);
          holder.savebutton.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View view) {
                  InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
                  imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                  holder.savebutton.setVisibility(View.INVISIBLE);
                  holder.editbutton.setVisibility(View.VISIBLE);
                  holder.upcedittext.setVisibility(View.INVISIBLE);
                  holder.failedtextview.setVisibility(View.VISIBLE);
                  productlist.set(position,holder.upcedittext.getText().toString());
                  Set<String> setTestId = new HashSet<String>();
                  setTestId.addAll(productlist);
                  productlist.clear();
                  productlist.addAll(setTestId);
                  editor.putStringSet("failedcountbarcode", setTestId);
                  editor.commit();
                  notifyDataSetChanged();
              }
          });
          holder.deletebutton.setTag(position);
          holder.deletebutton.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View view) {
                  productlist.remove(position);
                  notifyDataSetChanged();
                  if(productlist.size() == 0){

                     Productscan.faileddialog.dismiss();
                     Productscan.failedcart.setVisibility(View.INVISIBLE);
                  }
                  Set<String> setTestId = new HashSet<String>();
                  setTestId.addAll(productlist);
                  productlist.clear();
                  productlist.addAll(setTestId);
                  editor.putStringSet("failedcountbarcode", setTestId);
                  editor.commit();
              }
          });
          holder.varifybutton.setTag(position);
          holder.varifybutton.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View view) {
                  if(Productscan.barcodelist.size() < 10){
                      Upcvarify(productlist.get(position).toString(),"2",position);
                  }else{
                      Common.Customtoast(parentActivity,"You can save 10 products at a time.");
                  }

              }
          });
          holder.checkBox.setTag(position);
          holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
              @Override
              public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                  if(b){
                      count++;
                      checkedupc.append(productlist.get(position).toString().substring(0,8)+",");
                       varifyallbutton.setText("Varify All ("+""+count+")");
                  }else{
                       count--;
                      int i = checkedupc.indexOf(productlist.get(position).toString().substring(0,8));
                      if (i != -1) {
                          checkedupc.delete(i, i + productlist.get(position).toString().length());
                      }
                      varifyallbutton.setText("Varify All (" + "" + count + ")");
                      if(count == 0) {
                          varifyallbutton.setText("Varify All");
                      }
                  }

                  //Toast.makeText(context, ""+checkedupc.toString(), Toast.LENGTH_SHORT).show();
              }
          });

          varifyallbutton.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View view) {
                  Upcvarify(checkedupc.toString(),"2",position);
              }
          });

        return view;

    }

    static class ViewHolder {
        TextView editbutton,varifybutton,failedtextview,savebutton,deletebutton;
        private EditText upcedittext;
        CheckBox checkBox;



    }
    private ViewHolder createViewHolder(View view) {
        ViewHolder holder = new ViewHolder();
        holder.editbutton = (TextView) view.findViewById(co.gladminds.bajajcvl.R.id.editbutton);
        holder.varifybutton = (TextView) view.findViewById(co.gladminds.bajajcvl.R.id.varifybutton);
        holder.upcedittext = (EditText) view.findViewById(co.gladminds.bajajcvl.R.id.failededittext);
        holder.failedtextview = (TextView) view.findViewById(co.gladminds.bajajcvl.R.id.failedtextview);
        holder.savebutton = (TextView) view.findViewById(co.gladminds.bajajcvl.R.id.savebuttonfailed);
        holder.deletebutton = (TextView) view.findViewById(co.gladminds.bajajcvl.R.id.deletebutton);
        holder.checkBox= (CheckBox) view.findViewById(co.gladminds.bajajcvl.R.id.failedcheckbox);
        return holder;
    }

    private  void Upcvarify(final String upccode, String source, final int position) {
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
                            JSONArray jsonArray = jsonObject.getJSONArray("upc");
                            int count = Integer.parseInt(jsonObject.optString("count"));
                            appendstring = new StringBuilder();
                            for(int i=0;i<count;i++){
                                final JSONObject jb = jsonArray.getJSONObject(i);
                                String upc = jb.optString("upc");
                                String status_used = jb.optString("status_used");
                                String message = jb.optString("message");
                                if(status_used.equalsIgnoreCase("no")){
                                    if(Productscan.barcodelist.contains(upc)){
                                        appendstring.append(upc+" Already in cart"+"\n");
                                        //Common.Customtoast(Productscan.this,"Already in cart");
                                    }else{
                                        productlistone.remove(position);
                                        if(productlistone.size()==0){
                                            Productscan.faileddialog.dismiss();
                                            Productscan.failedcart.setVisibility(View.INVISIBLE);
                                        }
                                        Set<String> setTestId = new HashSet<String>();
                                        setTestId.addAll(productlistone);
                                        productlistone.clear();
                                        productlistone.addAll(setTestId);
                                        editor.putStringSet("failedcountbarcode", setTestId);
                                        editor.commit();
                                        notifyDataSetChanged();
                                        Productscan.barcodelist.add(upc);

                                        //Common.Customtoast(Productscan.this,message);
                                        appendstring.append(message);
                                    }

                                    Set<String> setTestId = new HashSet<String>();
                                    setTestId.addAll(Productscan.barcodelist);
                                    Productscan.barcodelist.clear();
                                    Productscan.barcodelist.addAll(setTestId);
                                    editor.putStringSet("countbarcode", setTestId);
                                    editor.commit();
                                    if (Productscan.barcodelist.size() == 1) {
                                        Productscan.countiamge.setImageResource(co.gladminds.bajajcvl.R.drawable.cartone);
                                    } else if (Productscan.barcodelist.size() == 2) {
                                        Productscan.countiamge.setImageResource(co.gladminds.bajajcvl.R.drawable.carttwo);
                                    } else if (Productscan.barcodelist.size() == 3) {
                                        Productscan.countiamge.setImageResource(co.gladminds.bajajcvl.R.drawable.cartthree);
                                    } else if (Productscan.barcodelist.size() == 4) {
                                        Productscan.countiamge.setImageResource(co.gladminds.bajajcvl.R.drawable.cartfour);
                                    } else if (Productscan.barcodelist.size() == 5) {
                                        Productscan.countiamge.setImageResource(co.gladminds.bajajcvl.R.drawable.cartfive);
                                    } else if (Productscan.barcodelist.size() == 6) {
                                        Productscan.countiamge.setImageResource(co.gladminds.bajajcvl.R.drawable.cartsix);
                                    } else if (Productscan.barcodelist.size() == 7) {
                                        Productscan.countiamge.setImageResource(co.gladminds.bajajcvl.R.drawable.cartseven);
                                    } else if (Productscan.barcodelist.size() == 8) {
                                        Productscan.countiamge.setImageResource(co.gladminds.bajajcvl.R.drawable.carteight);
                                    } else if (Productscan.barcodelist.size() == 9) {
                                        Productscan.countiamge.setImageResource(co.gladminds.bajajcvl.R.drawable.cartnine);
                                    } else if (Productscan.barcodelist.size() == 10) {
                                        Productscan.countiamge.setImageResource(co.gladminds.bajajcvl.R.drawable.cartten);
                                    } else {
                                        Productscan.countiamge.setImageResource(co.gladminds.bajajcvl.R.drawable.cart);
                                    }

                                }else if(status_used.equalsIgnoreCase("yes")){
                                    //Common.Customtoast(Productscan.this,message);
                                    appendstring.append(message);
                                }else{
                                    //Common.Customtoast(Productscan.this,message);
                                    appendstring.append(message);

                                }

                            }
                            Common.Customtoast(parentActivity,appendstring.toString());
                            //scanbuttonone.setVisibility(View.INVISIBLE);
                            //scanbutton.setVisibility(View.VISIBLE);


                            //textView.setVisibility(View.VISIBLE);
                            //*  scanbutton.setVisibility(View.VISIBLE);




                        }else{
                            Common.Customtoast(parentActivity,"Please try again");


                            //scanbutton.setVisibility(View.VISIBLE);

                        }
                    }else {

                        Common.Customtoast(parentActivity,"Please try again");

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
        String finalupccode = upccode.replace("\n","");
        Log.e("detect text is",finalupccode);
        String usertype = Common.getPreferences(context,"user_type");
        String jsonstring = "{\"upc\":\""+finalupccode+"\",\"user_type\":\"" + usertype + "\",\"source\":\"" + source + "\"}";
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        okhttp3.RequestBody body = RequestBody.create(JSON, jsonstring);
        Request request = new Request.Builder()
                .url(Common.mainurl+"transaction/upcverifywithpicture")
                .header("Authorization", "Basic "+ Base64.encodeToString(data, Base64.NO_WRAP))
                .post(body)
                .build();
        okHttpRequest.httpPost(parentActivity, request);


    }



}
