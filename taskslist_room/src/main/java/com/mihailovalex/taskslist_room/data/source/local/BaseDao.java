package com.mihailovalex.taskslist_room.data.source.local;

import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.mihailovalex.taskslist_room.data.Group;
import com.mihailovalex.taskslist_room.data.Task;

import java.util.List;

public interface BaseDao<T> {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(T t);

    @Update
    int update(T t);


}
