package com.mihailovalex.reminder_room.data;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.mihailovalex.reminder_room.R;


@Entity(tableName = "tasks")
public final class Task implements Item {
    @Ignore
    public final static int PRIORITY_LOW = 0;
    @Ignore
    public final static int PRIORITY_NORMAL = 1;
    @Ignore
    public final static int PRIORITY_HIGH = 2;
    @Ignore
    public final static String[] PRIORITY_LEVELS = {"Low priority","Normal priority","High priority"};

    @PrimaryKey(autoGenerate = true)
    @NonNull
    private  long id;

    @NonNull
    private  String title;

    private  long date;

    private  int priority;

    private  boolean completed;

    @Ignore
    private int dateStatus;

    public Task(long id, @NonNull String title, long date, int priority, boolean completed) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.priority = priority;
        this.completed = completed;
    }

    @Ignore
    public Task(@NonNull String title, long date, int priority) {
        this.title = title;
        this.date = date;
        this.priority = priority;
        this.completed = false;
    }

    @Override
    public boolean isTask() {
        return true;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @NonNull
    public String getTitle() {
        return title;
    }

    public void setTitle(@NonNull String title) {
        this.title = title;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public int getDateStatus() {
        return dateStatus;
    }

    public void setDateStatus(int dateStatus) {
        this.dateStatus = dateStatus;
    }
    public boolean isCompleted() {
        return completed;
    }

    public boolean isActive() {
        return !completed;
    }

    public boolean isEmpty() {
        return title.isEmpty(); //Strings.isNullOrEmpty(title);
    }
    public int getPriorityColor(){
        switch (getPriority()){
            case PRIORITY_HIGH:
                if(isActive()){
                    return R.color.priority_high;
                } else return R.color.priority_high_selected;
            case PRIORITY_NORMAL:
                if(isActive()){
                    return R.color.priority_normal;
                } else return R.color.priority_normal_selected;
            case PRIORITY_LOW:
                if(isActive()){
                    return R.color.priority_low;
                } else return R.color.priority_low_selected;
            default:
                return  R.color.priority_high;//0;
        }
    }

}
