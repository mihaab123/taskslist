package com.mihailovalex.reminder.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.mihailovalex.reminder.model.ModelTask;

public class AlarmHelper {
    private static AlarmHelper instance;
    private Context context;
    private AlarmManager manager;

    public static AlarmHelper getInstance() {
        if(instance==null){
            instance = new AlarmHelper();
        }
        return instance;
    }
    public void init(Context context){
        this.context = context;
        this.manager = (AlarmManager) context.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
    }
    public void setAlarm(ModelTask task){
        Intent intent = new Intent(context,AlarmReceiver.class);
        intent.putExtra("title",task.getTitle());
        intent.putExtra("timestamp",task.getTimeStamp());
        intent.putExtra("color",task.getPriorityColor());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), (int) task.getTimeStamp(),intent,PendingIntent.FLAG_UPDATE_CURRENT);
        manager.set(AlarmManager.RTC_WAKEUP,task.getDate(),pendingIntent);
    }
    public void removeAlarm(long timestamp){
        Intent intent = new Intent(context,AlarmReceiver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, (int) timestamp,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        manager.cancel(pendingIntent);
    }
}
