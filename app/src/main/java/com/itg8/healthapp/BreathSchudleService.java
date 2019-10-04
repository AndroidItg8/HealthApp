package com.itg8.healthapp;


import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Parcelable;
import android.support.v4.os.ResultReceiver;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.gson.Gson;
import com.itg8.healthapp.model.BreathModel;


public class BreathSchudleService extends JobIntentService {
    final Handler mHandler = new Handler();

    private static final String TAG = "MyJobIntentService";
    /**
     * Unique job ID for this service.
     */
    private static final int JOB_ID = 2;

    public static void enqueueWork(Context context, Intent intent) {
        enqueueWork(context, BreathSchudleService.class, JOB_ID, intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        showToast("Job Execution Started");
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {

//        ResultReceiver resultReceiver = intent.getParcelableExtra(PARAM.RESULT_RECEIVER.name());
        BreathModel  model = intent.getParcelableExtra("maxCountValue");
        /**
         * Suppose we want to print 1 to 1000 number with one-second interval, Each task will take time 1 sec, So here now sleeping thread for one second.
         */
        for (int i = 0; i < 10; i++) {
            Log.d(TAG, "onHandleWork: The number is: " +new Gson().toJson( model));
            sendDataToActivity(model);
            try {
                Thread.sleep(1000*60);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    private void sendDataToActivity(BreathModel model) {
        Intent localIntent = new Intent("BROADCAST_ACTION").putExtra("EXTENDED_DATA_STATUS", model);
        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        showToast("Job Execution Finished");
    }


    // Helper for showing tests
    void showToast(final CharSequence text) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(BreathSchudleService.this, text, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
