package com.example.rowan.pplcontact;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
     final DatabaseHelper  databaseHelper =new DatabaseHelper(this);
        Button sync=(Button) findViewById(R.id.btnSync);
      sync.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              SyncData syncData=new SyncData();
              syncData.getDataFromFirebase(MainActivity.this);
          }
      });
      Button show=(Button) findViewById(R.id.btnContactlist);
      show.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              Intent intent=new Intent(MainActivity.this,ContactList.class);
              startActivity(intent);
          }
      });
        Button profile=(Button) findViewById(R.id.profie);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!databaseHelper.getUserId().equals("NotFound"))
                {
                    DataHolder details=databaseHelper.getUserProfile();
                    Intent intent =new Intent(MainActivity.this,Profile.class);
                    intent.putExtra("details",details);
                    startActivity(intent);
                }
                else
                {
                    Toast.makeText(MainActivity.this,
                            "Please Set Your Profile",Toast.LENGTH_LONG).show();
                    Intent intent=new Intent(MainActivity.this,EditProfile.class);
                    startActivity(intent);
                }

            }
        });
    }
}
