package com.mihailovalex.reminder.database;

import android.database.sqlite.SQLiteDatabase;

public class DBQueryManager {
    private SQLiteDatabase database;

    public DBQueryManager(SQLiteDatabase database) {
        this.database = database;
    }

}
