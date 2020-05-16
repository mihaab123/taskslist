package com.mihailovalex.reminder_room.widget;

import android.appwidget.AppWidgetManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.mihailovalex.reminder_room.R;
import com.mihailovalex.reminder_room.data.Task;

import com.mihailovalex.reminder_room.data.source.local.TasksDao;
import com.mihailovalex.reminder_room.data.source.local.TasksDatabase;
import com.mihailovalex.reminder_room.utils.DateUtils;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class WidgetRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private Context mContext;
    private TasksDao tasksDao;
    private List<Task> allTasks;
    private int mWidgetId;

    public WidgetRemoteViewsFactory(Context context, Intent intent) {
        mContext = context;
        mWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
        TasksDatabase database = TasksDatabase.getInstance(context.getApplicationContext());
        tasksDao = database.taskDao();
    }

    @Override public void onCreate() {
        allTasks = new ArrayList<>();

    }

    @Override public void onDataSetChanged() {
        allTasks = tasksDao.getTasks();

    }

    @Override public void onDestroy() {
    }

    @Override public int getCount() {
        return allTasks.size();
    }

    @Override public RemoteViews getViewAt(int i) {
        Task task = allTasks.get(i);
        RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.widget_list_item);
        rv.setTextViewText(R.id.date_tv_widget, DateUtils.getFullDate(task.getDate(),true));
        rv.setTextViewText(R.id.title_tv_widget, task.getTitle());
        rv.setOnClickFillInIntent(R.id.llWidget, createIntent(TasksListWidget.SHARE, task));

        return rv;
    }

    @Override public RemoteViews getLoadingView() {
        return null;
    }

    @Override public int getViewTypeCount() {
        return 1;
    }

    @Override public long getItemId(int i) {
        return i;
    }

    @Override public boolean hasStableIds() {
        return true;
    }
    private Intent createIntent(String cmd, Task wi) {
        Intent intent = new Intent();
        intent.setAction(TasksListWidget.ACTION_ON_ITEM_CLICK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // для андроид 6 и выше
        Bundle bundle = new Bundle();
        bundle.putString(TasksListWidget.COMMAND, cmd);
        bundle.putLong(TasksListWidget.ITEM, wi.getId());
        intent.putExtras(bundle);
        return intent;
    }

}
