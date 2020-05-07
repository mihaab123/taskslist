package com.mihailovalex.reminder_room.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.mihailovalex.reminder_room.MainActivity;
import com.mihailovalex.reminder_room.R;
import com.mihailovalex.reminder_room.adapter.TaskAdapter;
import com.mihailovalex.reminder_room.data.Item;
import com.mihailovalex.reminder_room.data.Task;

public abstract class TaskFragment extends Fragment {
    protected RecyclerView recyclerView;
    protected RecyclerView.LayoutManager layoutManager;
    protected TaskAdapter adapter;
    public MainActivity activity;
    //public AlarmHelper alarmHelper;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(getActivity()!=null){
            activity = (MainActivity) getActivity();
        }
        //alarmHelper = AlarmHelper.getInstance();
        addTaskFromDB();
    }

    public abstract void addTask(Task newTask, boolean saveToDB);

    public void updateTask(Task updateTask){
        adapter.updateTask(updateTask);
    }

    public void removeTaskDialog(final int location){
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        Item item = adapter.getItem(location);
        if(item.isTask()){
            Task removingTask = (Task)item;
            final long taksId = removingTask.getId();
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
                            //addTask(activity.dbHelper.query().getTask(taksId),false);
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
                                //activity.dbHelper.deleteTask(taksId);
                                //alarmHelper.removeAlarm(taksId);
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
    public void showEditTaskDialog(Task task){
        //DialogFragment dialogFragment = EditTaskDialogFragment.newInstance(task);
        //dialogFragment.show(getActivity().getSupportFragmentManager(),"EditTaskDialogFragment");
    }
    public abstract void moveTask(Task task);
    public abstract void addTaskFromDB();
    public abstract void checkAdapter();
    public abstract void findTasks(String title);

}
