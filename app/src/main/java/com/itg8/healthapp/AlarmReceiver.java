package com.itg8.healthapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.widget.Toast;



public class AlarmReceiver  extends BroadcastReceiver {

    int count =0;
    @Override
    public void onReceive(Context arg0, Intent arg1) {
        Toast.makeText(arg0, "Alarm received!", Toast.LENGTH_LONG).show();
        sendWhatsUp(arg0);

        count++;
         if(count>3){

         }



    }

    private void sendWhatsUp(Context context) {
        Toast.makeText(context, "sendWhatsUp", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent("android.intent.action.SmsReceiver");
        intent.putExtra("number", " 9890410668");
        context.sendBroadcast(intent);
//        sendMessageToWhatsAppContact(context,"9890410668");
    }

}
