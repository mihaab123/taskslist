
package com.mihailovalex.reminder_room.ui.birthdays;


import android.view.View;

import com.mihailovalex.reminder_room.data.Birthday;


public interface BirthdayItemUserActionsListener {

    void onBirthdayClicked(Birthday birthday);

    void onLongBirthdayClicked(View v, Birthday birthday);

    void onCompleteChanged(Birthday birthday, View v);
}
