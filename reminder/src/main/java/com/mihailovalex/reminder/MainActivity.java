package com.mihailovalex.reminder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.mihailovalex.reminder.adapter.TabAdapter;
import com.mihailovalex.reminder.dialog.AddTaskDialogFragment;
import com.mihailovalex.reminder.fragment.CurrentTaskFragment;
import com.mihailovalex.reminder.fragment.DoneTaskFragment;
import com.mihailovalex.reminder.fragment.SplashFragment;
import com.mihailovalex.reminder.fragment.TaskFragment;
import com.mihailovalex.reminder.model.ModelTask;

import static com.mihailovalex.reminder.adapter.TabAdapter.CURRENT_TASK_FRAGMENT_POSITION;
import static com.mihailovalex.reminder.adapter.TabAdapter.DONE_TASK_FRAGMENT_POSITION;


public class MainActivity extends AppCompatActivity implements AddTaskDialogFragment.AddTaskListener , CurrentTaskFragment.OnTaskDoneListener, DoneTaskFragment.OnTaskRestoreListener {
    FragmentManager fragmentManager;
    PreferenceHelper preferenceHelper;
    TabAdapter tabAdapter;
    TaskFragment currentTaskFragment;
    TaskFragment doneTaskFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fragmentManager = getSupportFragmentManager();
        PreferenceHelper.getInstance().init(getApplicationContext());
        preferenceHelper = PreferenceHelper.getInstance();
        runSplash();
        setUI();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem splashItem = menu.findItem(R.id.action_splash);
        splashItem.setChecked(preferenceHelper.getBoolean(PreferenceHelper.SPLASH_IN_VISIBLE));
        //initSearchView(menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_splash:
                item.setChecked(!item.isChecked());
                preferenceHelper.putBoolean(PreferenceHelper.SPLASH_IN_VISIBLE,item.isChecked());
                //Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                //startActivity(intent);
                break;
        }

        return true;
    }

    public void runSplash(){
        if(!preferenceHelper.getBoolean(PreferenceHelper.SPLASH_IN_VISIBLE)) {
            SplashFragment splashFragment = new SplashFragment();
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, splashFragment)
                    .addToBackStack(null)
                    .commit();
        }
    }
    private void setUI(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        if(toolbar!=null){
            toolbar.setTitleTextColor(getResources().getColor(R.color.white));
            setSupportActionBar(toolbar);
        }
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText(R.string.current_tab));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.done_tab));
        final ViewPager viewPager = findViewById(R.id.pager);
        tabAdapter = new TabAdapter(fragmentManager, 2);
        viewPager.setAdapter(tabAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        currentTaskFragment = (CurrentTaskFragment) tabAdapter.getItem(CURRENT_TASK_FRAGMENT_POSITION);
        doneTaskFragment = (DoneTaskFragment) tabAdapter.getItem(DONE_TASK_FRAGMENT_POSITION);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment addTaskDialogFragment = new AddTaskDialogFragment();
                addTaskDialogFragment.show(getSupportFragmentManager(),"AddTaskDialogFragment");
            }
        });
    }

    @Override
    public void onTaskAdd(ModelTask newTask) {
        currentTaskFragment.addTask(newTask);
    }

    @Override
    public void onTaskCancel() {
        Toast.makeText(this,"onTaskCancel",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onTaskDone(ModelTask task) {
        doneTaskFragment.addTask(task);
    }

    @Override
    public void onTaskRestore(ModelTask task) {
        currentTaskFragment.addTask(task);
    }
}
