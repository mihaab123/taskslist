package com.mihailovalex.oxiwyletest;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }
    @OnClick(R.id.menu_btn)
    public void typeOne() {
        sendNumber("1");
    }

    private void sendNumber(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.shop_btn)
    public void typeTwo() {
        sendNumber("2");
    }
    @OnClick(R.id.pause_btn)
    public void typeThree() {
        sendNumber("3");
    }
    @OnClick(R.id.normal_btn)
    public void typeFour() {
        sendNumber("4");
    }
    @OnClick(R.id.accelerate_btn)
    public void typeFive() {
        sendNumber("5");
    }
}
