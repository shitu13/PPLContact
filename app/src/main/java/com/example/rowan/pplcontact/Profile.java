package com.example.rowan.pplcontact;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

public class Profile extends AppCompatActivity {


    TextView Name,Phone,Email,FbId,Mobile,Designation,Branch;
    ImageView ProfilePic,Edit;
    private static final String TAG = "Profile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.layout_profile);
        initImageloader();
        Name=(TextView)findViewById(R.id.user_name);
        Phone=(TextView)findViewById(R.id.phone);
        Email=(TextView)findViewById(R.id.mail);
        FbId=(TextView)findViewById(R.id.Fb);
        Mobile=(TextView)findViewById(R.id.mobile);
        Designation=(TextView)findViewById(R.id.Deisgnation);
        Branch=(TextView)findViewById(R.id.Branch);
        ProfilePic=(ImageView)findViewById(R.id.ProfilePic);
        Edit =(ImageView)findViewById(R.id.edit);


        Edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Profile.this,EditProfile.class);
                startActivity(intent);
            }
        });

        final DataHolder details=(DataHolder)getIntent().getSerializableExtra("details");
        Boolean User=(Boolean)getIntent().getBooleanExtra("User",false);
        if(User)
        {
            Edit.setVisibility(View.INVISIBLE);
        }
        List<DataHolder> Data= new ArrayList<>();
        Data.add(details);
        if(Data.size()>0){


            try{

                Name.setText(Data.get(0).getName());
                Phone.setText(Data.get(0).getPhone());
                Email.setText(Data.get(0).getEmail());
                FbId.setText(Data.get(0).getFbId());
                Mobile.setText("0"+Data.get(0).getMobile());
                Designation.setText(Data.get(0).getDesignation());
                Branch.setText(Data.get(0).getBranch());


                if(isConnected())
                {
                    if(isConnected()){
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("UserData").
                                child(Data.get(0).getEmpId());

                        reference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                String image=dataSnapshot.child("Image").getValue().toString();
                                if(!image.equals("NoImage")){
                                    UniversalImageloader.setImage(image,ProfilePic,null);
                                }


                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }
                    else {
                        Toast.makeText(Profile.this,
                                "Connect to Internet for Profile Picture",Toast.LENGTH_LONG).show();
                    }
                }



            }catch (Exception e)
            {
                Toast.makeText(Profile.this,
                    "Please Set Your Profile",Toast.LENGTH_LONG).show();
                Log.d(TAG, "onCreate: "+e.toString());
                /*Intent intent=new Intent(Profile.this,EditProfile.class);
                startActivity(intent);*/
            }
        }
        else
        {
            Intent intent=new Intent(Profile.this,EditProfile.class);
            startActivity(intent);
        }






    }

    @Override
    public void onBackPressed() {
        finish();
        Intent intent=new Intent(Profile.this, ContactList.class);
        startActivity(intent);
    }

    public  void initImageloader()
    {
        UniversalImageloader universalImageLoader=
                new UniversalImageloader(Profile.this);
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
    }
    public boolean isConnected()
    {
        ConnectivityManager connectivityManager=(ConnectivityManager)getSystemService(getBaseContext().CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(0).getState()== NetworkInfo.State.CONNECTED||connectivityManager.getNetworkInfo(0).getState()==NetworkInfo.State.CONNECTING||connectivityManager.getNetworkInfo(1).getState()== NetworkInfo.State.CONNECTED||connectivityManager.getNetworkInfo(1).getState()==NetworkInfo.State.CONNECTING) {
            return true;
        }
        else if(connectivityManager.getNetworkInfo(0).getState()== NetworkInfo.State.DISCONNECTED||connectivityManager.getNetworkInfo(1).getState()== NetworkInfo.State.DISCONNECTED)
        {
            return  false;
        }
        return false;

    }
}
