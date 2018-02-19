package co.gladminds.bajajcvl.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.support.v7.view.ContextThemeWrapper;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import co.gladminds.bajajcvl.Common.Common;
import co.gladminds.bajajcvl.R;
import co.gladminds.bajajcvl.interphace.OnResponseListener;
import co.gladminds.bajajcvl.util.OkHttpRequest;

import com.like.LikeButton;
import com.like.OnAnimationEndListener;
import com.like.OnLikeListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import pl.droidsonroids.gif.GifImageView;

/**
 * Created by vikram on 9/11/2017.
 */

public class Redeemadapter extends BaseAdapter implements OnAnimationEndListener {
    Context context;
    LayoutInflater inflater;
    List productlist;
    Activity parentActivity;
    private Animation rotate;

    public Redeemadapter(Context con, List productlist, Activity parentActivity) {
        // TODO Auto-generated constructor stub
        this.productlist = productlist;
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

    static class ViewHolder {
        TextView description, pointview, productid, redeembutton, requiredpoint, redeembuttonone;
        ImageView productimage;
        LikeButton likeButton;
        ProgressBar seekBar;
        Animation rotate;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup arg2) {


        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.redeemview, null);
            holder = new ViewHolder();
            holder.productimage = (ImageView) convertView.findViewById(R.id.profile_image);
            holder.description = (TextView) convertView.findViewById(R.id.redeemdescription);
            holder.pointview = (TextView) convertView.findViewById(R.id.redeempoints);
            holder.productid = (TextView) convertView.findViewById(R.id.redeemproductid);
            holder.redeembutton = (TextView) convertView.findViewById(R.id.redeembutton);
            holder.requiredpoint = (TextView) convertView.findViewById(R.id.redeempointrequire);
            holder.redeembuttonone = (TextView) convertView.findViewById(R.id.redeembuttonone);

            holder.likeButton = (LikeButton) convertView.findViewById(R.id.thumb_button);
            holder.seekBar = (ProgressBar) convertView.findViewById(R.id.pb);
            rotate = AnimationUtils.loadAnimation(context, R.anim.rotateone);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        String fullstring = productlist.get(position).toString();


        final String idvalue = fullstring.substring(0, fullstring.indexOf("@"));
        final String seekbarvalue = fullstring.substring(fullstring.indexOf("%"), fullstring.indexOf("&")).replace("%", "");
        final String imagevalue = fullstring.substring(fullstring.indexOf("$"), fullstring.indexOf("%")).replace("$", "");
        final String desvalue = fullstring.substring(fullstring.indexOf("#"), fullstring.indexOf("$")).replace("#", "");
        final String pointvalue = fullstring.substring(fullstring.indexOf("@"), fullstring.indexOf("#")).replace("@", "");
        final String targetvalue = fullstring.substring(fullstring.indexOf("&"), fullstring.indexOf("*")).replace("&", "");
        final String requirevalue = fullstring.substring(fullstring.indexOf("*") + 1);
        Log.e("targetvalue is", targetvalue);
        Log.e("seekbarvalue is", seekbarvalue);
        Log.e("imagevalue is", imagevalue);
        Log.e("desvalue is", desvalue);
        Log.e("pointvalue is", pointvalue);
        Log.e("idvalue is", idvalue);

        if (targetvalue.equalsIgnoreCase("1")) {
            holder.likeButton.setLiked(true);
            holder.likeButton.setVisibility(View.VISIBLE);
        }
        if (targetvalue.equalsIgnoreCase("0")) {
            holder.likeButton.setLiked(false);
            holder.likeButton.setVisibility(View.VISIBLE);
        }
        if (targetvalue.equalsIgnoreCase("")) {
            holder.likeButton.setVisibility(View.INVISIBLE);
        }

        if (seekbarvalue.equalsIgnoreCase("100")) {
            holder.redeembutton.setVisibility(View.VISIBLE);
            holder.redeembuttonone.setVisibility(View.INVISIBLE);
        } else {
            holder.redeembuttonone.setVisibility(View.VISIBLE);
            holder.redeembutton.setVisibility(View.INVISIBLE);
        }
        holder.seekBar.setMax(100);
        holder.seekBar.setProgress(Integer.parseInt(seekbarvalue));


        holder.likeButton.setOnAnimationEndListener(this);

        if (!imagevalue.equalsIgnoreCase("")) {
            Glide.with(context).load(imagevalue)
                    .thumbnail(0.5f)
                    .crossFade()
                    //.placeholder(context.getResources().getDrawable(R.drawable.placeholder))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.productimage);
        }
        String finaldesvalue = "";
        if (desvalue.length() > 30) {
            finaldesvalue = desvalue.substring(0, 30).concat("...");
            holder.description.setText(finaldesvalue);
        } else {
            holder.description.setText(desvalue);
        }


        holder.pointview.setText("Points: " + pointvalue);
        holder.requiredpoint.setText("Required Point: " + requirevalue);
        holder.productid.setText("Product id: " + idvalue);
        holder.redeembutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String fullstring = productlist.get(position).toString();

                final String idvalue = fullstring.substring(0, fullstring.indexOf("@"));
                showDailog(idvalue);

            }
        });

        holder.redeembuttonone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Common.Customtoast(parentActivity, "You are not able to redeem");
            }
        });


        holder.likeButton.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                Settotarget("" + productlist.get(position), "1");
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                Settotarget("" + productlist.get(position), "3");
            }
        });


        return convertView;

    }

    private void Redeempoint(String productid) {

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
                            Common.Customtoast(parentActivity, jsonObject.optString("message"));
                        } else {
                            Common.Customtoast(parentActivity, "Please try again");
                        }
                    } else {

                        Common.Customtoast(parentActivity, "Please try again");
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
        String userid = Common.getPreferences(context, "user_id");
        String user_type = Common.getPreferences(context, "user_type");
        String mobile_no = Common.getPreferences(context, "mobile_no");
        String user_code = Common.getPreferences(context, "user_code");
        String jsonstring = "{\"product_id\":\"" + "" + productid + "\", \"user_id\":\"" + userid + "\"," +
                "\"user_type\":" + user_type + ", \"mobile_no\":\"" + mobile_no + "\", \"user_code\":\"" + user_code + "\"}";
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        okhttp3.RequestBody body = RequestBody.create(JSON, jsonstring);
        Request request = new Request.Builder()
                .url(Common.mainurl + "transaction/pointredeem")
                .header("Authorization", "Basic " + Base64.encodeToString(data, Base64.NO_WRAP))
                .post(body)
                .build();
        okHttpRequest.httpPost(parentActivity, request);
    }


    private void Settotarget(String productid, String status) {

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
                            Common.Customtoast(parentActivity, jsonObject.optString("message"));
                        } else {
                            Common.Customtoast(parentActivity, jsonObject.optString("message"));
                        }
                    } else {

                        Common.Customtoast(parentActivity, "Please try again");
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

        String userid = Common.getPreferences(context, "user_id");
        String user_type = Common.getPreferences(context, "user_type");
        String mobile_no = Common.getPreferences(context, "mobile_no");
        String user_code = Common.getPreferences(context, "user_code");
        String jsonstring = "{\"product_id\":\"" + "" + productid + "\", \"user_id\":\"" + userid + "\"," +
                "\"user_type\":" + user_type + ", \"mobile_no\":\"" + mobile_no + "\", \"user_code\":\"" + user_code + "\",\"status\":\"" + status + "\"}";
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        okhttp3.RequestBody body = RequestBody.create(JSON, jsonstring);
        Request request = new Request.Builder()
                .url(Common.mainurl + "transaction/gifttarget")
                .header("Authorization", "Basic " + Base64.encodeToString(data, Base64.NO_WRAP))
                .post(body)
                .build();
        okHttpRequest.httpPost(parentActivity, request);
    }


    @Override
    public void onAnimationEnd(LikeButton likeButton) {
        Log.d("Animation End for %s", "" + likeButton);
    }


    public void showDailog(final String idvalue) {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ContextThemeWrapper ctw = new ContextThemeWrapper(parentActivity, R.style.AppDialog);

                final Dialog dialog = new Dialog(ctw);
                //dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.redeemdialog);
                dialog.setCancelable(true);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationtwo;
                TextView cancel = (TextView) dialog.findViewById(R.id.cancelredeem);
                TextView confirm = (TextView) dialog.findViewById(R.id.confirmredeem);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                confirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Redeempoint(idvalue);
                        dialog.dismiss();
                    }
                });

                dialog.show();

            }
        }, 200);

    }


}