package com.mihailovalex.reminder_room.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.RemoteViews;

import com.mihailovalex.reminder_room.R;
import com.mihailovalex.reminder_room.ui.addedittask.AddEditTaskActivity;
import com.mihailovalex.reminder_room.ui.addedittask.AddEditTaskFragment;


/**
 * Implementation of App Widget functionality.
 */
public class TasksListWidget extends AppWidgetProvider {
    public static final String ACTION_ON_ITEM_CLICK = "ON_MORE_CLICK";
    public static final String ACTION_ON_ADD_CLICK = "ON_ADD_CLICK";
    public static final String SHARE = "SHARE";
    public static final String ADD = "ADD";
    public static final String COMMAND = "COMMAND";
    public static final String ITEM = "ITEM";
    final String LOG_TAG = "MyLogs";

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.tasks_list_widget);
        // обработка нажатия на элемент списка
        final Intent onItemClick = new Intent(context, TasksListWidget.class);
        onItemClick.setAction(ACTION_ON_ITEM_CLICK);
        onItemClick.setData(Uri.parse(onItemClick.toUri(Intent.URI_INTENT_SCHEME)));
        final PendingIntent onClickPendingIntent =
                PendingIntent.getBroadcast(context, 0, onItemClick, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setPendingIntentTemplate(R.id.widgetList, onClickPendingIntent);
        //нажатие добавить новую задачу
        final Intent onAddClick = new Intent(context, TasksListWidget.class);
        onAddClick.setAction(ACTION_ON_ADD_CLICK);
        onAddClick.setData(Uri.parse(onAddClick.toUri(Intent.URI_INTENT_SCHEME)));
        final PendingIntent onAddClickPendingIntent =
                PendingIntent.getBroadcast(context, 0, onAddClick, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.imAddWidget,onAddClickPendingIntent);

        // обновляем адаптер
        setList(views, context, appWidgetId);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
        //Обновляем collection view этого виджета
        appWidgetManager.notifyAppWidgetViewDataChanged(
                appWidgetId,
                R.id.widgetList
        );

    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
    private static void setList(RemoteViews views, Context context, int widgetId) {
        Intent intent = new Intent(context, WidgetRemoteViewsService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
        views.setRemoteAdapter(R.id.widgetList, intent);
    }
   @Override
   public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        //Log.d(LOG_TAG, action);
        //if (BuildConfig.DEBUG) Log.d(TAG, action);
        if (!TextUtils.isEmpty(action)) {
            if (action.equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE)) {
                AppWidgetManager manager = AppWidgetManager.getInstance(context);
                ComponentName cn = new ComponentName(context, TasksListWidget.class);
                manager.notifyAppWidgetViewDataChanged(manager.getAppWidgetIds(cn), R.id.widgetList);
            }
            if (action.equals(ACTION_ON_ITEM_CLICK)) {
                parseItemClick(context, intent.getExtras());
            }
            if (action.equals(ACTION_ON_ADD_CLICK)){
                Intent addintent = new Intent(context, AddEditTaskActivity.class);
                addintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Long taskId = null;
                addintent.putExtra(AddEditTaskFragment.ARGUMENT_EDIT_TASK_ID, taskId);
                context.startActivity(addintent);
            }
        }
        super.onReceive(context, intent);
    }
    public static void sendRefreshBroadcast(Context context) {
        Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        intent.setComponent(new ComponentName(context, TasksListWidget.class));
        context.sendBroadcast(intent);
    }
    private void parseItemClick(Context context, Bundle bundle) {
        //Log.d(LOG_TAG, "parseItemClick");
        if (bundle != null) {
            String command = bundle.getString(COMMAND);
            //Log.d(LOG_TAG, command);
            if (!TextUtils.isEmpty(command)) {
                //if (BuildConfig.DEBUG) Log.d(TAG, command);
                Intent intent = new Intent(context, AddEditTaskActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Long taskId = null;
                switch (command) {

                    case SHARE: {
                        taskId = bundle.getLong(ITEM);
                        if (taskId != null) {

                            intent.putExtra(AddEditTaskFragment.ARGUMENT_EDIT_TASK_ID, taskId);

                            context.startActivity(intent);
                        }
                        break;
                    }

                }
            }

        }
    }

}

