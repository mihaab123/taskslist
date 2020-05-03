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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.mihailovalex.taskslist.adapter.GroupAdapter;
import com.mihailovalex.taskslist.adapter.TaskAdapter;
import com.mihailovalex.taskslist.data.TaskSchedulerClass;

import java.security.acl.Group;

public class EditGroupActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final String EXTRA_GROUP_ID = "group_id";
    private static final int LOADER_ID = 4;
    private RecyclerView recyclerView;
    private GroupAdapter mAdapter;
    private ArrayAdapter arrayAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_group);
        Toolbar toolbar = findViewById(R.id.toolbar_edit_group);
        setSupportActionBar(toolbar);
        initRecyclerView();
        getSupportLoaderManager().initLoader(LOADER_ID, null, this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        arrayAdapter = new ArrayAdapter(
                this,
                R.layout.support_simple_spinner_dropdown_item);
        arrayAdapter.addAll(getResources().getStringArray(R.array.array_color));
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
        addEditGroupGialog(-1, "", 0);
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
            public void onGroupEditClick(long groupId, String name, int color) {
                editGroup(groupId,name, color);
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

    private void editGroup(final long groupId,final String name, int color) {

        addEditGroupGialog(groupId, name, color);
    }

    private void addEditGroupGialog(final long groupId, String name, int color) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.dialog_title);
        LayoutInflater inflater = getLayoutInflater();
        View container = inflater.inflate(R.layout.dialog_group,null);
        final TextInputLayout tilTitle = container.findViewById(R.id.tilDialogGroupTitle);
        final EditText etTitle = tilTitle.getEditText();
        final Spinner spinner = container.findViewById(R.id.spDialogGroupColor);
        tilTitle.setHint(getResources().getString(R.string.group_title));
        etTitle.setText(name);
        builder.setView(container);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
        // выделяем элемент
        switch (color){
            case R.color.black:
                spinner.setSelection(0);
                break;
            case R.color.group_color1:
                spinner.setSelection(1);
                break;
            case R.color.group_color2:
                spinner.setSelection(2);
                break;
            case R.color.group_color3:
                spinner.setSelection(3);
                break;
            default:
                spinner.setSelection(0);
        }

        builder.setPositiveButton(getResources().getString(R.string.alert_group_save), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String title = etTitle.getText().toString();
                ContentValues contentValues = new ContentValues();
                contentValues.put(TaskSchedulerClass.Groups.COLUMN_NAME_NAME, title);
                switch ((int) spinner.getSelectedItemId()){
                    case 0:
                        contentValues.put(TaskSchedulerClass.Groups.COLUMN_NAME_GROUP_COLOR, R.color.black);
                        break;
                    case 1:
                        contentValues.put(TaskSchedulerClass.Groups.COLUMN_NAME_GROUP_COLOR, R.color.group_color1);
                        break;
                    case 2:
                        contentValues.put(TaskSchedulerClass.Groups.COLUMN_NAME_GROUP_COLOR, R.color.group_color2);
                        break;
                    case 3:
                        contentValues.put(TaskSchedulerClass.Groups.COLUMN_NAME_GROUP_COLOR, R.color.group_color3);
                        break;
                    default:
                        contentValues.put(TaskSchedulerClass.Groups.COLUMN_NAME_GROUP_COLOR, R.color.black);
                }
                if(groupId!=-1){
                    getContentResolver().update(ContentUris.withAppendedId(TaskSchedulerClass.Groups.CONTENT_URI, groupId),
                            contentValues,
                            null,
                            null);
                } else {
                    getContentResolver().insert(TaskSchedulerClass.Groups.CONTENT_URI, contentValues);
                }
            }
        });
        builder.setNegativeButton(getResources().getString(R.string.alert_group_cancel), new DialogInterface.OnClickListener() {
            @Override public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
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
