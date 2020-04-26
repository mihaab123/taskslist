package com.mihailovalex.taskslist;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import com.mihailovalex.taskslist.settings.SettingsActivity;

import java.util.HashMap;

public class NotificationUtils {

    private static final String TAG = NotificationUtils.class.getSimpleName();

    private static NotificationUtils instance;

    private static Context context;
    private NotificationManager manager; // Системная утилита, упарляющая уведомлениями
    private int lastId = 0; //постоянно увеличивающееся поле, уникальный номер каждого уведомления
    private HashMap<Integer, Notification> notifications; //массив ключ-значение на все отображаемые пользователю уведомления


    //приватный контструктор для Singleton
    private NotificationUtils(Context context) {
        this.context = context;
        manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notifications = new HashMap<Integer, Notification>();
    }

    /**
     * Получение ссылки на синглтон
     */
    public static NotificationUtils getInstance(Context context) {
        if (instance == null) {
            instance = new NotificationUtils(context);
        } else {
            instance.context = context;
        }
        return instance;
    }
    public int createInfoNotification(String message){
        Intent notificationIntent = new Intent(context, MainActivity.class); // по клику на уведомлении откроется HomeActivity
        NotificationCompat.Builder nb = new NotificationCompat.Builder(context)
//NotificationCompat.Builder nb = new NotificationBuilder(context) //для версии Android > 3.0
                .setSmallIcon(R.drawable.icon_app) //иконка уведомления
                .setAutoCancel(true) //уведомление закроется по клику на него
                .setTicker(message) //текст, который отобразится вверху статус-бара при создании уведомления
                .setContentText(message) // Основной текст уведомления
                .setContentIntent(PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT))
                .setWhen(System.currentTimeMillis()) //отображаемое время уведомления
                .setContentTitle(context.getResources().getString(R.string.app_name)) //заголовок уведомления
                .setDefaults(Notification.DEFAULT_ALL); // звук, вибро и диодный индикатор выставляются по умолчанию

        Notification notification = nb.getNotification(); //генерируем уведомление
        manager.notify(lastId, notification); // отображаем его пользователю.
        notifications.put(lastId, notification); //теперь мы можем обращаться к нему по id
        return lastId++;
    }
     /* Создание уведомления с прогрессбаром о загрузке
  * @param fileName - текст, отображённый в заголовке уведомления.
            */
    public int createDownloadNotification(String fileName){
        String text = "3 задачи";//context.getString(R.string.notification_downloading).concat(" ").concat(fileName); //текст уведомления
        RemoteViews contentView = createProgressNotification(text, text); //View уведомления
        //contentView.setImageViewResource(R.id.notification_download_layout_image, R.drawable.ic_stat_example); // иконка уведомления
        return lastId++; //увеличиваем id, которое будет соответствовать следующему уведомлению
    }

    /**
     * генерация уведомления с ProgressBar, иконкой и заголовком
     *
     * @param text заголовок уведомления
     * @param topMessage сообщение, уотображаемое в закрытом статус-баре при появлении уведомления
     * @return View уведомления.
     */
    private RemoteViews createProgressNotification(String text, String topMessage) {
        Notification notification = new Notification(R.drawable.icon_app, topMessage, System.currentTimeMillis());
        RemoteViews contentView = new RemoteViews(context.getPackageName(), R.layout.status_bar);
        contentView.setTextViewText(R.id.text_status_bar, text);
        contentView.setImageViewResource(R.id.imAddStatusBar, R.drawable.ic_add);
        Intent addIntent = new Intent(context, EditTaskActivity.class);
        PendingIntent addPendingIntent = PendingIntent.getActivity(context,0,addIntent,0);
        contentView.setOnClickPendingIntent(R.id.imAddStatusBar,addPendingIntent);
        contentView.setImageViewResource(R.id.imSettingStatusBar, R.drawable.ic_settings);
        Intent resultIntent = new Intent(context, SettingsActivity.class);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(context,0,resultIntent,0);
        contentView.setOnClickPendingIntent(R.id.imSettingStatusBar,resultPendingIntent);

        notification.contentView = contentView;
        notification.flags = Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT | Notification.FLAG_ONLY_ALERT_ONCE;

        Intent notificationIntent = new Intent(context, NotificationUtils.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
        notification.contentIntent = contentIntent;

        manager.notify(lastId, notification);
        notifications.put(lastId, notification);
        return contentView;
    }
}
