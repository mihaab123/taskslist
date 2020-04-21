package com.mihailovalex.taskslist.adapter;

import android.database.Cursor;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.mihailovalex.taskslist.R;
import com.mihailovalex.taskslist.data.TaskSchedulerClass;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TaskAdapter extends CursorRecyclerAdapter<TaskAdapter.ViewHolder>  {
    private final OnTaskClickListener OnTaskClickListener;

    public TaskAdapter(Cursor cursor, OnTaskClickListener OnTaskClickListener) {
        super(cursor);

        this.OnTaskClickListener = OnTaskClickListener;
    }


    public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor) {
        // заголовок уведомления
        int titleColumnIndex = cursor.getColumnIndexOrThrow(TaskSchedulerClass.Tasks.COLUMN_NAME_TITLE);
        String title = cursor.getString(titleColumnIndex);
        viewHolder.titleTv.setText(title);
        // дате уведомления
        int dateColumnIndex = cursor.getColumnIndexOrThrow(TaskSchedulerClass.Tasks.COLUMN_NAME_TIME);
        long updatedTs = cursor.getLong(dateColumnIndex);
        Date date = new Date(updatedTs);
        viewHolder.dateTv.setText(viewHolder.SDF.format(date));
        // заголовок уведомления
        int groupColumnIndex = cursor.getColumnIndexOrThrow(TaskSchedulerClass.Tasks.COLUMN_NAME_GROUP_NAME);
        String group = cursor.getString(groupColumnIndex);
        viewHolder.groupTv.setText(group);
        // ид уведомления
        int idColumnIndex = cursor.getColumnIndexOrThrow(TaskSchedulerClass.Tasks._ID);
        long taskid = cursor.getLong(idColumnIndex);
        viewHolder.itemView.setTag(taskid);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.view_task_item, parent, false);

        return new ViewHolder(view);
    }



    /**
     * View holder
     */
    class ViewHolder extends RecyclerView.ViewHolder{
        private final TextView titleTv;
        private final TextView dateTv;
        private final TextView groupTv;
        final SimpleDateFormat SDF = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());

        public ViewHolder(View itemView) {
            super(itemView);

            this.titleTv = itemView.findViewById(R.id.title_tv);
            this.dateTv = itemView.findViewById(R.id.date_tv);
            this.groupTv = itemView.findViewById(R.id.group_tv);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    long noteId = (Long) v.getTag();

                    OnTaskClickListener.onTaskClick(noteId);
                }
            });
        }


    }
    public interface OnTaskClickListener {
        void onTaskClick(long taskId);
    }
}