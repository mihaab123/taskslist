package com.mihailovalex.lapitchat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.google.android.material.textfield.TextInputLayout;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AllUsersActivity extends AppCompatActivity {
    @BindView(R.id.all_users_toolbar)
    Toolbar toolbar;
    @BindView(R.id.all_users_list)
    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_users);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.all_users);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}