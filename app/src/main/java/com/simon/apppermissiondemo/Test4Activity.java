package com.simon.apppermissiondemo;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

/**
 * description: 用于测试自定义view或者其他类型的类申请权限的处理
 * author: Simon
 * created at 2017/7/20 下午4:17
 */

public class Test4Activity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new CustomLayout(this));
    }
}
