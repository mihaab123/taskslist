package com.mihailovalex.reminder;

import android.app.Application;

public class MyApplication extends Application {
    public static boolean activityVisible;

    public static boolean isActivityVisible() {
        return activityVisible;
    }
    public static void activityResume(){
        activityVisible = true;
    }
    public static void activityPause(){
        activityVisible = false;
    }
}
