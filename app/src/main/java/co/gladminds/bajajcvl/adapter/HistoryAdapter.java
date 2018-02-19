package co.gladminds.bajajcvl.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import co.gladminds.bajajcvl.Common.Common;

import java.util.List;

/**
 * Created by vikram on 10/27/2017.
 */

public class HistoryAdapter extends BaseAdapter  {
    Context context;
    LayoutInflater inflater;
    List productlist;
    Activity parentActivity;
    private Animation upanim,downanim;
    private int count=0;

    public HistoryAdapter(Context con, List productlist,Activity parentActivity) {
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

    @Override
    public View getView(final int position, View view, ViewGroup arg2)
    {
           final ViewHolder holder;
        if (view == null) {
            view = inflater.inflate(co.gladminds.bajajcvl.R.layout.historyview,null);
            holder = createViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        upanim = AnimationUtils.loadAnimation(context, co.gladminds.bajajcvl.R.anim.slideup);
        downanim = AnimationUtils.loadAnimation(context, co.gladminds.bajajcvl.R.anim.slidedown);




        String fullstring = productlist.get(position).toString();

        final String upccode = fullstring.substring(0, fullstring.indexOf("@"));
        final String date = fullstring.substring(fullstring.indexOf("@"),fullstring.indexOf("#")).replace("@","");
        final String point = fullstring.substring(fullstring.indexOf("#"),fullstring.indexOf("$")).replace("#","");
        final String des = fullstring.substring(fullstring.indexOf("$"),fullstring.indexOf("%")).replace("$","");
        final String cat = fullstring.substring(fullstring.indexOf("%"),fullstring.indexOf("&")).replace("%","");
        final String part = fullstring.substring(fullstring.indexOf("&") + 1);
        Log.e("targetvalue is",des);
        Log.e("seekbarvalue is",cat);
        Log.e("imagevalue is",part);

        holder.upccodetextview.setText(upccode);
        holder.datetextview.setText(date);
        holder.pointtextview.setText(point+" Points");
        holder.categorytextview.setText("Category: "+cat);
        holder.parttextview.setText("Part No: "+part);
        holder.destextview.setText(des);
        holder.detailicon.setTag(position);
        holder.detailicon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pos = Common.getPreferences(context,"posiii");
                //Toast.makeText(context,pos+" "+position,Toast.LENGTH_SHORT).show();

                    if(((ImageView)holder.detailicon).getDrawable().getConstantState() != context
                            .getResources().getDrawable(co.gladminds.bajajcvl.R.drawable.down)
                            .getConstantState()) {
                         count = 0;
                    }else{
                        count = 1;
                    }


                count++;


                if(count == 1){
                  holder.detailicon.setImageDrawable(context.getResources().getDrawable(co.gladminds.bajajcvl.R.drawable.down));
                    holder.detaillayout.startAnimation(upanim);
                    holder.detaillayout.setVisibility(View.VISIBLE);
                    Common.SetPreferences(context,"posiii",""+position);


                }if(count == 2){
                    count = 0;
                    holder.detailicon.setImageResource(co.gladminds.bajajcvl.R.drawable.up);
                    holder.detaillayout.startAnimation(upanim);
                    holder.detaillayout.setVisibility(View.GONE);

                }
            }
        });

        return view;

    }

    static class ViewHolder {
        TextView upccodetextview,datetextview,pointtextview,
                categorytextview,parttextview,destextview;
        private ImageView detailicon;
        private RelativeLayout detaillayout;

    }
    private ViewHolder createViewHolder(View view) {
        ViewHolder holder = new ViewHolder();
        holder.upccodetextview = (TextView) view.findViewById(co.gladminds.bajajcvl.R.id.historyupccode);
        holder.datetextview = (TextView) view.findViewById(co.gladminds.bajajcvl.R.id.historydate);
        holder.pointtextview = (TextView) view.findViewById(co.gladminds.bajajcvl.R.id.historypoint);
        holder.categorytextview = (TextView) view.findViewById(co.gladminds.bajajcvl.R.id.historycategory);
        holder.parttextview = (TextView) view.findViewById(co.gladminds.bajajcvl.R.id.historypart);
        holder.destextview = (TextView) view.findViewById(co.gladminds.bajajcvl.R.id.historydescription);
        holder.detailicon = (ImageView) view.findViewById(co.gladminds.bajajcvl.R.id.historydetailicon);
        holder.detaillayout = (RelativeLayout) view.findViewById(co.gladminds.bajajcvl.R.id.detaillayout);
        return holder;
    }
}