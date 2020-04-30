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

package com.mihailovalex.taskslist_room.data.source.local;


import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;

import com.mihailovalex.taskslist_room.data.Group;
import com.mihailovalex.taskslist_room.data.Task;
import com.mihailovalex.taskslist_room.data.source.GroupsDataSource;
import com.mihailovalex.taskslist_room.data.source.TasksDataSource;
import com.mihailovalex.taskslist_room.util.AppExecutors;

import java.util.List;

import static com.google.firebase.components.Preconditions.checkNotNull;

/**
 * Concrete implementation of a data source as a db.
 */
public class GroupsLocalDataSource implements GroupsDataSource {

    private static volatile GroupsLocalDataSource INSTANCE;

    private GroupsDao mGroupsDao;

    private AppExecutors mAppExecutors;

    // Prevent direct instantiation.
    private GroupsLocalDataSource(@NonNull AppExecutors appExecutors,
                                  @NonNull GroupsDao groupsDao) {
        mAppExecutors = appExecutors;
        mGroupsDao = groupsDao;
    }

    public static GroupsLocalDataSource getInstance(@NonNull AppExecutors appExecutors,
                                                    @NonNull GroupsDao groupsDao) {
        if (INSTANCE == null) {
            synchronized (GroupsLocalDataSource.class) {
                if (INSTANCE == null) {
                    INSTANCE = new GroupsLocalDataSource(appExecutors, groupsDao);
                }
            }
        }
        return INSTANCE;
    }

    /**
     * Note: {@link LoadGroupsCallback#onDataNotAvailable()} is fired if the database doesn't exist
     * or the table is empty.
     */
    @Override
    public void getGroups(@NonNull final LoadGroupsCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final List<Group> groups = mGroupsDao.getGroups();
                mAppExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (groups.isEmpty()) {
                            // This will be called if the table is new or just empty.
                            callback.onDataNotAvailable();
                        } else {
                            callback.onGroupsLoaded(groups);
                        }
                    }
                });
            }
        };

        mAppExecutors.diskIO().execute(runnable);
    }

    /**
     * Note: {@link GetGroupCallback#onDataNotAvailable()} is fired if the {@link Task} isn't
     * found.
     */
    @Override
    public void getGroup(@NonNull final Long groupId, @NonNull final GetGroupCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final Group group = mGroupsDao.getGroupById(groupId);

                mAppExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (group != null) {
                            callback.onGroupLoaded(group);
                        } else {
                            callback.onDataNotAvailable();
                        }
                    }
                });
            }
        };

        mAppExecutors.diskIO().execute(runnable);
    }

    @Override
    public void saveGroup(@NonNull final Group group) {
        checkNotNull(group);
        Runnable saveRunnable = new Runnable() {
            @Override
            public void run() {
                mGroupsDao.insert(group);
            }
        };
        mAppExecutors.diskIO().execute(saveRunnable);
    }


    @Override
    public void refreshGroups() {
        // Not required because the {@link TasksRepository} handles the logic of refreshing the
        // tasks from all the available data sources.
    }

    @Override
    public void deleteAllGroups() {
        Runnable deleteRunnable = new Runnable() {
            @Override
            public void run() {
                mGroupsDao.deleteGroups();
            }
        };

        mAppExecutors.diskIO().execute(deleteRunnable);
    }

    @Override
    public void deleteGroup(@NonNull final Long groupId) {
        Runnable deleteRunnable = new Runnable() {
            @Override
            public void run() {
                mGroupsDao.deleteGroupById(groupId);
            }
        };

        mAppExecutors.diskIO().execute(deleteRunnable);
    }

    @VisibleForTesting
    static void clearInstance() {
        INSTANCE = null;
    }
}
