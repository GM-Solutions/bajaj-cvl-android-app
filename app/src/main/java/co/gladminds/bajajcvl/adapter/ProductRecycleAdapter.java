package co.gladminds.bajajcvl.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.like.LikeButton;
import com.like.OnAnimationEndListener;
import com.like.OnLikeListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import co.gladminds.bajajcvl.Common.Common;
import co.gladminds.bajajcvl.R;
import co.gladminds.bajajcvl.interphace.OnResponseListener;
import co.gladminds.bajajcvl.models.Product;
import co.gladminds.bajajcvl.models.UPC;
import co.gladminds.bajajcvl.util.OkHttpRequest;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by Nikhil on 1-12-2017.
 */
public class ProductRecycleAdapter extends RecyclerView.Adapter<ProductRecycleAdapter.ViewHolder> {

    private List<Product> arrayList;
    private static Activity activity;
    private static OnClickListener onClickListener;


    public ProductRecycleAdapter(Activity activity, List<Product> dataArrayList) {
        this.arrayList = dataArrayList;
        this.activity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.redeemview, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Product product = arrayList.get(position);

        if (product.getTarget().equalsIgnoreCase("1")) {
            holder.likeButton.setLiked(true);
            holder.likeButton.setVisibility(View.VISIBLE);
        }
        if (product.getTarget().equalsIgnoreCase("0")) {
            holder.likeButton.setLiked(false);
            holder.likeButton.setVisibility(View.VISIBLE);
        }
        if (product.getTarget().equalsIgnoreCase("")) {
            holder.likeButton.setVisibility(View.INVISIBLE);
        }

        if (product.getPointCollect() == 100) {
            holder.redeembutton.setVisibility(View.VISIBLE);
            holder.redeembutton.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.blink));
            holder.redeembuttonone.setVisibility(View.INVISIBLE);
        } else {
            holder.redeembuttonone.setVisibility(View.VISIBLE);
            holder.redeembutton.setVisibility(View.INVISIBLE);
            holder.redeembutton.clearAnimation();
        }

        holder.seekBar.setMax(100);
        holder.seekBar.setProgress(Integer.parseInt(product.getPoints()));


        holder.likeButton.setOnAnimationEndListener(new OnAnimationEndListener() {
            @Override
            public void onAnimationEnd(LikeButton likeButton) {
                Log.d("Animation End for %s", "" + likeButton);
            }
        });

        if (!TextUtils.isEmpty(product.getImageUrl())) {
            Glide.with(activity).load(product.getImageUrl())
                    .thumbnail(0.5f)
                    .crossFade()
                    //.placeholder(context.getResources().getDrawable(R.drawable.placeholder))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.productimage);
        }
        String finaldesvalue = "";
        if (product.getDescription().length() > 30) {
            finaldesvalue = product.getDescription().substring(0, 30).concat("...");
            holder.description.setText(finaldesvalue);
        } else {
            holder.description.setText(product.getDescription());
        }


        holder.pointview.setText("Points: " + product.getPoints());
        holder.requiredpoint.setText("Required Point: " + product.getPointReqNo());
        holder.productid.setText("Product id: " + product.getProductId());
        holder.redeembutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDailog(arrayList.get(position).getProductId());
            }
        });

        holder.redeembuttonone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Common.Customtoast(activity, "You are not able to redeem");
            }
        });


        holder.likeButton.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                Settotarget("" + arrayList.get(position).getProductId(), "1");
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                Settotarget("" + arrayList.get(position).getProductId(), "3");
            }
        });


    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView description, pointview, productid, redeembutton, requiredpoint, redeembuttonone;
        ImageView productimage;
        LikeButton likeButton;
        ProgressBar seekBar;
        Animation rotate;

        public ViewHolder(View v) {
            super(v);
            this.productimage = (ImageView) v.findViewById(R.id.profile_image);
            this.description = (TextView) v.findViewById(R.id.redeemdescription);
            this.pointview = (TextView) v.findViewById(R.id.redeempoints);
            this.productid = (TextView) v.findViewById(R.id.redeemproductid);
            this.redeembutton = (TextView) v.findViewById(R.id.redeembutton);
            this.requiredpoint = (TextView) v.findViewById(R.id.redeempointrequire);
            this.redeembuttonone = (TextView) v.findViewById(R.id.redeembuttonone);

            this.likeButton = (LikeButton) v.findViewById(R.id.thumb_button);
            this.seekBar = (ProgressBar) v.findViewById(R.id.pb);
            rotate = AnimationUtils.loadAnimation(activity, R.anim.rotateone);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.delete:
                    if (onClickListener != null) {
                        onClickListener.onDeleteClick(getAdapterPosition());
                    }
                    break;
                case R.id.tvVerify:
                    if (onClickListener != null) {
                        onClickListener.onVerify(getAdapterPosition());
                    }
                    break;
                default:
                    if (onClickListener != null) {
                        onClickListener.onItemClick(getAdapterPosition());
                    }
                    break;
            }

        }
    }

    public interface OnClickListener {
        public void onItemClick(int position);

        public void onVerify(int position);

        public void onUPCChange(int position, String Upc);

        public void onDeleteClick(int position);

    }

    public void SetOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }


    public void showDailog(final String idvalue) {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ContextThemeWrapper ctw = new ContextThemeWrapper(activity, R.style.AppDialog);

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
                            Common.Customtoast(activity, jsonObject.optString("message"));
                        } else {
                            Common.Customtoast(activity, "Please try again");
                        }
                    } else {

                        Common.Customtoast(activity, "Please try again");
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
        String userid = Common.getPreferences(activity, "user_id");
        String user_type = Common.getPreferences(activity, "user_type");
        String mobile_no = Common.getPreferences(activity, "mobile_no");
        String user_code = Common.getPreferences(activity, "user_code");
        String jsonstring = "{\"product_id\":\"" + "" + productid + "\", \"user_id\":\"" + userid + "\"," +
                "\"user_type\":" + user_type + ", \"mobile_no\":\"" + mobile_no + "\", \"user_code\":\"" + user_code + "\"}";
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        okhttp3.RequestBody body = RequestBody.create(JSON, jsonstring);
        Request request = new Request.Builder()
                .url(Common.mainurl + "transaction/pointredeem")
                .header("Authorization", "Basic " + Base64.encodeToString(data, Base64.NO_WRAP))
                .post(body)
                .build();
        okHttpRequest.httpPost(activity, request);
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
                            Common.Customtoast(activity, jsonObject.optString("message"));
                        } else {
                            Common.Customtoast(activity, jsonObject.optString("message"));
                        }
                    } else {

                        Common.Customtoast(activity, "Please try again");
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

        String userid = Common.getPreferences(activity, "user_id");
        String user_type = Common.getPreferences(activity, "user_type");
        String mobile_no = Common.getPreferences(activity, "mobile_no");
        String user_code = Common.getPreferences(activity, "user_code");
        String jsonstring = "{\"product_id\":\"" + "" + productid + "\", \"user_id\":\"" + userid + "\"," +
                "\"user_type\":" + user_type + ", \"mobile_no\":\"" + mobile_no + "\", \"user_code\":\"" + user_code + "\",\"status\":\"" + status + "\"}";
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        okhttp3.RequestBody body = RequestBody.create(JSON, jsonstring);
        Request request = new Request.Builder()
                .url(Common.mainurl + "transaction/gifttarget")
                .header("Authorization", "Basic " + Base64.encodeToString(data, Base64.NO_WRAP))
                .post(body)
                .build();
        okHttpRequest.httpPost(activity, request);
    }

}
