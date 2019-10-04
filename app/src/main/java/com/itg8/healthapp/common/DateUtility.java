package com.itg8.healthapp.common;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtility {
    private static SimpleDateFormat formatServerSend = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
    private static SimpleDateFormat format = new SimpleDateFormat("MMM-dd-yyyy", Locale.getDefault());
    private static SimpleDateFormat formateTime = new SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault());
    //    private static SimpleDateFormat formateTimeShow = new SimpleDateFormat("hh:mm:ss a", Locale.getDefault());
    private static SimpleDateFormat formateTimeShow = new SimpleDateFormat("hh:mm a", Locale.getDefault());
    private static SimpleDateFormat formatServer = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
    private static SimpleDateFormat formatLocal = new SimpleDateFormat("hh:mm aa", Locale.getDefault());
    private static SimpleDateFormat formatTime = new SimpleDateFormat("hh:mm", Locale.getDefault());

    public static String getDateFromDateTime(Date time) {
        try {
            return formatTime.format(time);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

}
