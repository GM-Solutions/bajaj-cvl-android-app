package co.gladminds.bajajcvl.activity;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import co.gladminds.bajajcvl.Common.Common;

import java.util.ArrayList;

/**
 * @author Paresh Mayani (@pareshmayani)
 */
public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.MyViewHolder> {

    private ArrayList<String> mImagesList;
    private Context mContext;
    private SparseBooleanArray mSparseBooleanArray;
    int count = 0;
    Activity parentactivity;

    public ImageAdapter(Context context, ArrayList<String> imageList, Activity parentactivity) {
        mContext = context;
        mSparseBooleanArray = new SparseBooleanArray();
        mImagesList = new ArrayList<String>();
        this.mImagesList = imageList;
        this.parentactivity = parentactivity;
    }

    public ArrayList<String> getCheckedItems() {
        ArrayList<String> mTempArry = new ArrayList<String>();

        for(int i=0;i<mImagesList.size();i++) {
            if(mSparseBooleanArray.get(i)) {
                mTempArry.add(mImagesList.get(i));
            }
        }

        return mTempArry;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    CompoundButton.OnCheckedChangeListener mCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            count++;
            if(count < 10){

                mSparseBooleanArray.put((Integer) buttonView.getTag(), isChecked);
            }else{
                buttonView.setChecked(false);
                //Toast.makeText(mContext,"select 10 images in one time",Toast.LENGTH_SHORT).show();
                Common.Customtoast(parentactivity,"select 10 images in one time");
            }

        }
    };

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(co.gladminds.bajajcvl.R.layout.row_multiphoto_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        String imageUrl = mImagesList.get(position);

               Glide.with(mContext)
                .load("file://"+imageUrl)
                .centerCrop()
                .placeholder(co.gladminds.bajajcvl.R.drawable.bajajlogo)
                .error(co.gladminds.bajajcvl.R.drawable.bajajlogo)
                .into(holder.imageView);

        holder.checkBox.setTag(position);
        holder.checkBox.setChecked(mSparseBooleanArray.get(position));
        holder.checkBox.setOnCheckedChangeListener(mCheckedChangeListener);

    }

    @Override
    public int getItemCount() {
        return mImagesList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public CheckBox checkBox;
        public ImageView imageView;

        public MyViewHolder(View view) {
            super(view);

            checkBox = (CheckBox) view.findViewById(co.gladminds.bajajcvl.R.id.checkBox1);
            imageView = (ImageView) view.findViewById(co.gladminds.bajajcvl.R.id.imageView1);
        }
    }

}
