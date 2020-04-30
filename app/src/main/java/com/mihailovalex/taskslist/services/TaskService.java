package com.mihailovalex.taskslist.services;


import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.mihailovalex.taskslist.NotificationUtils;
import com.mihailovalex.taskslist.R;
import com.mihailovalex.taskslist.data.DBHelper;
import com.mihailovalex.taskslist.data.MyContentProvider;
import com.mihailovalex.taskslist.data.TaskSchedulerClass;

import java.util.ArrayList;


public class TaskService extends Service {
    final String LOG_TAG = "MyLogs";
    private Context context;
    private Handler h;
    DBHelper db;
    Cursor cursor;

    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        h = new Handler();
        //Log.d(LOG_TAG, "onCreate");
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        //Log.d(LOG_TAG, "onStartCommand");
        h.post(checkTasksRun);
        //checkTasks();
        return super.onStartCommand(intent, flags, startId);
    }

    public void onDestroy() {
        super.onDestroy();
        //Log.d(LOG_TAG, "onDestroy");
    }

    public IBinder onBind(Intent intent) {
       // Log.d(LOG_TAG, "onBind");
        return null;
    }

    void checkTasks() {
       // Log.d(LOG_TAG, "checkTasks");
        DBHelper myDBHelper = new DBHelper(context);
        Cursor c = myDBHelper.getAllTasks();
        ArrayList<ContentValues> listadd = new ArrayList<>();
        int index =0;
        if (c != null) {
            if (c.moveToFirst()) {

                do {
                    ContentValues cv = new ContentValues();
                    for (String cn : c.getColumnNames()) {
                        index = c.getColumnIndex(cn);
                        if (cn == TaskSchedulerClass.Tasks._ID ) {

                        } else if (cn == TaskSchedulerClass.Tasks.COLUMN_NAME_TIME||cn == TaskSchedulerClass.Tasks.COLUMN_NAME_TIME_BEFORE||cn == TaskSchedulerClass.Tasks.COLUMN_NAME_GROUPID){
                            cv.put(cn,c.getLong(index));
                        }else cv.put(cn,c.getString(index));
                    }
                    listadd.add(cv);
                } while (c.moveToNext());
            }
            c.close();
        }
       // ArrayList<ContentValues> listadd = MyContentProvider.getTasksFromDatabase(this);
        for(ContentValues cv : listadd){
            //Log.d(LOG_TAG, "for");
            String Name = cv.getAsString(TaskSchedulerClass.Tasks.COLUMN_NAME_TITLE);
            //int pbId = NotificationUtils.getInstance(this).createInfoNotification(Name);
            }
       // Log.d(LOG_TAG, "createInfoNotification");
    }
    // проверка срабатывания уведомлений
    Runnable checkTasksRun = new Runnable() {
        public void run() {
            checkTasks();
            h.postDelayed(checkTasksRun, 60*1000);
        }
    };
}
