package com.mihailovalex.reminder.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.mihailovalex.reminder.fragment.CurrentTaskFragment;
import com.mihailovalex.reminder.fragment.DoneTaskFragment;

public class TabAdapter extends FragmentStatePagerAdapter {
    public static final int CURRENT_TASK_FRAGMENT_POSITION = 0;
    public static final int DONE_TASK_FRAGMENT_POSITION = 1;
    private CurrentTaskFragment currentTaskFragment;
    private DoneTaskFragment doneTaskFragment;
    private int numberOFTabs;
    public TabAdapter(@NonNull FragmentManager fm,int numberOFTabs) {
        super(fm);
        this.numberOFTabs =numberOFTabs;
        currentTaskFragment = new CurrentTaskFragment();
        doneTaskFragment = new DoneTaskFragment();
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return currentTaskFragment;
            case 1:
                return doneTaskFragment;
            default:
                return null;
        }

    }

    @Override
    public int getCount() {
        return numberOFTabs;
    }
}
