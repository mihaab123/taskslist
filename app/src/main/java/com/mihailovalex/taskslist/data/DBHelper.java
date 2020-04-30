package com.mihailovalex.taskslist.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.mihailovalex.taskslist.R;

import java.util.ArrayList;
import java.util.Date;

public class DBHelper extends SQLiteOpenHelper {
        final String LOG_TAG = "MyLogs";

        private static final String DATABASE_NAME = "TasksListDB";

        public static final String DATABASE_TABLE_GROUPS = TaskSchedulerClass.Groups.TABLE_NAME;
        public static final String DATABASE_TABLE_TASKS = TaskSchedulerClass.Tasks.TABLE_NAME;

        public static final String KEY_ROWID  = "_id";
        public static final String KEY_NAME   = "name";
        public static final String KEY_TITLE   = "title";
        public static final String KEY_GROUP_ID   = "group_id";
        public static final String KEY_TIME   = "time_alert";
        public static final String KEY_TIME_BEFORE   = "time_alert_before";
        public static final String KEY_COMPLITED   = "complited";


        private static final String DATABASE_CREATE_TABLE_GROUPS =
                "create table "+ DATABASE_TABLE_GROUPS + " ("
                        + KEY_ROWID + " integer primary key autoincrement, "
                        + KEY_NAME + " string default '');";

        private static final int DATABASE_VERSION = 9;

        private static final String DATABASE_CREATE_TABLE_TASKS =
                "create table "+ DATABASE_TABLE_TASKS + " ("
                        + KEY_ROWID + " integer primary key autoincrement, "
                        + KEY_TITLE + " string default '', "
                        + KEY_TIME + " integer default 0, "
                        + KEY_TIME_BEFORE + " integer default 0, "
                        + KEY_COMPLITED + " integer default 0, "
                        + KEY_GROUP_ID + " integer default 0 );";

        private Context ctx;
        public DBHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            ctx = context;
        }
        @Override
        public void onCreate(SQLiteDatabase db) {
            // создаем базу групп и добавляем две группы по умолчанию
            db.execSQL(DATABASE_CREATE_TABLE_GROUPS);
            ContentValues cv = new ContentValues();
            cv.put(TaskSchedulerClass.Groups.COLUMN_NAME_NAME, ctx.getResources().getString(R.string.group_default_personal));
            db.insert(DATABASE_TABLE_GROUPS, null, cv);
            cv.put(TaskSchedulerClass.Groups.COLUMN_NAME_NAME, ctx.getResources().getString(R.string.group_default_work));
            db.insert(DATABASE_TABLE_GROUPS, null, cv);
            // создаем базу задач
            db.execSQL(DATABASE_CREATE_TABLE_TASKS);
            // для теста заполнение
            /*Date date = new Date();
            db.execSQL("insert into "+DATABASE_TABLE_TASKS+" values (null, 'task 1', "+date.getTime()+",0,1);");
            db.execSQL("insert into "+DATABASE_TABLE_TASKS+" values (null, 'task 2', "+date.getTime()+",0,1);");
            db.execSQL("insert into "+DATABASE_TABLE_TASKS+" values (null, 'task 3', "+date.getTime()+",0,1);");*/

        }
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            if (oldVersion < 9 && newVersion == 9) {
                db.execSQL("ALTER TABLE "+DATABASE_TABLE_TASKS+" ADD COLUMN "+KEY_COMPLITED+" INTEGER DEFAULT 0");
            }
            else if (oldVersion < 8 && newVersion == 8) {
                db.beginTransaction();
                try {
                    // сохраняем данные
                    ArrayList<ContentValues> listTasks = writeStaff(db,DATABASE_TABLE_TASKS);
                    ArrayList<ContentValues> listTGroups = writeStaff(db,DATABASE_TABLE_GROUPS);
                    // удаляем таблицы
                    db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_GROUPS);
                    db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_TASKS);
                    // создаем новые таблицы
                    db.execSQL(DATABASE_CREATE_TABLE_GROUPS);
                    db.execSQL(DATABASE_CREATE_TABLE_TASKS);
                    // заполняем таблицы данными
                    for (ContentValues cv : listTGroups) {
                        db.insert(DATABASE_TABLE_GROUPS, null, cv);
                    }
                    for (ContentValues cv : listTasks) {
                        db.insert(DATABASE_TABLE_TASKS, null, cv);
                    }

                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
            }else {
                db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_GROUPS);
                db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_TASKS);
                onCreate(db);
            }
        }
    // запрос данных и вывод в список
    private ArrayList<ContentValues> writeStaff(SQLiteDatabase db,String Table) {
        Cursor c = db.rawQuery("select * from "+Table, null);
        ArrayList<ContentValues> list = logCursor(c, "Table people");
        c.close();
        return list;
    }

    // вывод в список данных из курсора
    private ArrayList<ContentValues> logCursor(Cursor c, String title) {
        ArrayList<ContentValues> list = new ArrayList<>();
        int index =0;
        if (c != null) {
            if (c.moveToFirst()) {

                do {
                    ContentValues cv = new ContentValues();
                    for (String cn : c.getColumnNames()) {
                        index = c.getColumnIndex(cn);
                        if (cn == TaskSchedulerClass.Tasks._ID ) {

                        } else if (cn == TaskSchedulerClass.Tasks.COLUMN_NAME_TIME||cn == TaskSchedulerClass.Tasks.COLUMN_NAME_TIME_BEFORE||cn == TaskSchedulerClass.Tasks.COLUMN_NAME_GROUPID){
                            cv.put(cn,c.getLong(index));
                        }else cv.put(cn,c.getString(index));
                    }
                    list.add(cv);
                } while (c.moveToNext());
            }
        }
        return list;
    }
    public Cursor getAllTasks() {
        //og.d(LOG_TAG, "getAllTasks 1");
        SQLiteDatabase db = getReadableDatabase();
        //Log.d(LOG_TAG, "getAllTasks 2");
        Cursor res = db.rawQuery("select * from "+DATABASE_TABLE_TASKS+" WHERE "+TaskSchedulerClass.Tasks.COLUMN_NAME_COMPLITED+" = 0 ORDER BY "+TaskSchedulerClass.Tasks.COLUMN_NAME_TIME, null);
        //Log.d(LOG_TAG, "getAllTasks 3");
        return res;
    }

}
