package com.mihailovalex.reminder_room.adapter;

import com.mihailovalex.reminder_room.R;
import com.mihailovalex.reminder_room.data.Item;

public class Separator implements Item {
    public static final int TYPE_OVERDUE = R.string.separator_overdue;
    public static final int TYPE_TODAY = R.string.separator_today;
    public static final int TYPE_TOMORROW = R.string.separator_tomorrow;
    public static final int TYPE_FUTURE = R.string.separator_future;
    private int type;

    public Separator(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public boolean isTask() {
        return false;
    }

    @Override
    public boolean isCompleted() {
        return false;
    }

    @Override
    public boolean isRepeated() {
        return false;
    }

    @Override
    public int getPriorityColor() {
        return 0;
    }
}
