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

package com.mihailovalex.taskslist_room.data.source;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.mihailovalex.taskslist_room.data.Group;
import com.mihailovalex.taskslist_room.data.Task;
import com.mihailovalex.taskslist_room.util.EspressoIdlingResource;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.google.firebase.components.Preconditions.checkNotNull;


/**
 * Concrete implementation to load tasks from the data sources into a cache.
 * <p>
 * For simplicity, this implements a dumb synchronisation between locally persisted data and data
 * obtained from the server, by using the remote data source only if the local database doesn't
 * exist or is empty.
 *
 * //TODO: Implement this class using LiveData.
 */
public class GroupsRepository implements GroupsDataSource {

    private volatile static GroupsRepository INSTANCE = null;

    private final GroupsDataSource mGroupsRemoteDataSource;

    private final GroupsDataSource mGroupsLocalDataSource;

    /**
     * This variable has package local visibility so it can be accessed from tests.
     */
    Map<Long, Group> mCachedGroups;

    /**
     * Marks the cache as invalid, to force an update the next time data is requested. This variable
     * has package local visibility so it can be accessed from tests.
     */
    private boolean mCacheIsDirty = false;

    // Prevent direct instantiation.
    private GroupsRepository(@NonNull GroupsDataSource groupsRemoteDataSource,
                             @NonNull GroupsDataSource groupsLocalDataSource) {
        mGroupsRemoteDataSource = checkNotNull(groupsRemoteDataSource);
        mGroupsLocalDataSource = checkNotNull(groupsLocalDataSource);
    }

