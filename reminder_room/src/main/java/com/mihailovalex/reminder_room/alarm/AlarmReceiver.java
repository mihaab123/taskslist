package com.mihailovalex.reminder_room.alarm;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

import androidx.core.app.NotificationCompat;
import androidx.legacy.content.WakefulBroadcastReceiver;

import com.mihailovalex.reminder_room.MainActivity;
import com.mihailovalex.reminder_room.MyApplication;
import com.mihailovalex.reminder_room.R;
import com.mihailovalex.reminder_room.ui.addedittask.AddEditTaskActivity;
import com.mihailovalex.reminder_room.ui.addedittask.AddEditTaskFragment;

public class AlarmReceiver extends BroadcastReceiver {
    private static final String CHANNEL_ID = "chanel";

    @Override
    public void onReceive(Context context, Intent intent) {
        String title = intent.getStringExtra("title");
        long taskId = intent.getLongExtra("taskId",0);
        int color = intent.getIntExtra("color",0);

        Intent resultIntent = new Intent(context, AddEditTaskActivity.class);
        resultIntent.putExtra(AddEditTaskFragment.ARGUMENT_EDIT_TASK_ID,taskId);
        if(MyApplication.isActivityVisible()){
            resultIntent = intent;
        }
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,(int)taskId,resultIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "My channel",
                    NotificationManager.IMPORTANCE_HIGH);
            channel.enableLights(true);
            channel.setLightColor(Color.BLUE);
            channel.enableVibration(true);
            notificationManager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,CHANNEL_ID);
        builder.setContentTitle("Reminder");
        builder.setContentText(title);
        builder.setColor(context.getResources().getColor(color));
        builder.setSmallIcon(R.drawable.ic_circle_check);
        builder.setDefaults(Notification.DEFAULT_ALL);
        builder.setContentIntent(pendingIntent);

        Notification notification =builder.build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify((int) taskId,notification);
    }
}
