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
        Toast.makeText(this,"1", Toast.LENGTH_SHORT).show();
    }
    @OnClick(R.id.shop_btn)
    public void typeTwo() {
        Toast.makeText(this,"2", Toast.LENGTH_SHORT).show();
    }
    @OnClick(R.id.pause_btn)
    public void typeThree() {
        Toast.makeText(this,"3", Toast.LENGTH_SHORT).show();
    }
    @OnClick(R.id.normal_btn)
    public void typeFour() {
        Toast.makeText(this,"4", Toast.LENGTH_SHORT).show();
    }
    @OnClick(R.id.accelerate_btn)
    public void typeFive() {
        Toast.makeText(this,"5", Toast.LENGTH_SHORT).show();
    }
}
