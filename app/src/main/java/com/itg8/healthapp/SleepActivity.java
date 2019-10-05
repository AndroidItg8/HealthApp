package com.itg8.healthapp;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
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

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;
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

    int count = 0;
    private static final String TAG = "SleepActivity";

    BroadcastReceiver mServiceReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            showNotification(context, SleepActivity.class,
                    "New Notification Alert..!", "scheduled for " + ALARM_TYPE_RTC + " seconds",ALARM_TYPE_RTC);
//                    sendMessageToWhatsAppContact("");
            String number = intent.getStringExtra("number");
            count++;
            if(count>3) {
                sendSMS(number, "The is not responding ...");
            }
        }
    };






    public void showNotification(Context context, Class<?> cls, String title, String content,int RequestCode)
    {
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Intent notificationIntent = new Intent(context, cls);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(cls);
        stackBuilder.addNextIntent(notificationIntent);

        PendingIntent pendingIntent = stackBuilder.getPendingIntent(
                RequestCode,PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,"Default");
        Notification notification = builder.setContentTitle(title)
                .setContentText(content).setAutoCancel(true)
                .setSound(alarmSound).setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentIntent(pendingIntent).build();

        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "id1";
            CharSequence channelName = "id1";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(channelId, channelName, importance);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            assert notificationManager != null;
            notificationManager.createNotificationChannel(notificationChannel);

//            NotificationCompat.Builder builder = new NotificationCompat.Builder(context,"Default");
//            Notification notification = builder.setContentTitle(title)
//                    .setContentText(content).setAutoCancel(true)
//                    .setSound(alarmSound).setSmallIcon(R.mipmap.ic_launcher_round)
//                    .setContentIntent(pendingIntent)
//                    .setChannel(channelId).build();

        }


        assert notificationManager != null;
        notificationManager.notify(RequestCode,notification);
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep);
        Toolbar toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.recyclerView);
        lblTime = findViewById(R.id.lblTime);
        btnSave = findViewById(R.id.btnSave);
        lblTime.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        calendar = Calendar.getInstance();
        setSupportActionBar(toolbar);
        setUpRecyclerView();
        setUpPrefs();



    }

    public void sendSMS(String phoneNo, String msg) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, msg, null, null);
            Toast.makeText(getApplicationContext(), "Message Sent",
                    Toast.LENGTH_LONG).show();
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), ex.getMessage().toString(),
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
                //  setAlarm();
                clickToggleButtonElapsed(v);
                break;


        }
    }

    public void clickToggleButtonElapsed(View view) {


        if (true) {
            NotificationHelper.scheduleRepeatingRTCNotification(this, hour, min);
            NotificationHelper.enableBootReceiver(this);
        } else {
            NotificationHelper.cancelAlarmElapsed();
            NotificationHelper.disableBootReceiver(this);
        }
    }

    public void cancelAlarms(View view) {
        NotificationHelper.cancelAlarmRTC();
        NotificationHelper.cancelAlarmElapsed();
        NotificationHelper.disableBootReceiver(this);
    }

    private void setAlarm() {
        Intent intent = new Intent(SleepActivity.this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(SleepActivity.this, RC_ALARM, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Log.d(TAG, "setAlarm: " + timeLong);
        alarmManager.set(AlarmManager.RTC_WAKEUP, timeLong,
                pendingIntent);


        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + AlarmManager.INTERVAL_FIFTEEN_MINUTES,
                AlarmManager.INTERVAL_FIFTEEN_MINUTES, pendingIntent);


        //Setting intent to class where notification will be handled


//        alarmManager.set(AlarmManager.RTC_WAKEUP, timeLong, pendingIntent);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.SmsReceiver");
        registerReceiver(mServiceReceiver, filter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(mServiceReceiver);
    }

    private void sendMessageToWhatsAppContact(String number) {
//        PackageManager packageManager = getPackageManager();
//        Intent i = new Intent(Intent.ACTION_SENDTO);
//        try {
//            String url = "https://api.whatsapp.com/send?phone=" + number + "&text=" + URLEncoder.encode("MEssge", "UTF-8");
//            i.setPackage("com.whatsapp");
//            i.setData(Uri.parse(url));
//            if (i.resolveActivity(packageManager) != null) {
//                startActivity(i);
//            }
//            Toast.makeText(this, "sendWhatsUp"+ number, Toast.LENGTH_SHORT).show();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }


        String toNumber = "+91 98904 10668"; // contains spaces.
        toNumber = toNumber.replace("+", "").replace(" ", "");

        Intent sendIntent = new Intent("android.intent.action.MAIN");
        sendIntent.putExtra("jid", toNumber + "@s.whatsapp.net");
        sendIntent.putExtra(Intent.EXTRA_TEXT, "hiii");
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.setPackage("com.whatsapp");
        sendIntent.setType("text/plain");
        startActivity(sendIntent);


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


}
