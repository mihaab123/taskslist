package com.mihailovalex.reminder_room.ui.birthdays;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;

import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableList;
import androidx.lifecycle.AndroidViewModel;
import androidx.preference.PreferenceManager;

import com.mihailovalex.reminder_room.R;
import com.mihailovalex.reminder_room.SingleLiveEvent;
import com.mihailovalex.reminder_room.data.Birthday;
import com.mihailovalex.reminder_room.data.Item;
import com.mihailovalex.reminder_room.data.Task;
import com.mihailovalex.reminder_room.data.source.BirthdaysDataSource;
import com.mihailovalex.reminder_room.data.source.BirthdaysRepository;
import com.mihailovalex.reminder_room.data.source.TasksDataSource;
import com.mihailovalex.reminder_room.data.source.TasksRepository;
import com.mihailovalex.reminder_room.ui.currenttasks.TasksFilterType;

import java.util.ArrayList;
import java.util.List;

public class BirthdayViewModel extends AndroidViewModel {

    // These observable fields will update Views automatically
    public final ObservableList<Item> items = new ObservableArrayList<>();

    public final ObservableField<String> noBirthdaysLabel = new ObservableField<>();

    public final ObservableField<Drawable> noBirthdayIconRes = new ObservableField<>();

    public final ObservableBoolean empty = new ObservableBoolean(false);

    public final ObservableBoolean birthdaysAddViewVisible = new ObservableBoolean();

    //private final SnackbarMessage mSnackbarText = new SnackbarMessage();


    private final BirthdaysRepository mBirthdaysRepository;

    private final ObservableBoolean mIsDataLoadingError = new ObservableBoolean(false);

    private final SingleLiveEvent<Long> mOpenBirthdayEvent = new SingleLiveEvent<>();

    private final Context mContext; // To avoid leaks, this must be an Application Context.

    private final SingleLiveEvent<Void> mNewBirthdayEvent = new SingleLiveEvent<>();


    private SharedPreferences sPref;

    public final SingleLiveEvent<String> searchString = new SingleLiveEvent<>();;

    public BirthdayViewModel(
            Application context,
            BirthdaysRepository repository) {
        super(context);
        mContext = context.getApplicationContext(); // Force use of Application Context.
        mBirthdaysRepository = repository;
        sPref = PreferenceManager.getDefaultSharedPreferences(mContext);

    }

    public boolean get24TimeFormat(){
        boolean time_format = sPref.getBoolean("time_format", false);
        return time_format;
    }

    public void start() {
        loadBirthdays(false);
    }

    public void loadBirthdays(boolean forceUpdate) {
        loadBirthdays(forceUpdate, true);
    }


    public void setFiltering() {

        noBirthdaysLabel.set(mContext.getResources().getString(R.string.no_birthdays_active));
        noBirthdayIconRes.set(mContext.getResources().getDrawable(
                R.drawable.ic_menu_birthdays));
        birthdaysAddViewVisible.set(false);

    }


    public void completeTask(Birthday birthday, boolean completed) {
        // Notify repository
        if (completed) {
            mBirthdaysRepository.completeBirthday(birthday);
            //showSnackbarMessage(R.string.task_marked_complete);
        } else {
            mBirthdaysRepository.activateBirthday(birthday);
            //showSnackbarMessage(R.string.task_marked_active);
        }
    }

    /*SnackbarMessage getSnackbarMessage() {
        return mSnackbarText;
    }
    */
    public SingleLiveEvent<Long> getOpenBirthdayEvent() {
        return mOpenBirthdayEvent;
    }

    public SingleLiveEvent<Void> getNewBirthdayEvent() {
        return mNewBirthdayEvent;
    }
    /*
    private void showSnackbarMessage(Integer message) {
        mSnackbarText.setValue(message);
    }*/

    public void addNewBirthday() {
        mNewBirthdayEvent.call();
    }

    private void loadBirthdays(boolean forceUpdate, final boolean showLoadingUI) {
        /*if (showLoadingUI) {
            dataLoading.set(true);
        }
        if (forceUpdate) {
        */
        mBirthdaysRepository.refreshBirthdays();
        //}

        mBirthdaysRepository.getBirthdays(new BirthdaysDataSource.LoadBirthdaysCallback() {
            @Override
            public void onBirthdaysLoaded(List<Birthday> birthdays) {
                List<Item> birthdaysToShow = new ArrayList<>();

                // We filter the birthdays based on the requestType
                for (Birthday birthday : birthdays) {
                    birthdaysToShow.add(birthday);
                }
                /*if (showLoadingUI) {
                    dataLoading.set(false);
                }*/
                mIsDataLoadingError.set(false);

                items.clear();
                items.addAll(birthdaysToShow);
                empty.set(items.isEmpty());
            }

            @Override
            public void onDataNotAvailable() {
                mIsDataLoadingError.set(true);
            }
        },searchString.getValue());
    }

    public BirthdaysRepository getBirthdayRepository() {
        return mBirthdaysRepository;
    }

    public SingleLiveEvent<String> getSearchString() {
        return searchString;
    }


}