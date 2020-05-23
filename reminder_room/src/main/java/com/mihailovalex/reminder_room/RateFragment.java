package com.mihailovalex.reminder_room;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class RateFragment extends Fragment {
    public static final String APP_PAGE_SHORT_LINK = "market://details?id=";
    public static final String APP_PAGE_LONG_LINK = "https://play.google.com/store/apps/details?id=";
    private void RateApp() {
        String appPackageName  = getActivity().getPackageName();
        try {
            startActivityForResult(new Intent(Intent.ACTION_VIEW, Uri.parse(APP_PAGE_SHORT_LINK+appPackageName)),1);
        } catch (Exception e) {
            startActivityForResult(new Intent(Intent.ACTION_VIEW, Uri.parse(APP_PAGE_LONG_LINK+appPackageName)),1);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RateApp();
        getActivity().onBackPressed();//.getSupportFragmentManager().popBackStack();
    }
}
