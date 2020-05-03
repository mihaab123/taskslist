package com.mihailovalex.reminder.adapter;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mihailovalex.reminder.R;
import com.mihailovalex.reminder.Utils;
import com.mihailovalex.reminder.fragment.CurrentTaskFragment;
import com.mihailovalex.reminder.model.Item;
import com.mihailovalex.reminder.model.ModelTask;

import de.hdodenhof.circleimageview.CircleImageView;


public class CurrentTasksAdapter extends TaskAdapter {

    private static final int TYPE_TASK = 0;
    private static final int TYPE_SEPARATOR = 1;


    public CurrentTasksAdapter(CurrentTaskFragment taskFragment) {
        super(taskFragment);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType){
            case TYPE_TASK:
                View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.model_task,parent,false);
                TextView title = v.findViewById(R.id.tvTaskTitle);
                TextView date = v.findViewById(R.id.tvTaskDate);
                CircleImageView priority = v.findViewById(R.id.cvTaskPriority);
                return new TaskViewHolder(v,title,date,priority);
            case TYPE_SEPARATOR:
                break;
            default:
                return null;
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final Item item = getItem(position);
        if(item.isTask()){
            holder.itemView.setEnabled(true);
            final ModelTask task = (ModelTask) item;

            final TaskViewHolder taskViewHolder = (TaskViewHolder) holder;
            final View itemView = taskViewHolder.itemView;
            final Resources resources =itemView.getResources();
            taskViewHolder.title.setText(task.getTitle());
            if(task.getDate()!=0){
                taskViewHolder.date.setText(Utils.getFullDate(task.getDate()));
            } else taskViewHolder.date.setText(null);
            itemView.setVisibility(View.VISIBLE);
            itemView.setBackgroundColor(resources.getColor(R.color.gray_50));
            taskViewHolder.title.setTextColor(resources.getColor(R.color.design_default_color_primary));
            taskViewHolder.date.setTextColor(resources.getColor(R.color.design_default_color_secondary));
            taskViewHolder.priority.setColorFilter(resources.getColor(task.getPriorityColor()));
            taskViewHolder.priority.setImageResource(R.drawable.ic_circle_blank);
            taskViewHolder.priority.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    task.setStatus(ModelTask.STATUS_DONE);
                    itemView.setBackgroundColor(resources.getColor(R.color.gray_200));
                    // change color later
                    taskViewHolder.title.setTextColor(resources.getColor(R.color.design_default_color_primary));
                    taskViewHolder.date.setTextColor(resources.getColor(R.color.design_default_color_secondary));
                    taskViewHolder.priority.setColorFilter(resources.getColor(task.getPriorityColor()));

                    ObjectAnimator flipin = ObjectAnimator.ofFloat(taskViewHolder.priority,"rotationY",-180f,0f);
                    flipin.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            if(task.getStatus()==ModelTask.STATUS_DONE){
                                taskViewHolder.priority.setImageResource(R.drawable.ic_circle_check);
                                ObjectAnimator translationX = ObjectAnimator.ofFloat(itemView,"translationX",0f,itemView.getWidth());
                                ObjectAnimator translationXBack = ObjectAnimator.ofFloat(itemView,"translationX",itemView.getWidth(),0f);
                                translationX.addListener(new Animator.AnimatorListener() {
                                    @Override
                                    public void onAnimationStart(Animator animation) {

                                    }

                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        itemView.setVisibility(View.GONE);
                                        getTaskFragment().moveTask(task);
                                        removeItem(taskViewHolder.getLayoutPosition());
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
                }
            });
        }
    }


    @Override
    public int getItemViewType(int position) {
        if (getItem(position).isTask()) return TYPE_TASK;
        else return TYPE_SEPARATOR;
    }

}
