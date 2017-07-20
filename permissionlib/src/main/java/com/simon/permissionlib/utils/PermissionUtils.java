package com.simon.permissionlib.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.content.pm.PackageManager;
import android.os.Build;

import com.simon.permissionlib.annotation.PermissionFail;
import com.simon.permissionlib.annotation.PermissionSuccess;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * description:  辅助类
 * autour: Simon
 * created at 2017/7/4 下午7:03
 */

public class PermissionUtils {
    /**
     * 当前版本大于等于 6.0则需要请求运行时权限，反之不需要
     *
     * @return
     */
    public static boolean needRequestPermission() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    /**
     * 获取权限中没用被授权的权限
     *
     * @param activity
     * @param permission
     * @return
     */
    @TargetApi(value = Build.VERSION_CODES.M)
    public static List<String> findDeniedPermissions(Activity activity, String... permission) {
        List<String> denyPermissions = new ArrayList<>();
        for (String value : permission) {
            if (activity.checkSelfPermission(value) != PackageManager.PERMISSION_GRANTED) {
                denyPermissions.add(value);
            }
        }
        return denyPermissions;
    }

    /**
     * 获取 真实的上下文
     *
     * @param object
     * @return
     */
    public static Activity getActivity(Object object) {
        if (object instanceof Fragment) {
            return ((Fragment) object).getActivity();
        } else if (object instanceof android.support.v4.app.Fragment) {
            return ((android.support.v4.app.Fragment) object).getActivity();
        } else if (object instanceof Activity) {
            return (Activity) object;
        }

        throw new RuntimeException("传入的上下文环境错误,请重新设置为activity或者fragement！");
    }

    /**
     * 判断是否 满足 注解和requestcode的双重条件
     *
     * @param m           --方法
     * @param clazz       --clazz
     * @param requestCode --请求码
     * @return
     */
    public static boolean isEqualRequestCodeFromAnntation(Method m, Class clazz, int requestCode) {
        if (clazz.equals(PermissionFail.class)) {
            return requestCode == m.getAnnotation(PermissionFail.class).requestCode();
        } else if (clazz.equals(PermissionSuccess.class)) {
            return requestCode == m.getAnnotation(PermissionSuccess.class).requestCode();
        } else {
            return false;
        }
    }

    public static <A extends Annotation> Method findMethodWithRequestCode(Class clazz,
                                                                          Class<A> annotation, int requestCode) {
        //获取当前监听的所有方法
        for (Method method : clazz.getDeclaredMethods()) {
            //判断方法上方是否有对应的注解
            if (method.isAnnotationPresent(annotation)) {
                //是否满足对应的 注解和requestCode
                if (isEqualRequestCodeFromAnntation(method, annotation, requestCode)) {
                    return method;
                }
            }
        }
        return null;
    }

    public static void checkNull(Object object, String msg) {
        if (object == null) {
            throw new NullPointerException(msg);
        }
    }
}
