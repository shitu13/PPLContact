package com.example.rowan.pplcontact;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.widget.Toast;

public class PhoneStateReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        String state=intent.getStringExtra(TelephonyManager.EXTRA_STATE);
        try{
            if(state.equals(TelephonyManager.EXTRA_STATE_RINGING))
            {
                String number=intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                DatabaseHelper databaseHelper =new DatabaseHelper(context);
                String Caller=databaseHelper.getCallerName(number.substring(4));
                Toast.makeText(context,Caller,Toast.LENGTH_SHORT).show();
            }



        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
