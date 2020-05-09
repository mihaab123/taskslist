package com.mihailovalex.reminder_room.adapter;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.mihailovalex.reminder_room.R;
import com.mihailovalex.reminder_room.data.Item;
import com.mihailovalex.reminder_room.data.Task;
import com.mihailovalex.reminder_room.databinding.TaskItemBinding;

import com.mihailovalex.reminder_room.ui.currenttasks.CurrentTasksViewModel;
import com.mihailovalex.reminder_room.ui.currenttasks.TaskItemUserActionsListener;


import java.util.List;



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
        if(item.isTask()){
            final TaskViewHolder holder = (TaskViewHolder) rawHolder;
            holder.itemView.setEnabled(true);
            final Task task = (Task) item;

            final View itemView = holder.itemView;
            TaskItemUserActionsListener userActionsListener = new TaskItemUserActionsListener() {
                @Override
                public void onCompleteChanged(Task task, View v) {
                    boolean checked = task.isActive();
                    tasksViewModel.completeTask(task, checked);
                    ObjectAnimator flipin = ObjectAnimator.ofFloat(holder.taskItemBinding.cvTaskPriority,"rotationY",-180f,0f);
                    flipin.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            if(task.isActive()){
                                holder.taskItemBinding.cvTaskPriority.setImageResource(R.drawable.ic_circle_check);
                                ObjectAnimator translationX = ObjectAnimator.ofFloat(itemView,"translationX",0f,itemView.getWidth());
                                ObjectAnimator translationXBack = ObjectAnimator.ofFloat(itemView,"translationX",itemView.getWidth(),0f);
                                translationX.addListener(new Animator.AnimatorListener() {
                                    @Override
                                    public void onAnimationStart(Animator animation) {

                                    }

                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        itemView.setVisibility(View.GONE);

                                    }

                                    @Override
                                    public void onAnimationCancel(Animator animation) {

                                    }

                                    @Override
                                    public void onAnimationRepeat(Animator animation) {

                                    }
                                });
                                AnimatorSet animatorSet = new AnimatorSet();
                                animatorSet.play(translationX).before(translationXBack);
                                animatorSet.start();
                            }
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    });
                    flipin.start();
                    notifyItemChanged(position);
                }

                @Override
                public void onTaskClicked(Task task) {
                    tasksViewModel.getOpenTaskEvent().setValue(task.getId());
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
