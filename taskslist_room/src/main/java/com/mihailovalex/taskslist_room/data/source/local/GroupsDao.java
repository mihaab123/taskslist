package com.mihailovalex.taskslist_room.data.source.local;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.mihailovalex.taskslist_room.data.Group;
import com.mihailovalex.taskslist_room.data.Task;

import java.util.List;


    /**
     * Data Access Object for the Groups table.
     */
    @Dao
    public interface GroupsDao extends BaseDao<Group>{

        /**
         * Select all Groups from the Groups table.
         *
         * @return all Groups.
         */
        @Query("SELECT * FROM Groups")
        List<Group> getGroups();

        /**
         * Select a group by id.
         *
         * @param groupId the group id.
         * @return the group with groupId.
         */
        @Query("SELECT * FROM Groups WHERE entryid = :groupId")
        Group getGroupById(Long groupId);


        /**
         * Delete a Group by id.
         *
         * @return the number of Groups deleted. This should always be 1.
         */
        @Query("DELETE FROM Groups WHERE entryid = :groupId")
        int deleteGroupById(Long groupId);

        /**
         * Delete all Groups.
         */
        @Query("DELETE FROM Groups")
        void deleteGroups();


    }


