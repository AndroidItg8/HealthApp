package com.itg8.healthapp;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import android.widget.Toast;



public class AlarmReceiver  extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent arg1) {
        BreathSchudleService.enqueueWork(context,arg1);

    // sendWhatsUp(context);


    }





}
