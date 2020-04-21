package com.mihailovalex.taskslist.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.HashMap;

public class MyContentProvider extends ContentProvider {
    // возможным типам запроса к нашей БД
    private static final int GROUPS = 1;
    private static final int GROUPS_ID = 2;
    private static final int TASKS = 3;
    private static final int TASKS_ID = 4;
    //Затем объявим переменную класса UriMatcher
    private static final UriMatcher sUriMatcher;
    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(TaskSchedulerClass.AUTHORITY, "groups", GROUPS);
        sUriMatcher.addURI(TaskSchedulerClass.AUTHORITY, "groups/#", GROUPS_ID);
        sUriMatcher.addURI(TaskSchedulerClass.AUTHORITY, "tasks", TASKS);
        sUriMatcher.addURI(TaskSchedulerClass.AUTHORITY, "tasks/#", TASKS_ID);
    }
    //  необходимо задать проекции для выборки столбцов в запрос
    private static HashMap sGroupsProjectionMap;
    private static HashMap sTasksProjectionMap;
    static {
        /*sGroupsProjectionMap = new HashMap();
        for(int i=0; i < TaskSchedulerClass.Groups.DEFAULT_PROJECTION.length; i++) {
            sGroupsProjectionMap.put(
                    TaskSchedulerClass.Groups.DEFAULT_PROJECTION[i],
                    TaskSchedulerClass.Groups.DEFAULT_PROJECTION[i]);
        }
        sTasksProjectionMap = new HashMap();
        for(int i=0; i < TaskSchedulerClass.Tasks.DEFAULT_PROJECTION.length; i++) {
            sTasksProjectionMap.put(
                    TaskSchedulerClass.Tasks.DEFAULT_PROJECTION[i],
                    TaskSchedulerClass.Tasks.DEFAULT_PROJECTION[i]+"1");
        }*/
        sGroupsProjectionMap = TaskSchedulerClass.Groups.DEFAULT_PROJECTION_MAP;
        sTasksProjectionMap = TaskSchedulerClass.Tasks.DEFAULT_PROJECTION_MAP;
    }


    private DBHelper dbHelper;

    @Override
    public boolean onCreate() {
        dbHelper = new DBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        String orderBy = null;
        switch (sUriMatcher.match(uri)) {
            case GROUPS:
                qb.setTables(TaskSchedulerClass.Groups.TABLE_NAME);
                qb.setProjectionMap(sGroupsProjectionMap);
                orderBy = TaskSchedulerClass.Groups.DEFAULT_SORT_ORDER;
                break;
            case GROUPS_ID:
                qb.setTables(TaskSchedulerClass.Groups.TABLE_NAME);
                qb.setProjectionMap(sGroupsProjectionMap);
                qb.appendWhere(TaskSchedulerClass.Groups._ID + "=" + uri.getPathSegments().get(TaskSchedulerClass.Groups.PATH_GROUPS_ID_PATH_POSITION));
                orderBy = TaskSchedulerClass.Groups.DEFAULT_SORT_ORDER;
                break;
            case TASKS:
                //qb.setTables(TaskSchedulerClass.Tasks.TABLE_NAME);
                qb.setTables(TaskSchedulerClass.Tasks.TABLE_NAME+" LEFT JOIN "+TaskSchedulerClass.Groups.TABLE_NAME+
                        " ON " + TaskSchedulerClass.Tasks.TABLE_NAME+
                        "." + TaskSchedulerClass.Tasks.COLUMN_NAME_GROUPID+
                                " = " + TaskSchedulerClass.Groups.TABLE_NAME +
                                "." + TaskSchedulerClass.Groups._ID);
                qb.setProjectionMap(sTasksProjectionMap);
                orderBy = TaskSchedulerClass.Tasks.DEFAULT_SORT_ORDER;
                break;
            case TASKS_ID:
                //qb.setTables(TaskSchedulerClass.Tasks.TABLE_NAME);
                qb.setTables(TaskSchedulerClass.Tasks.TABLE_NAME+" LEFT JOIN "+TaskSchedulerClass.Groups.TABLE_NAME+
                        " ON " + TaskSchedulerClass.Tasks.TABLE_NAME+
                        "." + TaskSchedulerClass.Tasks.COLUMN_NAME_GROUPID+
                        " = " + TaskSchedulerClass.Groups.TABLE_NAME +
                        "." + TaskSchedulerClass.Groups._ID);
                qb.setProjectionMap(sTasksProjectionMap);
                qb.appendWhere(TaskSchedulerClass.Tasks.TABLE_NAME+"."+TaskSchedulerClass.Tasks._ID + "=" + uri.getPathSegments().get(TaskSchedulerClass.Tasks.TASKS_ID_PATH_POSITION));
                orderBy = TaskSchedulerClass.Tasks.DEFAULT_SORT_ORDER;
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, orderBy);
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case GROUPS:
                return TaskSchedulerClass.Groups.CONTENT_TYPE;
            case GROUPS_ID:
                return TaskSchedulerClass.Groups.CONTENT_ITEM_TYPE;
            case TASKS:
                return TaskSchedulerClass.Tasks.CONTENT_TYPE;
            case TASKS_ID:
                return TaskSchedulerClass.Tasks.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues initialValues) {
        if (sUriMatcher.match(uri) != GROUPS && sUriMatcher.match(uri) != TASKS) {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values;
        if (initialValues != null) {
            values = new ContentValues(initialValues);
        }
        else {
            values = new ContentValues();
        }
        long rowId = -1;
        Uri rowUri = Uri.EMPTY;
        switch (sUriMatcher.match(uri)) {
            case GROUPS:
                rowId = db.insert(TaskSchedulerClass.Groups.TABLE_NAME,
                        TaskSchedulerClass.Groups.COLUMN_NAME_NAME,
                        values);
                if (rowId > 0) {
                    rowUri = ContentUris.withAppendedId(TaskSchedulerClass.Groups.CONTENT_ID_URI_BASE, rowId);
                    getContext().getContentResolver().notifyChange(rowUri, null);
                }
                break;
            case TASKS:
                rowId = db.insert(TaskSchedulerClass.Tasks.TABLE_NAME,
                        TaskSchedulerClass.Tasks.COLUMN_NAME_TITLE,
                        values);
                if (rowId > 0) {
                    rowUri = ContentUris.withAppendedId(TaskSchedulerClass.Tasks.CONTENT_ID_URI_BASE, rowId);
                    getContext().getContentResolver().notifyChange(rowUri, null);
                }
                break;
        }
        return rowUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String finalWhere;
        int count;
        switch (sUriMatcher.match(uri)) {
            case GROUPS:
                count = db.delete(TaskSchedulerClass.Groups.TABLE_NAME,selection,selectionArgs);
                break;
            case GROUPS_ID:
                finalWhere = TaskSchedulerClass.Groups._ID + " = " + uri.getPathSegments().get(TaskSchedulerClass.Groups.PATH_GROUPS_ID_PATH_POSITION);
                if (selection != null) {
                    finalWhere = finalWhere + " AND " + selection;
                }
                count = db.delete(TaskSchedulerClass.Groups.TABLE_NAME,finalWhere,selectionArgs);
                break;
            case TASKS:
                count = db.delete(TaskSchedulerClass.Tasks.TABLE_NAME,selection,selectionArgs);
                break;
            case TASKS_ID:
                finalWhere = TaskSchedulerClass.Tasks._ID + " = " + uri.getPathSegments().get(TaskSchedulerClass.Tasks.TASKS_ID_PATH_POSITION);
                if (selection != null) {
                    finalWhere = finalWhere + " AND " + selection;
                }
                count = db.delete(TaskSchedulerClass.Tasks.TABLE_NAME,finalWhere,selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int count;
        String finalWhere;
        String id;
        switch (sUriMatcher.match(uri)) {
            case GROUPS:
                count = db.update(TaskSchedulerClass.Groups.TABLE_NAME, values, selection, selectionArgs);
                break;
            case GROUPS_ID:
                id = uri.getPathSegments().get(TaskSchedulerClass.Groups.PATH_GROUPS_ID_PATH_POSITION);
                finalWhere = TaskSchedulerClass.Groups._ID + " = " + id;
                if (selection !=null) {
                    finalWhere = finalWhere + " AND " + selection;
                }
                count = db.update(TaskSchedulerClass.Groups.TABLE_NAME, values, finalWhere, selectionArgs);
                break;
            case TASKS:
                count = db.update(TaskSchedulerClass.Tasks.TABLE_NAME, values, selection, selectionArgs);
                break;
            case TASKS_ID:
                id = uri.getPathSegments().get(TaskSchedulerClass.Tasks.TASKS_ID_PATH_POSITION);
                finalWhere = TaskSchedulerClass.Tasks._ID + " = " + id;
                if (selection !=null) {
                    finalWhere = finalWhere + " AND " + selection;
                }
                count = db.update(TaskSchedulerClass.Tasks.TABLE_NAME, values, finalWhere, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }
}
