package com.itg8.healthapp;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.itg8.healthapp.utils.AppConst;

public class SleepAlarmService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sendLocalBroadCast();
        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void sendLocalBroadCast() {
        Intent intent = new Intent(AppConst.ACTION_BROADCAST_SLEEP);
        intent.putExtra(AppConst.EXTRA_SMS_NUMBER, " 9890410668");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

}
