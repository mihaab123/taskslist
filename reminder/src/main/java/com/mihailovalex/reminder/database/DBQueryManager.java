package com.mihailovalex.reminder.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.mihailovalex.reminder.model.ModelTask;

import java.util.ArrayList;
import java.util.List;

public class DBQueryManager {
    private SQLiteDatabase database;

    public DBQueryManager(SQLiteDatabase database) {
        this.database = database;
    }
    public List<ModelTask> getTasks(String selection, String[] selectionArgs, String roderBY){
        List<ModelTask> tasks = new ArrayList<>();
        Cursor c= database.query(DBHelper.TASKS_TABLE,null,selection,selectionArgs,null,null,roderBY);
        if(c.moveToFirst()){
            do{
            String title = c.getString(c.getColumnIndex(DBHelper.TASK_TITLE_COLUMN));
            Long date = c.getLong(c.getColumnIndex(DBHelper.TASK_DATE_COLUMN));
            Long timeStamp = c.getLong(c.getColumnIndex(DBHelper.TASK_TIMESTAMP_COLUMN));
            int priority = c.getInt(c.getColumnIndex(DBHelper.TASK_PRIORITY_COLUMN));
            int status = c.getInt(c.getColumnIndex(DBHelper.TASK_STATUS_COLUMN));
            ModelTask newTask = new ModelTask(title,date,priority,status,timeStamp);
            tasks.add(newTask);
            }while (c.moveToNext());
        }
        c.close();
        return tasks;
    }
    public ModelTask getTask(Long timestamp){
        ModelTask task = new ModelTask();
        Cursor c= database.query(DBHelper.TASKS_TABLE,null,DBHelper.SELECTION_TIMESTAMP,new String[]{Long.toString(timestamp)},null,null,null);
        if(c.moveToFirst()){

                String title = c.getString(c.getColumnIndex(DBHelper.TASK_TITLE_COLUMN));
                Long date = c.getLong(c.getColumnIndex(DBHelper.TASK_DATE_COLUMN));
                Long timeStamp = c.getLong(c.getColumnIndex(DBHelper.TASK_TIMESTAMP_COLUMN));
                int priority = c.getInt(c.getColumnIndex(DBHelper.TASK_PRIORITY_COLUMN));
                int status = c.getInt(c.getColumnIndex(DBHelper.TASK_STATUS_COLUMN));
                task = new ModelTask(title,date,priority,status,timeStamp);

        }
        c.close();
        return task;
    }

}
