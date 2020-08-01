package com.mihailovalex.lapitchat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        ButterKnife.bind(this);

    }
    @OnClick(R.id.start_reg_button)
    public void startRegister() {
        Intent reg_intent = new Intent(StartActivity.this,RegisterActivity.class);
        startActivity(reg_intent);
    }
}