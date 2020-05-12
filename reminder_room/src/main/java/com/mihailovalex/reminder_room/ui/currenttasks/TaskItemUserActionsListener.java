
package com.mihailovalex.reminder_room.ui.currenttasks;


import android.view.View;


import com.mihailovalex.reminder_room.data.Task;

/**
 * Listener used with data binding to process user actions.
 */
public interface TaskItemUserActionsListener {
    void onCompleteChanged(Task task, View v);

    void onTaskClicked(Task task);

    void onLongTaskClicked(View v,Task task);
}
