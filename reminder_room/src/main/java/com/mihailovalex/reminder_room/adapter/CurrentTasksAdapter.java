package com.mihailovalex.reminder_room.adapter;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Handler;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.mihailovalex.reminder_room.R;
import com.mihailovalex.reminder_room.data.Item;
import com.mihailovalex.reminder_room.data.Task;
import com.mihailovalex.reminder_room.databinding.SeparatorItemBinding;
import com.mihailovalex.reminder_room.databinding.TaskItemBinding;

import com.mihailovalex.reminder_room.ui.currenttasks.CurrentTasksViewModel;
import com.mihailovalex.reminder_room.ui.currenttasks.TaskItemUserActionsListener;
import com.mihailovalex.reminder_room.utils.DateUtils;
import com.mihailovalex.reminder_room.widget.TasksListWidget;


import java.util.List;



public class CurrentTasksAdapter extends TaskAdapter {

    private static final int TYPE_TASK = 0;
    private static final int TYPE_SEPARATOR = 1;
    SharedPreferences sPref;


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
                SeparatorItemBinding binding1 = DataBindingUtil.inflate(
                        LayoutInflater.from(parent.getContext()),
                        R.layout.separator_item, parent, false);

                SeparatorViewHolder holder1 = new SeparatorViewHolder(binding1);
                return holder1;
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
            holder.itemView.setEnabled(true);
            final Task task = (Task) item;

            final View itemView = holder.itemView;
            holder.taskItemBinding.tvTaskTitle.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    removeTaskDialog(v,task);
                    return true;
                }
            });
            sPref = PreferenceManager.getDefaultSharedPreferences(holder.context);
            String fontText = sPref.getString("font", "middle");
            switch (fontText){
                case "small":
                    holder.taskItemBinding.tvTaskTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                    break;
                case "middle":
                    holder.taskItemBinding.tvTaskTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                    break;
                case "big":
                    holder.taskItemBinding.tvTaskTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
                    break;
            }
            TaskItemUserActionsListener userActionsListener = new TaskItemUserActionsListener() {
                @Override
                public void onCompleteChanged(Task task, View v) {
                    boolean checked = task.isActive();
                    if (!task.isRepeated()) {
                        tasksViewModel.completeTask(task, checked);
                    }
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
                                        if (!task.isRepeated()) {
                                            removeItem(position);
                                        }else {
                                            task.setDate(DateUtils.repeatTask(task.getDate(),task.getRepeat()));
                                            tasksViewModel.getTasksRepository().saveTask(task);
                                            updateTask(task);
                                        }
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
                            }else {
                                holder.taskItemBinding.cvTaskPriority.setImageResource(R.drawable.ic_circle_blank);
                                ObjectAnimator translationX = ObjectAnimator.ofFloat(itemView,"translationX",0f,-itemView.getWidth());
                                ObjectAnimator translationXBack = ObjectAnimator.ofFloat(itemView,"translationX",-itemView.getWidth(),0f);
                                translationX.addListener(new Animator.AnimatorListener() {
                                    @Override
                                    public void onAnimationStart(Animator animation) {

                                    }

                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        itemView.setVisibility(View.GONE);
                                        removeItem(position);
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
                    //notifyItemChanged(position);
                    //notifyDataSetChanged();

                }

                @Override
                public void onTaskClicked(Task task) {
                    tasksViewModel.getOpenTaskEvent().setValue(task.getId());
                }

                @Override
                public void onLongTaskClicked(View v, Task task) {
                    removeTaskDialog(v,task);
                }
            };
            holder.taskItemBinding.setTask((Task) item);
            holder.taskItemBinding.setViewmodel(tasksViewModel);
            holder.taskItemBinding.setListener(userActionsListener);
           // holder.taskItemBinding.executePendingBindings();
        } else {
            Separator separator = (Separator) item;
            SeparatorViewHolder separatorViewHolder = (SeparatorViewHolder) rawHolder;
            separatorViewHolder.separatorItemBinding.setTask((Separator) item);
            //separatorViewHolder.type.setText(resources.getString(separator.getType()));
        }
    }


    @Override
    public int getItemViewType(int position) {
        if (getItem(position).isTask()) return TYPE_TASK;
        else return TYPE_SEPARATOR;
    }
    public void removeTaskDialog(View v,final Task task){
        final AlertDialog.Builder builder = new AlertDialog.Builder(v.getRootView().getContext());
        Item item = task;
        if(item.isTask()){
            Task removingTask = (Task)item;
            final long taksId = removingTask.getId();
            final boolean[] isRemoved = {false};
            final int position =items.indexOf(item);
            builder.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    removeItem(position);
                    isRemoved[0] = true;
                    Snackbar snackbar = Snackbar.make(v,
                            R.string.removed, Snackbar.LENGTH_LONG);
                    snackbar.setAction(R.string.dialog_cancel, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    setSeparator(task);
                                }
                            },1000);
                            //tasksViewModel.getTasksRepository().saveTask(task);
                            isRemoved[0] =false;
                        }
                    });
                    snackbar.getView().addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
                        @Override
                        public void onViewAttachedToWindow(View v) {

                        }

                        @Override
                        public void onViewDetachedFromWindow(View v) {
                            if(isRemoved[0]){
                                //activity.dbHelper.deleteTask(taksId);
                                //alarmHelper.removeAlarm(taksId);
                                tasksViewModel.getTasksRepository().deleteTask(taksId);
                                TasksListWidget.sendRefreshBroadcast(v.getContext());
                            }
                        }
                    });
                    snackbar.show();
                    dialog.dismiss();
                }
            });
            builder.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
        }
        builder.setMessage(R.string.dialog_removing_mess);
        builder.show();
    }
}
