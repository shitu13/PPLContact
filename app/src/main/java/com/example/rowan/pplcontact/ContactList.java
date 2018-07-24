package com.example.rowan.pplcontact;

import android.app.Dialog;
import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

import java.util.ArrayList;
import java.util.List;

public class ContactList extends AppCompatActivity {
    private static final String TAG = "ContactList";
    ListView listView;
    CustomAdapter customAdapter ;
    List<DataHolder> Datalist=new ArrayList<>();
    DatabaseHelper databaseHelper;
    SearchView search;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initImageloader();
        setContentView(R.layout.activity_contact_list);
        listView=(ListView)findViewById(R.id.ContactListView);
        boolean pauseOnScroll = true; // or true
        boolean pauseOnFling = true; // or false
        PauseOnScrollListener listener = new PauseOnScrollListener(ImageLoader.getInstance(),
                pauseOnScroll, pauseOnFling);
        listView.setOnScrollListener(listener);

         databaseHelper =new DatabaseHelper(this);
        Boolean user=(Boolean)getIntent().getBooleanExtra("User",true);
        Datalist=databaseHelper.RetrieveData(user);
         search=(SearchView) findViewById(R.id.search_bar);

        BottomNavigationView bottomNavigationView=
                (BottomNavigationView)findViewById(R.id.botom_nav_bar);

       if(!user)
        bottomNavigationView.setSelectedItemId(R.id.corpoff);



        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId()==R.id.profile)
                {
                    if(!databaseHelper.getUserId().equals("NotFound"))
                    {
                        DataHolder details=databaseHelper.getUserProfile();
                        Intent intent =new Intent(ContactList.this,Profile.class);
                        intent.putExtra("details",details);
                        startActivity(intent);
                        return true;
                    }
                    else
                    {
                        Log.d(TAG, "onNavigationItemReselected: "+"i am here");
                        Toast.makeText(ContactList.this,
                                "Please Set Your Profile",Toast.LENGTH_LONG).show();
                        Intent intent=new Intent(ContactList.this,EditProfile.class);
                        startActivity(intent);
                        return true;
                    }
                }
                if(item.getItemId()==R.id.sync)
                {
                    //finish();
                    SyncData syncData=new SyncData();
                    syncData.getDataFromFirebase(ContactList.this);
                    customAdapter.notifyDataSetChanged();

                    return true;
                }
                if(item.getItemId()==R.id.headoff)
                {
                    //customAdapter.notifyDataSetChanged();

                    finish();
                    getIntent().putExtra("User",true);
                    startActivity(getIntent());
                    return true;
                }
                if(item.getItemId()==R.id.corpoff)
                {
                    finish();
                    getIntent().putExtra("User",false);
                    startActivity(getIntent());
                    return true;
                }
                if(item.getItemId()==R.id.gruopsms)
                {
                    final Dialog dialog=new Dialog(ContactList.this);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.dialog_group_sms);
                    ImageView head=(ImageView)dialog.findViewById(R.id.btnHead);
                    ImageView corp=(ImageView)dialog.findViewById(R.id.btnCorp);
                    head.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            finish();

                            Intent intent =new Intent(ContactList.this,GroupSms.class);
                            intent.putExtra("User",true);

                            startActivity(intent);
                            dialog.dismiss();
                        }
                    });

                    corp.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            finish();

                            Intent intent =new Intent(ContactList.this,GroupSms.class);
                            intent.putExtra("User",false);

                            startActivity(intent);
                            dialog.dismiss();
                        }
                    });

                    dialog.show();

                    return true;
                }
                return true;
            }
        });


        customAdapter =new CustomAdapter(ContactList.this,Datalist);
        listView.setAdapter(customAdapter);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search.setIconified(false);
            }
        });

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
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final  DataHolder  details= (DataHolder) customAdapter.getItem(position);



                final Dialog dialog=new Dialog(ContactList.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialogue_options);
                ImageView btnCall=(ImageView)dialog.findViewById(R.id.call);
                ImageView    btnSms=(ImageView)dialog.findViewById(R.id.sms);
                ImageView   btnMail=(ImageView)dialog.findViewById(R.id.mail);
                ImageView   btnFb=(ImageView)dialog.findViewById(R.id.fb);
                ImageView   btnProfile=(ImageView)dialog.findViewById(R.id.user);
                ImageView   btnwhatsapp=(ImageView)dialog.findViewById(R.id.whtsapp);
                ImageView   btnviber=(ImageView)dialog.findViewById(R.id.viber);



                btnSms.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent SmsIntent=new Intent(Intent.ACTION_VIEW);
                        SmsIntent.setType("vnd.android-dir/mms-sms");
                        SmsIntent.putExtra("address", "+880"+details.getMobile());
                        SmsIntent.putExtra("sms_body","Write your SMS");

                        if(SmsIntent.resolveActivity(getPackageManager())!=null)
                        {
                            startActivity(SmsIntent);
                        }
                        dialog.dismiss();

                    }
                });


                btnCall.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent CallIntent=new Intent(Intent.ACTION_DIAL);
                        CallIntent.setData(Uri.parse("tel:0"+details.getMobile()));
                        if(CallIntent.resolveActivity(getPackageManager())!=null)
                        {
                            startActivity(CallIntent);
                        }
                        dialog.dismiss();
                    }
                });

                btnMail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent MailIntent = new Intent(Intent.ACTION_SEND);
                        MailIntent.setType("message/rfc822");
                        MailIntent.putExtra(Intent.EXTRA_EMAIL  , new String[]{details.getEmail()});

                        try {
                            startActivity(Intent.createChooser(MailIntent, "Send mail..."));
                        } catch (ActivityNotFoundException ex) {
                            //Toast.makeText(ContactDetails.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();
                    }
                });

                btnFb.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String search="www.facebook.com/";

                        Intent IdSearch=new Intent(Intent.ACTION_WEB_SEARCH);
                        IdSearch.putExtra(SearchManager.QUERY,search+details.getFbId());
                        if(IdSearch.resolveActivity(getPackageManager())!=null)
                        {
                            startActivity(IdSearch);
                        }
                        else
                        {
                            // Toast.makeText(ContactDetails.this,"Error",Toast.LENGTH_LONG).show();
                        }
                        dialog.dismiss();


                    }
                });

                btnProfile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent =new Intent(ContactList.this,Profile.class);
                        intent.putExtra("details",details);
                        intent.putExtra("User",true);
                        startActivity(intent);
                        dialog.dismiss();
                    }
                });

                btnwhatsapp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PackageManager pm = getPackageManager();
                        try {


                            String toNumber = details.getWhtsapp();
                            Intent sendIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + "" + toNumber + "?body=" + ""));
                            sendIntent.setPackage("com.whatsapp");
                            startActivity(sendIntent);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(ContactList.this, "Please install whats app", Toast.LENGTH_LONG).show();

                        }
                    }
                });

                btnviber.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String sphone = details.getViber();
                        Uri uri = Uri.parse("tel:" + Uri.encode(sphone));
                        Intent intent = new Intent("android.intent.action.VIEW");
                        intent.setClassName("com.viber.voip", "com.viber.voip.WelcomeActivity");
                        intent.setData(uri);
                       startActivity(intent);
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
                int dialogWindowHeight = (int) (displayHeight * 0.4f);


                layoutParams.width = dialogWindowWidth;
                layoutParams.height = dialogWindowHeight;


                dialog.getWindow().setAttributes(layoutParams);

            }
        });



    }
    public  void initImageloader()
    {
        UniversalImageloader universalImageLoader=
                new UniversalImageloader(ContactList.this);
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
    }


}
