package com.mihailovalex.reminder.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.textfield.TextInputLayout;
import com.mihailovalex.reminder.R;
import com.mihailovalex.reminder.Utils;
import com.mihailovalex.reminder.model.ModelTask;

import java.util.Calendar;

public class AddTaskDialogFragment extends DialogFragment {
    private AddTaskListener addTaskListener;
    Calendar dateAndTime = Calendar.getInstance();
    public interface AddTaskListener{
        void onTaskAdd(ModelTask newTask);
        void onTaskCancel();
    }

    @Override
    public void onAttach(@NonNull Activity activity) {
        super.onAttach(activity);
        try {
            addTaskListener = (AddTaskListener) activity;
        }catch (ClassCastException e){
            throw new ClassCastException();
        }
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable final Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.dialog_title);
        LayoutInflater inflater = getLayoutInflater();
        View container = inflater.inflate(R.layout.dialog_task,null);
        final TextInputLayout tilTitle = container.findViewById(R.id.tilDialogTaskTitle);
        TextInputLayout tilDate = container.findViewById(R.id.tilDialogTaskDate);
        TextInputLayout tilTime = container.findViewById(R.id.tilDialogTaskTime);
        final EditText etTitle = tilTitle.getEditText();
        final EditText etDate = tilDate.getEditText();
        final EditText etTime = tilTime.getEditText();
        Spinner spPriprity = container.findViewById(R.id.spDialogTaskPriority);
        tilTitle.setHint(getResources().getString(R.string.task_title));
        tilDate.setHint(getResources().getString(R.string.task_date));
        tilTime.setHint(getResources().getString(R.string.task_time));
        builder.setView(container);
        final ModelTask task = new ModelTask();
        ArrayAdapter<String> priorityAdapter = new ArrayAdapter<String>(getActivity(),R.layout.support_simple_spinner_dropdown_item,ModelTask.PRIORITY_LEVELS);
        spPriprity.setAdapter(priorityAdapter);
        spPriprity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                task.setPriority(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        // установка обработчика выбора времени
        final TimePickerDialog.OnTimeSetListener t=new TimePickerDialog.OnTimeSetListener() {
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                //dateAndTime.set(0,0,0,hourOfDay,minute,0);
                dateAndTime.set(Calendar.HOUR,hourOfDay);
                dateAndTime.set(Calendar.MINUTE,minute);
                dateAndTime.set(Calendar.SECOND,0);
                etTime.setText(Utils.getTime(dateAndTime.getTimeInMillis()));
            }
        };

        // установка обработчика выбора даты
        final DatePickerDialog.OnDateSetListener d=new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                dateAndTime.set(year,monthOfYear,dayOfMonth);
                etDate.setText(Utils.getDate(dateAndTime.getTimeInMillis()));
            }
        };
        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etDate.length()==0){
                    etDate.setText(" ");
                }
                new DatePickerDialog(getActivity(), d,
                        dateAndTime.get(Calendar.YEAR),
                        dateAndTime.get(Calendar.MONTH),
                        dateAndTime.get(Calendar.DAY_OF_MONTH))
                        .show();
                /*final DialogFragment datePickerFragment = new DatePickerFragment(){
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        Calendar dateCalendar = Calendar.getInstance();
                        dateCalendar.set(year,month,dayOfMonth);
                        etDate.setText(Utils.getDate(dateCalendar.getTimeInMillis()));
                    }

                    @Override
                    public void onCancel(@NonNull DialogInterface dialog) {
                        etDate.setText(null);
                    }
                };
                datePickerFragment.show(getFragmentManager(),"DatePickerFragment");*/
            }
        });
        etTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etTime.length()==0){
                    etTime.setText(" ");
                }
                new TimePickerDialog(getActivity(),t,
                        dateAndTime.get(Calendar.HOUR)+1,
                        dateAndTime.get(Calendar.MINUTE),
                        DateFormat.is24HourFormat(getActivity()))
                        .show();
                /*final DialogFragment timePickerFragment = new TimePickerFragment(){
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        Calendar timeCalendar = Calendar.getInstance();
                        timeCalendar.set(0,0,0,hourOfDay,minute);
                        etTime.setText(Utils.getTime(timeCalendar.getTimeInMillis()));
                    }

                    @Override
                    public void onCancel(@NonNull DialogInterface dialog) {
                        etTime.setText(null);
                    }
                };
                timePickerFragment.show(getFragmentManager(),"TimePickerFragment");*/
            }
        });
        builder.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                task.setTitle(etTitle.getText().toString());
                if(etDate.length()!=0 && etTime.length()!=0) task.setDate(dateAndTime.getTimeInMillis());
                task.setStatus(ModelTask.STATUS_CURRENT);
                addTaskListener.onTaskAdd(task);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                addTaskListener.onTaskCancel();
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                final Button buttonPositive = ((AlertDialog)dialog).getButton(DialogInterface.BUTTON_POSITIVE);
                if(etTitle.length() == 0) {
                    buttonPositive.setEnabled(false);
                    tilTitle.setError(getResources().getString(R.string.dialog_error_title_empty));
                }
                etTitle.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if(s.length() == 0) {
                            buttonPositive.setEnabled(false);
                            tilTitle.setError(getResources().getString(R.string.dialog_error_title_empty));
                        }else {
                            buttonPositive.setEnabled(true);
                            tilTitle.setErrorEnabled(false);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
            }
        });
        return alertDialog;
    }


}
