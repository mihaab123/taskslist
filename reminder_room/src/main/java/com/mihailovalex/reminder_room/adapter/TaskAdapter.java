package com.mihailovalex.reminder_room.adapter;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.mihailovalex.reminder_room.data.Item;
import com.mihailovalex.reminder_room.data.Task;
import com.mihailovalex.reminder_room.ui.TaskFragment;
import com.mihailovalex.reminder_room.ui.currenttasks.CurrentTasksViewModel;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public abstract class TaskAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    List<Item> items;
    CurrentTasksViewModel tasksViewModel;

    public boolean containsSeparatorOverdue;
    public boolean containsSeparatorToday;
    public boolean containsSeparatorTomorrow;
    public boolean containsSeparatorFuture;

    public TaskAdapter(List<Item> tasks,
                        CurrentTasksViewModel tasksViewModel) {
        tasksViewModel = tasksViewModel;
        setList(tasks);

    }
    public Item getItem(int position){
        return items.get(position);
    }
    public void addItem(Task item){
        items.add(item);
        notifyItemInserted(getItemCount()-1);
    }
    public void addItem(int location,Task item){
        items.add(location,item);
        notifyItemInserted(location);
    }
    public void updateTask(Task updateTask){
        for (int i = 0; i < getItemCount(); i++) {
            if(getItem(i).isTask()){
                Task task = (Task) getItem(i);
                if(task.getId()==updateTask.getId()){
                    removeItem(i);
                    //getTaskFragment().addTask(updateTask,false);
                }
            }
        }
    }
    public void removeItem(int location){
        if(location>=0 && location<=getItemCount()-1){
            items.remove(location);
            notifyItemRemoved(location);
            /*if(location-1>=0 && location<=getItemCount()-1){
                if(!getItem(location).isTask() && !getItem(location-1).isTask()){
                    ModelSeparator separator = (ModelSeparator) getItem(location-1);
                    checkSeparator(separator.getType());
                    items.remove(location-1);
                    notifyItemRemoved(location-1);
                } else if(getItemCount()-1>=0 && !getItem(location-1).isTask()){
                    ModelSeparator separator = (ModelSeparator) getItem(getItemCount()-1);
                    checkSeparator(separator.getType());
                    int locationTemp = getItemCount()-1;
                    items.remove(locationTemp);
                    notifyItemRemoved(locationTemp);
                }
            }*/
        }
    }

    private void checkSeparator(int type) {
        /*switch (type){
            case ModelSeparator.TYPE_OVERDUE:
                containsSeparatorOverdue = false;
                break;
            case ModelSeparator.TYPE_TODAY:
                containsSeparatorToday = false;
                break;
            case ModelSeparator.TYPE_TOMORROW:
                containsSeparatorTomorrow = false;
                break;
            case ModelSeparator.TYPE_FUTURE:
                containsSeparatorFuture = false;
                break;
        }*/
    }

    public void removeAllItems()
    {
        if(getItemCount()!=0){
            items = new ArrayList<>();
            notifyDataSetChanged();
            containsSeparatorOverdue = false;
            containsSeparatorToday = false;
            containsSeparatorTomorrow = false;
            containsSeparatorFuture = false;
        }
    }
    @Override
    public int getItemCount() {
        return items.size();
    }
    protected class TaskViewHolder extends RecyclerView.ViewHolder{
        protected TextView title;
        protected TextView date;
        protected CircleImageView priority;

        public TaskViewHolder(@NonNull View itemView, TextView title, TextView date, CircleImageView priority) {
            super(itemView);
            this.title = title;
            this.date = date;
            this.priority = priority;
        }
    }
    protected class SeparatorViewHolder extends RecyclerView.ViewHolder{
        protected TextView type;

        public SeparatorViewHolder(@NonNull View itemView,TextView type) {
            super(itemView);
            this.type = type;
        }
    }

    private void setList(List<Item> tasks) {
        items = tasks;
        notifyDataSetChanged();
    }
    public void replaceData(List<Item> tasks) {
        setList(tasks);
    }
}
