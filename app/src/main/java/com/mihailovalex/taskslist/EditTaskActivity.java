package com.mihailovalex.taskslist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.mihailovalex.taskslist.data.TaskSchedulerClass;
import com.mihailovalex.taskslist.widget.TasksListWidget;

import java.util.Calendar;

public class EditTaskActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{
    private TextInputLayout titleTil;
    private TextInputLayout dateTil;
    private EditText titleEt;
    private EditText dateEt;
    private TextInputLayout timeTil;
    private EditText timeEt;
    private TextInputLayout groupTil;
    private EditText groupEt;
    public static final String EXTRA_TASK_ID = "task_id";
    private static final int LOADER_ID = 3;
    private long taskId;
    Calendar dateAndTime= Calendar.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);
        Toolbar toolbar = findViewById(R.id.toolbar_edit);
        setSupportActionBar(toolbar);
        titleEt = findViewById(R.id.title_et);
        dateEt = findViewById(R.id.date_et);
        dateEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDate(v);
            }
        });
        timeTil = findViewById(R.id.time_til);
        timeEt = findViewById(R.id.time_et);
        timeEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTime(v);
            }
        });

        titleTil = findViewById(R.id.title_til);
        dateTil = findViewById(R.id.date_til);

        groupTil = findViewById(R.id.group_til);
        groupEt = findViewById(R.id.group_et);

        taskId = getIntent().getLongExtra(EXTRA_TASK_ID, -1);

        if (taskId != -1) {
            getSupportLoaderManager().initLoader(
                    LOADER_ID, // Идентификатор загрузчика
                    null, // Аргументы
                    this // Callback для событий загрузчика
            );
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.create_task, menu);

        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                saveTask();
                return true;
            case android.R.id.home:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private void saveTask() {
        String title = titleEt.getText().toString().trim();
        String date = dateEt.getText().toString().trim();

        boolean isCorrect = true;

        if (TextUtils.isEmpty(title)) {
            isCorrect = false;

            titleTil.setError(getString(R.string.error_empty_field));
            titleTil.setErrorEnabled(true);
        } else {
            titleTil.setErrorEnabled(false);
        }

        /*if (TextUtils.isEmpty(text)) {
            isCorrect = false;

            textTil.setError(getString(R.string.error_empty_field));
            textTil.setErrorEnabled(true);
        } else {
            textTil.setErrorEnabled(false);
        }*/

        if (isCorrect) {

            ContentValues contentValues = new ContentValues();

            contentValues.put(TaskSchedulerClass.Tasks.COLUMN_NAME_TITLE, title);
            contentValues.put(TaskSchedulerClass.Tasks.COLUMN_NAME_GROUPID, 2);
            contentValues.put(TaskSchedulerClass.Tasks.COLUMN_NAME_TIME, dateAndTime.getTimeInMillis());

            if (taskId == -1) {
                getContentResolver().insert(TaskSchedulerClass.Tasks.CONTENT_URI, contentValues);
            } else {
                getContentResolver().update(ContentUris.withAppendedId(TaskSchedulerClass.Tasks.CONTENT_URI, taskId),
                        contentValues,
                        null,
                        null);
            }
            // обновление виджета
            TasksListWidget.sendRefreshBroadcast(this);
            finish();
        }

    }
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                this,  // Контекст
                ContentUris.withAppendedId(TaskSchedulerClass.Tasks.CONTENT_URI, taskId), // URI
                TaskSchedulerClass.Tasks.DEFAULT_PROJECTION, // Столбцы
                null, // Параметры выборки
                null, // Аргументы выборки
                null // Сортировка по умолчанию
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        Log.i("Test", "Load finished: " + cursor.getCount());

        cursor.setNotificationUri(getContentResolver(), TaskSchedulerClass.Tasks.CONTENT_URI);

        displayNote(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private void displayNote(Cursor cursor) {
        if (!cursor.moveToFirst()) {
            // Если не получилось перейти к первой строке — завершаем Activity

            finish();
        }

        String title = cursor.getString(cursor.getColumnIndexOrThrow(TaskSchedulerClass.Tasks.COLUMN_NAME_TITLE));
        Long date = cursor.getLong(cursor.getColumnIndexOrThrow(TaskSchedulerClass.Tasks.COLUMN_NAME_TIME));
        String group = cursor.getString(cursor.getColumnIndexOrThrow(TaskSchedulerClass.Tasks.COLUMN_NAME_GROUP_NAME));

        titleEt.setText(title);
        dateAndTime.setTimeInMillis(date);
        setInitialDateTime();
        groupEt.setText(group);
    }
    // отображаем диалоговое окно для выбора даты
    public void setDate(View v) {
        new DatePickerDialog(this, d,
                dateAndTime.get(Calendar.YEAR),
                dateAndTime.get(Calendar.MONTH),
                dateAndTime.get(Calendar.DAY_OF_MONTH))
                .show();
    }

    // отображаем диалоговое окно для выбора времени
    public void setTime(View v) {
        new TimePickerDialog(this, t,
                dateAndTime.get(Calendar.HOUR_OF_DAY),
                dateAndTime.get(Calendar.MINUTE), true)
                .show();
    }
    // установка обработчика выбора времени
    TimePickerDialog.OnTimeSetListener t=new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            dateAndTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
            dateAndTime.set(Calendar.MINUTE, minute);
            setInitialDateTime();
        }
    };

    // установка обработчика выбора даты
    DatePickerDialog.OnDateSetListener d=new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            dateAndTime.set(Calendar.YEAR, year);
            dateAndTime.set(Calendar.MONTH, monthOfYear);
            dateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            setInitialDateTime();
        }
    };
    // установка начальных даты и времени
    private void setInitialDateTime() {

        dateEt.setText(DateUtils.formatDateTime(this,
                dateAndTime.getTimeInMillis(),
                DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR));
        timeEt.setText(DateUtils.formatDateTime(this,
                dateAndTime.getTimeInMillis(),
                 DateUtils.FORMAT_SHOW_TIME));
    }

}
