package com.simon.apppermissiondemo;

import android.Manifest;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.simon.permissionlib.core.PermissionHelper;

/**
 * Created by guohaiyang on 2017/7/4.
 */

public class Test3Activity extends Activity {
    FrameLayout frameLayout;
    Test1Fragement fragement;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test2);
        frameLayout = (FrameLayout) findViewById(R.id.content_layout);
        fragement = new Test1Fragement();
        getFragmentManager().beginTransaction().add(R.id.content_layout, fragement).commit();

        findViewById(R.id.call).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PermissionHelper.with(Test3Activity.this).requestCode(100).permissions(Manifest.permission.CALL_PHONE).lisener(fragement).hintMessage("缺少必须权限，不开启无法使用哦").request();
            }

        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Toast.makeText(Test3Activity.this, "activity", Toast.LENGTH_SHORT).show();
        PermissionHelper.onRequestPermissionsResult(Test3Activity.this, requestCode, permissions, grantResults);
    }
}
