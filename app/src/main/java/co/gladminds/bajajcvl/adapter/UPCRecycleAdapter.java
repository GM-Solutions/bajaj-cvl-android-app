package co.gladminds.bajajcvl.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import co.gladminds.bajajcvl.R;
import co.gladminds.bajajcvl.models.UPC;

import java.util.ArrayList;

/**
 * Created by Nikhil on 1-12-2017.
 */
public class UPCRecycleAdapter extends RecyclerView.Adapter<UPCRecycleAdapter.ViewHolder> {

    private ArrayList<UPC> arrayList;
    private Activity activity;
    private static OnClickListener onClickListener;


    public UPCRecycleAdapter(Activity activity, ArrayList<UPC> dataArrayList) {
        this.arrayList = dataArrayList;
        this.activity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_upc, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        UPC upc = arrayList.get(position);
        holder.tvSrNo.setText((position + 1) + ".");
        holder.tvUpc.setText("");
        holder.tvUpc.append(upc.getCode());
        holder.tvPoint.setText("- " + upc.getPoint() + "Pt");
        holder.progressBar.setVisibility(upc.isShowingProgress() ? View.VISIBLE : View.GONE);
//        holder.tvUpc.setEnabled(upc.getStatus() != 1);
        holder.tvVerify.setVisibility(upc.getStatus() != 1 ? View.VISIBLE : View.GONE);
        holder.tvEditMsg.setVisibility(upc.getStatus() != 1 ? View.VISIBLE : View.GONE);
        switch (upc.getStatus()) {
            case 0:
                holder.tvStatus.setText("Invalid");
                holder.tvStatus.setTextColor(activity.getResources().getColor(R.color.colorRed));
                holder.tvUpc.requestFocus();
                holder.tvEditMsg.setVisibility(View.VISIBLE);
                break;
            case 1:
                holder.tvStatus.setText("Valid");
                holder.tvStatus.setTextColor(activity.getResources().getColor(R.color.colorGreen));
                holder.tvEditMsg.setVisibility(View.GONE);
                break;
            case 2:
                holder.tvStatus.setText("Used");
                holder.tvStatus.setTextColor(activity.getResources().getColor(R.color.colorGrey));
                holder.tvEditMsg.setVisibility(View.GONE);
                break;
            default:
                holder.tvStatus.setText("");
                holder.tvStatus.setTextColor(activity.getResources().getColor(R.color.colorGrey));
                holder.tvEditMsg.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView tvSrNo, tvStatus, tvVerify, tvEditMsg, tvPoint;
        EditText tvUpc;
        ProgressBar progressBar;

        public ViewHolder(View v) {
            super(v);
            this.tvUpc = (EditText) v.findViewById(R.id.tvUpc);
            this.tvStatus = (TextView) v.findViewById(R.id.tvStatus);
            this.tvSrNo = (TextView) v.findViewById(R.id.tvSrNo);
            this.tvVerify = (TextView) v.findViewById(R.id.tvVerify);
            this.tvEditMsg = (TextView) v.findViewById(R.id.tvEditMsg);
            this.tvPoint = (TextView) v.findViewById(R.id.tvPoint);
            this.progressBar = (ProgressBar) v.findViewById(R.id.progressBar);
            ((ImageView) v.findViewById(R.id.delete)).setOnClickListener(this);
            this.tvVerify.setOnClickListener(this);

            this.tvUpc.addTextChangedListener(new TextWatcher() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                    Log.e("onTextChanged", "----------------- onTextChanged Start : " + start + "  , before : " + before + "   , count : " + count);
                    if (s.toString().trim().length() > 6) {
                        if (onClickListener != null) {
                            onClickListener.onUPCChange(getAdapterPosition(), s.toString());
                        }
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }
            });
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
}
