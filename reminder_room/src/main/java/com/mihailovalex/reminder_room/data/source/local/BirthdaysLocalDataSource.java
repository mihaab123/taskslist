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

package com.mihailovalex.reminder_room.data.source.local;


import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;

import com.mihailovalex.reminder_room.data.Birthday;
import com.mihailovalex.reminder_room.data.Task;
import com.mihailovalex.reminder_room.data.source.BirthdaysDataSource;
import com.mihailovalex.reminder_room.utils.AppExecutors;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;


/**
 * Concrete implementation of a data source as a db.
 */
public class BirthdaysLocalDataSource implements BirthdaysDataSource {

    private static volatile BirthdaysLocalDataSource INSTANCE;

    private BirthdaysDao mBirthdaysDao;

    private AppExecutors mAppExecutors;

    // Prevent direct instantiation.
    private BirthdaysLocalDataSource(@NonNull AppExecutors appExecutors,
                                     @NonNull BirthdaysDao birthdaysDao) {
        mAppExecutors = appExecutors;
        mBirthdaysDao = birthdaysDao;
    }

    public static BirthdaysLocalDataSource getInstance(@NonNull AppExecutors appExecutors,
                                                       @NonNull BirthdaysDao birthdaysDao) {
        if (INSTANCE == null) {
            synchronized (BirthdaysLocalDataSource.class) {
                if (INSTANCE == null) {
                    INSTANCE = new BirthdaysLocalDataSource(appExecutors, birthdaysDao);
                }
            }
        }
        return INSTANCE;
    }


    @Override
    public void getBirthdays(@NonNull final LoadBirthdaysCallback callback, String searchString) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if(searchString.isEmpty()) {
                    final List<Birthday> birthdays = mBirthdaysDao.getBirthdays();
                    mAppExecutors.mainThread().execute(new Runnable() {
                        @Override
                        public void run() {
                            if (birthdays.isEmpty()) {
                                // This will be called if the table is new or just empty.
                                callback.onDataNotAvailable();
                            } else {
                                callback.onBirthdaysLoaded(birthdays);
                            }
                        }
                    });
                }else {
                    final List<Birthday> birthdays = mBirthdaysDao.getBirthdaysByTitle(searchString);
                    mAppExecutors.mainThread().execute(new Runnable() {
                        @Override
                        public void run() {
                            if (birthdays.isEmpty()) {
                                // This will be called if the table is new or just empty.
                                callback.onDataNotAvailable();
                            } else {
                                callback.onBirthdaysLoaded(birthdays);
                            }
                        }
                    });
                }

            }
        };

        mAppExecutors.diskIO().execute(runnable);
    }


    @Override
    public void getBirthday(@NonNull final long birthdayId, @NonNull final GetBirthdayCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final Birthday birthday = mBirthdaysDao.getBirthdayById(birthdayId);

                mAppExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (birthday != null) {
                            callback.onBirthdayLoaded(birthday);
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
    public void saveBirthday(@NonNull final Birthday birthday) {
        checkNotNull(birthday);
        Runnable saveRunnable = new Runnable() {
            @Override
            public void run() {
                mBirthdaysDao.insertBirthday(birthday);
            }
        };
        mAppExecutors.diskIO().execute(saveRunnable);
    }

    @Override
    public void completeBirthday(@NonNull final Birthday birthday) {
        Runnable completeRunnable = new Runnable() {
            @Override
            public void run() {
                mBirthdaysDao.updateCompleted(birthday.getId(), true);
            }
        };

        mAppExecutors.diskIO().execute(completeRunnable);
    }

    @Override
    public void completeBirthday(@NonNull long birthdayId) {
        // Not required for the local data source because the {@link TasksRepository} handles
        // converting from a {@code taskId} to a {@link task} using its cached data.
    }

    @Override
    public void activateBirthday(@NonNull final Birthday birthday) {
        Runnable activateRunnable = new Runnable() {
            @Override
            public void run() {
                mBirthdaysDao.updateCompleted(birthday.getId(), false);
            }
        };
        mAppExecutors.diskIO().execute(activateRunnable);
    }

    @Override
    public void activateBirthday(@NonNull long birthdayId) {
        // Not required for the local data source because the {@link TasksRepository} handles
        // converting from a {@code taskId} to a {@link task} using its cached data.
    }



    @Override
    public void refreshBirthdays() {
        // Not required because the {@link TasksRepository} handles the logic of refreshing the
        // tasks from all the available data sources.
    }

    @Override
    public void deleteAllBirthdays() {
        /*Runnable deleteRunnable = new Runnable() {
            @Override
            public void run() {
                mBirthdaysDao.deleteTasks();
            }
        };

        mAppExecutors.diskIO().execute(deleteRunnable);*/
    }


    @Override
    public void deleteBirthday(@NonNull final long birthdayId) {
        Runnable deleteRunnable = new Runnable() {
            @Override
            public void run() {
                mBirthdaysDao.deleteBirthdayById(birthdayId);
            }
        };

        mAppExecutors.diskIO().execute(deleteRunnable);
    }

    @VisibleForTesting
    static void clearInstance() {
        INSTANCE = null;
    }
}
