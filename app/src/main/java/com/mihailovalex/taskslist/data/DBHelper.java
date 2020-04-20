package com.mihailovalex.taskslist.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.mihailovalex.taskslist.R;

import java.util.Date;

public class DBHelper extends SQLiteOpenHelper {

        private static final String DATABASE_NAME = "TasksListDB";

        public static final String DATABASE_TABLE_GROUPS = TaskSchedulerClass.Groups.TABLE_NAME;
        public static final String DATABASE_TABLE_TASKS = TaskSchedulerClass.Tasks.TABLE_NAME;

        public static final String KEY_ROWID  = "_id";
        public static final String KEY_NAME   = "name";
        public static final String KEY_TITLE   = "title";
        public static final String KEY_GROUP_ID   = "group_id";
        public static final String KEY_TIME   = "time_alert";
        public static final String KEY_TIME_BEFORE   = "time_alert_before";


        private static final String DATABASE_CREATE_TABLE_GROUPS =
                "create table "+ DATABASE_TABLE_GROUPS + " ("
                        + KEY_ROWID + " integer primary key autoincrement, "
                        + KEY_NAME + " string default '');";

        private static final int DATABASE_VERSION = 4;

        private static final String DATABASE_CREATE_TABLE_TASKS =
                "create table "+ DATABASE_TABLE_TASKS + " ("
                        + KEY_ROWID + " integer primary key autoincrement, "
                        + KEY_TITLE + " string default '', "
                        + KEY_TIME + " integer default 0, "
                        + KEY_TIME_BEFORE + " integer default 0, "
                        + KEY_GROUP_ID + " integer default 0 );";

        private Context ctx;
        DBHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            ctx = context;
        }
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DATABASE_CREATE_TABLE_GROUPS);
            ContentValues cv = new ContentValues();
            cv.put(TaskSchedulerClass.Groups.COLUMN_NAME_NAME, ctx.getResources().getString(R.string.group_default_personal));
            db.insert(DATABASE_TABLE_GROUPS, null, cv);
            cv.put(TaskSchedulerClass.Groups.COLUMN_NAME_NAME, ctx.getResources().getString(R.string.group_default_work));
            db.insert(DATABASE_TABLE_GROUPS, null, cv);

            db.execSQL(DATABASE_CREATE_TABLE_TASKS);
            Date date = new Date();
            db.execSQL("insert into "+DATABASE_TABLE_TASKS+" values (null, 'task 1', "+date.getTime()+",0,1);");
            db.execSQL("insert into "+DATABASE_TABLE_TASKS+" values (null, 'task 2', "+date.getTime()+",0,1);");
            db.execSQL("insert into "+DATABASE_TABLE_TASKS+" values (null, 'task 3', "+date.getTime()+",0,1);");

        }
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_GROUPS);
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_TASKS);
            onCreate(db);
        }
}