    /**
     * Returns the single instance of this class, creating it if necessary.
     *
     * @param groupsRemoteDataSource the backend data source
     * @param groupsLocalDataSource  the device storage data source
     * @return the {@link GroupsRepository} instance
     */
    public static GroupsRepository getInstance(GroupsDataSource groupsRemoteDataSource,
                                               GroupsDataSource groupsLocalDataSource) {
        if (INSTANCE == null) {
            synchronized (GroupsRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new GroupsRepository(groupsRemoteDataSource, groupsLocalDataSource);
                }
            }
        }
        return INSTANCE;
    }

    /**
     * Used to force {@link #getInstance(GroupsDataSource, GroupsDataSource)} to create a new instance
     * next time it's called.
     */
    public static void destroyInstance() {
        INSTANCE = null;
    }

    /**
     * Gets tasks from cache, local data source (SQLite) or remote data source, whichever is
     * available first.
     * <p>
     * Note: {@link LoadGroupsCallback#onDataNotAvailable()} is fired if all data sources fail to
     * get the data.
     */
    @Override
    public void getGroups(@NonNull final LoadGroupsCallback callback) {
        checkNotNull(callback);

        // Respond immediately with cache if available and not dirty
        if (mCachedGroups != null && !mCacheIsDirty) {
            callback.onGroupsLoaded(new ArrayList<>(mCachedGroups.values()));
            return;
        }

        EspressoIdlingResource.increment(); // App is busy until further notice

        if (mCacheIsDirty) {
            // If the cache is dirty we need to fetch new data from the network.
            getGroupsFromRemoteDataSource(callback);
        } else {
            // Query the local storage if available. If not, query the network.
            mGroupsLocalDataSource.getGroups(new LoadGroupsCallback() {
                @Override
                public void onGroupsLoaded(List<Group> groups) {
                    refreshCache(groups);

                    EspressoIdlingResource.decrement(); // Set app as idle.
                    callback.onGroupsLoaded(new ArrayList<>(mCachedGroups.values()));
                }

                @Override
                public void onDataNotAvailable() {
                    getGroupsFromRemoteDataSource(callback);
                }
            });
        }
    }

    @Override
    public void saveGroup(@NonNull Group group) {
        checkNotNull(group);
        mGroupsRemoteDataSource.saveGroup(group);
        mGroupsLocalDataSource.saveGroup(group);

        // Do in memory cache update to keep the app UI up to date
        if (mCachedGroups == null) {
            mCachedGroups = new LinkedHashMap<>();
        }
        mCachedGroups.put(group.getId(), group);
    }

    /**
     * Gets tasks from local data source (sqlite) unless the table is new or empty. In that case it
     * uses the network data source. This is done to simplify the sample.
     * <p>
     * Note: {@link GetGroupCallback#onDataNotAvailable()} is fired if both data sources fail to
     * get the data.
     */
    @Override
    public void getGroup(@NonNull final Long groupId, @NonNull final GetGroupCallback callback) {
        checkNotNull(groupId);
        checkNotNull(callback);

        Group cachedGroup = getGroupWithId(groupId);

        // Respond immediately with cache if available
        if (cachedGroup != null) {
            callback.onGroupLoaded(cachedGroup);
            return;
        }

        EspressoIdlingResource.increment(); // App is busy until further notice

        // Load from server/persisted if needed.

        // Is the task in the local data source? If not, query the network.
        mGroupsLocalDataSource.getGroup(groupId, new GetGroupCallback() {
            @Override
            public void onGroupLoaded(Group group) {
                // Do in memory cache update to keep the app UI up to date
                if (mCachedGroups == null) {
                    mCachedGroups = new LinkedHashMap<>();
                }
                mCachedGroups.put(group.getId(), group);

                EspressoIdlingResource.decrement(); // Set app as idle.

                callback.onGroupLoaded(group);
            }

            @Override
            public void onDataNotAvailable() {
                mGroupsRemoteDataSource.getGroup(groupId, new GetGroupCallback() {
                    @Override
                    public void onGroupLoaded(Group group) {
                        if (group == null) {
                            onDataNotAvailable();
                            return;
                        }
                        // Do in memory cache update to keep the app UI up to date
                        if (mCachedGroups == null) {
                            mCachedGroups = new LinkedHashMap<>();
                        }
                        mCachedGroups.put(group.getId(), group);
                        EspressoIdlingResource.decrement(); // Set app as idle.

                        callback.onGroupLoaded(group);
                    }

                    @Override
                    public void onDataNotAvailable() {
                        EspressoIdlingResource.decrement(); // Set app as idle.

                        callback.onDataNotAvailable();
                    }
                });
            }
        });
    }

    @Override
    public void refreshGroups() {
        mCacheIsDirty = true;
    }


    @Override
    public void deleteGroup(@NonNull Long taskId) {
        mGroupsRemoteDataSource.deleteGroup(checkNotNull(taskId));
        mGroupsLocalDataSource.deleteGroup(checkNotNull(taskId));

        mCachedGroups.remove(taskId);
    }

    private void getGroupsFromRemoteDataSource(@NonNull final LoadGroupsCallback callback) {
        mGroupsRemoteDataSource.getGroups(new LoadGroupsCallback() {
            @Override
            public void onGroupsLoaded(List<Group> groups) {
                refreshCache(groups);
                refreshLocalDataSource(groups);

                EspressoIdlingResource.decrement(); // Set app as idle.
                callback.onGroupsLoaded(new ArrayList<>(mCachedGroups.values()));
            }

            @Override
            public void onDataNotAvailable() {

                EspressoIdlingResource.decrement(); // Set app as idle.
                callback.onDataNotAvailable();
            }
        });
    }

    private void refreshCache(List<Group> groups) {
        if (mCachedGroups == null) {
            mCachedGroups = new LinkedHashMap<>();
        }
        mCachedGroups.clear();
        for (Group group : groups) {
            mCachedGroups.put(group.getId(), group);
        }
        mCacheIsDirty = false;
    }

    private void refreshLocalDataSource(List<Group> groups) {
        mGroupsLocalDataSource.deleteAllGroups();
        for (Group group : groups) {
            mGroupsLocalDataSource.saveGroup(group);
        }
    }

    @Nullable
    private Group getGroupWithId(@NonNull Long id) {
        checkNotNull(id);
        if (mCachedGroups == null || mCachedGroups.isEmpty()) {
            return null;
        } else {
            return mCachedGroups.get(id);
        }
    }
    @Override
    public void deleteAllGroups() {
        mGroupsRemoteDataSource.deleteAllGroups();
        mGroupsLocalDataSource.deleteAllGroups();

        if (mCachedGroups == null) {
            mCachedGroups = new LinkedHashMap<>();
        }
        mCachedGroups.clear();
    }
}
