/*
 * Copyright 2016, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mihailovalex.reminder_room.ui.currenttasks;


import android.graphics.Color;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import androidx.databinding.BindingAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.mihailovalex.reminder_room.R;
import com.mihailovalex.reminder_room.adapter.TaskAdapter;
import com.mihailovalex.reminder_room.data.Item;
import com.mihailovalex.reminder_room.data.Task;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Contains {@link BindingAdapter}s for the {@link Task} list.
 */
public class CurrentTasksListBindings {

    @SuppressWarnings("unchecked")
    @BindingAdapter("app:items")
    public static void setItems(RecyclerView recyclerView, List<Item> items) {
        //Log.d("MyLogs","setItems");
        TaskAdapter adapter = (TaskAdapter) recyclerView.getAdapter();
        if (adapter != null)
        {
            //Log.d("MyLogs", String.valueOf(items.size()));
            adapter.replaceData(items);
        }
    }
    @SuppressWarnings("unchecked")
    @BindingAdapter("android:src")
    public static void setPriorityColor(CircleImageView priority, Task task) {
        //priority.setColorFilter(task.getPriorityColor());
        priority.setColorFilter(Color.RED);
        if(!task.isCompleted()) {
            priority.setImageResource(R.drawable.ic_circle_blank);
        } else {
            priority.setImageResource(R.drawable.ic_circle_check);
        }
    }
}
