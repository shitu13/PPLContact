package com.example.rowan.pplcontact;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.SyncStateContract;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

import java.util.ArrayList;
import java.util.List;

public class GroupSms extends AppCompatActivity {

    private static final String TAG = "GroupSms";
    private static final int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE =1697 ;


    ListView contact;
    List<DataHolder> Datalist=new ArrayList<>();
    SparseBooleanArray sparseBooleanArray ;

    String msg,phone;
    int count=0,smsToSendFrom;
    SearchView search;
    boolean flag=false,CheckPer=false;
    String SEND = "SMS_SENT";
    String DELIVERED = "SMS_DELIVERED";
    CustomAdapterforMultipleSelecet customAdapter;
    private boolean CheckPer2=false;
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS=11611;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initImageloader();
        setContentView(R.layout.list_group_sms);
         search=(SearchView) findViewById(R.id.search_bar2);
        contact=(ListView)findViewById(R.id.selectcontactlist);


        boolean pauseOnScroll = true; // or true
        boolean pauseOnFling = true; // or false
        PauseOnScrollListener listener = new PauseOnScrollListener(ImageLoader.getInstance(),
                pauseOnScroll, pauseOnFling);
        contact.setOnScrollListener(listener);
        Boolean user=(Boolean)getIntent().getBooleanExtra("User",true);

        final List<ExtendedDataHolder> list=makelist(user);


