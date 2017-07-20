package com.simon.apppermissiondemo;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.simon.permissionlib.annotation.PermissionFail;
import com.simon.permissionlib.annotation.PermissionSuccess;
import com.simon.permissionlib.core.PermissionHelper;

/**
 * description:  测试 v4 下fragement
 * author: Simon
 * created at 2017/7/20 下午4:10
 */

public class Test3Fragement extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        //此处重用activity_test_activity的布局
        View view = inflater.from(getActivity()).inflate(R.layout.activity_test_activity, null);
        view.findViewById(R.id.call).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //请求电话权限
                //****-----此处使用的是fragement自己调用的，注意lisener 是自己-----****
                PermissionHelper.with(Test3Fragement.this).permissions(Manifest.permission.CALL_PHONE).requestCode(100).lisener(Test3Fragement.this).request();
            }
        });
        view.findViewById(R.id.sms).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //请求短信权限
                //****-----此处使用的是fragement的activity调用的，注意lisener 是自己-----****
                PermissionHelper.with(getActivity()).permissions(Manifest.permission.SEND_SMS).requestCode(200).lisener(Test3Fragement.this).request();
            }
        });
        return view;
    }

    @PermissionSuccess(requestCode = 100)
    public void onSucess() {
        Toast.makeText(getActivity(), "电话成功：使用 Fragement发起,注意lisener 是自己", Toast.LENGTH_SHORT).show();
    }

    @PermissionFail(requestCode = 100)
    public void onFail() {
        Toast.makeText(getActivity(), "电话失败：使用 Fragement发起,注意lisener 是自己", Toast.LENGTH_SHORT).show();
    }

    @PermissionSuccess(requestCode = 200)
    public void onSucessSMS() {
        Toast.makeText(getActivity(), "短信成功：使用 Activity发起,注意lisener 是自己", Toast.LENGTH_SHORT).show();
    }

    @PermissionFail(requestCode = 200)
    public void onFailSMS() {
        Toast.makeText(getActivity(), "短信失败：使用 Activity发起,注意lisener 是自己", Toast.LENGTH_SHORT).show();
    }
}
