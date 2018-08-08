package com.example.rowan.pplcontact;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.content.ContentValues.TAG;

public class SyncData extends Activity{
    String path;
    public void  getDataFromFirebase(final Context context)
   {

       final ProgressDialog dialog=ProgressDialog.
               show(context,"","Updating...",true);

       DatabaseReference reference =FirebaseDatabase.getInstance().getReference("UserData");

       reference.addListenerForSingleValueEvent(new ValueEventListener() {
           @Override
           public void onDataChange(DataSnapshot dataSnapshot) {

               for(DataSnapshot ds:dataSnapshot.getChildren()){


                   String name = ds.child("Name").getValue().toString();
                   String designation = ds.child("Designation").getValue().toString();
                   String branch = ds.child("Branch").getValue().toString();
                   String phone = ds.child("Phone").getValue().toString();
                   String mobile= ds.child("Mobile").getValue().toString();
                   String mail = ds.child("Email").getValue().toString();
                   String fb = ds.child("FbId").getValue().toString();
                   String image=ds.child("Image").getValue().toString();
                   String empId=ds.getKey();
                   String whtsapp=ds.child("Whtsapp").getValue().toString();
                   String viber=ds.child("Viber").getValue().toString();
                   Log.d(TAG, "onDataChange: "+empId+" "+whtsapp);
                   DataHolder newData = new DataHolder(name, phone, mail, fb,
                           mobile, designation,branch,image,empId,whtsapp,viber);
                   DatabaseHelper databaseHelper=new DatabaseHelper(context);
                   databaseHelper.InsertData(newData);



               }
               dialog.dismiss();
        //    startActivity(new Intent(context,ContactList.class));


           }

           @Override
           public void onCancelled(DatabaseError databaseError) {

           }
       });


   }
   public void UpdateData(HashMap<String,String> data,boolean type)
   {
       DatabaseReference reference =FirebaseDatabase.getInstance().
               getReference("UserData").child(data.get("EmpId"));

       if(data.containsKey("Name"))
       reference.child("Name").setValue(data.get("Name"));

       if(data.containsKey("Designation"))
       reference.child("Designation").setValue(data.get("Designation"));

       if(data.containsKey("Branch"))
       reference.child("Branch").setValue(data.get("Branch"));

       if(data.containsKey("Phone"))
       reference.child("Phone").setValue(data.get("Phone"));

       if(data.containsKey("Mobile"))
       reference.child("Mobile").setValue(data.get("Mobile"));

       if(data.containsKey("Email"))
       reference.child("Email").setValue(data.get("Email"));

       if(data.containsKey("FbId"))
       reference.child("FbId").setValue(data.get("FbId"));

       if(data.containsKey("Whtsapp"))
           reference.child("Whtsapp").setValue(data.get("Whtsapp"));

       if(data.containsKey("Viber"))
           reference.child("Viber").setValue(data.get("Viber"));



        if(type)
       reference.child("Image").setValue("NoImage");


   }

   public void UploadPhoto(Uri uri, final String empid,Boolean flag)
   {
       final DatabaseReference reference =FirebaseDatabase.getInstance().
               getReference("UserData").child(empid);

      if(flag)
      {

          FirebaseStorage storage = FirebaseStorage.getInstance();
          StorageReference  storageReference = storage.getReference();
          StorageReference filepath = storageReference.child(empid).child(uri.getLastPathSegment());
          filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
              @Override
              public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                  Uri download = Uri.parse(taskSnapshot.getMetadata().getReference().getDownloadUrl().toString());
               path  = download.toString();
                  reference.child("Image").setValue(path);



              }
          });
      }
      else
      {
          reference.child("Image").setValue("NoImage");
          path="NoImage";
      }


   }

}
