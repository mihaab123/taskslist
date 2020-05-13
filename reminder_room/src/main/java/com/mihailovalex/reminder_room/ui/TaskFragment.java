package com.mihailovalex.reminder_room.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
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
    protected TaskAdapter adapter;
    public MainActivity activity;
    protected SearchView searchView;

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

    public abstract void moveTask(Task task);
    public abstract void addTaskFromDB();
    public abstract void checkAdapter();
    public abstract void findTasks(String title);

}
