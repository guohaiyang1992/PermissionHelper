package com.simon.permissionlib.core;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.simon.permissionlib.annotation.PermissionFail;
import com.simon.permissionlib.annotation.PermissionSuccess;
import com.simon.permissionlib.fragement.PermissionsFragment;
import com.simon.permissionlib.utils.PermissionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.simon.permissionlib.utils.PermissionUtils.getActivity;

/**
 * description: 权限类重写，去除非必要静态方法
 * author: Simon
 * created at 2017/7/20 下午3:14
 * 1.增加内部权限回调，用户无需自己写回调
 * 2.用户判断必须传入lisener
 * 3.用户需要用注解写回调方法
 */

public class PermissionHelper {
    //--基础必备数据--
    private String[] mPermission;//请求的权限
    private int mRequestCode = Integer.MIN_VALUE; //请求码
    private Object mObject;//上下文环境，支持activity、fragement、v4.fragement
    private PermissionsFragment permissionsFragment;//用于接收权限的fragement
    private static final String TAG = "PermissionHelper";

    //--以requestCode为key--
    private String hint = null;//再次申请的时候提示的内容
    private List<Object> liseners = new ArrayList<>();//回调监听（不设置此处按照上下文环境回调，设置此处按照此处回调）

    //保存上下文环境
    private PermissionHelper(@NonNull Object context) {
        mObject = context;
    }

    //传入activity
    public static PermissionHelper with(@NonNull Activity activity) {
        return new PermissionHelper(activity);
    }

    //传入fragement
    public static PermissionHelper with(@NonNull Fragment fragment) {
        return new PermissionHelper(fragment);
    }

    //传入v4下的fragement
    public static PermissionHelper with(@NonNull android.support.v4.app.Fragment fragment) {
        return new PermissionHelper(fragment);
    }

    //传入所需要的权限
    public PermissionHelper permissions(String... permissions) {
        this.mPermission = permissions;
        return this;
    }

    //传入请求code，用于匹配回调方法
    public PermissionHelper requestCode(int code) {
        this.mRequestCode = code;
        return this;
    }


    /**
     * 添加 hint 内容
     *
     * @param msg --用于再次申请权限时的提醒内容
     */
    public PermissionHelper hintMessage(String msg) {
        if (!TextUtils.isEmpty(msg)) {
            hint = msg;
        }
        return this;
    }

    /**
     * 传入监听，用于回调(非必要传入)
     *
     * @param lisener --回调监听
     */
    public PermissionHelper lisener(Object... lisener) {
        if (lisener != null) {
            this.liseners = Arrays.asList(lisener);
        }

        return this;
    }

    /**
     * 权限请求方法
     */
    @TargetApi(Build.VERSION_CODES.M)
    public void request() {
        requestPermissions(mRequestCode, mPermission);
    }

