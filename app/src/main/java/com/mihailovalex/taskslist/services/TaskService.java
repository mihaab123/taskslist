package com.mihailovalex.taskslist.services;


import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;


public class TaskService extends Service {
    final String LOG_TAG = "myLogs";
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
        h.post(checkTasks);
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

    }
    // проверка срабатывания уведомлений
    Runnable checkTasks = new Runnable() {
        public void run() {
            checkTasks();
            h.postDelayed(checkTasks, 60*1000);
        }
    };
}
