package com.mihailovalex.reminder_room.data;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.mihailovalex.reminder_room.R;


@Entity(tableName = "birthdays")
public final class Birthday implements Item {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    private  long id;

    @NonNull
    private  String title;

    private  long date;

    private  String comment;

    private  boolean completed;

    private String repeat;

    @Ignore
    private int dateStatus;

    public Birthday(long id, @NonNull String title, long date, String comment, boolean completed) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.comment = comment;
        this.completed = completed;
        this.repeat = "1y";
    }

    @Ignore
    public Birthday(@NonNull String title, long date, String comment) {
        this.title = title;
        this.date = date;
        this.comment = comment;
        this.completed = false;
        this.repeat = "1y";
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

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
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

    public String getRepeat() {
        return repeat;
    }

    public void setRepeat(String repeat) {
        this.repeat = repeat;
    }
    public boolean isRepeated(){
        return !repeat.isEmpty();
    }

    @Override
    public int getPriorityColor() {
        return R.color.priority_normal;
    }
}
