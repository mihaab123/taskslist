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

package com.mihailovalex.taskslist_room.data;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;


import com.google.android.gms.common.internal.Objects;
import com.google.android.gms.common.util.Strings;

/**
 * Immutable model class for a Task.
 */
@Entity(tableName = "tasks")
public final class Task {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "entryid")
    private final Long mId;

    @Nullable
    @ColumnInfo(name = "title")
    private final String mTitle;

    @Nullable
    @ColumnInfo(name = "time")
    private final Long mTime;

    @Nullable
    @ColumnInfo(name = "groupId")
    private final Long mGroupId;

    @ColumnInfo(name = "completed")
    private final boolean mCompleted;



    /**
     * Use this constructor to create an active Task if the Task already has an id (copy of another
     * Task).
     *  @param title       title of the task
     * @param time description of the task
     * @param id          id of the task
     * @param mGroupId
     */
    @Ignore
    public Task(@Nullable String title, @Nullable Long time, @NonNull Long id, Long mGroupId) {
        this(title, time, id, mGroupId, false);
    }

    /**
     * Use this constructor to create a new completed Task.
     *
     * @param title       title of the task
     * @param time description of the task
     * @param completed   true if the task is completed, false if it's active
     */

    /**
     * Use this constructor to specify a completed Task if the Task already has an id (copy of
     * another Task).
     *  @param title       title of the task
     * @param time description of the task
     * @param id          id of the task
     * @param mGroupId
     * @param completed   true if the task is completed, false if it's active
     */
    public Task(@Nullable String title, @Nullable Long time,
                @NonNull Long id, @Nullable Long mGroupId, boolean completed) {
        mId = id;
        mTitle = title;
        mTime = time;
        this.mGroupId = mGroupId;
        mCompleted = completed;
    }

    @NonNull
    public Long getId() {
        return mId;
    }

    @Nullable
    public String getTitle() {
        return mTitle;
    }


    @Nullable
    public Long getTime() {
        return mTime;
    }

    public boolean isCompleted() {
        return mCompleted;
    }

    public boolean isActive() {
        return !mCompleted;
    }

    public boolean isEmpty() {
        return Strings.isEmptyOrWhitespace(mTitle);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equal(mId, task.mId) &&
               Objects.equal(mTitle, task.mTitle) &&
               Objects.equal(mTime, task.mTime);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(mId, mTitle, mTime);
    }

    public Long getGroupId() {
        return mGroupId;
    }

    @Override
    public String toString() {
        return "Task with title " + mTitle;
    }
}