        customAdapter =
                new CustomAdapterforMultipleSelecet(GroupSms.this,list );
        contact.setAdapter(customAdapter);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search.setIconified(false);
            }
        });

        BottomNavigationView bottomNavigationView=
                (BottomNavigationView)findViewById(R.id.botom_nav_bar2);


        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if(item.getItemId()==R.id.selectAll)
                {
                    if(flag)
                        flag=false;
                    else
                        flag=true;
                    for(int i=0;i<list.size();i++)
                    {
                        ExtendedDataHolder extendedDataHolder=list.get(i);
                        extendedDataHolder.setSelected(flag);
                    }
                    customAdapter.notifyDataSetChanged();


                    return true;
                }
                if(item.getItemId()==R.id.headoff2)
                {

                    finish();
                    Intent intent=new Intent(GroupSms.this, ContactList.class);
                    intent.putExtra("User",true);
                    startActivity(intent);
                    return true;
                }
                if(item.getItemId()==R.id.corpoff2)
                {
                    finish();
                    Intent intent=new Intent(GroupSms.this, ContactList.class);
                    intent.putExtra("User",false);
                    startActivity(intent);
                    return true;
                }
                if(item.getItemId()==R.id.sendSms ) {

                    if (CountSelected(list)) {
                        final Dialog dialog = new Dialog(GroupSms.this);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(R.layout.dialog_write_sms);
                        final EditText editText = (EditText) dialog.findViewById(R.id.sms);
                        ImageView done = (ImageView) dialog.findViewById(R.id.smsdone);

                        done.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                msg = editText.getText().toString();

                                if (Build.VERSION.SDK_INT >= 22 && CheckPer && CheckPer2) {
                                    final Dialog dialog = new Dialog(GroupSms.this);
                                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                    dialog.setContentView(R.layout.dialog_group_sms);
                                    ImageView head = (ImageView) dialog.findViewById(R.id.btnHead);
                                    TextView sim1 = (TextView) dialog.findViewById(R.id.textsim1);
                                    TextView sim2 = (TextView) dialog.findViewById(R.id.textsim2);
                                    ImageView corp = (ImageView) dialog.findViewById(R.id.btnCorp);
                                    head.setImageResource(R.drawable.ic_sim_card_black_24dp);
                                    corp.setImageResource(R.drawable.ic_sim_card_black_24dp);
                                    sim1.setText("SIM 1");
                                    sim2.setText("SIM 2");

                                    head.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                            for (int i = 0; i < list.size(); i++) {
                                                Log.d(TAG, "onClick: " + list.get(i).isSelected() + " " + i);
                                                ExtendedDataHolder data = list.get(i);
                                                phone = "+880" + data.getHolder().getMobile();
                                                String name = data.getHolder().getName();

                                                if (data.isSelected()) {
                                                    CountSMS(phone, name,0);

                                                }
                                            }
                                            dialog.dismiss();
                                        }
                                    });
                                    corp.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                            for (int i = 0; i < list.size(); i++) {
                                                Log.d(TAG, "onClick: " + list.get(i).isSelected() + " " + i);
                                                ExtendedDataHolder data = list.get(i);
                                                phone = "+880" + data.getHolder().getMobile();
                                                String name = data.getHolder().getName();

                                                if (data.isSelected()) {
                                                    CountSMS(phone, name,1);
                                                }
                                            }
                                            dialog.dismiss();
                                        }
                                    });
                                    dialog.show();

                                }



                                dialog.dismiss();
                            }
                        });
                        dialog.show();
                        DisplayMetrics displayMetrics = new DisplayMetrics();
                        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                        // The absolute width of the available display size in pixels.
                        int displayWidth = displayMetrics.widthPixels;
                        // The absolute height of the available display size in pixels.
                        int displayHeight = displayMetrics.heightPixels;

                        // Initialize a new window manager layout parameters
                        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();

                        // Copy the alert dialog window attributes to new layout parameter instance
                        layoutParams.copyFrom(dialog.getWindow().getAttributes());

                        int dialogWindowWidth = (int) (displayWidth * 0.8f);
                        // Set alert dialog height equal to screen height 70%
                        int dialogWindowHeight = (int) (displayHeight * 0.5f);


                        layoutParams.width = dialogWindowWidth;
                        layoutParams.height = dialogWindowHeight;


                        dialog.getWindow().setAttributes(layoutParams);

                    }
                    else {
                        Toast.makeText(GroupSms.this,"Please Select Contact",Toast.LENGTH_SHORT).show();
                    }
                }
                return true;
            }
        });

        if (ContextCompat.checkSelfPermission(GroupSms.this,
                Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(GroupSms.this,
                    Manifest.permission.READ_PHONE_STATE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(GroupSms.this,
                        new String[]{Manifest.permission.READ_PHONE_STATE},
                        MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
            CheckPer=true;
        }

        if (ContextCompat.checkSelfPermission(GroupSms.this,
                Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(GroupSms.this,
                    Manifest.permission.SEND_SMS)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(GroupSms.this,
                        new String[]{Manifest.permission.SEND_SMS},
                        MY_PERMISSIONS_REQUEST_SEND_SMS);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
            CheckPer2=true;
        }


        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {

                customAdapter.getFilter().filter(query);

                return false;
            }
        });




        contact.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                CheckBox checkBox=view.findViewById(R.id.Check);
                ExtendedDataHolder extendedDataHolder=list.get(position);
                if(extendedDataHolder.isSelected())
                {
                    extendedDataHolder.setSelected(false);
                    checkBox.setChecked(false);
                }
                else
                {
                    extendedDataHolder.setSelected(true);
                    checkBox.setChecked(true);
                }
            }
        });



    }

    @Override
    public void onBackPressed() {
        finish();
        Intent intent=new Intent(GroupSms.this, ContactList.class);
        startActivity(intent);
    }

    public List<ExtendedDataHolder> makelist(Boolean user)
    {
        DatabaseHelper databaseHelper = new DatabaseHelper(this);

        Datalist=databaseHelper.RetrieveData(user);
        List<ExtendedDataHolder> data=new ArrayList<>();
        for(int i=0;i<Datalist.size();i++)
        {
            ExtendedDataHolder extendedDataHolder
                    =new ExtendedDataHolder(Datalist.get(i),false);

            data.add(extendedDataHolder);

        }

        return data;
    }
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_PHONE_STATE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    CheckPer=true;
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            case MY_PERMISSIONS_REQUEST_SEND_SMS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    CheckPer2=true;
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }


    public  void initImageloader()
    {
        UniversalImageloader universalImageLoader=
                new UniversalImageloader(GroupSms.this);
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
    }




    public void CountSMS(String Mobile,String name,int sim)
    {
        final String Phone=Mobile;
        final String Name=name;

        registerReceiver(new BroadcastReceiver(){
            @Override
            public void onReceive(Context arg0, Intent arg1)
            {
                switch (getResultCode())
                {
                    case Activity.RESULT_OK:
                      //  Toast.makeText(GroupSms.this, "SMS sent", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                       // Toast.makeText(GroupSms.this, "Generic failure", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                      //  Toast.makeText(GroupSms.this, "No service", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                       // Toast.makeText(GroupSms.this, "Null PDU", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                      //  Toast.makeText(GroupSms.this, "Radio off", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
            }
        }, new IntentFilter(SEND));
        registerReceiver(new BroadcastReceiver(){
            @Override
            public void onReceive(Context arg0, Intent arg1)
            {
                switch (getResultCode())
                {
                    case Activity.RESULT_OK:
                        Toast.makeText(GroupSms.this,
                                "SMS delivered to "+Name ,Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(GroupSms.this,
                                "SMS not delivered to "+Name, Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(DELIVERED));
        if (Build.VERSION.SDK_INT >= 22)
        {
            final ArrayList<Integer> simCardList = new ArrayList<>();
            SubscriptionManager subscriptionManager;
            subscriptionManager = SubscriptionManager.from(GroupSms.this);
            final List<SubscriptionInfo> subscriptionInfoList = subscriptionManager
                    .getActiveSubscriptionInfoList();
            for (SubscriptionInfo subscriptionInfo : subscriptionInfoList) {
                int subscriptionId = subscriptionInfo.getSubscriptionId();
                simCardList.add(subscriptionId);
            }


            smsToSendFrom = simCardList.get(sim);
            if (Build.VERSION.SDK_INT >= 22)
            {
                SmsManager smsManager = SmsManager.
                        getSmsManagerForSubscriptionId(smsToSendFrom);
                ArrayList<String> parts = smsManager.divideMessage(msg);
                ArrayList<PendingIntent> sentIntents = new ArrayList<PendingIntent>();
                ArrayList<PendingIntent> deliveryIntents = new ArrayList<PendingIntent>();

                for (int i = 0; i < parts.size(); i++) {
                    sentIntents.add(PendingIntent.getBroadcast(GroupSms.this,
                            0, new Intent(SEND), 0));
                    deliveryIntents.add(PendingIntent.getBroadcast(GroupSms.this,
                            0, new Intent(DELIVERED), 0));
                }
                smsManager.sendMultipartTextMessage(Phone, null,
                        parts, sentIntents, deliveryIntents);

            }


        }
        else
        {
            SmsManager smsManager = SmsManager.getDefault();
            ArrayList<String> parts = smsManager.divideMessage(msg);
            ArrayList<PendingIntent> sentIntents = new ArrayList<PendingIntent>();
            ArrayList<PendingIntent> deliveryIntents = new ArrayList<PendingIntent>();

            for (int i = 0; i < parts.size(); i++) {
                sentIntents.add(PendingIntent.getBroadcast(GroupSms.this,
                        0, new Intent(SEND), 0));
                deliveryIntents.add(PendingIntent.getBroadcast(GroupSms.this,
                        0, new Intent(DELIVERED), 0));
            }
            smsManager.sendMultipartTextMessage(Phone, null,
                    parts, sentIntents, deliveryIntents);
        }

    }


    public boolean CountSelected(List<ExtendedDataHolder> data)
    {
        boolean state=false;
        for(int i=0;i<data.size();i++)
        {
            ExtendedDataHolder extendedDataHolder=data.get(i);
            if(extendedDataHolder.isSelected())
            {
                state=true;
                break;

            }

        }
        return state;

    }
}
