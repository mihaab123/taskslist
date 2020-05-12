package com.mihailovalex.reminder_room.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.mihailovalex.reminder_room.data.Birthday;
import com.mihailovalex.reminder_room.data.Task;

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
    public void setAlarm(Task task){
        Intent intent = new Intent(context,AlarmReceiver.class);
        intent.putExtra("title",task.getTitle());
        intent.putExtra("taskId",task.getId());
        intent.putExtra("color",task.getPriorityColor());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), (int) task.getId(),intent,PendingIntent.FLAG_UPDATE_CURRENT);
        manager.set(AlarmManager.RTC_WAKEUP,task.getDate(),pendingIntent);
    }
    public void removeAlarm(long taskId){
        Intent intent = new Intent(context,AlarmReceiver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, (int) taskId,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        manager.cancel(pendingIntent);
    }
    public void setAlarm(Birthday birthday){
        Intent intent = new Intent(context,AlarmReceiver.class);
        intent.putExtra("title",birthday.getTitle());
        intent.putExtra("taskId",birthday.getId());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), (int) birthday.getId(),intent,PendingIntent.FLAG_UPDATE_CURRENT);
        manager.set(AlarmManager.RTC_WAKEUP,birthday.getDate(),pendingIntent);
    }

}
