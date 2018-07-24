package com.example.rowan.pplcontact;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;

public class EditProfile extends AppCompatActivity {
    EditText Name,Phone,Email,FbId,Mobile,Designation,Whtsapp,Viber;
    Spinner Branch;
    ImageView ProfilePic,Update;
    private static final int req = 2;
    Uri uri = null;
    Boolean x=false;
    long length;
    private static final String TAG = "EditProfile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_edit_profile);

        Name=(EditText)findViewById(R.id.user_name);
        Phone=(EditText)findViewById(R.id.phone);
        Email=(EditText)findViewById(R.id.mail);
        FbId=(EditText)findViewById(R.id.Fb);
        Mobile=(EditText)findViewById(R.id.mobile);
        Whtsapp=(EditText)findViewById(R.id.editwhatsapp);
        Viber=(EditText)findViewById(R.id.editviber);
        Designation=(EditText)findViewById(R.id.Deisgnation);
        Branch=(Spinner) findViewById(R.id.Branch);
        ProfilePic=(ImageView)findViewById(R.id.propic);
        Update=(ImageView)findViewById(R.id.update);

        String[] arraySpinner = new String[] {
               "Head Office","Corporate Office"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arraySpinner);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        Branch.setAdapter(adapter);


        Update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

    if(isConnected())
    {
        HashMap<String,String> data =new HashMap<>();
        DatabaseHelper helper=new DatabaseHelper(EditProfile.this);


        String empid=helper.getUserId();
        String name = Name.getText().toString();
        String designation = Designation.getText().toString();
        String branch = Branch.getSelectedItem().toString();
        String phone = Phone.getText().toString();
        String mobile= Mobile.getText().toString();
        String mail = Email.getText().toString();
        String fb = FbId.getText().toString();
        String whtsapp=Whtsapp.getText().toString();
        String viber=Viber.getText().toString();



        if(!TextUtils.isEmpty(name))
        {
            data.put("Name",name);
        }
        if(!TextUtils.isEmpty(designation))
        {
            data.put("Designation",designation);
        }

        {
            data.put("Branch",branch);
        }
        if(!TextUtils.isEmpty(phone))
        {
            data.put("Phone",phone);
        }
        if(!TextUtils.isEmpty(mail))
        {
            data.put("Email",mail);
        }
        if(!TextUtils.isEmpty(mobile))
        {
            data.put("Mobile",mobile);
        }
        if(!TextUtils.isEmpty(fb))
        {
            data.put("FbId",fb);
        }
        if(!TextUtils.isEmpty(whtsapp))
        {
            data.put("Whtsapp",whtsapp);
        }
        if(!TextUtils.isEmpty(viber))
        {
            data.put("Viber",viber);
        }

        if(empid.equals("NotFound") && data.size()==9)
        {
            String image="NoImage";
            empid=mobile;
            data.put("EmpId",empid);
            SyncData task=new SyncData();
            task.UpdateData(data,true);
            if(x && length<102000)
            {
                task.UploadPhoto(uri,empid,true);
            }
            else
            {
                task.UploadPhoto(uri,empid,false);
            }


            DataHolder holder=new DataHolder(name,phone,mail,fb,mobile,designation,
                    branch,image,empid,whtsapp,viber);

            helper.InsertUserData(holder);
            DataHolder details=helper.getUserProfile();

            Intent intent=new Intent(EditProfile.this,Profile.class);
            intent.putExtra("details",details);
            startActivity(intent);


        }
        else if (!empid.equals("NotFound"))
        {
            String image="NoImage";
            data.put("EmpId",empid);
            SyncData task=new SyncData();
            task.UpdateData(data,false);
            if(x)
            {
                task.UploadPhoto(uri,empid,true);
            }
            else
            {
                task.UploadPhoto(uri,empid,false);
            }
            data.put("Image",image);
            helper.UpdateUserProfile(data);

            DataHolder details=helper.getUserProfile();

            Intent intent=new Intent(EditProfile.this,Profile.class);
            intent.putExtra("details",details);
            startActivity(intent);

        }
        else
        {
            Toast.makeText(EditProfile.this,
                    "Please Fill up All Data",Toast.LENGTH_LONG).show();
        }




    }else {
        Toast.makeText(EditProfile.this,
                "Check Your Connection",Toast.LENGTH_LONG).show();
    }

            }


        });




    }
    public void ChangePhoto(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // intent.setType("Image/*");
        Log.d(TAG, "ChangePhoto: "+"I am here");
        startActivityForResult(intent, req);
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == req && resultCode == RESULT_OK) {
            uri = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(uri,filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();



            File file = new File(picturePath);
            length = file.length();
            if(length>102000)
            {
                Toast.makeText(EditProfile.this,"Please choose a image less than 100kb",
                        Toast.LENGTH_LONG).show();
            }
            else
            {
                ProfilePic=(ImageView)findViewById(R.id.propic);
                ProfilePic.setImageBitmap(BitmapFactory.decodeFile(picturePath));
            }

            Log.d(TAG, "onActivityResult: -->"+length);
            x = true;

        }
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
