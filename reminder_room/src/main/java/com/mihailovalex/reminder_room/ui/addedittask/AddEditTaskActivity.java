package com.mihailovalex.reminder_room.ui.addedittask;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import com.mihailovalex.reminder_room.R;
import com.mihailovalex.reminder_room.ViewModelFactory;

public class AddEditTaskActivity extends AppCompatActivity implements AddEditTaskNavigator {
    public static final int REQUEST_CODE = 2;
    public static final int ADD_EDIT_RESULT_OK = RESULT_FIRST_USER + 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_edit_task_activity);

        // Set up the toolbar.
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        AddEditTaskFragment addEditTaskFragment = getAddEditTaskFragment();
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.addEditFrame, addEditTaskFragment)
                    .commitNow();
        }
        subscribeToNavigationChanges();
    }
    public static AddEditTaskViewModel obtainViewModel(FragmentActivity activity) {
        // Use a Factory to inject dependencies into the ViewModel
        ViewModelFactory factory = ViewModelFactory.getInstance(activity.getApplication());

        return ViewModelProviders.of(activity, factory).get(AddEditTaskViewModel.class);
    }
    private void subscribeToNavigationChanges() {
        AddEditTaskViewModel viewModel = obtainViewModel(this);

        // The activity observes the navigation events in the ViewModel
        viewModel.getTaskUpdatedEvent().observe(this, new Observer<Void>() {
            @Override
            public void onChanged(@Nullable Void _) {
                AddEditTaskActivity.this.onTaskSaved();
            }
        });
    }
    private AddEditTaskFragment getAddEditTaskFragment() {
        // View Fragment
        AddEditTaskFragment addEditTaskFragment = (AddEditTaskFragment) getSupportFragmentManager()
                .findFragmentById(R.id.addEditFrame);
        if (addEditTaskFragment == null) {
            addEditTaskFragment = AddEditTaskFragment.newInstance();

            // Send the task ID to the fragment
            Bundle bundle = new Bundle();
            bundle.putLong(AddEditTaskFragment.ARGUMENT_EDIT_TASK_ID,
                    getIntent().getLongExtra(AddEditTaskFragment.ARGUMENT_EDIT_TASK_ID,0));
            addEditTaskFragment.setArguments(bundle);
        }
        return addEditTaskFragment;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onTaskSaved() {
        setResult(ADD_EDIT_RESULT_OK);
        finish();
    }
}
