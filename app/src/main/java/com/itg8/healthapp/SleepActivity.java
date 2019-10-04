package com.itg8.healthapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.itg8.healthapp.common.DateUtility;
import com.itg8.healthapp.common.Prefs;
import com.itg8.healthapp.model.AlarmModel;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.lang.ref.SoftReference;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class SleepActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int RC_ALARM = 346;
    private RecyclerView recyclerView;
    private List<AlarmModel> alramList = new ArrayList<>();
    private TextView lblTime;
    private Button btnSave;
    private Calendar calendar;
    private StringBuilder sb= new StringBuilder();
    private AlarmAdapter mAdapter;
    private long timeLong;
     int count=0;

    BroadcastReceiver mServiceReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent)
        {
            //Extract your data - better to use constants...

            sendMessageToWhatsAppContact("");
            count++;
            if(count>3) {
                String number = intent.getStringExtra("number");

            }

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
        btnSave.setOnClickListener(this);
        calendar = Calendar.getInstance();
        setSupportActionBar(toolbar);
        setUpRecyclerView();
        setUpPrefs();



        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
    }

    private void setUpPrefs() {
        if(Prefs.getString("time")!=null){
            String[] value = Prefs.getString("time").split(",");
            for (String s : value) {
                AlarmModel model = new AlarmModel();
                model.setEnable(true);
                model.setTime(s);
                alramList.add(model);
            }

            String s = new Gson().toJson(alramList);
            Prefs.putString("alarm",s);


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

        }
    }

    private void setAlarm() {
        Intent intent = new Intent(SleepActivity.this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(SleepActivity.this, RC_ALARM, intent, 0);
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                1000*60, pendingIntent);

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
        registerReceiver(mServiceReceiver , filter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(mServiceReceiver);
    }

    private void sendMessageToWhatsAppContact( String number) {
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
                if (time.getTime().before(Calendar.getInstance().getTime())) {
//                    model.setInvalidTimeErr(context.getString(R.string.invalidTime));
                    Toast.makeText(SleepActivity.this, "Invalid Time", Toast.LENGTH_SHORT).show();
                } else {
                    timeLong =calendar.getTimeInMillis();
                    lblTime.setText(DateUtility.getDateFromDateTime(calendar.getTime()));
                    sb.append(lblTime.getText().toString()).append(",");
                    Prefs.putString("time", sb.toString());
                }
            }
        }, mHour, mMinute, false).show();

        setAlarm();


    }

//    String toNumber = "+91 98765 43210"; // contains spaces.
//    toNumber = toNumber.replace("+", "").replace(" ", "");
//
//    Intent sendIntent = new Intent("android.intent.action.MAIN");
//sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(imageFile));
//sendIntent.putExtra("jid", toNumber + "@s.whatsapp.net");
//sendIntent.putExtra(Intent.EXTRA_TEXT, message);
//sendIntent.setAction(Intent.ACTION_SEND);
//sendIntent.setPackage("com.whatsapp");
//sendIntent.setType("image/png");
//context.startActivity(sendIntent);
}
