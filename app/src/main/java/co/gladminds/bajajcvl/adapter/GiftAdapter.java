package co.gladminds.bajajcvl.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.support.v7.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

import pl.droidsonroids.gif.GifImageView;

/**
 * Created by vikram on 10/6/2017.
 */

public class GiftAdapter extends BaseAdapter {
    Context context;
    LayoutInflater inflater;
    List transactionlist, idlist, pointlist, deslist, imagelist, pricelist, statuslist, datelist;
    TextView description, pointview, redeembutton, redeembuttonone, price, status, productid;
    private ImageView statusimage;
    private GifImageView productimage;
    private ProgressBar seekBar;
    Activity parentActivity;

    public GiftAdapter(Context con, List transactionlist, List idlist, List pointlist, List deslist,
                       List imagelist, List pricelist, List statuslist, List datelist, Activity parentActivity) {
        // TODO Auto-generated constructor stub
        this.transactionlist = transactionlist;
        this.idlist = idlist;
        this.pointlist = pointlist;
        this.deslist = deslist;
        this.imagelist = imagelist;
        this.pricelist = pricelist;
        this.statuslist = statuslist;
        this.datelist = datelist;
        this.context = con;
        this.parentActivity = parentActivity;
        inflater = LayoutInflater.from(con);
        // this.acp=a;

    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return idlist.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return idlist.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(final int position, View cview, ViewGroup arg2) {


        View view = cview;


        view = inflater.inflate(co.gladminds.bajajcvl.R.layout.giftview, null);

        productimage = (GifImageView) view.findViewById(co.gladminds.bajajcvl.R.id.profile_image);
        description = (TextView) view.findViewById(co.gladminds.bajajcvl.R.id.redeemdescription);
        pointview = (TextView) view.findViewById(co.gladminds.bajajcvl.R.id.redeempoints);
        productid = (TextView) view.findViewById(co.gladminds.bajajcvl.R.id.redeemproductid);
        redeembutton = (TextView) view.findViewById(co.gladminds.bajajcvl.R.id.redeembutton);
        redeembuttonone = (TextView) view.findViewById(co.gladminds.bajajcvl.R.id.viewdetailone);
        price = (TextView) view.findViewById(co.gladminds.bajajcvl.R.id.redeemprice);
        status = (TextView) view.findViewById(co.gladminds.bajajcvl.R.id.orderstatus);
        statusimage = (ImageView) view.findViewById(co.gladminds.bajajcvl.R.id.thumb_button);

        if (!imagelist.get(position).toString().equalsIgnoreCase("")) {
            Glide.with(context).load(imagelist.get(position).toString())
                    .thumbnail(0.5f)
                    .crossFade()
                    //.placeholder(context.getResources().getDrawable(R.drawable.placeholder))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(productimage);
        }

        if (deslist.get(position).toString().length() >= 25) {
            description.setText(deslist.get(position).toString().substring(0, 25).concat("..."));

        } else {
            description.setText(deslist.get(position).toString());
        }
        //description.setText(deslist.get(position).toString());
        pointview.setText("Points: " + pointlist.get(position).toString());
        productid.setText(idlist.get(position).toString());
        if (!pricelist.get(position).toString().equalsIgnoreCase("")) {
            price.setText("Price: " + pricelist.get(position).toString());
        }
        status.setText(statuslist.get(position).toString());
        if (statuslist.get(position).toString().equalsIgnoreCase("open")) {
            statusimage.setImageResource(co.gladminds.bajajcvl.R.drawable.open);
        } else if (statuslist.get(position).toString().equalsIgnoreCase("shipped")) {
            statusimage.setImageResource(co.gladminds.bajajcvl.R.drawable.shipped);
        } else if (statuslist.get(position).toString().equalsIgnoreCase("accepted")) {
            statusimage.setImageResource(co.gladminds.bajajcvl.R.drawable.packed);
        } else if (statuslist.get(position).toString().equalsIgnoreCase("approved")) {
            statusimage.setImageResource(co.gladminds.bajajcvl.R.drawable.ic_check);
        } else if (statuslist.get(position).toString().equalsIgnoreCase("rejected")) {
            statusimage.setImageResource(co.gladminds.bajajcvl.R.drawable.ic_rejected);
        } else if (statuslist.get(position).toString().equalsIgnoreCase("delivered")) {
            statusimage.setImageResource(co.gladminds.bajajcvl.R.drawable.deliver);
        }
        redeembutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String fullstring = datelist.get(position).toString();

                final String deliverydate = fullstring.substring(0, fullstring.indexOf("@"));

                final String shipdate = fullstring.substring(fullstring.indexOf("@"), fullstring.indexOf("#")).replace("@", "");
                final String expecteddate = fullstring.substring(fullstring.indexOf("#") + 1);
                showDailog(statuslist.get(position).toString(), deliverydate, shipdate, expecteddate);

            }
        });

        return view;
    }

    Dialog dialogA;

    public void showDailog(final String status, final String deliverdatevalue, final String shipdatevalue, final String expecteddate) {

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ContextThemeWrapper ctw = new ContextThemeWrapper(parentActivity, co.gladminds.bajajcvl.R.style.AppDialog);

                final Dialog dialog = new Dialog(ctw);
                //dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(co.gladminds.bajajcvl.R.layout.track_view);
                dialog.setCancelable(true);


                dialog.getWindow().getAttributes().windowAnimations = co.gladminds.bajajcvl.R.style.DialogAnimationone;

                final Animation animation = new AlphaAnimation((float) 0.5, 0);
                animation.setDuration(500);
                animation.setInterpolator(new LinearInterpolator());
                animation.setRepeatCount(Animation.INFINITE);
                animation.setRepeatMode(Animation.REVERSE);


                TextView approvedate = (TextView) dialog.findViewById(co.gladminds.bajajcvl.R.id.approveddate);
                TextView packeddate = (TextView) dialog.findViewById(co.gladminds.bajajcvl.R.id.packeddate);
                TextView shippdate = (TextView) dialog.findViewById(co.gladminds.bajajcvl.R.id.shipeddate);
                TextView deliverdate = (TextView) dialog.findViewById(co.gladminds.bajajcvl.R.id.deliverdate);

                TextView ordertext = (TextView) dialog.findViewById(co.gladminds.bajajcvl.R.id.approvedtext);
                TextView packedtext = (TextView) dialog.findViewById(co.gladminds.bajajcvl.R.id.packedtext);
                TextView shippedtext = (TextView) dialog.findViewById(co.gladminds.bajajcvl.R.id.shipedtext);
                TextView delivertext = (TextView) dialog.findViewById(co.gladminds.bajajcvl.R.id.delivertext);

                TextView ordertextone = (TextView) dialog.findViewById(co.gladminds.bajajcvl.R.id.approvedtextone);
                TextView ordertexttwo = (TextView) dialog.findViewById(co.gladminds.bajajcvl.R.id.approvedtexttwo);
                TextView packedtextone = (TextView) dialog.findViewById(co.gladminds.bajajcvl.R.id.packedtextone);
                TextView shippedtextone = (TextView) dialog.findViewById(co.gladminds.bajajcvl.R.id.shippedone);
                TextView delivertextone = (TextView) dialog.findViewById(co.gladminds.bajajcvl.R.id.deliverone);

                ImageView approvedimageview = (ImageView) dialog.findViewById(co.gladminds.bajajcvl.R.id.approvedimageview);
                ImageView packedimageview = (ImageView) dialog.findViewById(co.gladminds.bajajcvl.R.id.packedimageview);
                ImageView shipedimageview = (ImageView) dialog.findViewById(co.gladminds.bajajcvl.R.id.shipedimageview);
                ImageView deliverimageview = (ImageView) dialog.findViewById(co.gladminds.bajajcvl.R.id.deliverimageview);

                View approvedview = (View) dialog.findViewById(co.gladminds.bajajcvl.R.id.approvedview);
                View approvedviewone = (View) dialog.findViewById(co.gladminds.bajajcvl.R.id.approvedviewone);
                View packedview = (View) dialog.findViewById(co.gladminds.bajajcvl.R.id.packedview);
                View packedviewone = (View) dialog.findViewById(co.gladminds.bajajcvl.R.id.packedviewone);
                View shipedview = (View) dialog.findViewById(co.gladminds.bajajcvl.R.id.shipedview);
                View shipedviewone = (View) dialog.findViewById(co.gladminds.bajajcvl.R.id.shipedviewone);
                if (status.equalsIgnoreCase("delivered")) {
                    approvedimageview.setVisibility(View.VISIBLE);
                    packedimageview.setVisibility(View.VISIBLE);
                    shipedimageview.setVisibility(View.VISIBLE);
                    deliverimageview.setVisibility(View.VISIBLE);
                    approvedview.setVisibility(View.VISIBLE);
                    approvedviewone.setVisibility(View.VISIBLE);
                    packedview.setVisibility(View.VISIBLE);
                    packedviewone.setVisibility(View.VISIBLE);
                    shipedview.setVisibility(View.VISIBLE);
                    shipedviewone.setVisibility(View.VISIBLE);

                    approvedate.setVisibility(View.INVISIBLE);
                    packeddate.setVisibility(View.INVISIBLE);
                    shippdate.setVisibility(View.VISIBLE);
                    deliverdate.setVisibility(View.VISIBLE);
                    shippdate.setText(shipdatevalue);
                    deliverdate.setText(deliverdatevalue);

                    ordertext.setVisibility(View.VISIBLE);
                    packedtext.setVisibility(View.VISIBLE);
                    shippedtext.setVisibility(View.VISIBLE);
                    delivertext.setVisibility(View.VISIBLE);

                    ordertextone.setVisibility(View.VISIBLE);
                    ordertexttwo.setVisibility(View.VISIBLE);
                    packedtextone.setVisibility(View.VISIBLE);
                    shippedtextone.setVisibility(View.VISIBLE);
                    delivertextone.setVisibility(View.VISIBLE);


                } else if (status.equalsIgnoreCase("open") || status.equalsIgnoreCase("Accepted") ||
                        status.equalsIgnoreCase("Approved")) {
                    approvedimageview.setVisibility(View.VISIBLE);
                    packedimageview.setVisibility(View.VISIBLE);
                    shipedimageview.setVisibility(View.INVISIBLE);
                    deliverimageview.setVisibility(View.INVISIBLE);
                    approvedview.setVisibility(View.VISIBLE);
                    approvedviewone.setVisibility(View.VISIBLE);
                    packedview.setVisibility(View.INVISIBLE);
                    packedviewone.setVisibility(View.INVISIBLE);
                    shipedview.setVisibility(View.INVISIBLE);
                    shipedviewone.setVisibility(View.INVISIBLE);

                    approvedate.setVisibility(View.INVISIBLE);
                    packeddate.setVisibility(View.INVISIBLE);
                    shippdate.setVisibility(View.VISIBLE);
                    deliverdate.setVisibility(View.VISIBLE);

                    shippdate.setText(shipdatevalue);
                    deliverdate.setText(expecteddate);

                    ordertext.setVisibility(View.VISIBLE);
                    packedtext.setVisibility(View.VISIBLE);
                    shippedtext.setVisibility(View.VISIBLE);
                    delivertext.setVisibility(View.VISIBLE);

                    ordertextone.setVisibility(View.VISIBLE);
                    ordertexttwo.setVisibility(View.VISIBLE);
                    packedtextone.setVisibility(View.VISIBLE);
                    shippedtextone.setVisibility(View.VISIBLE);
                    delivertextone.setVisibility(View.VISIBLE);
                } else if (status.equalsIgnoreCase("Shipped")) {
                    approvedimageview.setVisibility(View.VISIBLE);
                    packedimageview.setVisibility(View.VISIBLE);
                    shipedimageview.setVisibility(View.VISIBLE);
                    deliverimageview.setVisibility(View.INVISIBLE);
                    approvedview.setVisibility(View.VISIBLE);
                    approvedviewone.setVisibility(View.VISIBLE);
                    packedview.setVisibility(View.VISIBLE);
                    packedviewone.setVisibility(View.VISIBLE);
                    shipedview.setVisibility(View.INVISIBLE);
                    shipedviewone.setVisibility(View.INVISIBLE);

                    approvedate.setVisibility(View.INVISIBLE);
                    packeddate.setVisibility(View.INVISIBLE);
                    shippdate.setVisibility(View.VISIBLE);
                    deliverdate.setVisibility(View.VISIBLE);

                    shippdate.setText(shipdatevalue);
                    deliverdate.setText(expecteddate);

                    ordertext.setVisibility(View.VISIBLE);
                    packedtext.setVisibility(View.VISIBLE);
                    shippedtext.setVisibility(View.VISIBLE);
                    delivertext.setVisibility(View.VISIBLE);

                    ordertextone.setVisibility(View.VISIBLE);
                    ordertexttwo.setVisibility(View.VISIBLE);
                    packedtextone.setVisibility(View.VISIBLE);
                    shippedtextone.setVisibility(View.VISIBLE);
                    delivertextone.setVisibility(View.INVISIBLE);
                }

                dialog.show();
            }
        }, 200);

    }

}
