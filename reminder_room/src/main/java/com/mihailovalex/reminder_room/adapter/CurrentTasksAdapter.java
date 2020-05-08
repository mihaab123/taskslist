package com.mihailovalex.reminder_room.adapter;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.res.Resources;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.mihailovalex.reminder_room.R;
import com.mihailovalex.reminder_room.data.Item;
import com.mihailovalex.reminder_room.data.Task;
import com.mihailovalex.reminder_room.databinding.TaskItemBinding;
import com.mihailovalex.reminder_room.ui.currenttasks.CurrentTasksFragment;
import com.mihailovalex.reminder_room.ui.currenttasks.CurrentTasksViewModel;
import com.mihailovalex.reminder_room.ui.currenttasks.TaskItemUserActionsListener;
import com.mihailovalex.reminder_room.utils.DateUtils;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class CurrentTasksAdapter extends TaskAdapter {

    private static final int TYPE_TASK = 0;
    private static final int TYPE_SEPARATOR = 1;


    public CurrentTasksAdapter(List<Item> tasks,
                               CurrentTasksViewModel tasksViewModel) {
        super(tasks, tasksViewModel);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType){
            case TYPE_TASK:
                TaskItemBinding binding = DataBindingUtil.inflate(
                        LayoutInflater.from(parent.getContext()),
                        R.layout.task_item, parent, false);

                TaskViewHolder holder = new TaskViewHolder(binding);
                return holder;
            case TYPE_SEPARATOR:
                /*View separator = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.model_separator,parent,false);
                TextView type = separator.findViewById(R.id.tvSeparatorName);

                return new SeparatorViewHolder(separator,type);*/
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder rawHolder, int position) {
        final Item item = getItem(position);
        final Resources resources =rawHolder.itemView.getResources();
        if(item.isTask()){
            final TaskViewHolder holder = (TaskViewHolder) rawHolder;
            TaskItemUserActionsListener userActionsListener = new TaskItemUserActionsListener() {
                @Override
                public void onCompleteChanged(Task task, View v) {
                    boolean checked = task.isActive();
                    tasksViewModel.completeTask(task, checked);
                    notifyItemChanged(position);
                }

                @Override
                public void onTaskClicked(Task task) {
                    //tasksViewModel.getOpenTaskEvent().setValue(task.getId());
                }
            };
            holder.taskItemBinding.setTask((Task) item);
            holder.taskItemBinding.setListener(userActionsListener);
            holder.taskItemBinding.executePendingBindings();
        } else {
           /* ModelSeparator separator = (ModelSeparator) item;
            SeparatorViewHolder separatorViewHolder = (SeparatorViewHolder) holder;
            separatorViewHolder.type.setText(resources.getString(separator.getType()));*/
        }
    }


    @Override
    public int getItemViewType(int position) {
        if (getItem(position).isTask()) return TYPE_TASK;
        else return TYPE_SEPARATOR;
    }

}
