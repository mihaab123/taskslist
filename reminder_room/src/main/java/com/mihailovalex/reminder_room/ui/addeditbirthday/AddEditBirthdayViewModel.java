package com.mihailovalex.reminder_room.ui.addeditbirthday;

import android.app.Application;
import android.content.SharedPreferences;

import androidx.annotation.Nullable;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.lifecycle.AndroidViewModel;
import androidx.preference.PreferenceManager;

import com.mihailovalex.reminder_room.SingleLiveEvent;
import com.mihailovalex.reminder_room.data.Birthday;
import com.mihailovalex.reminder_room.data.Task;
import com.mihailovalex.reminder_room.data.source.BirthdaysDataSource;
import com.mihailovalex.reminder_room.data.source.BirthdaysRepository;
import com.mihailovalex.reminder_room.utils.DateUtils;

import java.util.Calendar;

public class AddEditBirthdayViewModel extends AndroidViewModel implements BirthdaysDataSource.GetBirthdayCallback{
    public final ObservableField<String> title = new ObservableField<>();

    public final ObservableField<String> date = new ObservableField<>();

    public final ObservableField<String> time = new ObservableField<>();

    public final ObservableField<String> comment = new ObservableField<>();

    public final ObservableBoolean dataLoading = new ObservableBoolean(false);

    //private final SnackbarMessage mSnackbarText = new SnackbarMessage();

    private final SingleLiveEvent<Void> mBirthdayUpdated = new SingleLiveEvent<>();

    private final BirthdaysRepository mBirthdaysRepository;


    public final Calendar dateAndTime = Calendar.getInstance();

    @Nullable
    private Long mBirthdayId;

    private boolean mIsNewBirthday;

    private boolean mIsDataLoaded = false;

    private boolean mBirthdayCompleted = false;
    private SharedPreferences sPref;

    public AddEditBirthdayViewModel(Application context,
                                    BirthdaysRepository birthdaysRepository) {
        super(context);
        mBirthdaysRepository = birthdaysRepository;
        sPref = PreferenceManager.getDefaultSharedPreferences(context);
    }
    public boolean get24TimeFormat(){
        boolean time_format = sPref.getBoolean("time_format", false);
        return time_format;
    }
    public void start(Long birthdayId) {
        if (dataLoading.get()) {
            // Already loading, ignore.
            return;
        }
        mBirthdayId = birthdayId;
        if (birthdayId == 0) {
            // No need to populate, it's a new task
            mIsNewBirthday = true;
            return;
        }
        if (mIsDataLoaded) {
            // No need to populate, already have data.
            return;
        }
        mIsNewBirthday = false;
        dataLoading.set(true);

        mBirthdaysRepository.getBirthday(birthdayId, this);
    }

    @Override
    public void onBirthdayLoaded(Birthday birthday) {
        title.set(birthday.getTitle());
        dateAndTime.setTimeInMillis(birthday.getDate());
        date.set(DateUtils.getDate(birthday.getDate()));
        time.set(DateUtils.getTime(birthday.getDate(),get24TimeFormat()));
        comment.set(birthday.getComment());
        mBirthdayCompleted = birthday.isCompleted();
        dataLoading.set(false);
        mIsDataLoaded = true;

        // Note that there's no need to notify that the values changed because we're using
        // ObservableFields.
    }

    @Override
    public void onDataNotAvailable() {
        dataLoading.set(false);
    }

    // Called when clicking on fab.
    void saveBirthday() {
        Birthday birthday = new Birthday(title.get(), dateAndTime.getTimeInMillis(),comment.get());
        if (birthday.isEmpty()) {
            //mSnackbarText.setValue(R.string.empty_task_message);
            return;
        }
        if (isNewBirthday() || mBirthdayId == 0) {
            createBirthday(birthday);
        } else {
            birthday = new Birthday(mBirthdayId,title.get(), dateAndTime.getTimeInMillis(), comment.get(), mBirthdayCompleted);
            updateBirthday(birthday);
        }

    }

    /*SnackbarMessage getSnackbarMessage() {
        return mSnackbarText;
    }*/

    SingleLiveEvent<Void> getBirthdayUpdatedEvent() {
        return mBirthdayUpdated;
    }

    private boolean isNewBirthday() {
        return mIsNewBirthday;
    }

    private void createBirthday(Birthday newBirthday) {
        mBirthdaysRepository.saveBirthday(newBirthday);
        mBirthdayUpdated.call();
    }

    private void updateBirthday(Birthday birthday) {
        if (isNewBirthday()) {
            throw new RuntimeException("updateTask() was called but birthday is new.");
        }
        mBirthdaysRepository.saveBirthday(birthday);
        mBirthdayUpdated.call();
    }

}
