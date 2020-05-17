package com.mihailovalex.reminder_room;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class FeedbackFragment extends Fragment {
    private void sendFeedback() {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{getString(R.string.admin_email)});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.feedback_title));
        emailIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.feedback_device_info) + "\n" + Build.MODEL + " API "+Integer.toString(Build.VERSION.SDK_INT) + " Android "+ Build.VERSION.RELEASE
                + "\n" + getString(R.string.feedback_app_version) + BuildConfig.VERSION_NAME
                + "\n" + getString(R.string.feedback));
        emailIntent.setType("text/plain");
        startActivity(emailIntent);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sendFeedback();
    }
}
