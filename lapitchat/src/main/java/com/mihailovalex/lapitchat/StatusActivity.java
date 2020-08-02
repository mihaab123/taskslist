package com.mihailovalex.lapitchat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;

import butterknife.BindView;

public class StatusActivity extends AppCompatActivity {
    @BindView(R.id.status_change_toolbar)
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.account_status);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}