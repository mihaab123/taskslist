package com.mihailovalex.taskslist;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.mihailovalex.taskslist.adapter.GroupAdapter;
import com.mihailovalex.taskslist.adapter.TaskAdapter;
import com.mihailovalex.taskslist.data.TaskSchedulerClass;

import java.security.acl.Group;

public class EditGroupActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final String EXTRA_GROUP_ID = "group_id";
    private static final int LOADER_ID = 4;
    private RecyclerView recyclerView;
    private GroupAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_group);
        Toolbar toolbar = findViewById(R.id.toolbar_edit_group);
        setSupportActionBar(toolbar);
        initRecyclerView();
        getSupportLoaderManager().initLoader(LOADER_ID, null, this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.edit_group, menu);

        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                createGroup();
                return true;
            case android.R.id.home:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void createGroup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.alert_group_edit));
        final EditText input = new EditText(this);
        input.setSingleLine(true);
        builder.setView(input);
        builder.setPositiveButton(getResources().getString(R.string.alert_group_save), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String title = input.getText().toString();
                ContentValues contentValues = new ContentValues();
                contentValues.put(TaskSchedulerClass.Groups.COLUMN_NAME_NAME, title);
                getContentResolver().insert(TaskSchedulerClass.Groups.CONTENT_URI, contentValues);
            }
        });
        builder.setNegativeButton(getResources().getString(R.string.alert_group_cancel), new DialogInterface.OnClickListener() {
            @Override public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create(); alert.show();
    }
    private void initRecyclerView() {
        recyclerView = findViewById(R.id.lvGroupEdit);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this,
                layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
        GroupAdapter.OnGroupClickListener OnGroupClickListener = new GroupAdapter.OnGroupClickListener() {
            @Override
            public void onGroupClick(long groupId) {
               // editGroup(groupId);
            }

            @Override
            public void onGroupEditClick(long groupId, String name) {
                editGroup(groupId,name);
            }

            @Override
            public void onGroupDeleteClick(long groupId) {
                deleteGroup(groupId);
            }
        };
        mAdapter = new GroupAdapter(null, OnGroupClickListener);
        recyclerView.setAdapter(mAdapter);

    }

    private void deleteGroup(long groupId) {
        getContentResolver().delete(ContentUris.withAppendedId(TaskSchedulerClass.Groups.CONTENT_URI, groupId),
                null,
                null);
    }

    private void editGroup(final long groupId,final String name) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.alert_group_edit));
        final EditText input = new EditText(this);
        input.setText(name);
        input.setSingleLine(true);
        builder.setView(input);
        builder.setPositiveButton(getResources().getString(R.string.alert_group_save), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String title = input.getText().toString();
                ContentValues contentValues = new ContentValues();
                contentValues.put(TaskSchedulerClass.Groups.COLUMN_NAME_NAME, title);
                getContentResolver().update(ContentUris.withAppendedId(TaskSchedulerClass.Groups.CONTENT_URI, groupId),
                        contentValues,
                        null,
                        null);
            }
        });
        builder.setNegativeButton(getResources().getString(R.string.alert_group_cancel), new DialogInterface.OnClickListener() {
            @Override public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create(); alert.show();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String selection = null;
        /*if(groupPosition != 0) {
            selection = TaskSchedulerClass.Tasks.COLUMN_NAME_GROUPID+" = "+groupPosition;
        }*/
        return new CursorLoader(
                this,  // Контекст
                TaskSchedulerClass.Groups.CONTENT_URI, // URI
                TaskSchedulerClass.Groups.DEFAULT_PROJECTION, // Столбцы
                selection, // Параметры выборки
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
}
