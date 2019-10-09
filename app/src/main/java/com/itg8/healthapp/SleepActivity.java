package com.itg8.healthapp;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.itg8.healthapp.common.DateUtility;
import com.itg8.healthapp.common.Prefs;
import com.itg8.healthapp.model.AlarmModel;
import com.itg8.healthapp.utils.AppConst;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.SystemClock;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.lang.ref.SoftReference;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.itg8.healthapp.NotificationHelper.ALARM_TYPE_RTC;

public class SleepActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int RC_ALARM = 346;
    private static final String CHANNEL_ID = "12";
    private RecyclerView recyclerView;
    private List<AlarmModel> alramList = new ArrayList<>();
    private TextView lblTime;
    private Button btnSave;
    private Calendar calendar;
    private StringBuilder sb = new StringBuilder();
    private AlarmAdapter mAdapter;
    private long timeLong;
    private int hour;
    private int min;

   private static   int count = 0;
    private static final String TAG = "SleepActivity";
    private NotificationManager notifManager;

    BroadcastReceiver  mServiceReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context, "Alarm Received mServiceReceiver" , Toast.LENGTH_SHORT).show();


            Intent serviceIntent = new Intent(context, SleepAlarmService.class);
            //     serviceIntent.putExtra("inputExtra", "Foreground Service Example in Android");
//            startForegroundService(context);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(serviceIntent);
            }else{
                startService(serviceIntent);
            }


        }
    };

    BroadcastReceiver mAlarmReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context, "Alarm Received"+count , Toast.LENGTH_SHORT).show();
            createNotification(" Getting late hurry up ....wake up wake up !!!  notification count"+count,context);
            String number = intent.getStringExtra(AppConst.EXTRA_SMS_NUMBER);

            if(count>=3) {
                sendSMS(number, "XXX has set alarm for wake up but still not responding .. as soon as possible response him..");
                clickToggleButtonElapsed(false);
            }
            count++;

        }
    };

  @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep);
        Toolbar toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.recyclerView);
        lblTime = findViewById(R.id.lblTime);
        btnSave = findViewById(R.id.btnSave);
        lblTime.setOnClickListener(this);
        lblTime.setText(DateUtility.getDateFromDateTime(Calendar.getInstance().getTime()));
        btnSave.setOnClickListener(this);
        calendar = Calendar.getInstance();
        setSupportActionBar(toolbar);




    }

    public void sendSMS(String phoneNo, String msg) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, "9890756622", msg, null, null);

            Toast.makeText(this, "Message Sent",
                    Toast.LENGTH_LONG).show();
        } catch (Exception ex) {
            Toast.makeText(this, ex.getMessage().toString(),
                    Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
    }

    private void setUpPrefs() {
        if (Prefs.getString("time") != null) {
            String[] value = Prefs.getString("time").split(",");
            for (String s : value) {
                AlarmModel model = new AlarmModel();
                model.setEnable(true);
                model.setTime(s);
                alramList.add(model);
            }

            String s = new Gson().toJson(alramList);
            Prefs.putString("alarm", s);
            mAdapter.notifyDataSetChanged();



        }
    }

    private void setUpRecyclerView() {
        mAdapter = new AlarmAdapter(alramList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lblTime:
                openTimePicker();
                break;
            case R.id.btnSave:
                clickToggleButtonElapsed(true);
                break;


        }
    }

    public void clickToggleButtonElapsed(boolean isEnable) {


        if (isEnable) {
            NotificationHelper.scheduleRepeatingRTCNotification(this, hour, min);
            NotificationHelper.enableBootReceiver(this);
        } else {
            NotificationHelper.cancelAlarmElapsed();
            NotificationHelper.disableBootReceiver(this);
            NotificationHelper.cancelAlarmRTC();

        }
    }

    public void cancelAlarms() {
        NotificationHelper.cancelAlarmRTC();
        NotificationHelper.cancelAlarmElapsed();
        NotificationHelper.disableBootReceiver(this);
    }



    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onStart() {
        super.onStart();
//        IntentFilter filter = new IntentFilter();
//        filter.addAction(AppConst.ACTION_BROADCAST_SLEEP);
//        LocalBroadcastManager.getInstance(this).registerReceiver(mServiceReceiver, filter);

        IntentFilter filterB = new IntentFilter();
        filterB.addAction(AppConst.ACTION_BROADCAST_ALARM);
        LocalBroadcastManager.getInstance(this).registerReceiver(mAlarmReceiver, filterB);
    }

    @Override
    protected void onStop() {
        super.onStop();
//        LocalBroadcastManager.getInstance(this).unregisterReceiver(mServiceReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mAlarmReceiver);

    }



    public void openTimePicker() {
        int mHour = calendar.get(Calendar.HOUR_OF_DAY);
        int mMinute = calendar.get(Calendar.MINUTE);
        new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {


            @Override
            public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                Calendar time = Calendar.getInstance();
                time.set(Calendar.HOUR_OF_DAY, hourOfDay);
                time.set(Calendar.MINUTE, minute);
                hour = hourOfDay;
                min = minute;
//                if (time.getTime().after(Calendar.getInstance().getTime())) {
//                    model.setInvalidTimeErr(context.getString(R.string.invalidTime));
                timeLong = time.getTimeInMillis();
                lblTime.setText(DateUtility.getDateFromDateTime(time.getTime()));
                sb.append(lblTime.getText().toString()).append(",");
                Prefs.putString("time", sb.toString());
//                } else {
//                    Toast.makeText(SleepActivity.this, "Invalid Time", Toast.LENGTH_SHORT).show();
//
//                }
            }
        }, mHour, mMinute, false).show();


    }
    public void createNotification(String aMessage, Context context) {
        final int NOTIFY_ID = 0; // ID of notification
//        Uri soundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"+ getApplicationContext().getPackageName() + "/" + R.raw.siren);
        String id = context.getString(R.string.default_notification_channel_id); // default_channel_id
        String title = context.getString(R.string.default_notification_channel_title); // Default Channel
       Uri uri =  RingtoneManager.getValidRingtoneUri(this);
        Intent intent;
        PendingIntent pendingIntent;
        NotificationCompat.Builder builder;
        if (notifManager == null) {
            notifManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            AudioAttributes att = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build();
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = notifManager.getNotificationChannel(id);
            if (mChannel == null) {
                mChannel = new NotificationChannel(id, title, importance);
                mChannel.enableVibration(true);
                mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                mChannel.setSound(uri,att);
                notifManager.createNotificationChannel(mChannel);
            }
            builder = new NotificationCompat.Builder(context, id);
            intent = new Intent(context, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            builder.setContentTitle(aMessage)                            // required
                    .setSmallIcon(android.R.drawable.ic_popup_reminder)   // required
                    .setContentText(context.getString(R.string.app_name)) // required
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setTicker(aMessage)
                    .setSound(uri)
                    .setPriority(Notification.PRIORITY_MAX)
                    .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
        }
        else {
            builder = new NotificationCompat.Builder(context, id);
            intent = new Intent(context, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            builder.setContentTitle(aMessage)                            // required
                    .setSmallIcon(android.R.drawable.ic_popup_reminder)   // required
                    .setContentText(context.getString(R.string.app_name)) // required
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setTicker(aMessage)
                    .setSound(uri)
                    .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400})
                    .setPriority(Notification.PRIORITY_HIGH);
        }
        Notification notification = builder.build();
        notifManager.notify(NOTIFY_ID, notification);
    }




}
