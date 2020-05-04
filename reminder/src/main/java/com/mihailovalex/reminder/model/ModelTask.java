package com.mihailovalex.reminder.model;

import com.mihailovalex.reminder.R;

import java.util.Date;

public class ModelTask implements Item {
    public final static int PRIORITY_LOW = 0;
    public final static int PRIORITY_NORMAL = 1;
    public final static int PRIORITY_HIGH = 2;

    public final static int STATUS_OVERDUE = 0;
    public final static int STATUS_CURRENT = 1;
    public final static int STATUS_DONE = 2;
    public final static String[] PRIORITY_LEVELS = {"Low priority","Normal priority","High priority"};

    private String title;
    private long date;
    private long timeStamp;
    private int priority;
    private int status;
    private int dateStatus;

    public ModelTask(){
        status = -1;
        timeStamp = new Date().getTime();
    }

    public ModelTask(String title, long date, int priority, int status,long timeStamp) {
        this.title = title;
        this.date = date;
        this.priority = priority;
        this.status = status;
        this.timeStamp = timeStamp;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    @Override
    public boolean isTask() {
        return true;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getStatus() {
        return status;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getDateStatus() {
        return dateStatus;
    }

    public void setDateStatus(int dateStatus) {
        this.dateStatus = dateStatus;
    }

    public int getPriorityColor(){
        switch (getPriority()){
            case PRIORITY_HIGH:
                if(getStatus()==STATUS_CURRENT||getStatus()==STATUS_OVERDUE){
                    return R.color.priority_high;
                } else return R.color.priority_high_selected;
            case PRIORITY_NORMAL:
                if(getStatus()==STATUS_CURRENT||getStatus()==STATUS_OVERDUE){
                    return R.color.priority_normal;
                } else return R.color.priority_normal_selected;
            case PRIORITY_LOW:
                if(getStatus()==STATUS_CURRENT||getStatus()==STATUS_OVERDUE){
                    return R.color.priority_low;
                } else return R.color.priority_low_selected;
            default:
                return  0;
        }
    }
}
