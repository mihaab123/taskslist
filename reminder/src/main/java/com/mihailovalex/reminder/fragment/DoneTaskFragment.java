package com.mihailovalex.reminder.fragment;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mihailovalex.reminder.R;
import com.mihailovalex.reminder.adapter.CurrentTasksAdapter;
import com.mihailovalex.reminder.adapter.DoneTaskAdapter;
import com.mihailovalex.reminder.database.DBHelper;
import com.mihailovalex.reminder.model.ModelTask;

import java.util.ArrayList;
import java.util.List;


public class DoneTaskFragment extends TaskFragment {
    OnTaskRestoreListener onTaskRestoreListener;


    public DoneTaskFragment() {
        // Required empty public constructor
    }

    public interface OnTaskRestoreListener {
        void onTaskRestore(ModelTask task);
    }
    @Override
    public void onAttach(@NonNull Activity activity) {
        super.onAttach(activity);
        try {
            onTaskRestoreListener = (OnTaskRestoreListener) activity;
        }catch (ClassCastException e){
            throw new ClassCastException();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_done_task, container, false);
        recyclerView = rootView.findViewById(R.id.rvDoneTask);
        layoutManager =  new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new DoneTaskAdapter(this);
        recyclerView.setAdapter(adapter);
        return rootView;
    }

    @Override
    public void addTask(ModelTask newTask, boolean saveToDB) {
        int position = -1;
        for (int i = 0; i < adapter.getItemCount(); i++) {
            if(adapter.getItem(i).isTask()){
                ModelTask task = (ModelTask) adapter.getItem(i);
                if(newTask.getDate()<task.getDate()){
                    position = i;
                    break;
                }
            }
        }
        if(position!=-1){
            adapter.addItem(position,newTask);
        } else adapter.addItem(newTask);
        if(saveToDB){
            activity.dbHelper.saveTask(newTask);
        }
    }

    @Override
    public void moveTask(ModelTask task) {
        if (task.getDate()!=0){
            alarmHelper.setAlarm(task);
        }
        onTaskRestoreListener.onTaskRestore(task);

    }

    @Override
    public void addTaskFromDB() {
        adapter.removeAllItems();
        List<ModelTask> tasks = new ArrayList<>();
        tasks.addAll(activity.dbHelper.query().getTasks(DBHelper.SELECTION_STATUS,new String[]{Integer.toString(ModelTask.STATUS_DONE)}, DBHelper.TASK_DATE_COLUMN));
        for (int i = 0; i < tasks.size(); i++) {
            addTask(tasks.get(i),false);
        }
    }

    @Override
    public void checkAdapter() {
        if(adapter==null){
            adapter = new DoneTaskAdapter(this);
            addTaskFromDB();
        }
    }

    @Override
    public void findTasks(String title) {
        adapter.removeAllItems();
        List<ModelTask> tasks = new ArrayList<>();
        tasks.addAll(activity.dbHelper.query().getTasks(DBHelper.SELECTION_LIKE_TITLE+" and "+DBHelper.SELECTION_STATUS,new String[]{"%"+title+"%",Integer.toString(ModelTask.STATUS_DONE)}, DBHelper.TASK_DATE_COLUMN));
        for (int i = 0; i < tasks.size(); i++) {
            addTask(tasks.get(i),false);
        }
    }
}
