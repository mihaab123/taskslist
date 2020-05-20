package com.mihailovalex.reminder_room;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.res.Configuration;

import androidx.preference.PreferenceManager;

import com.mihailovalex.reminder_room.data.source.TasksRepository;
import com.mihailovalex.reminder_room.data.source.local.TasksDatabase;
import com.mihailovalex.reminder_room.data.source.local.TasksLocalDataSource;
import com.mihailovalex.reminder_room.utils.AppExecutors;
import com.mihailovalex.reminder_room.utils.DateUtils;

import java.util.Calendar;
import java.util.Locale;

public class MyApplication extends Application {
    public static boolean activityVisible;
    private SharedPreferences preferences;
    private Locale locale;
    private String lang;

    public static boolean isActivityVisible() {
        return activityVisible;
    }
    public static void activityResume(){
        activityVisible = true;
    }
    public static void activityPause(){
        activityVisible = false;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        lang = preferences.getString("language", "default");
        if (lang.equals("default")) {
            lang = getResources().getConfiguration().locale.getCountry();
        }
        locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getResources().getDisplayMetrics());
        deleteDoneTasks();

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getResources().getDisplayMetrics());
    }
    private void deleteDoneTasks() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String countDays = preferences.getString("notify_time_delete", "0");
        if (countDays!= "0") {
            Calendar calendar = Calendar.getInstance();
            TasksDatabase database = TasksDatabase.getInstance(getApplicationContext());
            TasksRepository tasksRepository = TasksRepository.getInstance(TasksLocalDataSource.getInstance(new AppExecutors(),
                    database.taskDao()));
            tasksRepository.clearCompletedTasks(DateUtils.backTask(calendar.getTimeInMillis(),countDays+"d"));
        }
    }
}
