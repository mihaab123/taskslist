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



import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.mihailovalex.reminder_room.data.Task;

import java.util.List;

/**
 * Data Access Object for the tasks table.
 */
@Dao
public interface TasksDao {

    /**
     * Select all tasks from the tasks table.
     *
     * @return all tasks.
     */
    @Query("SELECT * FROM Tasks order by date")
    List<Task> getTasks();

    @Query("SELECT * FROM Tasks WHERE completed = 0 order by date")
    List<Task> getActiveTasks();

    @Query("SELECT * FROM Tasks where title LIKE  :search order by date")
    List<Task> getTasksByTitle(String search);
    /**
     * Select a task by id.
     *
     * @param taskId the task id.
     * @return the task with taskId.
     */
    @Query("SELECT * FROM Tasks WHERE id = :taskId")
    Task getTaskById(long taskId);

    /**
     * Insert a task in the database. If the task already exists, replace it.
     *
     * @param task the task to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTask(Task task);

    /**
     * Update a task.
     *
     * @param task task to be updated
     * @return the number of tasks updated. This should always be 1.
     */
    @Update
    int updateTask(Task task);

    /**
     * Update the complete status of a task
     *
     * @param taskId    id of the task
     * @param completed status to be updated
     */
    @Query("UPDATE tasks SET completed = :completed WHERE id = :taskId")
    void updateCompleted(long taskId, boolean completed);

    /**
     * Delete a task by id.
     *
     * @return the number of tasks deleted. This should always be 1.
     */
    @Query("DELETE FROM Tasks WHERE id = :taskId")
    int deleteTaskById(long taskId);

    /**
     * Delete all tasks.
     */
    @Query("DELETE FROM Tasks")
    void deleteTasks();

    /**
     * Delete all completed tasks from the table.
     *
     * @return the number of tasks deleted.
     */
    @Query("DELETE FROM Tasks WHERE completed = 1")
    int deleteCompletedTasks();
}
