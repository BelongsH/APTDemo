package com.balala.aptdemo;

import android.widget.TextView;

import com.balala.inject_annotation.BindView;

public class MainActivity extends BaseActivity {

    @BindView(R.id.tvHello)
    TextView tvHello;

    @BindView(R.id.tvHello)
    TextView tvHello2;
    @BindView(R.id.tvHello)
    TextView tvHello3;
    @BindView(R.id.tvHello)
    TextView tvHello4;

    @Override
    protected int providerView() {
        return R.layout.activity_main;
    }

    @Override
    void configView() {
        tvHello.setText("我是测试数据");
    }
}

