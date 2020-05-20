package com.mihailovalex.reminder_room.ui.addeditbirthday;


import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mihailovalex.reminder_room.R;
import com.mihailovalex.reminder_room.SnackbarMessage;
import com.mihailovalex.reminder_room.data.Task;
import com.mihailovalex.reminder_room.databinding.AddEditBirthdayFragmentBinding;
import com.mihailovalex.reminder_room.databinding.AddEditTaskFragmentBinding;
import com.mihailovalex.reminder_room.ui.addedittask.AddEditTaskActivity;
import com.mihailovalex.reminder_room.ui.addedittask.AddEditTaskViewModel;
import com.mihailovalex.reminder_room.utils.DateUtils;
import com.mihailovalex.reminder_room.utils.SnackbarUtils;

import java.util.Calendar;


public class AddEditBirthdayFragment extends Fragment {


    public static final String ARGUMENT_EDIT_TASK_ID = "EDIT_TASK_ID";

    private AddEditBirthdayViewModel mViewModel;

    private AddEditBirthdayFragmentBinding mViewDataBinding;
    private SharedPreferences sPref;


    public static AddEditBirthdayFragment newInstance() {
        return new AddEditBirthdayFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        final View root = inflater.inflate(R.layout.add_edit_birthday_fragment, container, false);
        if (mViewDataBinding == null) {
            mViewDataBinding = AddEditBirthdayFragmentBinding.bind(root);
        }
        mViewModel = AddEditBirthdayActivity.obtainViewModel(getActivity());
        mViewDataBinding = AddEditBirthdayFragmentBinding.inflate(inflater, container, false);
        mViewDataBinding.setViewmodel(mViewModel);

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

        setupDateTime();

    }

    private void setupDateTime() {
        // установка обработчика выбора времени
        final TimePickerDialog.OnTimeSetListener t=new TimePickerDialog.OnTimeSetListener() {
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                //dateAndTime.set(0,0,0,hourOfDay,minute,0);
                mViewModel.dateAndTime.set(Calendar.HOUR_OF_DAY,hourOfDay);
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



    private void loadData() {
        // Add or edit an existing task?
        if (getArguments() != null) {
            mViewModel.start(getArguments().getLong(ARGUMENT_EDIT_TASK_ID));
        } else {
            mViewModel.start(0l);
        }
    }
    private void setupSnackbar() {
        mViewModel.getSnackbarMessage().observe(this, new SnackbarMessage.SnackbarObserver() {
            @Override
            public void onNewMessage(@StringRes int snackbarMessageResourceId) {
                SnackbarUtils.showSnackbar(getView(), getString(snackbarMessageResourceId));
            }
        });
    }

    private void setupFab() {
        FloatingActionButton fab = getActivity().findViewById(R.id.fab_edit_task_done);
        //fab.setImageResource(R.drawable.ic_done);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewModel.saveBirthday();
            }
        });
    }

    private void setupActionBar() {
        ActionBar actionBar =  ((AppCompatActivity)getActivity()).getSupportActionBar();
        if (actionBar == null) {
            return;
        }
        if (getArguments() != null && getArguments().get(ARGUMENT_EDIT_TASK_ID) != null && getArguments().getLong(ARGUMENT_EDIT_TASK_ID) != 0l) {
            actionBar.setTitle(R.string.edit_birthday);
        } else {
            actionBar.setTitle(R.string.add_birthday);
        }
    }

}
