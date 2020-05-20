package com.mihailovalex.reminder_room.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtils {
    public static String getDate(Long date){
        if (date== null){
            date = new Date().getTime();
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yy");
        return dateFormat.format(date);
    }
    public static String getTime(Long time, boolean time24Format){
        if (time== null){
            time = new Date().getTime();
        }
        if (time24Format){
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
            return timeFormat.format(time);
        } else {
            SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a",Locale.US);
            return timeFormat.format(time);
        }
    }
    public static String getFullDate(Long date,boolean time24Format){
        if (date== null){
            date = new Date().getTime();
        }
        if (time24Format){
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yy HH:mm");
            return dateFormat.format(date);
        } else {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yy hh:mm a",Locale.US);
            return dateFormat.format(date);
        }

    }
    public static long repeatTask(long date, String repeat){
        Calendar dateAndTime = Calendar.getInstance();
        dateAndTime.setTimeInMillis(date);
        if (!repeat.isEmpty()){
           int count = Integer.parseInt(repeat.substring(0,repeat.length()-1));
           String type = repeat.substring(repeat.length()-1,repeat.length());
           switch (type){
               case "h":
                   dateAndTime.add(Calendar.HOUR,count);
                   break;
               case "d":
                   dateAndTime.add(Calendar.DAY_OF_YEAR,count);
                   break;
               case "w":
                   dateAndTime.add(Calendar.DAY_OF_YEAR,count*7);
                   break;
               case "m":
                   dateAndTime.add(Calendar.MONTH,count);
                   break;
               case "y":
                   dateAndTime.add(Calendar.YEAR,count);
                   break;
           }
        }
        return dateAndTime.getTimeInMillis();
    }
    public static long backTask(long date, String repeat){
        Calendar dateAndTime = Calendar.getInstance();
        dateAndTime.setTimeInMillis(date);
        if (!repeat.isEmpty()){
            int count = Integer.parseInt(repeat.substring(0,repeat.length()-1));
            String type = repeat.substring(repeat.length()-1,repeat.length());
            switch (type){
                case "h":
                    dateAndTime.add(Calendar.HOUR,-count);
                    break;
                case "d":
                    dateAndTime.add(Calendar.DAY_OF_YEAR,-count);
                    break;
                case "w":
                    dateAndTime.add(Calendar.DAY_OF_YEAR,-count*7);
                    break;
                case "m":
                    dateAndTime.add(Calendar.MONTH,-count);
                    break;
                case "y":
                    dateAndTime.add(Calendar.YEAR,-count);
                    break;
            }
        }
        return dateAndTime.getTimeInMillis();
    }
}
