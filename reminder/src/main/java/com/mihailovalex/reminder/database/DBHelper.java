package com.mihailovalex.reminder.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import androidx.annotation.Nullable;

import com.mihailovalex.reminder.model.ModelTask;

public class DBHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "reminder_database";
    public static final String TASKS_TABLE = "tasks_table";
    public static final String TASK_TITLE_COLUMN = "task_title";
    public static final String TASK_DATE_COLUMN = "task_date";
    public static final String TASK_PRIORITY_COLUMN = "task_priority";
    public static final String TASK_STATUS_COLUMN = "task_status";
    public static final String TASK_TIMESTAMP_COLUMN = "task_timestamp";
    public static final String TASK_TABLE_CREATE_SCRIPT = "create table "+ TASKS_TABLE + " ("
            + BaseColumns._ID + " integer primary key autoincrement, "
            + TASK_TITLE_COLUMN + " text default '' not null, "
            + TASK_DATE_COLUMN + " long default 0, "
            + TASK_PRIORITY_COLUMN + " integer default 0, "
            + TASK_STATUS_COLUMN + " integer default 0, "
            + TASK_TIMESTAMP_COLUMN + " long default 0 );";
    public static final String SELECTION_STATUS = TASK_STATUS_COLUMN+" =?";
    public static final String SELECTION_TIMESTAMP = TASK_TIMESTAMP_COLUMN+" =?";
    public static final String SELECTION_LIKE_TITLE = TASK_TITLE_COLUMN+" LIKE ?";

    private DBQueryManager dbQueryManager;
    private DBUpdateManager dbUpdateManager;

    public DBQueryManager query() {
        return dbQueryManager;
    }

    public DBUpdateManager update() {
        return dbUpdateManager;
    }

    public DBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        dbQueryManager = new DBQueryManager(getReadableDatabase());
        dbUpdateManager = new DBUpdateManager(getWritableDatabase());
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TASK_TABLE_CREATE_SCRIPT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TASKS_TABLE);
        onCreate(db);
    }
    public void saveTask(ModelTask task){
        ContentValues contentValues = new ContentValues();
        contentValues.put(TASK_TITLE_COLUMN,task.getTitle());
        contentValues.put(TASK_DATE_COLUMN,task.getDate());
        contentValues.put(TASK_PRIORITY_COLUMN,task.getPriority());
        contentValues.put(TASK_STATUS_COLUMN,task.getStatus());
        contentValues.put(TASK_TIMESTAMP_COLUMN,task.getTimeStamp());
        getWritableDatabase().insert(TASKS_TABLE,null, contentValues);
    }
    public void deleteTask(long timestamp){
        getWritableDatabase().delete(TASKS_TABLE,SELECTION_TIMESTAMP, new String[]{Long.toString(timestamp)});
    }
}
