package com.itg8.healthapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

import static androidx.core.provider.FontsContractCompat.Columns.RESULT_CODE_OK;

public class BreathBroadcastReciever<T> extends ResultReceiver {


    /**
     * Create a new ResultReceive to receive results.  Your
     * {@link #onReceiveResult} method will be called from the thread running
     * <var>handler</var> if given, or from an arbitrary thread if null.
     *
     * @param handler
     */
    public BreathBroadcastReciever(Handler handler) {
        super(handler);
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {

        if (mReceiver != null) {
            if(resultCode == RESULT_CODE_OK){
                mReceiver.onSuccess(resultData.getSerializable(PARAM_RESULT));
            } else {
                mReceiver.onError((Exception) resultData.getSerializable(PARAM_EXCEPTION));
            }
        }
    }
}
