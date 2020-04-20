package com.mihailovalex.taskslist.adapter;

import android.database.Cursor;
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

public class TaskAdapter extends CursorRecyclerAdapter<TaskAdapter.ViewHolder> {


    public TaskAdapter(Cursor cursor) {
            super(cursor);
            }


    public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor) {
        int titleColumnIndex = cursor.getColumnIndexOrThrow(TaskSchedulerClass.Tasks.COLUMN_NAME_TITLE);
        String title = cursor.getString(titleColumnIndex);
        viewHolder.titleTv.setText(title);

        int dateColumnIndex = cursor.getColumnIndexOrThrow(TaskSchedulerClass.Tasks.COLUMN_NAME_TIME);
        long updatedTs = cursor.getLong(dateColumnIndex);

        Date date = new Date(updatedTs);

        viewHolder.dateTv.setText(viewHolder.SDF.format(date));
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
    class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView titleTv;
        private final TextView dateTv;
        final SimpleDateFormat SDF = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());

        public ViewHolder(View itemView) {
            super(itemView);

            this.titleTv = itemView.findViewById(R.id.title_tv);
            this.dateTv = itemView.findViewById(R.id.date_tv);
        }
    }
}