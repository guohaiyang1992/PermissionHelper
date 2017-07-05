package com.simon.apppermissiondemo;

import android.Manifest;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Toast;

import com.simon.permissionlib.annotation.PermissionFail;
import com.simon.permissionlib.annotation.PermissionSuccess;
import com.simon.permissionlib.core.PermissionHelper;

/**
 * Created by guohaiyang on 2017/7/4.
 */

public class Test1Activity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test1);
        findViewById(R.id.call).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PermissionHelper.requestPermissions(Test1Activity.this, 100, new String[]{Manifest.permission.CALL_PHONE});
            }
        });
        findViewById(R.id.sms).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PermissionHelper.requestPermissions(Test1Activity.this, 200, new String[]{Manifest.permission.SEND_SMS});
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionHelper.onRequestPermissionsResult(Test1Activity.this, requestCode, permissions, grantResults);
    }

    @PermissionSuccess(requestCode = 100)
    public void onSucess() {
        Toast.makeText(Test1Activity.this, "Test2Activity:电话成功", Toast.LENGTH_SHORT).show();
    }

    @PermissionFail(requestCode = 100)
    public void onFail() {
        Toast.makeText(Test1Activity.this, "Test2Activity:电话失败", Toast.LENGTH_SHORT).show();
    }

    @PermissionSuccess(requestCode = 200)
    public void onSucessSMS() {
        Toast.makeText(Test1Activity.this, "Test2Activity:短信成功", Toast.LENGTH_SHORT).show();
    }

    @PermissionFail(requestCode = 200)
    public void onFailSMS() {
        Toast.makeText(Test1Activity.this, "Test2Activity:短信失败", Toast.LENGTH_SHORT).show();
    }
}
