/*
 * Copyright 2017, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mihailovalex.reminder_room.data.source.local;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.mihailovalex.reminder_room.data.Birthday;
import com.mihailovalex.reminder_room.data.Task;

import java.util.Date;

/**
 * The Room Database that contains the Task table.
 */
@Database(entities = {Task.class, Birthday.class}, version = 4)
public abstract class TasksDatabase extends RoomDatabase {

    private static TasksDatabase INSTANCE;

    public abstract TasksDao taskDao();
    public abstract BirthdaysDao birthdayDao();


    public synchronized static TasksDatabase getInstance(Context context) {

        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                    TasksDatabase.class, "Tasks.db")
                    .addCallback(roomCallback)
                    //.fallbackToDestructiveMigration()
                    .addMigrations(TasksDatabase.MIGRATION_3_4)
                    .allowMainThreadQueries()
                    .build();
        }
        return INSTANCE;

    }
    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback(){
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            //new PopulateDBAsyncTask(INSTANCE).execute();
        }
    };
    private static class PopulateDBAsyncTask extends AsyncTask<Void,Void,Void>{
        private TasksDao tasksDao;

        public PopulateDBAsyncTask(TasksDatabase db) {
            tasksDao = db.taskDao();
        }

        @Override
        protected Void doInBackground(Void... tasks) {
            tasksDao.insertTask(new Task("Task 1",(new Date()).getTime(),1));
            tasksDao.insertTask(new Task("Task 2",(new Date()).getTime(),1));
            tasksDao.insertTask(new Task("Task 3",(new Date()).getTime(),1));
            return null;
        }
    }
    public static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(final SupportSQLiteDatabase database) {
            // проблема с типом
            database.execSQL("CREATE TABLE Birthdays (id integer NOT NULL PRIMARY KEY AUTOINCREMENT DEFAULT 0, title text default '' not null, comment text default '' not null, date long default 0, completed boolean)");
        }
    };
    public static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(final SupportSQLiteDatabase database) {
            // проблема с типом

            database.execSQL("alter table tasks add column repeat text;");
            database.execSQL("alter table birthdays add column repeat text;");
        }
    };

}
