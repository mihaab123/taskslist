package com.mihailovalex.reminder;

import android.app.Activity;
import android.view.View;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class Ads {
    public static void showBanner(final Activity activity){
        final AdView adView = activity.findViewById(R.id.banner);
        AdRequest adRequest = new  AdRequest.Builder().build();
        adView.loadAd(adRequest);
        adView.setAdListener(new AdListener(){
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                setupContentViewPadding(activity,adView.getHeight());
            }
        });
    }
    public static void setupContentViewPadding(Activity activity, int padding){
        View view = activity.findViewById(R.id.coordinator);
        view.setPadding(view.getPaddingLeft(),view.getPaddingTop(),view.getPaddingRight(), padding);
    }
}
