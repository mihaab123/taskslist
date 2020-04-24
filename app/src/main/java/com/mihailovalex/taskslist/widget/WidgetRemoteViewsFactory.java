package com.mihailovalex.taskslist.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.mihailovalex.taskslist.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;

public class WidgetRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private Context mContext;
    //private List<WifiInfoWidget> list;
    private DateFormat dateFormat;
    private int mWidgetId;

    public WidgetRemoteViewsFactory(Context context, Intent intent) {
        mContext = context;
        mWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    @Override public void onCreate() {
        //list = new ArrayList<>();
        dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());
    }

    @Override public void onDataSetChanged() {
        /*list.clear();
        Set<WifiInfoWidget> networkList = new HashSet<>();
        String json = SP.getString(mContext, WIDGET_LIST, null);
        if (json != null) {
            networkList = SP.getWidgetList(json);
        }
        if (networkList != null) {
            list.addAll(networkList);
        }*/
    }

    @Override public void onDestroy() {
    }

    @Override public int getCount() {
        return 0;//list.size();
    }

    @Override public RemoteViews getViewAt(int i) {
       // WifiInfoWidget wi = list.get(i);
        RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.widget_list_item);
        //rv.setTextViewText(R.id.date_tv_widget, wi.SSID);

        //rv.setTextViewText(R.id.title_tv_widget, wi.password);

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
