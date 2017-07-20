package com.simon.apppermissiondemo;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.simon.permissionlib.annotation.PermissionFail;
import com.simon.permissionlib.annotation.PermissionSuccess;
import com.simon.permissionlib.core.PermissionHelper;

/**
 * Created by guohaiyang on 2017/7/4.
 */

public class CustomLayout extends LinearLayout {
    public CustomLayout(@NonNull Context context) {
        this(context, null);
    }

    public CustomLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomLayout(@NonNull final Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //重用view
        View view = LayoutInflater.from(context).inflate(R.layout.activity_test_activity, this, true);
        findViewById(R.id.call).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //请求电话权限
                PermissionHelper.with((Activity) getContext()).permissions(Manifest.permission.CALL_PHONE).requestCode(100).lisener(CustomLayout.this).request();
            }
        });
        findViewById(R.id.sms).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //请求短信权限
                PermissionHelper.with((Activity) getContext()).permissions(Manifest.permission.SEND_SMS).requestCode(200).lisener(CustomLayout.this).request();
            }
        });


    }

    @PermissionSuccess(requestCode = 100)
    public void onSucess() {
        Toast.makeText(getContext(), "电话成功", Toast.LENGTH_SHORT).show();
    }

    @PermissionFail(requestCode = 100)
    public void onFail() {
        Toast.makeText(getContext(), "电话失败", Toast.LENGTH_SHORT).show();
    }

    @PermissionSuccess(requestCode = 200)
    public void onSucessSMS() {
        Toast.makeText(getContext(), "短信成功", Toast.LENGTH_SHORT).show();
    }

    @PermissionFail(requestCode = 200)
    public void onFailSMS() {
        Toast.makeText(getContext(), "短信失败", Toast.LENGTH_SHORT).show();
    }

}
