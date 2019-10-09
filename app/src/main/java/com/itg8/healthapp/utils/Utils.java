package com.itg8.healthapp.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import com.itg8.healthapp.AlarmReceiver;
import com.itg8.healthapp.DeviceBootReceiver;

import java.util.Calendar;
import java.util.Date;

import static android.os.Build.VERSION.SDK_INT;

public class Utils {


    private static final String TAG = "Utils";
    private static final int RC_REQUEST_CODE = 123;


    public static void scheduleTimer(Context context, int mDelayedTime){
        cancelAlarm(context);
        AlarmManager mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, RC_REQUEST_CODE, intent,  PendingIntent.FLAG_CANCEL_CURRENT);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        Log.d(TAG, "scheduleTimer:before "+calendar.getTimeInMillis());
        calendar.add(Calendar.MILLISECOND,mDelayedTime);
        Log.d(TAG, "scheduleTimer:After "+calendar.getTimeInMillis());
        //// TODO: use calendar.add(Calendar.SECOND,MINUTE,HOUR, int);

        //calendar.add(Calendar.SECOND, 10);

        //ALWAYS recompute the calendar after using add, set, roll
        Date date = calendar.getTime();

//        mAlarmManager.setExact(AlarmManager.RTC_WAKEUP, date.getTime(), alarmIntent);

        if (SDK_INT < Build.VERSION_CODES.M)
            mAlarmManager.setExact(AlarmManager.RTC_WAKEUP, date.getTime(), alarmIntent);
        else {
            mAlarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, date.getTime(), alarmIntent);
        }


        // Enable {@code BootReceiver} to automatically restart when the
        // device is rebooted.
        //// TODO: you may need to reference the context by ApplicationActivity.class
        ComponentName receiver = new ComponentName(context, DeviceBootReceiver.class);
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);

    }

    /**
     * Cancels the next alarm from running. Removes any intents set by this
     * WakefulBroadcastReceiver.
     * @param context the context of the app's Activity
     */
    public static void cancelAlarm(Context context) {
        Log.d("WakefulAlarmReceiver", "{cancelAlarm}");

        AlarmManager mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);

        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        mAlarmManager.cancel(alarmIntent);

        // Disable {@code BootReceiver} so that it doesn't automatically restart when the device is rebooted.
        //// TODO: you may need to reference the context by ApplicationActivity.class
        ComponentName receiver = new ComponentName(context, DeviceBootReceiver.class);
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
    }

    public static BreathState getStateByCount(int breathCount){
        if(breathCount>AppConst.THRESHOLD_STRESS){
            return BreathState.STRESS;
        }else if(breathCount<AppConst.THRESHOLD_CALM)
            return BreathState.CALM;
        else {
            return BreathState.NORMAL;
        }
    }




}
