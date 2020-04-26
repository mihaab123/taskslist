package com.mihailovalex.taskslist.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.RemoteViews;

import com.mihailovalex.taskslist.R;

/**
 * Implementation of App Widget functionality.
 */
public class TasksListWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.tasks_list_widget);
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
        //if (BuildConfig.DEBUG) Log.d(TAG, action);
        if (!TextUtils.isEmpty(action)) {
            if (action.equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE)) {
                AppWidgetManager manager = AppWidgetManager.getInstance(context);
                ComponentName cn = new ComponentName(context, TasksListWidget.class);
                manager.notifyAppWidgetViewDataChanged(manager.getAppWidgetIds(cn), R.id.widgetList);
            }
        }
        super.onReceive(context, intent);
    }
}

