package com.itg8.healthapp.background;

import android.util.Log;

import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.google.gson.Gson;
import com.itg8.healthapp.model.SleepModel;

import java.util.ArrayList;
import java.util.List;


public abstract class SleepModelThread implements Runnable {

    private  List<SleepModel> data ;
    private  static final List<Entry> yVals= new ArrayList<>();
    private static final String TAG = "SleepModelThread";

    public SleepModelThread(List<SleepModel> data) {
        this.data = data;
    }




    @Override
    public void run() {
        Log.d(TAG, "run: ");


        SleepModel d = null;
        if(data.size()>0) {
            for (int i = 0; i < data.size(); i++) {
                d = data.get(i);

                BarEntry entry = new BarEntry(d.xValue, d.yValue);
                Log.d(TAG, "xValue:" + d.xValue + "yValue:" + d.yValue);
                yVals.add(entry);
            }
        }
        getSleepEntryModel(yVals,d );

    }

     public abstract void  getSleepEntryModel(List<Entry> yVals, SleepModel sleepModel);





}
