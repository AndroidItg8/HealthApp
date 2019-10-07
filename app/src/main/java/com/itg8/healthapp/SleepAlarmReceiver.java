package com.itg8.healthapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.itg8.healthapp.utils.AppConst;


public class SleepAlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        sendLocalBroadCast(context);



    }
    private void sendLocalBroadCast(Context context) {
        Intent intent = new Intent(AppConst.ACTION_BROADCAST_ALARM);
        intent.putExtra(AppConst.EXTRA_SMS_NUMBER, " 9890410668");

        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

}