    /**
     * 具体的请求方法
     *
     * @param requestCode --请求码
     * @param permissions --请求权限
     */
    @TargetApi(Build.VERSION_CODES.M)
    public void requestPermissions(final int requestCode, String[] permissions) {
        //判断是否需要申请权限，不满足申请条件则直接回调成功  //sdk较小，没有传入所需权限-> 回调成功
        if (!PermissionUtils.needRequestPermission() || permissions == null || permissions.length == 0) {
            doExecuteSuccess(requestCode);
            return;
        }
        //获取所有权限中未获取的权限
        final List<String> deniedPermissions = PermissionUtils.findDeniedPermissions(getActivity(mObject), permissions);

        //筛选后所需权限 大于0 还需申请权限
        if (deniedPermissions.size() > 0) {
            //需要向用户解释为什么申请这个权限
            boolean shouldShowRationale = false;
            //如果含有拒绝过得权限则提示
            for (String perm : permissions) {
                shouldShowRationale =
                        shouldShowRationale || shouldShowRequestPermissionRationale(perm);
            }

            //提示用户，为何再次申请权限
            if (shouldShowRationale) {
                showMessageOKCancel(requestCode, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //用户确定后开始申请权限
                        executePermissionsRequest(deniedPermissions.toArray(new String[deniedPermissions.size()]), requestCode);
                    }
                });
            } else {
                //如果不需要提示，之前没拒绝过
                executePermissionsRequest(deniedPermissions.toArray(new String[deniedPermissions.size()]), requestCode);
            }

        } else {
            //回调成功
            doExecuteSuccess(requestCode);
        }
    }

    /**
     * 执行申请权限
     *
     * @param perms       --权限
     * @param requestCode --请求集合
     */
    @TargetApi(Build.VERSION_CODES.M)
    private void executePermissionsRequest(@NonNull String[] perms, int requestCode) {
        //检查上下文
        checkType();
        //初始化fragement,如果有问题将抛出异常
        ifNotCreatePermissionsFragment();
        //申请权限
        permissionsFragment.requestPermissions(perms, requestCode);
    }

    /**
     * 是否需要提示说明
     *
     * @param perm --权限
     * @return true表示需要，false 表示不需要
     */
    @TargetApi(Build.VERSION_CODES.M)
    private boolean shouldShowRequestPermissionRationale(String perm) {
        //检查上下文
        checkType();
        //获取fragement
        ifNotCreatePermissionsFragment();
        //申请权限
        return permissionsFragment.shouldShowRequestPermissionRationale(perm);
    }

    /**
     * 成功时回调
     *
     * @param requestCode
     */
    private void doExecuteSuccess(int requestCode) {
        //首先判断,是否写了监听回调，如果没有则看当前的调用类是否有
        if (checkHasLisener()) {
            for (Object lisener : liseners) {
                findAndExecuteMethod(lisener, requestCode, PermissionSuccess.class);
            }
        }

    }

    /**
     * 失败时回调
     *
     * @param requestCode
     */
    private void doExecuteFail(int requestCode) {
        if (checkHasLisener()) {
            for (Object lisener : liseners) {
                findAndExecuteMethod(lisener, requestCode, PermissionFail.class);
            }
        }

    }

    /**
     * 根据对应的 object 、requestCode、annotation 寻找并执行对应的方法
     *
     * @param lisenser    --具体对象
     * @param requestCode --请求码
     * @param annotation  --注解
     * @param <A>         --注解类型
     */
    private <A extends Annotation> void findAndExecuteMethod(Object lisenser, int requestCode, Class<A> annotation) {
        Method executeMethod = PermissionUtils.findMethodWithRequestCode(lisenser.getClass(),
                annotation, requestCode);
        executeMethod(lisenser, executeMethod);
    }

    /**
     * 反射执行方法
     *
     * @param object        --具体对象  lisener
     * @param executeMethod --对应方法
     */
    private void executeMethod(Object object, Method executeMethod) {
        if (executeMethod != null) {
            try {
                if (!executeMethod.isAccessible()) executeMethod.setAccessible(true);
                executeMethod.invoke(object, new Object[]{});//此处传null也可
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 检查是否有监听
     *
     * @return --true 表示有监听, false 表示没有
     */
    private boolean checkHasLisener() {
        return liseners != null && liseners.size() > 0;
    }

    /**
     * 提示为什么需要申请该权限
     *
     * @param okListener --点击确定的时候回调
     */
    private void showMessageOKCancel(final int requestCode, final DialogInterface.OnClickListener okListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(mObject));
        builder.setTitle("提示");
        builder.setMessage(getHintMessage());

        // 拒绝, 回调 权限申请失败
        builder.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        doExecuteFail(requestCode);
                    }
                });
        //确定
        builder.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (okListener != null) {
                            okListener.onClick(dialog, which);
                        }
                    }
                });

        builder.setCancelable(false);
        builder.show();
    }

    /**
     * 获取再次申请权限时的提示
     *
     * @return hint 默认值或者用户设置的值
     */
    private String getHintMessage() {
        if (TextUtils.isEmpty(hint)) {
            hint = "当前应用缺少必要权限,且在此之前您曾经拒绝过授权，为正常使用该应用，请允许打开对应权限。\n";
        }
        return hint;
    }

    /**
     * 权限申请结果回调的方法
     *
     * @param requestCode  -请求码
     * @param permissions  -请求权限
     * @param grantResults -请求结果
     */
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        requestResult(requestCode, permissions, grantResults);
    }


    /**
     * 权限结果回调
     *
     * @param requestCode  --请求码
     * @param permissions  --请求权限
     * @param grantResults --权限结果
     */
    private void requestResult(int requestCode, String[] permissions,
                               int[] grantResults) {
        List<String> deniedPermissions = new ArrayList<>();
        for (int i = 0; i < grantResults.length; i++) {
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                deniedPermissions.add(permissions[i]);
            }
        }

        //当权限都允许后则 回调成功，反之显示缺少必要权限
        if (deniedPermissions.size() > 0) {
            showMissingPermissionDialog(requestCode);
        } else {
            doExecuteSuccess(requestCode);
        }
    }


    /**
     * 提示，缺少必要权限，是否开启，如果不开启则回调申请权限失败，反之进入设置
     *
     * @param requestCode --申请权限的requestCode
     */
    private void showMissingPermissionDialog(final int requestCode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(mObject));
        builder.setTitle("提示");
        builder.setMessage("当前应用缺少必要权限，是否进行开启");

        // 拒绝, 回调 权限申请失败
        builder.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        doExecuteFail(requestCode);
                    }
                });
        //确定，跳转设置开启权限
        builder.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startAppSettings();
                    }
                });

        builder.setCancelable(false);
        builder.show();
    }

    /**
     * 启动应用的设置
     */
    private void startAppSettings() {
        Intent intent = new Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + getActivity(mObject).getPackageName()));
        getActivity(mObject).startActivity(intent);
    }


    /**
     * fragementactivity是activity父类 所以此处可以统一使用activity,此处获取附属的fragement
     *
     * @return --权限fragement
     */
    private PermissionsFragment ifNotCreatePermissionsFragment() {
        //获取当前的activity
        Activity activity = getActivity(mObject);
        //根据宿主activity获取权限fragement
        permissionsFragment = findPermissionsFragment(activity);
        //判断是否之前添加过
        boolean isNew = permissionsFragment == null;
        //如果是未添加的则添加一次
        if (isNew) {
            permissionsFragment = new PermissionsFragment();
            permissionsFragment.setPermissionHelper(this);//将当前helper传入，为了后续回调
            FragmentManager fm = activity.getFragmentManager();
            fm.beginTransaction().add(permissionsFragment, TAG).commitAllowingStateLoss();
            fm.executePendingTransactions();
        }
        return permissionsFragment;
    }

    /**
     * 使用tag获取fragement
     *
     * @param activity --宿主 activity
     * @return --权限 fragement
     */
    private PermissionsFragment findPermissionsFragment(Activity activity) {
        return (PermissionsFragment) activity.getFragmentManager().findFragmentByTag(TAG);
    }

    /**
     * 检查传入的类的类型，如果是不支持的则抛出异常
     */
    private void checkType() {
        if (!(mObject instanceof android.app.Activity || mObject instanceof android.support.v4.app.Fragment || mObject instanceof android.app.Fragment)) {
            throw new IllegalArgumentException(mObject.getClass().getName() + " is not supported");
        }
    }


}
