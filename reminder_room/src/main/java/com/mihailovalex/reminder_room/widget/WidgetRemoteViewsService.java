package com.mihailovalex.reminder_room.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

public class WidgetRemoteViewsService extends RemoteViewsService {
    @Override public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new WidgetRemoteViewsFactory(this.getApplicationContext(), intent);
    }
}
