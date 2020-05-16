package com.mihailovalex.reminder_room.data;

public interface Item {
    public boolean isTask();

    public boolean isCompleted();
    public boolean isRepeated();
    public int getPriorityColor();
}
