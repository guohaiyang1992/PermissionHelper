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
 * description:  测试activity内请求权限
 * author: Simon
 * created at 2017/7/20 下午3:51
 */
public class Test1Activity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_activity);
        findViewById(R.id.call).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //请求电话权限
                PermissionHelper.with(Test1Activity.this)
                        .permissions(Manifest.permission.CALL_PHONE)
                        .requestCode(100)
                        .lisener(Test1Activity.this)
                        .request();
            }
        });
        findViewById(R.id.sms).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //请求短信权限
                PermissionHelper.with(Test1Activity.this).permissions(Manifest.permission.SEND_SMS).requestCode(200).lisener(Test1Activity.this).request();
            }
        });
    }


    @PermissionSuccess(requestCode = 100)
    public void onSucess() {
        Toast.makeText(Test1Activity.this, "电话成功", Toast.LENGTH_SHORT).show();
    }

    @PermissionFail(requestCode = 100)
    public void onFail() {
        Toast.makeText(Test1Activity.this, "电话失败", Toast.LENGTH_SHORT).show();
    }

    @PermissionSuccess(requestCode = 200)
    public void onSucessSMS() {
        Toast.makeText(Test1Activity.this, "短信成功", Toast.LENGTH_SHORT).show();
    }

    @PermissionFail(requestCode = 200)
    public void onFailSMS() {
        Toast.makeText(Test1Activity.this, "短信失败", Toast.LENGTH_SHORT).show();
    }
}
