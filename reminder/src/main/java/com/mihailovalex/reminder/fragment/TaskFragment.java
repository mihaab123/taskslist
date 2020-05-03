package com.mihailovalex.reminder.fragment;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.mihailovalex.reminder.adapter.CurrentTasksAdapter;
import com.mihailovalex.reminder.adapter.TaskAdapter;
import com.mihailovalex.reminder.model.ModelTask;

public abstract class TaskFragment extends Fragment {
    protected RecyclerView recyclerView;
    protected RecyclerView.LayoutManager layoutManager;
    protected TaskAdapter adapter;

    public void addTask(ModelTask newTask){
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

    }
    public abstract void moveTask(ModelTask task);
}
