package com.itg8.healthapp;

import android.Manifest;

import android.content.Intent;

import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
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
import com.itg8.healthapp.common.Prefs;
import com.itg8.healthapp.model.BreathModel;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.qap.ctimelineview.TimelineRow;
import org.qap.ctimelineview.TimelineViewAdapter;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    private static final int RC_CALL = 2345;
    ArrayList<TimelineRow> timelineRowsList = new ArrayList<>();
    private static final String TAG = "MainActivity";
    BreathModel model = new BreathModel();
    List<BreathModel> list = new ArrayList<>();
    int breathValue = 12;
    StringBuilder sb = new StringBuilder();
    private static int countStress = 0;
    private boolean isPrermission;
    ArrayList<String> mLabels = new ArrayList<>();
    private LineChart mChart;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        mChart = findViewById(R.id.barchart);
        setSupportActionBar(toolbar);
        sb.append("12").append(",");
        Prefs.putString("value", sb.toString());
//        setTimeLine();
        setObservable();

        //  checkPermissionLocation();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                startActivity(new Intent(MainActivity.this, SleepActivity.class));

            }
        });



    }

    private void setObservable() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                cubicLineChart();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mChart.invalidate();
//                        mChart.getData().notifyDataChanged();
                        mChart.notifyDataSetChanged();
                    }
                });
                mChart.animateX(1000);
            }
        }, 100);
    }

    private void getServiceData() {
        try {
            Thread.sleep(1000);
            breathValue = breathValue + 5;
            callJobServiceToDownload();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }


    private void cubicLineChart() {
        mChart.setViewPortOffsets(70, 10, 70, 10);
        mChart.setTouchEnabled(true);
        mChart.getDescription().setEnabled(false);
        mChart.setTouchEnabled(true);
        mChart.setDragEnabled(false);
        mChart.setScaleEnabled(false);
        mChart.setDrawGridBackground(false);
        mChart.setHighlightPerDragEnabled(false);
        mChart.setHighlightPerTapEnabled(true);
        mChart.setHorizontalScrollBarEnabled(true);
        mChart.setVisibleXRangeMaximum(10);
        mChart.moveViewToX(10);
        mChart.setMaxHighlightDistance(300);


        XAxis x = mChart.getXAxis();
        x.setEnabled(true);
        x.setDrawGridLines(false);
        x.setAvoidFirstLastClipping(true);
        x.setLabelCount(6, true);
        x.setAxisLineColor(getResources().getColor(R.color.colorPrimary));
        x.setPosition(XAxis.XAxisPosition.BOTTOM);
        x.setDrawAxisLine(true);
        x.setAxisLineWidth(1f);


        YAxis y = mChart.getAxisLeft();
        y.setLabelCount(3, true);
        y.setTextColor(Color.BLACK);
        y.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        y.setDrawGridLines(false);
        x.setAxisLineWidth(1f);
        y.setAxisLineColor(getResources().getColor(R.color.colorPrimary));
        y.setYOffset(80f);


        mLabels.add("Deep");
        mLabels.add("Awake");
        mLabels.add("Light");
        mChart.getAxisRight().setEnabled(false);
        mChart.getLegend().setEnabled(false);



        ArrayList<Entry> yVals = new ArrayList<Entry>();
        final List<Data> data = new ArrayList<>();
        data.add(new Data(0f, 50f, "11-1"));
        data.add(new Data(1f, 2338.5f, "1-3"));
        data.add(new Data(2f, -2438.1f, "3-5"));
        data.add(new Data(3f, 50f, "5-6"));
        data.add(new Data(4f, -2238.1f, "6-8"));
        data.add(new Data(5f, 50f, "11-1"));
        data.add(new Data(6f, 2338.5f, "1-3"));
        data.add(new Data(7f, -2438.1f, "3-5"));
        data.add(new Data(8f, -1538.1f, "5-6"));
        data.add(new Data(9f, 50f, "6-8"));
        List<Integer> colors = new ArrayList<Integer>();


       final  List<String> timeList = new ArrayList<>();
        timeList.add("11-1");
        timeList.add("1-3");
        timeList.add("3-5");
        timeList.add("5-6");
        timeList.add("6-8");

        x.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return timeList.get(Math.min(Math.max((int) value, 0), timeList.size() - 1));
            }
        });

        y.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return mLabels.get(Math.min(Math.max((int) value, 0), mLabels.size() - 1));
            }
        });
        setDataForCubicLineChart(colors,yVals, data);


    }

    private void setDataForCubicLineChart(List<Integer> colors, ArrayList<Entry> yVals, List<Data> data) {

        int blue = Color.parseColor("#00B0EC");
        //rgb(110, 190, 102);
        int blueLight = Color.parseColor("#7f6ecded");
        int awakeColor = Color.parseColor("#F39CDEF4");

        Data d = null;
        for (int i = 0; i < data.size(); i++) {
            d = data.get(i);
            BarEntry entry = new BarEntry(d.xValue, d.yValue);
            Log.d(TAG, "xValue:" + d.xValue + "yValue:" + d.yValue);
            yVals.add(entry);
        }
        LineDataSet set1;
        if (mChart.getData() != null && mChart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) mChart.getData().getDataSetByIndex(0);
            set1.setValues(yVals);
            mChart.getData().notifyDataChanged();
            mChart.notifyDataSetChanged();

        } else {
            set1 = new LineDataSet(yVals, "DataSet 1");
            set1.setMode(LineDataSet.Mode.CUBIC_BEZIER);
            set1.setCubicIntensity(0.2f);
            set1.setDrawFilled(true);
            set1.setDrawCircles(false);
            set1.setLineWidth(0.5f);
            set1.setHighLightColor(getResources().getColor(R.color.colorPrimary));
            set1.setCircleRadius(0.5f);
            set1.setCircleColor(getResources().getColor(R.color.colorPrimary));
//                set1.setHighLightColor(Color.rgb(244, 117, 117));


            // specific colors

            set1.setColor(Color.BLUE);
            if (d.yValue >= 0) {
                colors.add(blue);
                set1.setFillColor(colors.get(0));
            } else {
                colors.add(blueLight);
                set1.setFillColor(colors.get(1));
            }

            if (d.yValue >= 0 && d.yValue <= 100) {
                colors.add(awakeColor);
                set1.setFillColor(Color.BLUE);
            }

                set1.setFillAlpha(50);
            set1.setDrawFilled(true);

            set1.setDrawHorizontalHighlightIndicator(true);
            LineData dataLine = new LineData(set1);
            dataLine.setValueTextSize(9f);
            dataLine.setDrawValues(false);
            CustomMarkerView mv = new CustomMarkerView(getApplicationContext(), R.layout.linechart_three_tooltip);
            mv.setChartView(mChart);
            mv.setPadding(10, 8, 10, 8);
            mChart.setMarkerView(mv);
            mChart.setData(dataLine);


        }
    }


    @Override
    protected void onResume() {
        super.onResume();

//        if (breathValue < 40)
//            getServiceData();
//        else
//            breathValue = 12;





    }


    private void callJobServiceToDownload() {
        model = new BreathModel();
        model.setValue(String.valueOf(breathValue));
        sb.append(breathValue).append(",");
        Prefs.putString("value", sb.toString());
        list.add(model);
        setTimeLine();


//            if (breathValue >= 12 && breathValue < 20)
//
//            else if (breathValue > 20 && breathValue < 30) {
//                model.setStatus("Focus");
//            } else if (breathValue > 30 && breathValue < 50) {
//                model.setStatus("Stress");
//            }

        //  setJoSchedule(model);


        Log.d(TAG, "callJobServiceToDownload: " + new Gson().toJson(model));
        Log.d(TAG, "callJobServiceToDownload: " + new Gson().toJson(list));
        Log.d(TAG, "callJobServiceToDownload: " + Prefs.getString("value"));


    }


    private void setTimeLine() {
        // Create Timeline rows List
        ListView myListView = (ListView) findViewById(R.id.timeline_listView);
        ArrayAdapter<TimelineRow> myAdapter = new TimelineViewAdapter(this, 0, timelineRowsList,
                false);

        String sb = Prefs.getString("value");
        String[] values = sb.split(",");
        Log.d(TAG, "setTimeLine: " + new Gson().toJson(values));
        for (int i = 0; i < values.length; i++)
            timelineRowsList.add(setData(values[i]));


        myListView.setAdapter(myAdapter);
        myAdapter.notifyDataSetChanged();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void callSOS() {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:9890410668"));
        startActivity(intent);

//        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CALL_PHONE,}, RC_CALL);
//        } else {
//            startActivity(intent);
//        }
    }

    private TimelineRow setData(String model) {

        TimelineRow myRow = new TimelineRow(0);

        String title = null;
        int colorTitle = 0;
        int colorBackground = 0;
// To set the row Date (optional)
        myRow.setDate(new Date());
// To set the row Title (optional)
        if (Integer.parseInt(model) > 11 && Integer.parseInt(model) < 19) {
            title = "Normal";
            colorTitle = Color.parseColor("#00BCD4");
            colorBackground = Color.parseColor("#00BCD4");
        } else if (Integer.parseInt(model) > 20 && Integer.parseInt(model) < 30) {
            title = "Focus";
            colorTitle = Color.parseColor("#FF5722");
            colorBackground = Color.parseColor("#FF5722");

        } else if (Integer.parseInt(model) > 30) {
            title = "Stress";
            countStress++;
            colorTitle = Color.parseColor("#FF540E01");
            colorBackground = Color.parseColor("#FF540E01");
            Log.d(TAG, "setData: " + countStress);
        }


        myRow.setTitle(title);
// To set the row Description (optional)
        myRow.setDescription(model + " Breath Value");
// To set the row bitmap image (optional)
        myRow.setImage(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
// To set row Below Line Color (optional)
// To set row Below Line Size in dp (optional)
        myRow.setBellowLineSize(4);
// To set row Image Size in dp (optional)
        myRow.setImageSize(40);
        myRow.setBellowLineColor(colorBackground);
// To set background color of the row image (optional)
        myRow.setBackgroundColor(colorBackground);
// To set the Background Size of the row image in dp (optional)
        myRow.setBackgroundSize(20);
// To set row Date text color (optional)
        myRow.setDateColor(getResources().getColor(R.color.colorGray));
// To set row Title text color (optional)
        myRow.setTitleColor(colorTitle);
// To set row Description text color (optional)
        myRow.setDescriptionColor(colorTitle);
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



    @AfterPermissionGranted(RC_CALL)
    private void checkPermissionLocation() {
        String[] perms = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
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


    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> list) {
        if (list.contains(Manifest.permission.CALL_PHONE)) {
            isPrermission = false;
        }
    }

    private class Data {

        public String xAxisValue;
        public float yValue;
        public float xValue;

        public Data(float xValue, float yValue, String xAxisValue) {
            this.xAxisValue = xAxisValue;
            this.yValue = yValue;
            this.xValue = xValue;
        }
    }

}
