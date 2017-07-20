package com.simon.permissionlib.fragement;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.simon.permissionlib.core.PermissionHelper;

public class PermissionsFragment extends Fragment {

    private PermissionHelper permissionHelper;

    public PermissionsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //防止转屏导致重启
        setRetainInstance(true);
    }


    @TargetApi(Build.VERSION_CODES.M)
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        notifyResult(requestCode, permissions, grantResults);
    }

    private void notifyResult(int requestCode, String[] permissions, int[] grantResults) {
        if (permissionHelper != null) {
            permissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    public void setPermissionHelper(PermissionHelper permissionHelper) {
        this.permissionHelper = permissionHelper;
    }
}
