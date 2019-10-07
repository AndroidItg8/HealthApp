package com.itg8.healthapp;


import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.itg8.healthapp.common.Prefs;
import com.itg8.healthapp.model.BreathModel;
import com.itg8.healthapp.utils.AppConst;
import com.itg8.healthapp.utils.SharedPrefUtils;
import com.itg8.healthapp.utils.Utils;



public class BreathSchudleService extends JobIntentService {
    private static Context mContext;
    final Handler mHandler = new Handler();

    private static final String TAG = "MyJobIntentService";
    /**
     * Unique job ID for this service.
     */
    private static final int JOB_ID = 2;

    public static void enqueueWork(Context context, Intent intent) {
      mContext= context;
        Log.d(TAG, "enqueueWork: ");
        enqueueWork(context, BreathSchudleService.class, JOB_ID, intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        int mCurrentBreath= getCurrentBreath();

        if(mCurrentBreath==0){
            mCurrentBreath=getCurrentBreath();
            return;
        }

        boolean isIncreament= Prefs.getBoolean(SharedPrefUtils.IS_INCREAMENT,true);
        if(isIncreament&& mCurrentBreath<=AppConst.MAX_BREATH_VAKLUE){
            mCurrentBreath+=1;
            if(mCurrentBreath==AppConst.MAX_BREATH_VAKLUE){
                Prefs.putBoolean(SharedPrefUtils.IS_INCREAMENT,false);


            }
        }else if(mCurrentBreath>=AppConst.MIN_BREATH_VAKLUE && !isIncreament){
            mCurrentBreath-=1;
            if(mCurrentBreath==AppConst.MIN_BREATH_VAKLUE){
                Prefs.putBoolean(SharedPrefUtils.IS_INCREAMENT,true);
            }
        }
        setCurrentBreathDetail(mCurrentBreath);

//        if(mCurrentBreath<AppConst.MAX_BREATH_VAKLUE && mCurrentBreath>AppConst.MIN_BREATH_VAKLUE){
//            mCurrentBreath+=1;
//
//            return;
//        }
//
//        if(mCurrentBreath>=AppConst.MAX_BREATH_VAKLUE){
//            mCurrentBreath-=1;
//            setCurrentBreathDetail(mCurrentBreath);
//
//        }else if(mCurrentBreath<AppConst.MAX_BREATH_VAKLUE && mCurrentBreath>AppConst.MIN_BREATH_VAKLUE){
//            mCurrentBreath-=1;
//            setCurrentBreathDetail(mCurrentBreath);
//        }
//        else if(mCurrentBreath<=AppConst.MIN_BREATH_VAKLUE){
//            mCurrentBreath+=1;
//            setCurrentBreathDetail(mCurrentBreath);
//
//        }


    }

    private void setCurrentBreathDetail(int mCurrentBreath) {
        setCurrentBreath(mCurrentBreath);
        notifyAllBroadcast();

    }

    private void notifyAllBroadcast() {
        Intent localIntent = new Intent(AppConst.ACTION_BROADCAST_BREATH);
        localIntent.putExtra(AppConst.EXTRA_BREATH_CHANGE, true);
        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
    }

    private void setCurrentBreath(int mBreathValue) {
        BreathModel model=new BreathModel();
        model.setValue(mBreathValue);
        model.setStatus(Utils.getStateByCount(mBreathValue).name());
        model.setTimestamp(System.currentTimeMillis());
        SharedPrefUtils.saveBreath(model);
        if(mBreathValue>=AppConst.MAX_BREATH_VAKLUE)
        SharedPrefUtils.setCurrentBreathHighCount(mContext);
    }

    private int getCurrentBreath() {
        BreathModel  breathModel= SharedPrefUtils.getLastBreathModel();
        return breathModel!=null?breathModel.getValue():AppConst.DEFAULT_BREATH_VALUE;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Utils.scheduleTimer(this,AppConst.TIMER_DELAY);
    }



}
