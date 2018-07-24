package com.example.rowan.pplcontact;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.xml.sax.DTDHandler;

import java.util.ArrayList;
import java.util.List;

public class CustomAdapter extends BaseAdapter implements Filterable{
    private Context mContext;

    private List<DataHolder> Data =new ArrayList<>();
    private List<DataHolder> FreshData =new ArrayList<>();
    CustomFilter filter;
    public CustomAdapter(@NonNull Context context, @NonNull List<DataHolder> Data) {


        this.FreshData=Data;
        this.Data=Data;
        mContext=context;
    }
    private static class ViewHolder {
        TextView txtName;
        TextView txtPhone;
        TextView txtDesignation;
        ImageView proPic;

    }


    @Override
    public int getCount() {
        return FreshData.size();
    }

    @Override
    public Object getItem(int position) {
        return FreshData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return FreshData.indexOf(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {


        final ViewHolder viewHolder;

        if(convertView==null)
        {
            viewHolder=new ViewHolder();
            LayoutInflater layoutInflater= (LayoutInflater) mContext.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView =layoutInflater.inflate
                    (R.layout.layout_full_contact_list,null);

            viewHolder.txtName=(TextView)convertView.findViewById(R.id.name) ;
            viewHolder.txtPhone=(TextView)convertView.findViewById(R.id.officenum);
            viewHolder.txtDesignation=(TextView)convertView.findViewById(R.id.designation);
            viewHolder.proPic=(ImageView)convertView.findViewById(R.id.userPic);

            convertView.setTag(viewHolder);

        }
        else
        {
            viewHolder=(ViewHolder)convertView.getTag();
        }
        DataHolder SingleData=(DataHolder)getItem(position);

        viewHolder.txtName.setText(SingleData.getName());
        viewHolder.txtPhone.setText("0"+SingleData.getMobile());
        viewHolder.txtDesignation.setText(SingleData.getDesignation());
        if(isConnected()){

            String image=SingleData.getImage();
            if(!image.equals("NoImage")){
                UniversalImageloader.setImage(image,viewHolder.proPic,null);
            }


        }



        return convertView;
    }
    @NonNull
    public Filter getFilter() {
        if(filter==null){
            filter=new CustomFilter();
        }
        return filter;
    }
    public  void initImageloader()
    {
        UniversalImageloader universalImageLoader=
                new UniversalImageloader(mContext);
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
    }
    public boolean isConnected()
    {
        ConnectivityManager connectivityManager=(ConnectivityManager)mContext.getSystemService(mContext.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(0).getState()== NetworkInfo.State.CONNECTED||connectivityManager.getNetworkInfo(0).getState()==NetworkInfo.State.CONNECTING||connectivityManager.getNetworkInfo(1).getState()== NetworkInfo.State.CONNECTED||connectivityManager.getNetworkInfo(1).getState()==NetworkInfo.State.CONNECTING) {
            return true;
        }
        else if(connectivityManager.getNetworkInfo(0).getState()== NetworkInfo.State.DISCONNECTED||connectivityManager.getNetworkInfo(1).getState()== NetworkInfo.State.DISCONNECTED)
        {
            return  false;
        }
        return false;

    }
    class CustomFilter extends Filter{

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            FilterResults results=new FilterResults();
            if(constraint!=null && constraint.length()>1){
                constraint=constraint.toString();
                char[] help=constraint.toString().toCharArray();
                Boolean capital=true;
                for(int i=0;i<help.length;i++){
                    if(Character.isWhitespace(help[i])){
                        capital=true;
                        continue;
                    }
                    if(capital){
                        capital=false;
                        help[i]=Character.toUpperCase(help[i]);
                    }
                }
                constraint=new String(help);
                List<DataHolder> filter=new ArrayList<>();
                for(int i=0;i<Data.size();i++){
                    if(Data.get(i).getName().contains(constraint) || (Data.get(i).getDesignation().toUpperCase()).contains(constraint)){
                        DataHolder p=new DataHolder(Data.get(i).getName(),Data.get(i).getPhone(),
                                Data.get(i).getEmail(),Data.get(i).getFbId(),Data.get(i).getMobile(),
                                Data.get(i).getDesignation(),Data.get(i).getBranch(),
                                Data.get(i).getImage(),Data.get(i).getEmpId(),
                                Data.get(i).getWhtsapp(),Data.get(i).getViber());
                        filter.add(p);
                    }
                }
                results.count=filter.size();
                results.values=filter;

            }
            else{
                results.count=Data.size();
                results.values=Data;
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            FreshData=(List<DataHolder>)results.values;
            notifyDataSetChanged();
        }
    }


}