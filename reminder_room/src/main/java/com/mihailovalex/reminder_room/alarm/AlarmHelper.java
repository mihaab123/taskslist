package com.mihailovalex.reminder_room.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.mihailovalex.reminder_room.R;
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
        if(Build.VERSION.SDK_INT < 23){
            if(Build.VERSION.SDK_INT >= 19){
                manager.setExact(AlarmManager.RTC_WAKEUP,task.getDate(),pendingIntent);
            }
            else{
                manager.set(AlarmManager.RTC_WAKEUP,task.getDate(),pendingIntent);
            }
        }
        else{
            manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,task.getDate(),pendingIntent);
        }
        //manager.set(AlarmManager.RTC_WAKEUP,task.getDate(),pendingIntent);
    }
    public void removeAlarm(long taskId){
        Intent intent = new Intent(context,AlarmReceiver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, (int) taskId,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        manager.cancel(pendingIntent);
    }
    public void setAlarm(Birthday birthday){
        Intent intent = new Intent(context,AlarmReceiver.class);
        intent.putExtra("title", context.getString(R.string.birthday_at)+" "+birthday.getTitle());
        intent.putExtra("taskId",birthday.getId());
        intent.putExtra("type",1);
        intent.putExtra("color",birthday.getPriorityColor());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), (int) birthday.getId(),intent,PendingIntent.FLAG_UPDATE_CURRENT);
        //manager.set(AlarmManager.RTC_WAKEUP,birthday.getDate(),pendingIntent);
        if(Build.VERSION.SDK_INT < 23){
            if(Build.VERSION.SDK_INT >= 19){
                manager.setExact(AlarmManager.RTC_WAKEUP,birthday.getDate(),pendingIntent);
            }
            else{
                manager.set(AlarmManager.RTC_WAKEUP,birthday.getDate(),pendingIntent);
            }
        }
        else{
            manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,birthday.getDate(),pendingIntent);
        }
    }

}
