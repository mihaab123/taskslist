package com.mihailovalex.reminder_room.ui.currenttasks;

import android.app.Application;
import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableList;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.mihailovalex.reminder_room.R;
import com.mihailovalex.reminder_room.SingleLiveEvent;
import com.mihailovalex.reminder_room.data.Item;
import com.mihailovalex.reminder_room.data.Task;
import com.mihailovalex.reminder_room.data.source.TasksDataSource;
import com.mihailovalex.reminder_room.data.source.TasksRepository;

import java.util.ArrayList;
import java.util.List;

public class CurrentTasksViewModel extends AndroidViewModel {


    // These observable fields will update Views automatically
    public final ObservableList<Item> items = new ObservableArrayList<>();

    public final ObservableField<String> noTasksLabel = new ObservableField<>();

    public final ObservableField<Drawable> noTaskIconRes = new ObservableField<>();

    public final ObservableBoolean empty = new ObservableBoolean(false);

    public final ObservableBoolean tasksAddViewVisible = new ObservableBoolean();

    //private final SnackbarMessage mSnackbarText = new SnackbarMessage();


    private final TasksRepository mTasksRepository;

    private final ObservableBoolean mIsDataLoadingError = new ObservableBoolean(false);

    private final SingleLiveEvent<Long> mOpenTaskEvent = new SingleLiveEvent<>();

    private final Context mContext; // To avoid leaks, this must be an Application Context.

    private final SingleLiveEvent<Void> mNewTaskEvent = new SingleLiveEvent<>();

    public CurrentTasksViewModel(
            Application context,
            TasksRepository repository) {
        super(context);
        mContext = context.getApplicationContext(); // Force use of Application Context.
        mTasksRepository = repository;

        // Set initial state
        setFiltering();
    }



    public void start() {
        loadTasks(false);
    }

    public void loadTasks(boolean forceUpdate) {
        loadTasks(forceUpdate, true);
    }


    public void setFiltering() {

                noTasksLabel.set(mContext.getResources().getString(R.string.no_tasks_all));
                noTaskIconRes.set(mContext.getResources().getDrawable(
                        R.drawable.ic_assignment_turned_in_24dp));
                tasksAddViewVisible.set(false);

    }

    public void clearCompletedTasks() {
        mTasksRepository.clearCompletedTasks();
        //mSnackbarText.setValue(R.string.completed_tasks_cleared);
        loadTasks(false, false);
    }

    public void completeTask(Task task, boolean completed) {
        // Notify repository
        if (completed) {
            mTasksRepository.completeTask(task);
            //showSnackbarMessage(R.string.task_marked_complete);
        } else {
            mTasksRepository.activateTask(task);
            //showSnackbarMessage(R.string.task_marked_active);
        }
    }

    /*SnackbarMessage getSnackbarMessage() {
        return mSnackbarText;
    }
    */
    public SingleLiveEvent<Long> getOpenTaskEvent() {
        return mOpenTaskEvent;
    }

    public SingleLiveEvent<Void> getNewTaskEvent() {
        return mNewTaskEvent;
    }
    /*
    private void showSnackbarMessage(Integer message) {
        mSnackbarText.setValue(message);
    }*/


    public void addNewTask() {
        mNewTaskEvent.call();
    }

    void handleActivityResult(int requestCode, int resultCode) {
       /* if (AddEditTaskActivity.REQUEST_CODE == requestCode) {
            switch (resultCode) {
                case TaskDetailActivity.EDIT_RESULT_OK:
                    mSnackbarText.setValue(R.string.successfully_saved_task_message);
                    break;
                case AddEditTaskActivity.ADD_EDIT_RESULT_OK:
                    mSnackbarText.setValue(R.string.successfully_added_task_message);
                    break;
                case TaskDetailActivity.DELETE_RESULT_OK:
                    mSnackbarText.setValue(R.string.successfully_deleted_task_message);
                    break;
            }
        }*/
    }


    private void loadTasks(boolean forceUpdate, final boolean showLoadingUI) {
        /*if (showLoadingUI) {
            dataLoading.set(true);
        }
        if (forceUpdate) {
        */
            mTasksRepository.refreshTasks();
        //}

        mTasksRepository.getTasks(new TasksDataSource.LoadTasksCallback() {
            @Override
            public void onTasksLoaded(List<Task> tasks) {
                List<Item> tasksToShow = new ArrayList<>();

                // We filter the tasks based on the requestType
                for (Task task : tasks) {
                    //if (task.isActive()) {
                        tasksToShow.add(task);
                    //}
                }
                /*if (showLoadingUI) {
                    dataLoading.set(false);
                }*/
                mIsDataLoadingError.set(false);

                items.clear();
                items.addAll(tasksToShow);
                empty.set(items.isEmpty());
            }

            @Override
            public void onDataNotAvailable() {
                mIsDataLoadingError.set(true);
            }
        });
    }
}