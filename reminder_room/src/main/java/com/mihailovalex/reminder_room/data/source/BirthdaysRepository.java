

package com.mihailovalex.reminder_room.data.source;



import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.mihailovalex.reminder_room.alarm.AlarmHelper;
import com.mihailovalex.reminder_room.data.Birthday;
import com.mihailovalex.reminder_room.data.Task;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public class BirthdaysRepository implements BirthdaysDataSource {

    private volatile static BirthdaysRepository INSTANCE = null;

    //private final TasksDataSource mTasksRemoteDataSource;

    private final BirthdaysDataSource mBirthdaysLocalDataSource;


    Map<Long, Birthday> mCacheBirthdays;


    private boolean mCacheIsDirty = false;
    private static AlarmHelper alarmHelper;


    private BirthdaysRepository(@NonNull BirthdaysDataSource birthdaysDataSource) {
        mBirthdaysLocalDataSource = checkNotNull(birthdaysDataSource);
    }

    public static BirthdaysRepository getInstance(BirthdaysDataSource birthdaysLocalDataSource) {
        if (INSTANCE == null) {
            synchronized (BirthdaysRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new BirthdaysRepository(birthdaysLocalDataSource);
                }
            }
        }
        alarmHelper = AlarmHelper.getInstance();
        return INSTANCE;
    }


    public static void destroyInstance() {
        INSTANCE = null;
    }


    @Override
    public void getBirthdays(@NonNull final LoadBirthdaysCallback callback, String searchString) {
        checkNotNull(callback);

        // Respond immediately with cache if available and not dirty
        /*if (mCachedTasks != null && !mCacheIsDirty) {
            callback.onTasksLoaded(new ArrayList<>(mCachedTasks.values()));
            return;
        }*/

        //EspressoIdlingResource.increment(); // App is busy until further notice

        if (mCacheIsDirty) {
            // If the cache is dirty we need to fetch new data from the network.
            getBirthdaysFromRemoteDataSource(callback);
        } else {
            // Query the local storage if available. If not, query the network.
            mBirthdaysLocalDataSource.getBirthdays(new LoadBirthdaysCallback() {
                @Override
                public void onBirthdaysLoaded(List<Birthday> birthdays) {
                    refreshCache(birthdays);

                    //EspressoIdlingResource.decrement(); // Set app as idle.
                    callback.onBirthdaysLoaded(new ArrayList<>(mCacheBirthdays.values()));
                }

                @Override
                public void onDataNotAvailable() {
                    // Do in memory cache update to keep the app UI up to date
                    if (mCacheBirthdays == null) {
                        mCacheBirthdays = new LinkedHashMap<>();
                    }
                    callback.onBirthdaysLoaded(new ArrayList<>(mCacheBirthdays.values()));
                    //getBirthdaysFromRemoteDataSource(callback);
                }
            },searchString);
        }
    }

    @Override
    public void saveBirthday(@NonNull Birthday birthday) {
        checkNotNull(birthday);
        //mTasksRemoteDataSource.saveTask(task);
        mBirthdaysLocalDataSource.saveBirthday(birthday);

        // Do in memory cache update to keep the app UI up to date
        if (mCacheBirthdays == null) {
            mCacheBirthdays = new LinkedHashMap<>();
        }
        mCacheBirthdays.put(birthday.getId(), birthday);
        if(birthday.isActive()) {
            alarmHelper.setAlarm(birthday);
        } else {
            alarmHelper.removeAlarm(birthday.getId());
        }
    }

    @Override
    public void completeBirthday(@NonNull Birthday birthday) {
        checkNotNull(birthday);
        //mTasksRemoteDataSource.completeTask(task);
        mBirthdaysLocalDataSource.completeBirthday(birthday);

        Birthday completedBirthday = new Birthday(birthday.getId(), birthday.getTitle(), birthday.getDate(), birthday.getComment(),true);

        // Do in memory cache update to keep the app UI up to date
        if (mCacheBirthdays == null) {
            mCacheBirthdays = new LinkedHashMap<>();
        }
        mCacheBirthdays.put(birthday.getId(), completedBirthday);
        alarmHelper.removeAlarm(birthday.getId());
    }

    @Override
    public void completeBirthday(@NonNull long birthdayId) {
        checkNotNull(birthdayId);
        completeBirthday(getBirthdayWithId(birthdayId));
    }

    @Override
    public void activateBirthday(@NonNull Birthday birthday) {
        checkNotNull(birthday);
        //mTasksRemoteDataSource.activateTask(task);
        mBirthdaysLocalDataSource.activateBirthday(birthday);

        Birthday activeTask = new Birthday(birthday.getTitle(), birthday.getDate(),birthday.getComment());

        // Do in memory cache update to keep the app UI up to date
        if (mCacheBirthdays == null) {
            mCacheBirthdays = new LinkedHashMap<>();
        }
        mCacheBirthdays.put(birthday.getId(), activeTask);
        alarmHelper.setAlarm(birthday);
    }

    @Override
    public void activateBirthday(@NonNull long birthdayId) {
        checkNotNull(birthdayId);
        activateBirthday(getBirthdayWithId(birthdayId));
    }

    @Override
    public void getBirthday(@NonNull final long birthdayId, @NonNull final GetBirthdayCallback callback) {
        checkNotNull(birthdayId);
        checkNotNull(callback);

        Birthday cachedBirthday = getBirthdayWithId(birthdayId);

        // Respond immediately with cache if available
        if (cachedBirthday != null) {
            callback.onBirthdayLoaded(cachedBirthday);
            return;
        }

        //EspressoIdlingResource.increment(); // App is busy until further notice

        // Load from server/persisted if needed.

        // Is the task in the local data source? If not, query the network.
        mBirthdaysLocalDataSource.getBirthday(birthdayId, new GetBirthdayCallback() {
            @Override
            public void onBirthdayLoaded(Birthday birthday) {
                // Do in memory cache update to keep the app UI up to date
                if (mCacheBirthdays == null) {
                    mCacheBirthdays = new LinkedHashMap<>();
                }
                mCacheBirthdays.put(birthday.getId(), birthday);

                //EspressoIdlingResource.decrement(); // Set app as idle.

                callback.onBirthdayLoaded(birthday);
            }

            @Override
            public void onDataNotAvailable() {
                /*mTasksRemoteDataSource.getTask(birthdayId, new GetTaskCallback() {
                    @Override
                    public void onTaskLoaded(Task task) {
                        if (task == null) {
                            onDataNotAvailable();
                            return;
                        }
                        // Do in memory cache update to keep the app UI up to date
                        if (mCachedTasks == null) {
                            mCachedTasks = new LinkedHashMap<>();
                        }
                        mCachedTasks.put(task.getId(), task);
                        //EspressoIdlingResource.decrement(); // Set app as idle.

                        callback.onTaskLoaded(task);
                    }

                    @Override
                    public void onDataNotAvailable() {
                        //EspressoIdlingResource.decrement(); // Set app as idle.

                        callback.onDataNotAvailable();
                    }
                });*/
            }
        });
    }

    @Override
    public void refreshBirthdays() {
        //mCacheIsDirty = true;
    }

    @Override
    public void deleteAllBirthdays() {
        //mTasksRemoteDataSource.deleteAllTasks();
        mBirthdaysLocalDataSource.deleteAllBirthdays();

        if (mCacheBirthdays == null) {
            mCacheBirthdays = new LinkedHashMap<>();
        }
        mCacheBirthdays.clear();
    }

    @Override
    public void deleteBirthday(@NonNull long birthdayId) {
        //mTasksRemoteDataSource.deleteTask(checkNotNull(birthdayId));
        mBirthdaysLocalDataSource.deleteBirthday(checkNotNull(birthdayId));

        mCacheBirthdays.remove(birthdayId);
        alarmHelper.removeAlarm(birthdayId);
    }

    private void getBirthdaysFromRemoteDataSource(@NonNull final LoadBirthdaysCallback callback) {
        /*mTasksRemoteDataSource.getTasks(new LoadTasksCallback() {
            @Override
            public void onTasksLoaded(List<Task> tasks) {
                refreshCache(tasks);
                refreshLocalDataSource(tasks);

                //EspressoIdlingResource.decrement(); // Set app as idle.
                callback.onTasksLoaded(new ArrayList<>(mCachedTasks.values()));
            }

            @Override
            public void onDataNotAvailable() {

                //EspressoIdlingResource.decrement(); // Set app as idle.
                callback.onDataNotAvailable();
            }
        });*/
    }

    private void refreshCache(List<Birthday> birthdays) {
        if (mCacheBirthdays == null) {
            mCacheBirthdays = new LinkedHashMap<>();
        }
        mCacheBirthdays.clear();
        for (Birthday task : birthdays) {
            mCacheBirthdays.put(task.getId(), task);
        }
        mCacheIsDirty = false;
    }

    private void refreshLocalDataSource(List<Birthday> birthdays) {
        mBirthdaysLocalDataSource.deleteAllBirthdays();
        for (Birthday birthday : birthdays) {
            mBirthdaysLocalDataSource.saveBirthday(birthday);
        }
    }

    @Nullable
    private Birthday getBirthdayWithId(@NonNull long id) {
        checkNotNull(id);
        if (mCacheBirthdays == null || mCacheBirthdays.isEmpty()) {
            return null;
        } else {
            return mCacheBirthdays.get(id);
        }
    }
}
