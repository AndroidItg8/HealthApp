package com.itg8.healthapp.utils;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.telecom.PhoneAccountHandle;
import android.telecom.TelecomManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.itg8.healthapp.common.Prefs;
import com.itg8.healthapp.model.BreathModel;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static androidx.core.content.ContextCompat.checkSelfPermission;

public class SharedPrefUtils {

    public static final String CURRENT_BREATH_RATE = "current_breath_rate";
    private static final String BREATH_LIST = "breathing_model_list";
    public static final String IS_INCREAMENT = "IS_INCREAMENT";
    public static final int DEFULT_BREATH_COUNT = 3;
    public static int countBreath = 0;
    private static final String TAG = "SharedPrefUtils";

    public static void saveBreath(BreathModel breathModel) {
        List<BreathModel> listBreath = getAllBreathModel();
        listBreath.add(breathModel);
        Prefs.putString(BREATH_LIST, new Gson().toJson(listBreath));
    }

    public static List<BreathModel> getAllBreathModel() {
        String breaths = Prefs.getString(BREATH_LIST);
        if (breaths != null) {
            List<BreathModel> models = new Gson().fromJson(breaths, new TypeToken<List<BreathModel>>() {
            }.getType());
            if (models != null)
                return models;
        }
        return new ArrayList<>();
    }

    public static BreathModel getLastBreathModel() {
        List<BreathModel> breathModels = getAllBreathModel();
        if (breathModels.size() > 0)
            return breathModels.get(breathModels.size() - 1);

        return null;
    }


    public static void setCurrentBreathHighCount( Context mContext) {
        countBreath++;
        if (countBreath >= DEFULT_BREATH_COUNT) {
            callEmergencyContact(mContext);
        }
    }

    public static void callEmergencyContact( Context mContext) {
        String output = getOutput(mContext, "getCarrierName", 0);
        readPhoneState(mContext);
        Log.d(TAG, "callEmergencyContact: " + output);
//        if (!TextUtils.isEmpty(output)) {
//            Intent intent = new Intent(Intent.ACTION_DIAL);
//
//            intent.setData(Uri.parse("tel:9890410668"));
//            mContext.startActivity(intent);
//        }

    }

    private static void readPhoneState(Context mContext) {
        String primarySimId = null, secondarySimId = null;

        //To find SIM ID
        SubscriptionManager subscriptionManager = null;
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            subscriptionManager = (SubscriptionManager) mContext.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
            if (checkSelfPermission(mContext,Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    Activity#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.
                return;
            }
            List<SubscriptionInfo> subList = subscriptionManager.getActiveSubscriptionInfoList();
            int index=-1;
            for (SubscriptionInfo subscriptionInfo : subList) {
                index++;
                if(index == 0){
                    primarySimId=subscriptionInfo.getIccId();
                }else {
                    secondarySimId=subscriptionInfo.getIccId();
                }
            }

            // TO CREATE PhoneAccountHandle FROM SIM ID
            TelecomManager telecomManager =(TelecomManager) mContext.getSystemService(Context.TELECOM_SERVICE);
            List<PhoneAccountHandle> list = null;
            list = telecomManager.getCallCapablePhoneAccounts();

            PhoneAccountHandle primaryPhoneAccountHandle,secondaryPhoneAccountHandle = null;
            for(PhoneAccountHandle phoneAccountHandle:list){
                if(phoneAccountHandle.getId().contains(primarySimId)){
                    primaryPhoneAccountHandle=phoneAccountHandle;
                }
                if(phoneAccountHandle.getId().contains(secondarySimId)){
                    secondaryPhoneAccountHandle=phoneAccountHandle;
                }
            }

//            //To call from SIM 1
//            Uri uri = Uri.fromParts("tel",number, "");
//            Bundle extras = new Bundle();  extras.putParcelable(TelecomManager.EXTRA_PHONE_ACCOUNT_HANDLE,primaryPhoneAccountHandle);
//            telecomManager.placeCall(uri, extras);

            //To call from SIM 2
            Uri uri = Uri.fromParts("tel","9890756622", "");
            Bundle extras = new Bundle();  extras.putParcelable(TelecomManager.EXTRA_PHONE_ACCOUNT_HANDLE,secondaryPhoneAccountHandle);
            telecomManager.placeCall(uri, extras);
        }

    }


    private static String getOutput(Context context, String methodName, int slotId) {
        TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        Class<?> telephonyClass;
        String reflectionMethod = null;
        String output = null;
        try {
            telephonyClass = Class.forName(telephony.getClass().getName());
            for (Method method : telephonyClass.getMethods()) {
                String name = method.getName();
                if (name.contains(methodName)) {
                    Class<?>[] params = method.getParameterTypes();
                    if (params.length == 1 && params[0].getName().equals("int")) {
                        reflectionMethod = name;

                    }
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        if (reflectionMethod != null) {
            try {
                output = getOpByReflection(telephony, reflectionMethod, slotId, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return output;
    }

    private static String getOpByReflection(TelephonyManager telephony, String predictedMethodName, int slotID, boolean isPrivate) {

        //Log.i("Reflection", "Method: " + predictedMethodName+" "+slotID);
        String result = null;

        try {

            Class<?> telephonyClass = Class.forName(telephony.getClass().getName());

            Class<?>[] parameter = new Class[1];
            parameter[0] = int.class;
            Method getSimID;
            if (slotID != -1) {
                if (isPrivate) {
                    getSimID = telephonyClass.getDeclaredMethod(predictedMethodName, parameter);
                } else {
                    getSimID = telephonyClass.getMethod(predictedMethodName, parameter);
                }
            } else {
                if (isPrivate) {
                    getSimID = telephonyClass.getDeclaredMethod(predictedMethodName);
                } else {
                    getSimID = telephonyClass.getMethod(predictedMethodName);
                }
            }

            Object ob_phone;
            Object[] obParameter = new Object[1];
            obParameter[0] = slotID;
            if (getSimID != null) {
                if (slotID != -1) {
                    ob_phone = getSimID.invoke(telephony, obParameter);
                } else {
                    ob_phone = getSimID.invoke(telephony);
                }

                if (ob_phone != null) {
                    result = ob_phone.toString();

                }
            }
        } catch (Exception e) {
            //e.printStackTrace();
            return null;
        }
        //Log.i("Reflection", "Result: " + result);
        return result;
    }

}
