package com.mihailovalex.reminder.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.mihailovalex.reminder.MainActivity;
import com.mihailovalex.reminder.R;
import com.mihailovalex.reminder.adapter.CurrentTasksAdapter;
import com.mihailovalex.reminder.adapter.TaskAdapter;
import com.mihailovalex.reminder.alarm.AlarmHelper;
import com.mihailovalex.reminder.dialog.EditTaskDialogFragment;
import com.mihailovalex.reminder.model.Item;
import com.mihailovalex.reminder.model.ModelTask;

public abstract class TaskFragment extends Fragment {
    protected RecyclerView recyclerView;
    protected RecyclerView.LayoutManager layoutManager;
    protected TaskAdapter adapter;
    public MainActivity activity;
    public AlarmHelper alarmHelper;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(getActivity()!=null){
            activity = (MainActivity) getActivity();
        }
        alarmHelper = AlarmHelper.getInstance();
        addTaskFromDB();
    }

    public abstract void addTask(ModelTask newTask, boolean saveToDB);

    public void updateTask(ModelTask updateTask){
        adapter.updateTask(updateTask);
    }

    public void removeTaskDialog(final int location){
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        Item item = adapter.getItem(location);
        if(item.isTask()){
            ModelTask removingTask = (ModelTask)item;
            final long timestamp = removingTask.getTimeStamp();
            final boolean[] isRemoved = {false};
            builder.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    adapter.removeItem(location);
                    isRemoved[0] = true;
                    Snackbar snackbar = Snackbar.make(getActivity().findViewById(R.id.coordinator),
                            R.string.removed, Snackbar.LENGTH_LONG);
                    snackbar.setAction(R.string.dialog_cancel, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            addTask(activity.dbHelper.query().getTask(timestamp),false);
                            isRemoved[0] =false;
                        }
                    });
                    snackbar.getView().addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
                        @Override
                        public void onViewAttachedToWindow(View v) {

                        }

                        @Override
                        public void onViewDetachedFromWindow(View v) {
                            if(isRemoved[0]){
                                activity.dbHelper.deleteTask(timestamp);
                                alarmHelper.removeAlarm(timestamp);
                            }
                        }
                    });
                    snackbar.show();
                    dialog.dismiss();
                }
            });
            builder.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
        }
        builder.setMessage(R.string.dialog_removing_mess);
        builder.show();
    }
    public void showEditTaskDialog(ModelTask task){
        DialogFragment dialogFragment = EditTaskDialogFragment.newInstance(task);
        dialogFragment.show(getActivity().getSupportFragmentManager(),"EditTaskDialogFragment");
    }
    public abstract void moveTask(ModelTask task);
    public abstract void addTaskFromDB();
    public abstract void checkAdapter();
    public abstract void findTasks(String title);

}
