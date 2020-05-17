package com.mihailovalex.reminder_room.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


import androidx.legacy.content.WakefulBroadcastReceiver;

import com.mihailovalex.reminder_room.data.Task;
import com.mihailovalex.reminder_room.data.source.local.TasksDao;
import com.mihailovalex.reminder_room.data.source.local.TasksDatabase;

import java.util.ArrayList;
import java.util.List;

public class AlarmSetter extends WakefulBroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        TasksDatabase database = TasksDatabase.getInstance(context.getApplicationContext());
        TasksDao tasksDao = database.taskDao();
        AlarmHelper.getInstance().init(context);
        AlarmHelper alarmHelper = AlarmHelper.getInstance();

        List<Task> tasks = new ArrayList<>();
        tasks.addAll(tasksDao.getActiveTasks());
        for(Task task : tasks){
            alarmHelper.setAlarm(task);
        }
    }
}
