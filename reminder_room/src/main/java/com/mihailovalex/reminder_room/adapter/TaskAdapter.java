package com.mihailovalex.reminder_room.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.mihailovalex.reminder_room.data.Item;
import com.mihailovalex.reminder_room.data.Task;
import com.mihailovalex.reminder_room.databinding.SeparatorItemBinding;
import com.mihailovalex.reminder_room.databinding.TaskItemBinding;
import com.mihailovalex.reminder_room.ui.TaskFragment;
import com.mihailovalex.reminder_room.ui.currenttasks.CurrentTasksViewModel;

import java.util.ArrayList;
import java.util.Calendar;
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
        this.tasksViewModel = tasksViewModel;
        items = new ArrayList<>();
        setList(tasks);

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
    public void updateTask(Task updateTask){
        int position = items.indexOf(updateTask);
        if(getItem(position).isTask()){
            removeItem(position);
            setSeparator(updateTask);
        }
    }
    public void removeItem(int location){
        if(location>=0 && location<=getItemCount()-1){
            items.remove(location);
            notifyItemRemoved(location);
            if(location-1>=0 && location<=getItemCount()-1){
                if(!getItem(location).isTask() && !getItem(location-1).isTask()){
                    Separator separator = (Separator) getItem(location-1);
                    checkSeparator(separator.getType());
                    items.remove(location-1);
                    notifyItemRemoved(location-1);
                }

            }
            if(getItemCount()-1>=0 && !getItem(getItemCount()-1).isTask()){
                Separator separator = (Separator) getItem(getItemCount()-1);
                checkSeparator(separator.getType());
                int locationTemp = getItemCount()-1;
                items.remove(locationTemp);
                notifyItemRemoved(locationTemp);
            }
        }
    }

    private void checkSeparator(int type) {
        switch (type){
            case Separator.TYPE_OVERDUE:
                containsSeparatorOverdue = false;
                break;
            case Separator.TYPE_TODAY:
                containsSeparatorToday = false;
                break;
            case Separator.TYPE_TOMORROW:
                containsSeparatorTomorrow = false;
                break;
            case Separator.TYPE_FUTURE:
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
        protected Context context;
        protected TaskItemBinding taskItemBinding;

        public TaskViewHolder(TaskItemBinding  binding) {
            super(binding.getRoot());
            context = itemView.getContext();
            taskItemBinding = binding;
        }

    }
    protected class SeparatorViewHolder extends RecyclerView.ViewHolder{
        protected Context context;
        protected SeparatorItemBinding separatorItemBinding;

        public SeparatorViewHolder(SeparatorItemBinding  binding) {
            super(binding.getRoot());
            context = itemView.getContext();
            separatorItemBinding = binding;
        }
    }

    private void setList(List<Item> tasks) {
        //items = tasks;
        removeAllItems();
        for (Item item :tasks){
            setSeparator((Task) item);
        }
        notifyDataSetChanged();
    }
    public void replaceData(List<Item> tasks) {
        setList(tasks);
    }
    protected void setSeparator(Task newTask){
        int position = -1;
        Separator separator = null;
        for (int i = 0; i < getItemCount(); i++) {
            if(getItem(i).isTask()){
                Task task = (Task) getItem(i);
                if(newTask.getDate()<task.getDate()){
                    position = i;
                    break;
                }
            }
        }
        if(newTask.getDate()!=0){
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(newTask.getDate());
            if(calendar.get(Calendar.YEAR)<Calendar.getInstance().get(Calendar.YEAR)){
                newTask.setDateStatus(Separator.TYPE_OVERDUE);
                if(!containsSeparatorOverdue){
                    containsSeparatorOverdue = true;
                    separator = new Separator(Separator.TYPE_OVERDUE);
                }
            }else if(calendar.get(Calendar.YEAR)>Calendar.getInstance().get(Calendar.YEAR)){
                newTask.setDateStatus(Separator.TYPE_FUTURE);
                if(!containsSeparatorFuture){
                    containsSeparatorFuture = true;
                    separator = new Separator(Separator.TYPE_FUTURE);
                }
            }else if(calendar.get(Calendar.DAY_OF_YEAR)<Calendar.getInstance().get(Calendar.DAY_OF_YEAR)){
                newTask.setDateStatus(Separator.TYPE_OVERDUE);
                if(!containsSeparatorOverdue){
                    containsSeparatorOverdue = true;
                    separator = new Separator(Separator.TYPE_OVERDUE);
                }
            } else if(calendar.get(Calendar.DAY_OF_YEAR)==Calendar.getInstance().get(Calendar.DAY_OF_YEAR)){
                newTask.setDateStatus(Separator.TYPE_TODAY);
                if(!containsSeparatorToday){
                    containsSeparatorToday = true;
                    separator = new Separator(Separator.TYPE_TODAY);
                }
            } else if(calendar.get(Calendar.DAY_OF_YEAR) ==Calendar.getInstance().get(Calendar.DAY_OF_YEAR)+1){
                newTask.setDateStatus(Separator.TYPE_TOMORROW);
                if(!containsSeparatorTomorrow){
                    containsSeparatorTomorrow = true;
                    separator = new Separator(Separator.TYPE_TOMORROW);
                }
            } else if(calendar.get(Calendar.DAY_OF_YEAR)>Calendar.getInstance().get(Calendar.DAY_OF_YEAR)){
                newTask.setDateStatus(Separator.TYPE_FUTURE);
                if(!containsSeparatorFuture){
                    containsSeparatorFuture = true;
                    separator = new Separator(Separator.TYPE_FUTURE);
                }
            }
        }

        if(position!=-1){
            if(!getItem(position -1).isTask()){
                if(position-2>=0 && getItem(position -2 ).isTask()){
                    Task task = (Task) getItem(position -2 );
                    if(task.getDateStatus()==newTask.getDateStatus()){
                        position -= 1;
                    }
                }
            }
            if (separator != null){
                addItem(position-1,separator);
            }
            addItem(position,newTask);
        } else {
            if (separator != null){
                addItem(separator);
            }
            addItem(newTask);
        }
    }
}
