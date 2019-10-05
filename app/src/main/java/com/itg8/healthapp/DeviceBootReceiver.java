package com.itg8.healthapp;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class DeviceBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
                //only enabling one type of notifications for demo purposes
//                NotificationHelper.scheduleRepeatingElapsedNotification(context);


                Intent serviceIntent = new Intent(context, BreathSchudleService.class);
                context.startService(serviceIntent);
            }
        }
    }
}