package com.itg8.healthapp.common;

import android.app.Application;

import androidx.appcompat.app.AppCompatDelegate;



public class MyApplication  extends Application {


    private static final String PREF_NAME = "Health_APP";

    private static MyApplication mInstance;
    public boolean isLoggingNeeded;
    //TODO version: change when new api changes arrives


    public static MyApplication getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        isLoggingNeeded = true;
        mInstance = this;
        mInstance.initPref();

    }









    private void initPref() {
        new Prefs.Builder()
                .setContext(this)
                .setMode(MODE_PRIVATE)
                .setPrefsName(PREF_NAME)
                .setUseDefaultSharedPreference(false)
                .build();
    }
}
