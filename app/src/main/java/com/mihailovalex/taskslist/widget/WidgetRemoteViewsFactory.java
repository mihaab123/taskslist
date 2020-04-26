package com.mihailovalex.taskslist.widget;

import android.appwidget.AppWidgetManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import android.widget.Toast;

import com.mihailovalex.taskslist.R;
import com.mihailovalex.taskslist.data.DBHelper;
import com.mihailovalex.taskslist.data.MyContentProvider;
import com.mihailovalex.taskslist.data.TaskSchedulerClass;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

public class WidgetRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private Context mContext;
    private List<ContentValues> list;
    private DateFormat dateFormat;
    private int mWidgetId;

    public WidgetRemoteViewsFactory(Context context, Intent intent) {
        mContext = context;
        mWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    @Override public void onCreate() {
        list = new ArrayList<>();
        dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());
    }

    @Override public void onDataSetChanged() {
        list.clear();

        ArrayList<ContentValues> listadd = MyContentProvider.getAlarmsFromDatabase();

        list.addAll(listadd);

    }

    @Override public void onDestroy() {
    }

    @Override public int getCount() {
        return list.size();
    }

    @Override public RemoteViews getViewAt(int i) {
        ContentValues cv = list.get(i);
        RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.widget_list_item);
        Date date = new Date();
        date.setTime(Long.parseLong((String) cv.get(TaskSchedulerClass.Tasks.COLUMN_NAME_TIME)));
        dateFormat.format(date);
        rv.setTextViewText(R.id.date_tv_widget, dateFormat.format(date));
        rv.setTextViewText(R.id.title_tv_widget, (CharSequence) cv.get(TaskSchedulerClass.Tasks.COLUMN_NAME_TITLE));

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


}
