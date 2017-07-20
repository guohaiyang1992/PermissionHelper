package com.simon.apppermissiondemo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

/**
 * Created by guohaiyang on 2017/7/4.
 */

public class Test3Activity extends FragmentActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //重用布局
        setContentView(R.layout.activity_test_fragement2);
        initFragement();
    }

    //此处添加的是v4包下的
    private void initFragement() {
        Test3Fragement fragement = new Test3Fragement();
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().add(R.id.content_layout, fragement).commit();
        fm.executePendingTransactions();
    }
}
