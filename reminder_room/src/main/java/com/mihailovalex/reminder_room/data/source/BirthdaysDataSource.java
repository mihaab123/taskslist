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

package com.mihailovalex.reminder_room.data.source;


import androidx.annotation.NonNull;

import com.mihailovalex.reminder_room.data.Birthday;
import com.mihailovalex.reminder_room.data.Task;

import java.util.List;


public interface BirthdaysDataSource {

    interface LoadBirthdaysCallback {

        void onBirthdaysLoaded(List<Birthday> birthdays);

        void onDataNotAvailable();
    }

    interface GetBirthdayCallback {

        void onBirthdayLoaded(Birthday birthday);

        void onDataNotAvailable();
    }

    void getBirthdays(@NonNull LoadBirthdaysCallback callback, String searchString);

    void getBirthday(@NonNull long birthdayId, @NonNull GetBirthdayCallback callback);

    void saveBirthday(@NonNull Birthday birthday);

    void completeBirthday(@NonNull Birthday birthday);

    void completeBirthday(@NonNull long birthdayId);

    void activateBirthday(@NonNull Birthday birthday);

    void activateBirthday(@NonNull long birthdayId);

    void refreshBirthdays();

    void deleteAllBirthdays();

    void deleteBirthday(@NonNull long birthdayId);
}
