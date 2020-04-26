package com.mihailovalex.taskslist.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MyBroadReceiv extends BroadcastReceiver {

    final String LOG_TAG = "MyLogs";

    public void onReceive(Context context, Intent intent) {
        Log.d(LOG_TAG, "onReceive " + intent.getAction());
        context.startService(new Intent(context, TaskService.class));
    }
}
