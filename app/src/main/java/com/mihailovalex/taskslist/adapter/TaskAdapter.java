package com.mihailovalex.taskslist.adapter;

import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.mihailovalex.taskslist.R;
import com.mihailovalex.taskslist.data.TaskSchedulerClass;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TaskAdapter extends CursorRecyclerAdapter<RecyclerView.ViewHolder>  {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private final OnTaskClickListener OnTaskClickListener;

    public TaskAdapter(Cursor cursor, OnTaskClickListener OnTaskClickListener) {
        super(cursor);

        this.OnTaskClickListener = OnTaskClickListener;
    }


    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, Cursor cursor) {
        switch (viewHolder.getItemViewType()) {
            case TYPE_ITEM:
                setTypeItem((ItemViewHolder) viewHolder, cursor);
                break;
            case TYPE_HEADER:
                HeaderViewHolder viewHolder2  = (HeaderViewHolder)viewHolder;
                // дате уведомления
                int dateHeaderColumnIndex = cursor.getColumnIndexOrThrow(TaskSchedulerClass.Tasks.COLUMN_NAME_TIME);
                long datedTs = cursor.getLong(dateHeaderColumnIndex);
                Date dateHeader = new Date(datedTs);
                viewHolder2.dateHeaderTv.setText(viewHolder2.SDF.format(dateHeader));
                break;
        }
    }

    private void setTypeItem(ItemViewHolder viewHolder, Cursor cursor) {
        ItemViewHolder viewHolder1 = viewHolder;
        // заголовок уведомления
        int titleColumnIndex = cursor.getColumnIndexOrThrow(TaskSchedulerClass.Tasks.COLUMN_NAME_TITLE);
        String title = cursor.getString(titleColumnIndex);
        viewHolder1.titleTv.setText(title);
        // дате уведомления
        int dateColumnIndex = cursor.getColumnIndexOrThrow(TaskSchedulerClass.Tasks.COLUMN_NAME_TIME);
        long updatedTs = cursor.getLong(dateColumnIndex);
        Date date = new Date(updatedTs);
        viewHolder1.dateTv.setText(viewHolder1.SDF.format(date));
        // заголовок уведомления
        int groupColumnIndex = cursor.getColumnIndexOrThrow(TaskSchedulerClass.Tasks.COLUMN_NAME_GROUP_NAME);
        String group = cursor.getString(groupColumnIndex);
        viewHolder1.groupTv.setText(group);
        // ид уведомления
        int idColumnIndex = cursor.getColumnIndexOrThrow(TaskSchedulerClass.Tasks._ID);
        long taskid = cursor.getLong(idColumnIndex);
        viewHolder1.itemView.setTag(taskid);
        // завершенные
        int ComplitedColumnIndex = cursor.getColumnIndexOrThrow(TaskSchedulerClass.Tasks.COLUMN_NAME_COMPLITED);
        long complited = cursor.getLong(ComplitedColumnIndex);
        if (complited == 1){
            viewHolder.titleTv.setPaintFlags(viewHolder.titleTv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            viewHolder.llTaskItem.setBackgroundColor(Color.parseColor("#E1E1E1"));
        }

    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater;
        View view;
        switch (viewType){
            case TYPE_ITEM:
                layoutInflater = LayoutInflater.from(parent.getContext());
                view = layoutInflater.inflate(R.layout.view_task_item, parent, false);
                return new ItemViewHolder(view);
            case TYPE_HEADER:
                layoutInflater = LayoutInflater.from(parent.getContext());
                view = layoutInflater.inflate(R.layout.view_task_item_header, parent, false);
                return new HeaderViewHolder(view);
        }
        throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");
    }



    /**
     * View holder
     */
    class ItemViewHolder extends RecyclerView.ViewHolder{
        private final TextView titleTv;
        private final TextView dateTv;
        private final TextView groupTv;
        private final LinearLayout llTaskItem;
        final SimpleDateFormat SDF = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());

        public ItemViewHolder(View itemView) {
            super(itemView);

            this.titleTv = itemView.findViewById(R.id.title_tv);
            this.dateTv = itemView.findViewById(R.id.date_tv);
            this.groupTv = itemView.findViewById(R.id.group_tv);
            this.llTaskItem = itemView.findViewById(R.id.llTaskItem);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    long taskId = (Long) v.getTag();

                    OnTaskClickListener.onTaskClick(taskId);
                }
            });
        }
    }
    class HeaderViewHolder extends RecyclerView.ViewHolder{

        private final TextView dateHeaderTv;

        final SimpleDateFormat SDF = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());

        public HeaderViewHolder(View itemView) {
            super(itemView);

            this.dateHeaderTv = itemView.findViewById(R.id.date_header_tv);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    long taskId = (Long) v.getTag();

                    OnTaskClickListener.onTaskClick(taskId);
                }
            });
        }
    }
    public interface OnTaskClickListener {
        void onTaskClick(long taskId);
        void onSwypeClick(long taskId);
    }
    public void onItemDismiss(long taskId) {
        // mItems.remove(position);
        //notifyItemRemoved(position);
        OnTaskClickListener.onSwypeClick(taskId);
    }

    public void onItemMove(int fromPosition, int toPosition) {
        /*String prev = mItems.remove(fromPosition);
        mItems.add(toPosition > fromPosition ? toPosition - 1 : toPosition, prev);
        notifyItemMoved(fromPosition, toPosition);*/
    }
    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position)) {
            return TYPE_HEADER;
        }
        return TYPE_ITEM;
    }

    private boolean isPositionHeader(int position) {
        return false;//position == 0;
    }



}