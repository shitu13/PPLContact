package com.example.rowan.pplcontact;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

public class CustomAdapterforMultipleSelecet extends BaseAdapter implements Filterable{
    private Context mContext;

    private List<ExtendedDataHolder> Data =new ArrayList<>();
    private List<ExtendedDataHolder> FreshData =new ArrayList<>();
    CustomAdapterforMultipleSelecet.CustomFilter filter;
    public CustomAdapterforMultipleSelecet(@NonNull Context context, @NonNull List<ExtendedDataHolder> Data) {


        this.FreshData=Data;
        this.Data=Data;
        mContext=context;
    }
    private static class ViewHolder {
        TextView txtName;
        TextView txtPhone;
        TextView txtDesignation;
        ImageView proPic;
        CheckBox Check;

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
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {


        final CustomAdapterforMultipleSelecet.ViewHolder viewHolder;

        if(convertView==null)
        {
            viewHolder=new CustomAdapterforMultipleSelecet.ViewHolder();
            LayoutInflater layoutInflater=LayoutInflater.from(mContext);
            convertView =layoutInflater.inflate
                    (R.layout.group_sms_selection,parent,false);

            viewHolder.txtName=(TextView)convertView.findViewById(R.id.name) ;
            viewHolder.txtPhone=(TextView)convertView.findViewById(R.id.officenum);
            viewHolder.txtDesignation=(TextView)convertView.findViewById(R.id.designation);
            viewHolder.proPic=(ImageView)convertView.findViewById(R.id.userPic);
            viewHolder.Check=(CheckBox)convertView.findViewById(R.id.Check);

            viewHolder.Check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                  //  int getPosition=(Integer) buttonView.getTag();
                    FreshData.get(position).setSelected(buttonView.isChecked());
                }
            });

            convertView.setTag(viewHolder);

        }
        else
        {
            viewHolder=(CustomAdapterforMultipleSelecet.ViewHolder)convertView.getTag();
        }
       ExtendedDataHolder SingleData=(ExtendedDataHolder)getItem(position);

        viewHolder.txtName.setText(SingleData.getHolder().getName());
        viewHolder.txtPhone.setText("0"+SingleData.getHolder().getMobile());
        viewHolder.txtDesignation.setText(SingleData.getHolder().getDesignation());
        viewHolder.Check.setChecked(SingleData.isSelected());
        if(isConnected()){
            String image=SingleData.getHolder().getImage();
            if(!image.equals("NoImage")){
                UniversalImageloader.setImage(image,viewHolder.proPic,null);
            }


        }
        else {
            //Toast.makeText(Profile.this,
            // "Connect to Internet for Profile Picture",Toast.LENGTH_LONG).show();
        }


        return convertView;
    }
   @NonNull
    public Filter getFilter() {
        if(filter==null){
            filter=new CustomAdapterforMultipleSelecet.CustomFilter();
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
                List<ExtendedDataHolder> filter=new ArrayList<>();
                for(int i=0;i<Data.size();i++){
                    if(Data.get(i).getHolder().getName().contains(constraint) || (Data.get(i).getHolder().getDesignation().toUpperCase()).contains(constraint)){
                        DataHolder p=new DataHolder(Data.get(i).getHolder().getName(),Data.get(i).getHolder().getPhone(),
                                Data.get(i).getHolder().getEmail(),Data.get(i).getHolder().getFbId(),Data.get(i).getHolder().getMobile(),
                                Data.get(i).getHolder().getDesignation(),Data.get(i).getHolder().getBranch(),
                                Data.get(i).getHolder().getImage(),Data.get(i).getHolder().getEmpId(),
                                Data.get(i).getHolder().getWhtsapp(),Data.get(i).getHolder().getViber());
                        filter.add(new ExtendedDataHolder(p,Data.get(i).isSelected()));
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
            FreshData=(List<ExtendedDataHolder>)results.values;
            notifyDataSetChanged();
        }
    }


}
