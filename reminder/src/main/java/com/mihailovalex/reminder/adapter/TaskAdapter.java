package com.mihailovalex.reminder.adapter;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mihailovalex.reminder.fragment.TaskFragment;
import com.mihailovalex.reminder.model.Item;
import com.mihailovalex.reminder.model.ModelSeparator;
import com.mihailovalex.reminder.model.ModelTask;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public abstract class TaskAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    List<Item> items;
    TaskFragment taskFragment;
    public boolean containsSeparatorOverdue;
    public boolean containsSeparatorToday;
    public boolean containsSeparatorTomorrow;
    public boolean containsSeparatorFuture;

    public TaskAdapter(TaskFragment taskFragment) {
        this.taskFragment = taskFragment;
        items = new ArrayList<>();
    }
    public Item getItem(int position){
        return items.get(position);
    }
    public void addItem(Item item){
        items.add(item);
        notifyItemInserted(getItemCount()-1);
    }
    public void addItem(int location,Item item){
        items.add(location,item);
        notifyItemInserted(location);
    }
    public void updateTask(ModelTask updateTask){
        for (int i = 0; i < getItemCount(); i++) {
            if(getItem(i).isTask()){
                ModelTask task = (ModelTask) getItem(i);
                if(task.getTimeStamp()==updateTask.getTimeStamp()){
                    removeItem(i);
                    getTaskFragment().addTask(updateTask,false);
                }
            }
        }
    }
    public void removeItem(int location){
        if(location>=0 && location<=getItemCount()-1){
            items.remove(location);
            notifyItemRemoved(location);
            if(location-1>=0 && location<=getItemCount()-1){
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
            }
        }
    }

    private void checkSeparator(int type) {
        switch (type){
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
        }
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
    public TaskFragment getTaskFragment() {
        return taskFragment;
    }
}
