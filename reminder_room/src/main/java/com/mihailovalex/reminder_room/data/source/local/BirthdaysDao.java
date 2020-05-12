/*
 * Copyright 2017, The Android Open Source Project
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

package com.mihailovalex.reminder_room.data.source.local;



import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.mihailovalex.reminder_room.data.Birthday;
import com.mihailovalex.reminder_room.data.Task;

import java.util.List;

/**
 * Data Access Object for the tasks table.
 */
@Dao
public interface BirthdaysDao {


    @Query("SELECT * FROM BIRTHDAYS order by date")
    List<Birthday> getBirthdays();

    @Query("SELECT * FROM BIRTHDAYS where title LIKE  :search order by date")
    List<Birthday> getBirthdaysByTitle(String search);

    @Query("SELECT * FROM birthdays WHERE id = :birthdayId")
    Birthday getBirthdayById(long birthdayId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertBirthday(Birthday birthday);

    @Update
    int updateBirthday(Birthday birthday);

    @Query("UPDATE birthdays SET completed = :completed WHERE id = :birthdayId")
    void updateCompleted(long birthdayId, boolean completed);


    @Query("DELETE FROM birthdays WHERE id = :birthdayId")
    int deleteBirthdayById(long birthdayId);


    @Query("DELETE FROM birthdays")
    void deleteBirthdays();

}
