package com.simon.permissionlib.core;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;

import com.simon.permissionlib.R;
import com.simon.permissionlib.annotation.PermissionFail;
import com.simon.permissionlib.annotation.PermissionSuccess;
import com.simon.permissionlib.utils.Utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.simon.permissionlib.utils.Utils.getActivity;

/**
 * description: 权限工具类
 * autour: Simon
 * created at 2017/7/4 下午6:40
 */

public class PermissionHelper {
    //--基础必备数据--
    private String[] mPermission;//请求的权限
    private int mRequestCode = Integer.MIN_VALUE; //请求码
    private Object mObject;//上下文环境，支持activity、fragement、v4.fragement

    //--以requestCode为key--
    private static Map<Integer, String> hintMap = new HashMap<>();//再次申请的时候提示的内容
    private static Map<Integer, List<Object>> lisenertMap = new HashMap<>();//回调监听（不设置此处按照上下文环境回调，设置此处按照此处回调）

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
        updateValueMap();//更新对应code的内容
        return this;
    }

    //更新 key
    private void updateValueMap() {
        if (mRequestCode != Integer.MIN_VALUE) {
            //hint
            String hint = hintMap.get(Integer.MIN_VALUE);
            if (!TextUtils.isEmpty(hint)) {
                hintMap.remove(Integer.MIN_VALUE);
                hintMap.put(mRequestCode, hint);
            }
            //lisener
            List<Object> list = lisenertMap.get(Integer.MIN_VALUE);
            if (list != null) {
                lisenertMap.remove(Integer.MIN_VALUE);
                lisenertMap.put(mRequestCode, list);
            }
        }
    }

    /**
     * 添加 hint 内容
     *
     * @param msg --用于再次申请权限时的提醒内容
     */
    public PermissionHelper hintMessage(String msg) {
        if (!TextUtils.isEmpty(msg)) {
            hintMap.put(mRequestCode, msg);
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
            List<Object> temp = new ArrayList<>();
            Collections.addAll(temp, lisener);
            lisenertMap.put(mRequestCode, temp);
        }

        return this;
    }

    /**
     * 权限请求方法
     */
    @TargetApi(Build.VERSION_CODES.M)
    public void request() {
        requestPermissions(mObject, mRequestCode, mPermission);
    }

    /**
     * 具体的请求方法
     *
     * @param object      --上下文环境
     * @param requestCode --请求码
     * @param permissions --请求权限
     */
    @TargetApi(Build.VERSION_CODES.M)
    public static void requestPermissions(final Object object, final int requestCode, String[] permissions) {
        //判断是否需要申请权限，不满足申请条件则直接回调成功  //sdk较小，没有传入所需权限-> 回调成功
        if (!Utils.needRequestPermission() || permissions == null || permissions.length == 0) {
            doExecuteSuccess(object, requestCode);
            return;
        }
        //获取所有权限中未获取的权限
        final List<String> deniedPermissions = Utils.findDeniedPermissions(getActivity(object), permissions);

        //筛选后所需权限 大于0 还需申请权限
        if (deniedPermissions.size() > 0) {
            //需要向用户解释为什么申请这个权限
            boolean shouldShowRationale = false;
            //如果含有拒绝过得权限则提示
            for (String perm : permissions) {
                shouldShowRationale =
                        shouldShowRationale || shouldShowRequestPermissionRationale(object, perm);
            }

            //提示用户，为何再次申请权限
            if (shouldShowRationale) {
                showMessageOKCancel(object, requestCode, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //用户确定后开始申请权限
                        executePermissionsRequest(object, deniedPermissions.toArray(new String[deniedPermissions.size()]), requestCode);
                    }
                });
            } else {
                //如果不需要提示，之前没拒绝过
                executePermissionsRequest(object, deniedPermissions.toArray(new String[deniedPermissions.size()]), requestCode);
            }

        } else {
            //回调成功
            doExecuteSuccess(object, requestCode);
        }
    }

    /**
     * 执行申请权限
     *
     * @param object      --上下文环境
     * @param perms       --权限
     * @param requestCode --请求集合
     */
    @TargetApi(Build.VERSION_CODES.M)
    private static void executePermissionsRequest(@NonNull Object object, @NonNull String[] perms, int requestCode) {
        if (object instanceof android.app.Activity) {
            ActivityCompat.requestPermissions((Activity) object, perms, requestCode);
        } else if (object instanceof android.support.v4.app.Fragment) {
            ((android.support.v4.app.Fragment) object).requestPermissions(perms, requestCode);
        } else if (object instanceof android.app.Fragment) {
            ((android.app.Fragment) object).requestPermissions(perms, requestCode);
        } else {
            throw new IllegalArgumentException(object.getClass().getName() + " is not supported");
        }

    }

    /**
     * 是否需要提示说明
     *
     * @param object --上下文环境
     * @param perm   --权限
     * @return true表示需要，false 表示不需要
     */
    @TargetApi(Build.VERSION_CODES.M)
    private static boolean shouldShowRequestPermissionRationale(Object object, String perm) {
        if (object instanceof Activity) {
            return ActivityCompat.shouldShowRequestPermissionRationale((Activity) object, perm);
        } else if (object instanceof Fragment) {
            return ((Fragment) object).shouldShowRequestPermissionRationale(perm);
        } else if (object instanceof android.app.Fragment) {
            return ((android.app.Fragment) object).shouldShowRequestPermissionRationale(perm);
        } else {
            return false;
        }
    }

    /**
     * 成功时回调
     *
     * @param activity
     * @param requestCode
     */
    private static void doExecuteSuccess(Object activity, int requestCode) {
        //首先判断,是否写了监听回调，如果没有则看当前的调用类是否有
        if (checkHasLisener(requestCode)) {
            List<Object> lisener = lisenertMap.get(requestCode);
            for (Object ob : lisener) {
                findAndExecuteMethod(ob, requestCode, PermissionSuccess.class);
            }

        } else {//没有的时候，回调调用的上下文
            findAndExecuteMethod(activity, requestCode, PermissionSuccess.class);
        }

    }

    /**
     * 失败时回调
     *
     * @param activity
     * @param requestCode
     */
    private static void doExecuteFail(Object activity, int requestCode) {
        if (checkHasLisener(requestCode)) {
            List<Object> lisener = lisenertMap.get(requestCode);
            for (Object ob : lisener) {
                findAndExecuteMethod(ob, requestCode, PermissionFail.class);
            }

        } else {//没有的时候，回调调用的上下文
            findAndExecuteMethod(activity, requestCode, PermissionFail.class);
        }

    }

    /**
     * 根据对应的 object 、requestCode、annotation 寻找并执行对应的方法
     *
     * @param ob          --具体对象
     * @param requestCode --请求码
     * @param annotation  --注解
     * @param <A>         --注解类型
     */
    private static <A extends Annotation> void findAndExecuteMethod(Object ob, int requestCode, Class<A> annotation) {
        Method executeMethod = Utils.findMethodWithRequestCode(ob.getClass(),
                annotation, requestCode);
        executeMethod(ob, executeMethod);
    }

    /**
     * 反射执行方法
     *
     * @param object        --具体对象
     * @param executeMethod --对应方法
     */
    private static void executeMethod(Object object, Method executeMethod) {
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
    private static boolean checkHasLisener(int requestCode) {
        List<Object> lisener = lisenertMap.get(requestCode);
        return lisener != null && lisener.size() > 0;
    }

    /**
     * 提示为什么需要申请该权限
     *
     * @param object     --上下文
     * @param okListener --点击确定的时候回调
     */
    private static void showMessageOKCancel(final Object object, final int requestCode, final DialogInterface.OnClickListener okListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(object));
        builder.setTitle("提示");
        builder.setMessage(getHintMessage(requestCode));

        // 拒绝, 回调 权限申请失败
        builder.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        doExecuteFail(object, requestCode);
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
    private static String getHintMessage(int requestcode) {
        String hint = hintMap.get(requestcode);
        if (TextUtils.isEmpty(hint)) {
            hint = "当前应用缺少必要权限,且在此之前您曾经拒绝过授权，为正常使用该应用，请允许打开对应权限。\n";
        }
        return hint;
    }

    /**
     * 权限申请结果回调的方法
     *
     * @param ob           -上下文环境（activity或者fragement）
     * @param requestCode  -请求码
     * @param permissions  -请求权限
     * @param grantResults -请求结果
     */
    public static void onRequestPermissionsResult(Object ob, int requestCode, String[] permissions,
                                                  int[] grantResults) {
        requestResult(ob, requestCode, permissions, grantResults);
    }


    private static void requestResult(Object obj, int requestCode, String[] permissions,
                                      int[] grantResults) {
        List<String> deniedPermissions = new ArrayList<>();
        for (int i = 0; i < grantResults.length; i++) {
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                deniedPermissions.add(permissions[i]);
            }
        }

        //当权限都允许后则 回调成功，反之显示缺少必要权限
        if (deniedPermissions.size() > 0) {
            showMissingPermissionDialog(obj, requestCode);
        } else {
            doExecuteSuccess(obj, requestCode);
        }
    }


    /**
     * 提示，缺少必要权限，是否开启，如果不开启则回调申请权限失败，反之进入设置
     *
     * @param object      --上下文
     * @param requestCode --申请权限的requestCode
     */
    private static void showMissingPermissionDialog(final Object object, final int requestCode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(object));
        builder.setTitle("提示");
        builder.setMessage("当前应用缺少必要权限，是否进行开启");

        // 拒绝, 回调 权限申请失败
        builder.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        doExecuteFail(object, requestCode);
                    }
                });
        //确定，跳转设置开启权限
        builder.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startAppSettings(object);
                    }
                });

        builder.setCancelable(false);
        builder.show();
    }

    /**
     * 启动应用的设置
     */
    private static void startAppSettings(Object object) {
        Intent intent = new Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + getActivity(object).getPackageName()));
        getActivity(object).startActivity(intent);
    }


}
