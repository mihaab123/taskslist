package com.mihailovalex.taskslist.adapter;

import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.mihailovalex.taskslist.R;
import com.mihailovalex.taskslist.data.TaskSchedulerClass;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class GroupAdapter extends CursorRecyclerAdapter<GroupAdapter.ViewHolder>  {
    private final OnGroupClickListener OnGroupClickListener;

    public GroupAdapter(Cursor cursor, OnGroupClickListener OnGroupClickListener) {
        super(cursor);

        this.OnGroupClickListener = OnGroupClickListener;
    }


    public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor) {
        // заголовок группы
        int nameColumnIndex = cursor.getColumnIndexOrThrow(TaskSchedulerClass.Groups.COLUMN_NAME_NAME);
        final String name = cursor.getString(nameColumnIndex);
        viewHolder.nameTv.setText(name);

        // ид уведомления
        int idColumnIndex = cursor.getColumnIndexOrThrow(TaskSchedulerClass.Groups._ID);
        final long groupId = cursor.getLong(idColumnIndex);
        viewHolder.itemView.setTag(groupId);
        viewHolder.imEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnGroupClickListener.onGroupEditClick(groupId,name);
            }
        });
        viewHolder.imDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnGroupClickListener.onGroupDeleteClick(groupId);
            }
        });
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.view_group_item, parent, false);

        return new ViewHolder(view);
    }



    /**
     * View holder
     */
    class ViewHolder extends RecyclerView.ViewHolder{
        private final TextView nameTv;
        private final ImageButton imEdit;
        private final ImageButton imDel;


        public ViewHolder(View itemView) {
            super(itemView);

            this.nameTv = itemView.findViewById(R.id.group_tv);
            this.imEdit = itemView.findViewById(R.id.ib_edit);
            this.imDel = itemView.findViewById(R.id.ib_del);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    long groupId = (Long) v.getTag();

                    OnGroupClickListener.onGroupClick(groupId);
                }
            });
        }


    }
    public interface OnGroupClickListener {
        void onGroupClick(long groupId);
        void onGroupEditClick(long groupId, String name);
        void onGroupDeleteClick(long groupId);
    }
}