package com.simon.apppermissiondemo;

import android.Manifest;
import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Toast;

import com.simon.permissionlib.annotation.PermissionFail;
import com.simon.permissionlib.annotation.PermissionSuccess;
import com.simon.permissionlib.core.PermissionHelper;
import com.simon.permissionlib.fragement.PermissionsFragment;

/**
 * description:  测试 activity 内的 fragement 申请权限 （非 v4下）
 * author: Simon
 * created at 2017/7/20 下午3:53
 */

public class Test2Activity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_fragement);
        initFragement();
    }

    private void initFragement() {
        Test2Fragement fragement = new Test2Fragement();
        FragmentManager fm = getFragmentManager();
        fm.beginTransaction().add(R.id.content_layout, fragement).commit();
        fm.executePendingTransactions();
    }

}
