package com.mihailovalex.reminder_room.ui.currenttasks;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mihailovalex.reminder_room.R;
import com.mihailovalex.reminder_room.ViewModelFactory;
import com.mihailovalex.reminder_room.adapter.CurrentTasksAdapter;
import com.mihailovalex.reminder_room.data.Item;
import com.mihailovalex.reminder_room.data.Task;
import com.mihailovalex.reminder_room.databinding.FragmentCurrentTasksBinding;
import com.mihailovalex.reminder_room.ui.TaskFragment;

import java.util.ArrayList;

public class CurrentTasksFragment extends TaskFragment {

    private CurrentTasksViewModel currentTasksViewModel;

    private FragmentCurrentTasksBinding tasksFragBinding;
    private CurrentTasksAdapter adapter;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        // Use a Factory to inject dependencies into the ViewModel
        ViewModelFactory factory = ViewModelFactory.getInstance(getActivity().getApplication());

        currentTasksViewModel=
                ViewModelProviders.of(getActivity(), factory).get(CurrentTasksViewModel.class);
        tasksFragBinding = FragmentCurrentTasksBinding.inflate(inflater, container, false);
        tasksFragBinding.setViewmodel(currentTasksViewModel);
        //setupListAdapter(tasksFragBinding);

        // Subscribe to "open task" event
        currentTasksViewModel.getOpenTaskEvent().observe(this, new Observer<Long>() {
            @Override
            public void onChanged(@Nullable Long taskId) {
                if (taskId != null) {
                    openTaskDetails(taskId);
                }
            }
        });

        // Subscribe to "new task" event
        currentTasksViewModel.getNewTaskEvent().observe(this, new Observer<Void>() {
            @Override
            public void onChanged(@Nullable Void _) {
                addNewTask();
            }
        });
        return tasksFragBinding.getRoot();
    }

    public void openTaskDetails(long taskId) {
        /*Intent intent = new Intent(this, TaskDetailActivity.class);
        intent.putExtra(TaskDetailActivity.EXTRA_TASK_ID, taskId);
        startActivityForResult(intent, AddEditTaskActivity.REQUEST_CODE);*/

    }
    public void addNewTask() {
       // Intent intent = new Intent(this, AddEditTaskActivity.class);
       // startActivityForResult(intent, AddEditTaskActivity.REQUEST_CODE);
    }
    @Override
    public void addTask(Task newTask, boolean saveToDB) {

    }

    @Override
    public void moveTask(Task task) {

    }

    @Override
    public void addTaskFromDB() {

    }

    @Override
    public void checkAdapter() {

    }

    @Override
    public void findTasks(String title) {

    }

    @Override
    public void onResume() {
        super.onResume();
        currentTasksViewModel.start();
    }

    private void setupListAdapter() {
        recyclerView = tasksFragBinding.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new CurrentTasksAdapter(
                new ArrayList<Item>(0),
                currentTasksViewModel
        );
        recyclerView.setAdapter(adapter);
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //setupSnackbar();

        setupFab();

        setupListAdapter();

        //setupRefreshLayout();
    }
    private void setupFab() {
        FloatingActionButton fab =
                (FloatingActionButton) getActivity().findViewById(R.id.fab);

        fab.setImageResource(R.drawable.ic_add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentTasksViewModel.addNewTask();
            }
        });
    }
}
