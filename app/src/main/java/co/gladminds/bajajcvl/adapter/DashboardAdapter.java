package co.gladminds.bajajcvl.adapter;

/**
 * Created by vikram on 11/23/2017.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import co.gladminds.bajajcvl.R;

import java.util.List;


/**
 * Created by vikram on 10/6/2017.
 */

public class DashboardAdapter extends BaseAdapter {
    Context context;
    LayoutInflater inflater;
    List namelist;
    TextView categoryname;
    private ImageView categoryimage;
    private GridView gridView;


    public DashboardAdapter(Context con, List namelist,GridView gridView) {
        // TODO Auto-generated constructor stub
        this.namelist = namelist;
        this.context = con;
        this.gridView = gridView;
        inflater = LayoutInflater.from(con);
        // this.acp=a;

    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return namelist.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return namelist.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(final int position, View cview, ViewGroup arg2) {


        View view = cview;


        view = inflater.inflate(R.layout.dashboardview, null);

        AbsListView.LayoutParams param = new AbsListView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                gridView.getHeight()/2);
        view.setLayoutParams(param);

        categoryname = (TextView) view.findViewById(R.id.dashboardtextview);
        categoryimage = (ImageView) view.findViewById(R.id.dashboardimageview);
         categoryname.setText(namelist.get(position).toString());
         if(namelist.get(position).toString().equalsIgnoreCase("Accumulation")){
             categoryimage.setImageResource(R.drawable.scanner);
         }else if(namelist.get(position).toString().equalsIgnoreCase("Reedem")){
             categoryimage.setImageResource(R.drawable.reedem);
         }else if(namelist.get(position).toString().equalsIgnoreCase("Check Balance")){
             categoryimage.setImageResource(R.drawable.balance);
         }else if(namelist.get(position).toString().equalsIgnoreCase("Support")){
             categoryimage.setImageResource(R.drawable.support);
         }else if(namelist.get(position).toString().equalsIgnoreCase("History")){
             categoryimage.setImageResource(R.drawable.historynn);
         }else{
             categoryimage.setImageResource(R.drawable.gift);
         }
        return view;
    }
}
