package com.balala.aptdemo;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.balala.inject_core.Inject;

/**
 * <pre>
 *     author : 刘辉良
 *     time   : 2019/11/19
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(providerView());
        Inject.inject(this);
        configView();
    }

    protected abstract int providerView();

    abstract void configView();
}
