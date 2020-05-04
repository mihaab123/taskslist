package com.mihailovalex.reminder.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
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

import com.google.android.material.textfield.TextInputLayout;
import com.mihailovalex.reminder.R;
import com.mihailovalex.reminder.Utils;
import com.mihailovalex.reminder.alarm.AlarmHelper;
import com.mihailovalex.reminder.model.ModelTask;

import java.util.Calendar;

public class EditTaskDialogFragment extends DialogFragment {
    EditTaskLintener editTaskLintener;
    Calendar dateAndTime = Calendar.getInstance();
    public static EditTaskDialogFragment newInstance(ModelTask task){
        EditTaskDialogFragment editTaskDialogFragment = new EditTaskDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", task.getTitle());
        args.putLong("date", task.getDate());
        args.putLong("timestamp", task.getTimeStamp());
        args.putInt("priority",task.getPriority());
        editTaskDialogFragment.setArguments(args);
        return editTaskDialogFragment;
    }
    public interface EditTaskLintener{
        void onTaskEdit(ModelTask updateTask);
    }

    @Override
    public void onAttach(@NonNull Activity activity) {
        super.onAttach(activity);
        try {
            editTaskLintener = (EditTaskLintener) activity;
        }catch (ClassCastException e){
            throw new ClassCastException();
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Bundle args = getArguments();
        String title = args.getString("title");
        long date = args.getLong("date",0);
        long timestamp = args.getLong("timestamp",0);
        int priority = args.getInt("priority",0);
        final ModelTask task = new ModelTask(title,date,priority,0,timestamp);

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.edit_dialog_title);
        LayoutInflater inflater = getLayoutInflater();
        View container = inflater.inflate(R.layout.dialog_task,null);
        final TextInputLayout tilTitle = container.findViewById(R.id.tilDialogTaskTitle);
        TextInputLayout tilDate = container.findViewById(R.id.tilDialogTaskDate);
        TextInputLayout tilTime = container.findViewById(R.id.tilDialogTaskTime);
        final EditText etTitle = tilTitle.getEditText();
        final EditText etDate = tilDate.getEditText();
        final EditText etTime = tilTime.getEditText();
        Spinner spPriprity = container.findViewById(R.id.spDialogTaskPriority);

        etTitle.setText(task.getTitle());
        etTitle.setSelection(etTitle.length());
        if(task.getDate()!=0){
            etDate.setText(Utils.getDate(task.getDate()));
            etTime.setText(Utils.getTime(task.getDate()));
        }

        tilTitle.setHint(getResources().getString(R.string.task_title));
        tilDate.setHint(getResources().getString(R.string.task_date));
        tilTime.setHint(getResources().getString(R.string.task_time));
        builder.setView(container);

        ArrayAdapter<String> priorityAdapter = new ArrayAdapter<String>(getActivity(),R.layout.support_simple_spinner_dropdown_item,ModelTask.PRIORITY_LEVELS);
        spPriprity.setAdapter(priorityAdapter);
        spPriprity.setSelection(task.getPriority());
        if (etDate.length()!=0 && etTime.length()!=0){
            dateAndTime.setTimeInMillis(task.getDate());
        }
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

            }
        });
        builder.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                task.setTitle(etTitle.getText().toString());
                task.setStatus(ModelTask.STATUS_CURRENT);
                if(etDate.length()!=0 && etTime.length()!=0){
                    task.setDate(dateAndTime.getTimeInMillis());
                    AlarmHelper alarmHelper =  AlarmHelper.getInstance();
                    alarmHelper.setAlarm(task);
                }
                task.setStatus(ModelTask.STATUS_CURRENT);
                editTaskLintener.onTaskEdit(task);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
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
