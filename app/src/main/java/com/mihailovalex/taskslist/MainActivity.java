package com.mihailovalex.taskslist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mihailovalex.taskslist.adapter.TaskAdapter;
import com.mihailovalex.taskslist.data.TaskSchedulerClass;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private Spinner spinner;
    private RecyclerView recyclerView;
    private static final int LOADER_ID = 1;
    private TaskAdapter mAdapter;
    String[] data = {"one", "two", "three", "four", "five"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initToobar();
        initSpinner();
        initRecyclerView();
        initFab();
        getSupportLoaderManager().initLoader(LOADER_ID, null, this);
        initADS();

    }

    private void initADS() {
        // подключаем блок рекламы
        MobileAds.initialize(this, getString(R.string.app_id));
        AdView adView = findViewById(R.id.banner_ad);
        adView.loadAd(getAdRequest());
    }

    private AdRequest getAdRequest() {
        return new  AdRequest.Builder().build();
    }
    private void initFab() {
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditTaskActivity.class);
                startActivity(intent);
                /*Snackbar.make(view, "Replace with your own action "+view.getId(), Snackbar.LENGTH_LONG)
                        .setAction("Action", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        }).show();*/
            }
        });
    }

    private void initRecyclerView() {
        recyclerView = findViewById(R.id.lvItems);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this,
                layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
        TaskAdapter.OnTaskClickListener OnTaskClickListener = new TaskAdapter.OnTaskClickListener() {
            @Override
            public void onTaskClick(long taskId) {
                editTask(taskId);
            }
        };
        mAdapter = new TaskAdapter(null, OnTaskClickListener);
        recyclerView.setAdapter(mAdapter);




    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                this,  // Контекст
                TaskSchedulerClass.Tasks.CONTENT_URI, // URI
                TaskSchedulerClass.Tasks.DEFAULT_PROJECTION, // Столбцы
                null, // Параметры выборки
                null, // Аргументы выборки
                null // Сортировка по умолчанию
        );
    }

    @Override
    public void onLoadFinished(Loader loader, Cursor newData) {
        //этот метод вызывается после получения данных из БД. Адаптеру
        //посылаются новые данные в виде Cursor и сообщение о том, что
        //данные обновились и нужно заново отобразить список.
        mAdapter.swapCursor(newData);
    }

    @Override
    public void onLoaderReset(Loader loader) {
        //если в полученном результате sql-запроса нет никаких строк,
        //то говорим адаптеру, что список нужно очистить
        mAdapter.swapCursor(null);
    }
    private void initSpinner() {
        // адаптер
        //ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, data);
        ArrayAdapter arrayAdapter = new ArrayAdapter(
                MainActivity.this,
                android.R.layout.simple_list_item_1);

            //Получаем список студентов для класса с id = holder.classID
            String[] data = getGroups();
            //Заполняем ArrayAdapter нашими данными
            if(data != null && data.length > 0) {
                arrayAdapter.addAll(data);
            }
            else {
                arrayAdapter.add("No group");
            }

        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //mAdapter = new GroupAdapter(this, null, 0);
        spinner = findViewById(R.id.spinner_groups);
        spinner.setAdapter(arrayAdapter);
        // выделяем элемент
        spinner.setSelection(0);
        // устанавливаем обработчик нажатия
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                // показываем позиция нажатого элемента
                //Toast.makeText(getBaseContext(), "Position = " + position, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void initToobar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /*if(item.getItemId() == R.id.action_search) {
            Intent intent = new Intent(this, SearchUsersActivity.class);
            startActivity(intent);
        }*/
        return true;
    }
    private String[] getGroups() {
        String[] groups = null;
        Cursor c = null;

        c =  getContentResolver().query(
             TaskSchedulerClass.Groups.CONTENT_URI, // URI
             TaskSchedulerClass.Groups.DEFAULT_PROJECTION, // Столбцы
            null, // Параметры выборки
            null, // Аргументы выборки
            null); // Сортировка по умолчанию
        groups = new String[c.getCount()+2];
        groups[0] = getResources().getString(R.string.group_default_all);
        groups[c.getCount()+1] = getResources().getString(R.string.group_default_closes);
        if(c != null) {
            if(c.moveToFirst()) {

                int i=1;
                do {
                    String groupName = c.getString(c.getColumnIndex(TaskSchedulerClass.Groups.COLUMN_NAME_NAME));

                    groups[i] = groupName;
                    i++;
                } while (c.moveToNext());
            }
            c.close();
        }
        return groups;
    }
    private void editTask(long taskId) {
        Intent intent = new Intent(this, EditTaskActivity.class);
        intent.putExtra(EditTaskActivity.EXTRA_TASK_ID, taskId);

        startActivity(intent);
    }
    private void deleteTask(long taskId) {
        getContentResolver().delete(ContentUris.withAppendedId(TaskSchedulerClass.Tasks.CONTENT_URI, taskId),
                null,
                null);
    }
}
