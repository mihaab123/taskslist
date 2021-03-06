package com.mihailovalex.reminder_room.ui.donetasks;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mihailovalex.reminder_room.MainActivity;
import com.mihailovalex.reminder_room.R;
import com.mihailovalex.reminder_room.ViewModelFactory;
import com.mihailovalex.reminder_room.adapter.CurrentTasksAdapter;
import com.mihailovalex.reminder_room.data.Item;
import com.mihailovalex.reminder_room.data.Task;
import com.mihailovalex.reminder_room.databinding.FragmentCurrentTasksBinding;
import com.mihailovalex.reminder_room.ui.TaskFragment;
import com.mihailovalex.reminder_room.ui.addedittask.AddEditTaskActivity;
import com.mihailovalex.reminder_room.ui.addedittask.AddEditTaskFragment;
import com.mihailovalex.reminder_room.ui.currenttasks.CurrentTasksViewModel;
import com.mihailovalex.reminder_room.ui.currenttasks.TasksFilterType;

import java.util.ArrayList;

public class DoneTasksFragment extends TaskFragment {

    private CurrentTasksViewModel currentTasksViewModel;

    private FragmentCurrentTasksBinding tasksFragBinding;
    private CurrentTasksAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // Use a Factory to inject dependencies into the ViewModel
        ViewModelFactory factory = ViewModelFactory.getInstance(getActivity().getApplication());

        currentTasksViewModel=
                ViewModelProviders.of(getActivity(), factory).get(CurrentTasksViewModel.class);
        currentTasksViewModel.setFiltering(TasksFilterType.COMPLETED_TASKS);
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
        // Subscribe to "search task" event
        currentTasksViewModel.searchString.setValue("");
        currentTasksViewModel.getSearchString().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String taskId) {
                //if (!taskId.isEmpty()) {
                currentTasksViewModel.loadTasks(true);
                //}
            }
        });
        setHasOptionsMenu(true); // It's important here
        return tasksFragBinding.getRoot();
    }
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()){
                    currentTasksViewModel.searchString.setValue("");
                } else {
                    currentTasksViewModel.searchString.setValue("%"+newText+"%");
                }
                return true;
            }
        });
    }
    public void openTaskDetails(long taskId) {
        Intent intent = new Intent(getActivity(), AddEditTaskActivity.class);
        intent.putExtra(AddEditTaskFragment.ARGUMENT_EDIT_TASK_ID, taskId);
        startActivityForResult(intent, AddEditTaskActivity.REQUEST_CODE);

    }
    public void addNewTask() {
        Intent intent = new Intent(getActivity(), AddEditTaskActivity.class);
        startActivityForResult(intent, AddEditTaskActivity.REQUEST_CODE);
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
        fab.setVisibility(View.GONE);
    }
}
