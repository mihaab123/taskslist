/*
 * Copyright 2016, The Android Open Source Project
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

package com.mihailovalex.reminder_room.data.source;


import androidx.annotation.NonNull;

import com.mihailovalex.reminder_room.data.Task;

import java.util.List;


public interface TasksDataSource {

    interface LoadTasksCallback {

        void onTasksLoaded(List<Task> tasks);

        void onDataNotAvailable();
    }

    interface GetTaskCallback {

        void onTaskLoaded(Task task);

        void onDataNotAvailable();
    }

    void getTasks(@NonNull LoadTasksCallback callback, String searchString);

    void getTask(@NonNull long taskId, @NonNull GetTaskCallback callback);

    void saveTask(@NonNull Task task);

    void completeTask(@NonNull Task task);

    void completeTask(@NonNull long taskId);

    void activateTask(@NonNull Task task);

    void activateTask(@NonNull long taskId);

    void clearCompletedTasks(@NonNull long date);

    void refreshTasks();

    void deleteAllTasks();

    void deleteTask(@NonNull long taskId);
}
