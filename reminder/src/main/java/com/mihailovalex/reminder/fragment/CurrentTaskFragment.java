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
import com.mihailovalex.reminder.model.ModelTask;



public class CurrentTaskFragment extends TaskFragment {
    OnTaskDoneListener onTaskDoneListener;

    public CurrentTaskFragment() {
        // Required empty public constructor
    }

    public interface OnTaskDoneListener {
        void onTaskDone(ModelTask task);
    }
    @Override
    public void onAttach(@NonNull Activity activity) {
        super.onAttach(activity);
        try {
            onTaskDoneListener = (OnTaskDoneListener) activity;
        }catch (ClassCastException e){
            throw new ClassCastException();
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_current_task, container, false);
        recyclerView = rootView.findViewById(R.id.rvCurrentTask);
        layoutManager =  new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new CurrentTasksAdapter(this);
        recyclerView.setAdapter(adapter);
        return rootView;
    }

    @Override
    public void moveTask(ModelTask task) {
        onTaskDoneListener.onTaskDone(task);
    }
}
