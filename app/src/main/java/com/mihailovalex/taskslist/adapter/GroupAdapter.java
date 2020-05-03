package com.mihailovalex.taskslist.adapter;

import android.content.res.Resources;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.mihailovalex.taskslist.R;
import com.mihailovalex.taskslist.data.TaskSchedulerClass;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class GroupAdapter extends CursorRecyclerAdapter<RecyclerView.ViewHolder>  {
    private final OnGroupClickListener OnGroupClickListener;

    public GroupAdapter(Cursor cursor, OnGroupClickListener OnGroupClickListener) {
        super(cursor);

        this.OnGroupClickListener = OnGroupClickListener;
    }


    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, Cursor cursor) {
        ViewHolder viewHolder1 = (ViewHolder)viewHolder;
        final Resources resources =viewHolder1.itemView.getResources();
        // заголовок группы
        int nameColumnIndex = cursor.getColumnIndexOrThrow(TaskSchedulerClass.Groups.COLUMN_NAME_NAME);
        final String name = cursor.getString(nameColumnIndex);
        viewHolder1.nameTv.setText(name);
        int GroupColorColumnIndex = cursor.getColumnIndexOrThrow(TaskSchedulerClass.Tasks.COLUMN_NAME_GROUP_COLOR);
        final int groupColor = cursor.getInt(GroupColorColumnIndex);
        viewHolder1.nameTv.setTextColor(resources.getColor(groupColor));

        // ид уведомления
        int idColumnIndex = cursor.getColumnIndexOrThrow(TaskSchedulerClass.Groups._ID);
        final long groupId = cursor.getLong(idColumnIndex);
        viewHolder1.itemView.setTag(groupId);
        viewHolder1.imEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnGroupClickListener.onGroupEditClick(groupId,name,groupColor);
            }
        });
        viewHolder1.imDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnGroupClickListener.onGroupDeleteClick(groupId);
            }
        });
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
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
        void onGroupEditClick(long groupId, String name, int color);
        void onGroupDeleteClick(long groupId);
    }

}