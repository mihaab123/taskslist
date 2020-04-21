package com.mihailovalex.taskslist.data;

import android.net.Uri;
import android.provider.BaseColumns;

import java.util.HashMap;

public final class TaskSchedulerClass {
    public static final String AUTHORITY = "com.mihailovalex.taskslist.data.MyContentProvider";
    private TaskSchedulerClass() {}
    // определяем поля для хранения групп
    public static final class Groups implements BaseColumns {
        public static final String TABLE_NAME ="groups";
        private static final String SCHEME = "content://";
        private static final String PATH_GROUPS = "/groups";
        private static final String PATH_GROUPS_ID = "/groups/";
        public static final int PATH_GROUPS_ID_PATH_POSITION = 1;
        public static final Uri CONTENT_URI =  Uri.parse(SCHEME + AUTHORITY + PATH_GROUPS);
        public static final Uri CONTENT_ID_URI_BASE = Uri.parse(SCHEME + AUTHORITY + PATH_GROUPS_ID);
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.google.students";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.google.students";
        public static final String DEFAULT_SORT_ORDER = "name ASC";
        public static final String COLUMN_NAME_NAME   = "name";
        public static final String[] DEFAULT_PROJECTION = new String[] {
                TaskSchedulerClass.Groups._ID,
                TaskSchedulerClass.Groups.COLUMN_NAME_NAME
        };
        public static final HashMap<String,String> DEFAULT_PROJECTION_MAP = new HashMap<>();
        static {
            DEFAULT_PROJECTION_MAP.put(TaskSchedulerClass.Groups._ID, TaskSchedulerClass.Groups._ID);
            DEFAULT_PROJECTION_MAP.put(TaskSchedulerClass.Groups.COLUMN_NAME_NAME, TaskSchedulerClass.Groups.COLUMN_NAME_NAME);
        }
    }
    // определяем поля для хранения
    public static final class Tasks implements BaseColumns {
        public static final String TABLE_NAME ="tasks";
        private static final String SCHEME = "content://";
        private static final String PATH_TASKS = "/tasks";
        private static final String PATH_TASKS_ID = "/tasks/";
        public static final int TASKS_ID_PATH_POSITION = 1;
        public static final Uri CONTENT_URI =  Uri.parse(SCHEME + AUTHORITY + PATH_TASKS);
        public static final Uri CONTENT_ID_URI_BASE = Uri.parse(SCHEME + AUTHORITY + PATH_TASKS_ID);
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.google.students";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.google.students";
        public static final String DEFAULT_SORT_ORDER = "time_alert ASC";
        public static final String COLUMN_NAME_TITLE   = "title";
        public static final String COLUMN_NAME_GROUPID   = "group_id";
        public static final String COLUMN_NAME_GROUP_NAME   = "name";
        public static final String COLUMN_NAME_TIME   = "time_alert";
        public static final String COLUMN_NAME_TIME_BEFORE   = "time_alert_before";
        public static final String[] DEFAULT_PROJECTION = new String[] {
                TaskSchedulerClass.Tasks._ID,
                TaskSchedulerClass.Tasks.COLUMN_NAME_TITLE,
                TaskSchedulerClass.Tasks.COLUMN_NAME_GROUPID,
                TaskSchedulerClass.Tasks.COLUMN_NAME_TIME,
                TaskSchedulerClass.Tasks.COLUMN_NAME_TIME_BEFORE,
                TaskSchedulerClass.Tasks.COLUMN_NAME_GROUP_NAME
        };
        public static final HashMap<String,String> DEFAULT_PROJECTION_MAP = new HashMap<>();
        static {
            DEFAULT_PROJECTION_MAP.put(TaskSchedulerClass.Tasks._ID, TaskSchedulerClass.Tasks.TABLE_NAME+"."+TaskSchedulerClass.Tasks._ID+" as "+TaskSchedulerClass.Tasks._ID);
            DEFAULT_PROJECTION_MAP.put(TaskSchedulerClass.Tasks.COLUMN_NAME_GROUP_NAME, TaskSchedulerClass.Groups.TABLE_NAME+"."+ Groups.COLUMN_NAME_NAME+" as "+ Tasks.COLUMN_NAME_GROUP_NAME);
            DEFAULT_PROJECTION_MAP.put(TaskSchedulerClass.Tasks.COLUMN_NAME_TITLE,TaskSchedulerClass.Tasks.COLUMN_NAME_TITLE);
            DEFAULT_PROJECTION_MAP.put(TaskSchedulerClass.Tasks.COLUMN_NAME_GROUPID,TaskSchedulerClass.Tasks.COLUMN_NAME_GROUPID);
            DEFAULT_PROJECTION_MAP.put(TaskSchedulerClass.Tasks.COLUMN_NAME_TIME,TaskSchedulerClass.Tasks.COLUMN_NAME_TIME);
            DEFAULT_PROJECTION_MAP.put(TaskSchedulerClass.Tasks.COLUMN_NAME_TIME_BEFORE,TaskSchedulerClass.Tasks.COLUMN_NAME_TIME_BEFORE);
             /*   //TaskSchedulerClass.Tasks._ID,
                TaskSchedulerClass.Tasks.COLUMN_NAME_TITLE,
                TaskSchedulerClass.Tasks.COLUMN_NAME_GROUPID,
                TaskSchedulerClass.Tasks.COLUMN_NAME_TIME,
                TaskSchedulerClass.Tasks.COLUMN_NAME_TIME_BEFORE
                // TaskSchedulerClass.Tasks.COLUMN_NAME_GROUP_NAME*/
        }
    }
}
