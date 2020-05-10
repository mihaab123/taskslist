package com.mihailovalex.reminder_room.ui.addedittask;


import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TimePicker;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mihailovalex.reminder_room.R;
import com.mihailovalex.reminder_room.data.Task;
import com.mihailovalex.reminder_room.databinding.AddEditTaskActivityBinding;
import com.mihailovalex.reminder_room.databinding.AddEditTaskFragmentBinding;
import com.mihailovalex.reminder_room.utils.DateUtils;

import java.util.Calendar;


public class AddEditTaskFragment extends Fragment {


    public static final String ARGUMENT_EDIT_TASK_ID = "EDIT_TASK_ID";

    private AddEditTaskViewModel mViewModel;

    private AddEditTaskFragmentBinding mViewDataBinding;
    private SharedPreferences sPref;


    public static AddEditTaskFragment newInstance() {
        return new AddEditTaskFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        final View root = inflater.inflate(R.layout.add_edit_task_fragment, container, false);
        if (mViewDataBinding == null) {
            mViewDataBinding = AddEditTaskFragmentBinding.bind(root);
        }
        mViewModel = AddEditTaskActivity.obtainViewModel(getActivity());
        mViewDataBinding = AddEditTaskFragmentBinding.inflate(inflater, container, false);
        mViewDataBinding.setViewmodel(mViewModel);
        //setupListAdapter(tasksFragBinding);

        setHasOptionsMenu(true);
        setRetainInstance(false);
        sPref = PreferenceManager.getDefaultSharedPreferences(mViewModel.getApplication());
        String fontText = sPref.getString("font", "middle");

        return mViewDataBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setupFab();

        setupSnackbar();

        setupActionBar();

        loadData();

        setupPriorityAdapter();

        setupDateTime();

    }

    private void setupDateTime() {
        // установка обработчика выбора времени
        final TimePickerDialog.OnTimeSetListener t=new TimePickerDialog.OnTimeSetListener() {
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                //dateAndTime.set(0,0,0,hourOfDay,minute,0);
                mViewModel.dateAndTime.set(Calendar.HOUR,hourOfDay);
                mViewModel.dateAndTime.set(Calendar.MINUTE,minute);
                mViewModel.dateAndTime.set(Calendar.SECOND,0);
                mViewModel.time.set(DateUtils.getTime(mViewModel.dateAndTime.getTimeInMillis(),mViewModel.get24TimeFormat()));
            }
        };

        // установка обработчика выбора даты
        final DatePickerDialog.OnDateSetListener d=new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                mViewModel.dateAndTime.set(year,monthOfYear,dayOfMonth);
                mViewModel.date.set(DateUtils.getDate(mViewModel.dateAndTime.getTimeInMillis()));
            }
        };
        mViewDataBinding.etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mViewDataBinding.etDate.length()==0){
                    mViewDataBinding.etDate.setText(" ");
                }
                new DatePickerDialog(getActivity(), d,
                        mViewModel.dateAndTime.get(Calendar.YEAR),
                        mViewModel.dateAndTime.get(Calendar.MONTH),
                        mViewModel.dateAndTime.get(Calendar.DAY_OF_MONTH))
                        .show();

            }
        });
        mViewDataBinding.etTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mViewDataBinding.etTime.length()==0){
                    mViewDataBinding.etTime.setText(" ");
                }
                new TimePickerDialog(getActivity(),t,
                        mViewModel.dateAndTime.get(Calendar.HOUR),
                        mViewModel.dateAndTime.get(Calendar.MINUTE),
                        mViewModel.get24TimeFormat())
                        //DateFormat.is24HourFormat(getActivity()))
                        .show();
            }
        });
    }


    private void setupPriorityAdapter() {
        Spinner spPriprity = mViewDataBinding.spDialogTaskPriority;
        ArrayAdapter<String> priorityAdapter = new ArrayAdapter<String>(getActivity(),R.layout.support_simple_spinner_dropdown_item, Task.PRIORITY_LEVELS);
        spPriprity.setAdapter(priorityAdapter);
        spPriprity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    mViewModel.priority.set(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        if (mViewModel.priority.get()!=null){
            spPriprity.setSelection(mViewModel.priority.get());
        } else spPriprity.setSelection(0);
    }

    private void loadData() {
        // Add or edit an existing task?
        if (getArguments() != null) {
            mViewModel.start(getArguments().getLong(ARGUMENT_EDIT_TASK_ID));
        } else {
            mViewModel.start(0l);
        }
    }
    private void setupSnackbar() {
        /*mViewModel.getSnackbarMessage().observe(this, new SnackbarMessage.SnackbarObserver() {
            @Override
            public void onNewMessage(@StringRes int snackbarMessageResourceId) {
                SnackbarUtils.showSnackbar(getView(), getString(snackbarMessageResourceId));
            }
        });*/
    }

    private void setupFab() {
        FloatingActionButton fab = getActivity().findViewById(R.id.fab_edit_task_done);
        //fab.setImageResource(R.drawable.ic_done);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewModel.saveTask();
            }
        });
    }

    private void setupActionBar() {
        ActionBar actionBar =  ((AppCompatActivity)getActivity()).getSupportActionBar();
        if (actionBar == null) {
            return;
        }
        if (getArguments() != null && getArguments().get(ARGUMENT_EDIT_TASK_ID) != null && getArguments().getLong(ARGUMENT_EDIT_TASK_ID) != 0l) {
            actionBar.setTitle(R.string.edit_task);
        } else {
            actionBar.setTitle(R.string.add_task);
        }
    }

}
