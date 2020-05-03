package com.mihailovalex.reminder.adapter;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mihailovalex.reminder.fragment.TaskFragment;
import com.mihailovalex.reminder.model.Item;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public abstract class TaskAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    List<Item> items;
    TaskFragment taskFragment;

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
    public void removeItem(int location){
        if(location>=0 && location<=getItemCount()-1){
            items.remove(location);
            notifyItemRemoved(location);
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

    public TaskFragment getTaskFragment() {
        return taskFragment;
    }
}
