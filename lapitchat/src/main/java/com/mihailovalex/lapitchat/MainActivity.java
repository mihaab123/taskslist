package com.mihailovalex.lapitchat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemSelected;
import butterknife.Optional;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    @BindView(R.id.main_page_toolbar)
    Toolbar toolbar;
    @BindView(R.id.main_tabPager)
    ViewPager viewPager;
    @BindView(R.id.main_tabs)
    TabLayout tabLayout;

    SectionsPagerAdapter sectionsPagerAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mAuth = FirebaseAuth.getInstance();
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.app_name);
        sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(),this);
        viewPager.setAdapter(sectionsPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser==null){
            sendToStart();
        }

    }

    private void sendToStart() {
        Intent intent = new Intent(MainActivity.this, StartActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        if(item.getItemId() == R.id.main_logout_item){
            FirebaseAuth.getInstance().signOut();
            sendToStart();
        }
        if(item.getItemId() == R.id.main_settings_item){
            Intent intent = new Intent(MainActivity.this,SettingsActivity.class);
            startActivity(intent);
        }
        return true;
    }

    /*@OnItemSelected(R.id.main_logout_item)
    public void logoutAccount() {
        FirebaseAuth.getInstance().signOut();
        sendToStart();
    }*/
}
