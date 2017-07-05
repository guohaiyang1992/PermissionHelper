package com.simon.apppermissiondemo;

import android.Manifest;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.simon.permissionlib.annotation.PermissionFail;
import com.simon.permissionlib.annotation.PermissionSuccess;
import com.simon.permissionlib.core.PermissionHelper;

/**
 * Created by guohaiyang on 2017/7/4.
 */

public class Test1Fragement extends Fragment {
    View rootView;
    Button call, sms;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragement_test1, container, false);
//        call= (Button) rootView.findViewById(R.id.call);
//        sms= (Button) rootView.findViewById(R.id.sms);
        rootView.findViewById(R.id.call).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PermissionHelper.requestPermissions(Test1Fragement.this, 100, new String[]{Manifest.permission.CALL_PHONE});
            }
        });
        rootView.findViewById(R.id.sms).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PermissionHelper.requestPermissions(Test1Fragement.this, 200, new String[]{Manifest.permission.SEND_SMS});
            }
        });
        return rootView;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Toast.makeText(getActivity(), "fragement", Toast.LENGTH_SHORT).show();
        PermissionHelper.onRequestPermissionsResult(Test1Fragement.this, requestCode, permissions, grantResults);
    }

    @PermissionSuccess(requestCode = 100)
    public void onSucess() {
        Toast.makeText(getActivity(), "Test2Activity:电话成功", Toast.LENGTH_SHORT).show();
    }

    @PermissionFail(requestCode = 100)
    public void onFail() {
        Toast.makeText(getActivity(), "Test2Activity:电话失败", Toast.LENGTH_SHORT).show();
    }

    @PermissionSuccess(requestCode = 200)
    public void onSucessSMS() {
        Toast.makeText(getActivity(), "Test2Activity:短信成功", Toast.LENGTH_SHORT).show();
    }

    @PermissionFail(requestCode = 200)
    public void onFailSMS() {
        Toast.makeText(getActivity(), "Test2Activity:短信失败", Toast.LENGTH_SHORT).show();
    }
}
