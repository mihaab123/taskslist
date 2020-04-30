package com.mihailovalex.taskslist_room.tasks;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.mihailovalex.taskslist_room.R;
import com.mihailovalex.taskslist_room.data.Task;

public class TasksActivity extends AppCompatActivity implements TaskItemNavigator,TaskItemUserActionsListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void openTaskDetails(Long taskId) {

    }

    @Override
    public void onCompleteChanged(Task task, View v) {

    }

    @Override
    public void onTaskClicked(Task task) {

    }
}
