package com.itg8.healthapp;

import android.Manifest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import android.content.IntentFilter;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.itg8.healthapp.background.BgModelThread;
import com.itg8.healthapp.background.SleepModelThread;
import com.itg8.healthapp.common.Prefs;
import com.itg8.healthapp.model.BreathModel;
import com.itg8.healthapp.model.SleepModel;
import com.itg8.healthapp.utils.AppConst;
import com.itg8.healthapp.utils.SharedPrefUtils;
import com.itg8.healthapp.utils.Utils;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;


import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import org.qap.ctimelineview.TimelineRow;
import org.qap.ctimelineview.TimelineViewAdapter;

import java.util.ArrayList;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;


public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    private static final int RC_CALL = 2345;
    private static final String TAG = "MainActivity";
    ArrayList<String> mLabels = new ArrayList<>();


    private LineChart mChart;

    private Handler myHandler;

    private Runnable listInteractor;


    BroadcastReceiver mBreathReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != null) {
                if (intent.getAction().equalsIgnoreCase(AppConst.ACTION_BROADCAST_BREATH)) {
                    if (intent.getBooleanExtra(AppConst.EXTRA_BREATH_CHANGE, true)) {
                        Log.d(TAG, "onReceive:  BroadcastReceiver");
                        List<BreathModel> listBreathModel = SharedPrefUtils.getAllBreathModel();
                        generateTimeLine(listBreathModel);
                    }
                }
            }

        }
    };
    private TimelineViewAdapter myAdapter;
    private ListView myListView;
    private boolean isPrermission = false;

    private void generateTimeLine(List<BreathModel> listBreathModel) {
        listInteractor = new BgModelThread(listBreathModel) {
            @Override
            public void run(List<TimelineRow> list) {
                if (Looper.myLooper() == Looper.getMainLooper())
                    Log.d(TAG, "run: is main thread");
                setTimeLine(list);
            }
        };
        myHandler.post(listInteractor);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        init();

        Utils.scheduleTimer(this, AppConst.TIMER_DELAY);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, SleepActivity.class));

            }
        });


    }

    private void init() {
        myHandler = new Handler();
        initView();
        checkPermissionLocation();
        generateTimeLine(SharedPrefUtils.getAllBreathModel());
        generateSleepGraph();


    }

    private void generateSleepGraph() {
        cubicLineChart();
        mChart.invalidate();
        mChart.notifyDataSetChanged();
        mChart.animateX(1000);
        new SleepModelThread(mChart) {
            @Override
            public void run() {
                Log.d(TAG, "run: is main thread SleepGraph");
                if (Looper.myLooper() == Looper.getMainLooper())
                    Log.d(TAG, "run: is main thread SleepGraph");

            }
        };

    }

    private void cubicLineChart() {
        Log.d(TAG, "cubicLineChart: ");
        mChart.setViewPortOffsets(90, 70, 80, 70);
        mChart.setTouchEnabled(true);
        mChart.getDescription().setEnabled(false);
        mChart.setTouchEnabled(true);
        mChart.setDragEnabled(false);
        mChart.setScaleEnabled(false);
        mChart.setDrawGridBackground(false);
        mChart.setHighlightPerDragEnabled(false);
        mChart.setHighlightPerTapEnabled(true);
        mChart.setHorizontalScrollBarEnabled(true);
        mChart.setVisibleXRangeMaximum(6);
        mChart.moveViewToX(10);
        mChart.setMaxHighlightDistance(250);

        XAxis x = mChart.getXAxis();
        x.setEnabled(true);
        x.setDrawGridLines(false);
        x.setAvoidFirstLastClipping(true);
        x.setLabelCount(6, true);
        x.setPosition(XAxis.XAxisPosition.BOTTOM);
        x.setSpaceMax(10);
        x.setDrawAxisLine(true);
        x.setAxisLineWidth(0.4f);
        x.setTextColor(Color.BLACK);
        x.setTextSize(8f);
        x.setYOffset(4f);
        x.setAxisLineColor(getResources().getColor(R.color.colorAccent));



        YAxis y = mChart.getAxisLeft();
        y.setLabelCount(3, true);
        y.setTextColor(Color.BLACK);
        y.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        y.setDrawGridLines(false);
        y.setAxisLineColor(getResources().getColor(R.color.colorAccent));
        y.setYOffset(4f);
        y.setAxisLineWidth(0.4f);

        mLabels.add("Deep");
        mLabels.add("Light");
        mLabels.add("Awake");
        mChart.getAxisRight().setEnabled(false);
        mChart.getLegend().setEnabled(false);
        mChart.animateXY(2000, 2000);
        mChart.invalidate();



        ArrayList<Entry> yVals = new ArrayList<Entry>();
        final List<SleepModel> data = new ArrayList<>();
        data.add(new SleepModel(0f, 50f, "11-1"));
        data.add(new SleepModel(1f, 2338.5f, "1-3"));
        data.add(new SleepModel(2f, -2438.1f, "3-5"));
        data.add(new SleepModel(3f, 50f, "5-6"));
        data.add(new SleepModel(4f, -2238.1f, "6-8"));
        data.add(new SleepModel(5f, 50f, "11-1"));
        data.add(new SleepModel(6f, 2338.5f, "1-3"));
        data.add(new SleepModel(7f, -2438.1f, "3-5"));
        data.add(new SleepModel(8f, 50f, "5-6"));
        data.add(new SleepModel(9f, 70f, "6-8"));

        List<Integer> colors = new ArrayList<Integer>();


        final List<String> timeList = new ArrayList<>();
        timeList.add("11-1");
        timeList.add("1-3");
        timeList.add("3-5");
        timeList.add("5-6");
        timeList.add("6-8");

        x.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                Log.d(TAG, "getFormattedValue: " + timeList.get(Math.min(Math.max((int) value, 0), timeList.size()-1)));

                return timeList.get(Math.min(Math.max((int) value, 0), timeList.size()-1));
            }
        });

        y.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                Log.d(TAG, "getFormattedValue: " + mLabels.get(Math.min(Math.max((int) value, 0), mLabels.size()-1)));

                return mLabels.get(Math.min(Math.max((int) value, 0), mLabels.size()-1));
            }
        });

        setDataForCubicLineChart(colors, yVals, data);


    }

    private void setDataForCubicLineChart(List<Integer> colors, ArrayList<Entry> yVals, List<SleepModel> data) {

        int blue = Color.parseColor("#00B0EC");
        //rgb(110, 190, 102);
        int blueLight = Color.parseColor("#7f6ecded");
        int awakeColor = Color.parseColor("#F39CDEF4");

        SleepModel d = null;
        for (int i = 0; i < data.size(); i++) {
            d = data.get(i);
            BarEntry entry = new BarEntry(d.xValue, d.yValue);
            Log.d(TAG, "xValue:" + d.xValue + "yValue:" + d.yValue);
            yVals.add(entry);
        }
        Log.d(TAG, "setDataForCubicLineChart: "+new Gson().toJson(yVals));
        LineDataSet set1;
        if (mChart.getData() != null && mChart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) mChart.getData().getDataSetByIndex(0);
            set1.setValues(yVals);
            mChart.getData().notifyDataChanged();
            mChart.notifyDataSetChanged();
            mChart.invalidate();

        } else {
            set1 = new LineDataSet(yVals, "DataSet 1");
            set1.setMode(LineDataSet.Mode.CUBIC_BEZIER);
            set1.setCubicIntensity(0.2f);
            set1.setDrawFilled(true);
            set1.setDrawCircles(false);
            set1.setLineWidth(0.5f);
            set1.setHighLightColor(mChart.getContext().getResources().getColor(R.color.colorPrimary));
            set1.setCircleRadius(0.5f);
            set1.setCircleColor(mChart.getContext().getResources().getColor(R.color.colorPrimary));
//            set1.setHighLightColor(Color.rgb(244, 117, 117));


            // specific colors

            set1.setColor(Color.BLUE);
            if (d.yValue >= 0) {
//                colors.add(blue,0);
                set1.setFillColor(getResources().getColor(R.color.colorDeep));
            } else {
//                colors.add(blueLight,1);
//                set1.setFillColor(Col);
                set1.setFillColor(getResources().getColor(R.color.colorLight));

            }

            if (d.yValue >= 0 && d.yValue <= 100) {
//                colors.add(awakeColor,2);
                set1.setFillColor(getResources().getColor(R.color.colorAwake));
            }

//            set1.setFillAlpha(50);
            set1.setDrawFilled(true);

            set1.setDrawHorizontalHighlightIndicator(true);
            LineData dataLine = new LineData(set1);
            dataLine.setValueTextSize(9f);
            dataLine.setDrawValues(false);
            CustomMarkerView mv = new CustomMarkerView(mChart.getContext(), R.layout.linechart_three_tooltip);
            mv.setChartView(mChart);
            mv.setPadding(10, 8, 10, 8);
            mChart.setMarkerView(mv);
            mChart.setData(dataLine);


        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter();
        filter.addAction(AppConst.ACTION_BROADCAST_BREATH);
        LocalBroadcastManager.getInstance(this).registerReceiver(mBreathReceiver, filter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mBreathReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initView() {
        mChart = findViewById(R.id.barchart);
        myListView = (ListView) findViewById(R.id.timeline_listView);

    }


    @Override
    protected void onResume() {
        super.onResume();


    }


    private void setTimeLine(List<TimelineRow> list) {
        // Create Timeline rows List
        if (myAdapter == null) {
            Log.d(TAG, "setTimeLine: myAdapter");
            ArrayList<TimelineRow> listTimeLine = new ArrayList<>(list);

            myAdapter = new TimelineViewAdapter(this, 0, listTimeLine,
                    true);
            myListView.setAdapter(myAdapter);
            return;
        }
        Log.d(TAG, "setTimeLine: ");
        myAdapter.clear();
        myAdapter.addAll(list);
        myAdapter.notifyDataSetChanged();
    }
//        Log.d(TAG, "setTimeLine: "+new Gson().toJson(list));


    @RequiresApi(api = Build.VERSION_CODES.M)
    private void callSOS() {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:9890410668"));
        startActivity(intent);


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


    @AfterPermissionGranted(RC_CALL)
    private void checkPermissionLocation() {
        String[] perms = {Manifest.permission.CALL_PHONE, Manifest.permission.SEND_SMS, Manifest.permission.READ_PHONE_STATE};
        if (EasyPermissions.hasPermissions(this, perms)) {

        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.rc_grant_ext_call),
                    RC_CALL, perms);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {
        if (list.contains(Manifest.permission.CALL_PHONE)) {
            isPrermission = true;
        }
        if (list.contains(Manifest.permission.READ_PHONE_STATE))
            isPrermission = true;
        if (list.contains(Manifest.permission.SEND_SMS))
            isPrermission = true;


    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> list) {
        if (list.contains(Manifest.permission.CALL_PHONE)) {
            isPrermission = false;
        }
    }


}
