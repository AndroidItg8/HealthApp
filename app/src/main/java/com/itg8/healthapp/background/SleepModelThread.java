package com.itg8.healthapp.background;

import android.graphics.Color;
import android.util.Log;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.itg8.healthapp.CustomMarkerView;
import com.itg8.healthapp.R;
import com.itg8.healthapp.model.SleepModel;

import java.util.ArrayList;
import java.util.List;

public  class SleepModelThread implements Runnable {
    private final LineChart mChart;

    private static final String TAG = "SleepModelThread";

    public SleepModelThread(LineChart chart) {
        this.mChart= chart;

    }

    @Override
    public void run() {
        Log.d(TAG, "run: ");

    }


}
