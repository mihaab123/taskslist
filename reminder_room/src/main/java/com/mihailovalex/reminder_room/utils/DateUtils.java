package com.mihailovalex.reminder_room.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {
    public static String getDate(Long date){
        if (date== null){
            date = new Date().getTime();
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yy");
        return dateFormat.format(date);
    }
    public static String getTime(Long time){
        if (time== null){
            time = new Date().getTime();
        }
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        return timeFormat.format(time);
    }
    public static String getFullDate(Long date){
        if (date== null){
            date = new Date().getTime();
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yy HH:mm");
        return dateFormat.format(date);
    }
}
