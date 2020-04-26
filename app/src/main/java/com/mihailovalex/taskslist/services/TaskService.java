package com.mihailovalex.taskslist.services;


import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.mihailovalex.taskslist.NotificationUtils;
import com.mihailovalex.taskslist.R;
import com.mihailovalex.taskslist.data.TaskSchedulerClass;


public class TaskService extends Service {
    final String LOG_TAG = "MyLogs";
    private Context context;
    private Handler h;

    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        h = new Handler();
        Log.d(LOG_TAG, "onCreate");
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(LOG_TAG, "onStartCommand");
        h.post(checkTasksRun);
        return super.onStartCommand(intent, flags, startId);
    }

    public void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG, "onDestroy");
    }

    public IBinder onBind(Intent intent) {
        Log.d(LOG_TAG, "onBind");
        return null;
    }

    void checkTasks() {
        Cursor c = null;
        Log.d(LOG_TAG, "checkTasks");
        c =  context.getContentResolver().query(
                TaskSchedulerClass.Tasks.CONTENT_URI, // URI
                TaskSchedulerClass.Tasks.DEFAULT_PROJECTION, // Столбцы
                null, // Параметры выборки
                null, // Аргументы выборки
                null); // Сортировка по умолчанию
        Log.d(LOG_TAG, "get cursor");
        if(c != null) {
            if(c.moveToFirst()) {

                int i=1;
                do {
                    String Name = c.getString(c.getColumnIndex(TaskSchedulerClass.Tasks.COLUMN_NAME_TITLE));
                    Log.d(LOG_TAG, "get name");
                   // groups[i] = groupName;
                    int pbId = NotificationUtils.getInstance(this).createInfoNotification(Name);
                    Log.d(LOG_TAG, "notification");
                    i++;
                } while (c.moveToNext());
            }
            c.close();
        }
    }
    // проверка срабатывания уведомлений
    Runnable checkTasksRun = new Runnable() {
        public void run() {
            checkTasks();
            h.postDelayed(checkTasksRun, 60*1000);
        }
    };
}
