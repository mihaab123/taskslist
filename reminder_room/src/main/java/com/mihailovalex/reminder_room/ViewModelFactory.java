/*
 *  Copyright 2017 Google Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.mihailovalex.reminder_room;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;


import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;


import com.mihailovalex.reminder_room.data.source.BirthdaysRepository;
import com.mihailovalex.reminder_room.data.source.TasksRepository;
import com.mihailovalex.reminder_room.data.source.local.BirthdaysLocalDataSource;
import com.mihailovalex.reminder_room.data.source.local.TasksDatabase;
import com.mihailovalex.reminder_room.data.source.local.TasksLocalDataSource;
import com.mihailovalex.reminder_room.ui.addeditbirthday.AddEditBirthdayViewModel;
import com.mihailovalex.reminder_room.ui.addedittask.AddEditTaskViewModel;
import com.mihailovalex.reminder_room.ui.birthdays.BirthdayViewModel;
import com.mihailovalex.reminder_room.ui.currenttasks.CurrentTasksViewModel;
import com.mihailovalex.reminder_room.utils.AppExecutors;

/**
 * A creator is used to inject the product ID into the ViewModel
 * <p>
 * This creator is to showcase how to inject dependencies into ViewModels. It's not
 * actually necessary in this case, as the product ID can be passed in a public method.
 */
public class ViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    @SuppressLint("StaticFieldLeak")
    private static volatile ViewModelFactory INSTANCE;
    private final TasksRepository mTasksRepository;
    private final BirthdaysRepository mBirthdaysRepository;
    private final Application mApplication;
    public static ViewModelFactory getInstance(Application application) {

        if (INSTANCE == null) {
            synchronized (ViewModelFactory.class) {
                if (INSTANCE == null) {
                    TasksDatabase database = TasksDatabase.getInstance(application.getApplicationContext());
                    INSTANCE = new ViewModelFactory(application,
                            TasksRepository.getInstance(TasksLocalDataSource.getInstance(new AppExecutors(),
                                    database.taskDao())),
                            BirthdaysRepository.getInstance(BirthdaysLocalDataSource.getInstance(new AppExecutors(),
                                    database.birthdayDao())));
                }
            }
        }
        return INSTANCE;
    }

    @VisibleForTesting
    public static void destroyInstance() {
        INSTANCE = null;
    }

    public ViewModelFactory(Application application,TasksRepository tasksRepository, BirthdaysRepository birthdaysRepository) {
        mTasksRepository = tasksRepository;
        mBirthdaysRepository = birthdaysRepository;
        mApplication = application;

    }

    @Override
    @NonNull
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(CurrentTasksViewModel.class)) {
            return (T) new CurrentTasksViewModel(mApplication,mTasksRepository);
        }
        if (modelClass.isAssignableFrom(AddEditTaskViewModel.class)) {
            return (T) new AddEditTaskViewModel(mApplication,mTasksRepository);
        }
        if (modelClass.isAssignableFrom(AddEditBirthdayViewModel.class)) {
            return (T) new AddEditBirthdayViewModel(mApplication,mBirthdaysRepository);
        }
        if (modelClass.isAssignableFrom(BirthdayViewModel.class)) {
            return (T) new BirthdayViewModel(mApplication,mBirthdaysRepository);
        }
        //noinspection unchecked
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
