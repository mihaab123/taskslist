package com.mihailovalex.reminder;

import java.text.SimpleDateFormat;

public class Utils {
    public static String getDate(Long date){
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yy");
        return dateFormat.format(date);
    }
    public static String getTime(Long time){
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        return timeFormat.format(time);
    }
    public static String getFullDate(Long date){
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yy HH:mm");
        return dateFormat.format(date);
    }
}
