package com.mihailovalex.reminder.fragment;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mihailovalex.reminder.R;
import com.mihailovalex.reminder.adapter.DoneTaskAdapter;
import com.mihailovalex.reminder.model.ModelTask;


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
    public void moveTask(ModelTask task) {
        onTaskRestoreListener.onTaskRestore(task);
    }
}
