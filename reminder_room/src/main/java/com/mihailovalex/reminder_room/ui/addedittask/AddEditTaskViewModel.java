package com.mihailovalex.reminder_room.ui.addedittask;

import android.app.Application;
import android.content.SharedPreferences;

import androidx.annotation.Nullable;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.lifecycle.AndroidViewModel;
import androidx.preference.PreferenceManager;

import com.mihailovalex.reminder_room.SingleLiveEvent;
import com.mihailovalex.reminder_room.data.Task;
import com.mihailovalex.reminder_room.data.source.TasksDataSource;
import com.mihailovalex.reminder_room.data.source.TasksRepository;
import com.mihailovalex.reminder_room.utils.DateUtils;

import java.util.Calendar;

public class AddEditTaskViewModel extends AndroidViewModel implements TasksDataSource.GetTaskCallback{
    public final ObservableField<String> title = new ObservableField<>();

    public final ObservableField<String> date = new ObservableField<>();

    public final ObservableField<String> time = new ObservableField<>();

    public final ObservableField<Integer> priority = new ObservableField<>();

    public final ObservableBoolean dataLoading = new ObservableBoolean(false);

    //private final SnackbarMessage mSnackbarText = new SnackbarMessage();

    private final SingleLiveEvent<Void> mTaskUpdated = new SingleLiveEvent<>();

    private final TasksRepository mTasksRepository;


    public final Calendar dateAndTime = Calendar.getInstance();

    @Nullable
    private Long mTaskId;

    private boolean mIsNewTask;

    private boolean mIsDataLoaded = false;

    private boolean mTaskCompleted = false;
    private SharedPreferences sPref;

    public AddEditTaskViewModel(Application context,
                                TasksRepository tasksRepository) {
        super(context);
        mTasksRepository = tasksRepository;
        sPref = PreferenceManager.getDefaultSharedPreferences(context);
    }
    public boolean get24TimeFormat(){
        boolean time_format = sPref.getBoolean("time_format", false);
        return time_format;
    }
    public void start(Long taskId) {
        if (dataLoading.get()) {
            // Already loading, ignore.
            return;
        }
        mTaskId = taskId;
        if (taskId == 0) {
            // No need to populate, it's a new task
            mIsNewTask = true;
            return;
        }
        if (mIsDataLoaded) {
            // No need to populate, already have data.
            return;
        }
        mIsNewTask = false;
        dataLoading.set(true);

        mTasksRepository.getTask(taskId, this);
    }

    @Override
    public void onTaskLoaded(Task task) {
        title.set(task.getTitle());
        dateAndTime.setTimeInMillis(task.getDate());
        date.set(DateUtils.getDate(task.getDate()));
        time.set(DateUtils.getTime(task.getDate(),get24TimeFormat()));
        priority.set(task.getPriority());
        mTaskCompleted = task.isCompleted();
        dataLoading.set(false);
        mIsDataLoaded = true;

        // Note that there's no need to notify that the values changed because we're using
        // ObservableFields.
    }

    @Override
    public void onDataNotAvailable() {
        dataLoading.set(false);
    }

    // Called when clicking on fab.
    void saveTask() {
        Task task = new Task(title.get(), dateAndTime.getTimeInMillis(),priority.get());
        if (task.isEmpty()) {
            //mSnackbarText.setValue(R.string.empty_task_message);
            return;
        }
        if (isNewTask() || mTaskId == 0) {
            createTask(task);
        } else {
            task = new Task(mTaskId,title.get(), dateAndTime.getTimeInMillis(), priority.get(), mTaskCompleted);
            updateTask(task);
        }

    }

    /*SnackbarMessage getSnackbarMessage() {
        return mSnackbarText;
    }*/

    SingleLiveEvent<Void> getTaskUpdatedEvent() {
        return mTaskUpdated;
    }

    private boolean isNewTask() {
        return mIsNewTask;
    }

    private void createTask(Task newTask) {
        mTasksRepository.saveTask(newTask);
        mTaskUpdated.call();
    }

    private void updateTask(Task task) {
        if (isNewTask()) {
            throw new RuntimeException("updateTask() was called but task is new.");
        }
        mTasksRepository.saveTask(task);
        mTaskUpdated.call();
    }

}
