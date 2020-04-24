package com.mihailovalex.taskslist.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.RemoteViews;

import com.mihailovalex.taskslist.R;

public class WidgetProvider extends AppWidgetProvider {
    private static final String TAG = "PROVIDER";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        for (int widgetId : appWidgetIds) {
            updateWidget(context, appWidgetManager, widgetId);
        }
    }

    private void updateWidget(Context context, AppWidgetManager appWidgetManager, int widgetId) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_list_item);
        setList(views, context, widgetId);

        appWidgetManager.updateAppWidget(widgetId, views);
    }

    private void setList(RemoteViews views, Context context, int widgetId) {
        Intent intent = new Intent(context, WidgetRemoteViewsService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
        views.setRemoteAdapter(R.id.widgetList, intent);
    }

    public static void sendRefreshBroadcast(Context context) {
        Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        intent.setComponent(new ComponentName(context, WidgetProvider.class));
        context.sendBroadcast(intent);
    }

    @Override public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        //if (BuildConfig.DEBUG) Log.d(TAG, action);
        if (!TextUtils.isEmpty(action)) {
            if (action.equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE)) {
                AppWidgetManager manager = AppWidgetManager.getInstance(context);
                ComponentName cn = new ComponentName(context, WidgetProvider.class);
                manager.notifyAppWidgetViewDataChanged(manager.getAppWidgetIds(cn), R.id.widgetList);
            }
        }
        super.onReceive(context, intent);
    }
}
