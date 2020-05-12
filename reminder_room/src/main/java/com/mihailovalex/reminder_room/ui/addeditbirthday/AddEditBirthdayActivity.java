package com.mihailovalex.reminder_room.ui.addeditbirthday;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.mihailovalex.reminder_room.R;
import com.mihailovalex.reminder_room.ViewModelFactory;
import com.mihailovalex.reminder_room.ui.addedittask.AddEditTaskFragment;

public class AddEditBirthdayActivity extends AppCompatActivity implements AddEditBirthdayNavigator {
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
        AddEditBirthdayFragment addEditBirthdayFragment = getAddEditBirthdayFragment();
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.addEditFrame, addEditBirthdayFragment)
                    .commitNow();
        }
        subscribeToNavigationChanges();
    }
    public static AddEditBirthdayViewModel obtainViewModel(FragmentActivity activity) {
        // Use a Factory to inject dependencies into the ViewModel
        ViewModelFactory factory = ViewModelFactory.getInstance(activity.getApplication());

        return ViewModelProviders.of(activity, factory).get(AddEditBirthdayViewModel.class);
    }
    private void subscribeToNavigationChanges() {
        AddEditBirthdayViewModel viewModel = obtainViewModel(this);

        // The activity observes the navigation events in the ViewModel
        viewModel.getBirthdayUpdatedEvent().observe(this, new Observer<Void>() {
            @Override
            public void onChanged(@Nullable Void _) {
                AddEditBirthdayActivity.this.onBirthdaySaved();
            }
        });
    }
    private AddEditBirthdayFragment getAddEditBirthdayFragment() {
        // View Fragment
        AddEditBirthdayFragment addEditBirthdayFragment = (AddEditBirthdayFragment) getSupportFragmentManager()
                .findFragmentById(R.id.addEditFrame);
        if (addEditBirthdayFragment == null) {
            addEditBirthdayFragment = AddEditBirthdayFragment.newInstance();

            // Send the task ID to the fragment
            Bundle bundle = new Bundle();
            bundle.putLong(AddEditBirthdayFragment.ARGUMENT_EDIT_TASK_ID,
                    getIntent().getLongExtra(AddEditBirthdayFragment.ARGUMENT_EDIT_TASK_ID,0));
            addEditBirthdayFragment.setArguments(bundle);
        }
        return addEditBirthdayFragment;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBirthdaySaved() {
        setResult(ADD_EDIT_RESULT_OK);
        finish();
    }
}
