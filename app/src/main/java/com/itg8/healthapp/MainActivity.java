package com.itg8.healthapp;

import android.annotation.SuppressLint;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.itg8.healthapp.model.BreathModel;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Handler;
import android.os.IBinder;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.qap.ctimelineview.TimelineRow;
import org.qap.ctimelineview.TimelineViewAdapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Handler mHandler= new Handler();
     ArrayList<TimelineRow> timelineRowsList = new ArrayList<>();
    private static final String TAG = "MainActivity";
    BreathModel model= new BreathModel();

    private void bindService() {
        Intent serviceBindIntent = new Intent(this, BreathSchudleService.class);
        bindService(serviceBindIntent, getServiceConnection(), Context.BIND_AUTO_CREATE);
    }


    // Keeping this in here because it doesn't require a context
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder iBinder) {
            Log.d(TAG, "ServiceConnection: connected to service.");
            // We've bound to MyService, cast the IBinder and get MyBinder instance
//            BreathSchudleService.MyBinder binder = (BreathSchudleService.MyBinder) iBinder;
//            mService = binder.getService();

//            mService.bindListener=MainActivity.this;

        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            Log.d(TAG, "ServiceConnection: disconnected from service.");
//            mBinder.postValue(null);
        }
    };

    public ServiceConnection getServiceConnection() {
        return serviceConnection;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        bindService();
        getServiceData();
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        callJobServiceToDownload();
    }

    private void getServiceData() {
        int breathValue= 12;
        try {
            Thread.sleep(1000*60);

            model.setValue(String.valueOf(breathValue));
            model.setStatus("Normal");

            breathValue=+2;

        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }

    @SuppressLint("MissingPermission")
    private void setJoSchedule(BreathModel model) {
        Intent mIntent = new Intent(this, BreathSchudleService.class);
        mIntent.putExtra("maxCountValue",model );
        BreathSchudleService.enqueueWork(this, mIntent);




    }
    private void callJobServiceToDownload() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

            setJoSchedule(model);
        } else {
            //StartAlarmManager
//            AlarmBroadcastReciever.setAlarm(true, this);
//
        }


    }


    private void setTimeLine() {
        // Create Timeline rows List
        ListView myListView = (ListView) findViewById(R.id.timeline_listView);
        ArrayAdapter<TimelineRow> myAdapter = new TimelineViewAdapter(this, 0, timelineRowsList,
                false);
        for (int i=0; i<=10; i++) {
            timelineRowsList.add(setData());
        }
        myListView.setAdapter(myAdapter);

    }

    private TimelineRow setData() {
        TimelineRow  myRow = new TimelineRow(0);

// To set the row Date (optional)
        myRow.setDate(new Date());
// To set the row Title (optional)
        myRow.setTitle("Title");
// To set the row Description (optional)
        myRow.setDescription("Description");
// To set the row bitmap image (optional)
        myRow.setImage(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
// To set row Below Line Color (optional)
// To set row Below Line Size in dp (optional)
        myRow.setBellowLineSize(6);
// To set row Image Size in dp (optional)
        myRow.setImageSize(40);
        myRow.setBellowLineColor(Color.argb(255, 0, 0, 0));
// To set background color of the row image (optional)
        myRow.setBackgroundColor(Color.argb(255, 0, 0, 0));
// To set the Background Size of the row image in dp (optional)
        myRow.setBackgroundSize(60);
// To set row Date text color (optional)
        myRow.setDateColor(Color.argb(255, 0, 0, 0));
// To set row Title text color (optional)
        myRow.setTitleColor(Color.argb(255, 0, 0, 0));
// To set row Description text color (optional)
        myRow.setDescriptionColor(Color.argb(255, 0, 0, 0));
        return myRow;
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
