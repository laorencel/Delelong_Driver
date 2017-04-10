package com.delelong.diandiandriver.utils;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.delelong.diandiandriver.bean.Str;


/**
 * Created by Administrator on 2017/3/9.
 */

public class PermissionUtils {

    /**
     * 打开设置界面
     * @param context
     */
    public static  void startManagerSettingIntent(Context context,String pkg, String cls){
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ComponentName componentName = new ComponentName(pkg,cls);
        intent.setComponent(componentName);
        try{
            context.startActivity(intent);
        }catch (Exception e){//抛出异常就直接打开设置页面
            intent=new Intent(Settings.ACTION_SETTINGS);
            context.startActivity(intent);
        }
    }

    /**
     * 自启动（未完成）
     * @param context
     * @param pkg
     * @param cls
     */
    public static void permissionSelfStart(Context context,String pkg, String cls) {
        //乐视：Letv   魅族：Meizu    OPPO:OPPO
        Log.i(Str.TAG, "onCreate:品牌 " + android.os.Build.MANUFACTURER);

        String manufacturer = android.os.Build.MANUFACTURER;//品牌名称

        PackageManager packageManager = context.getPackageManager();
        ComponentName componentName = new ComponentName(pkg, cls);
        int res = packageManager.getComponentEnabledSetting(componentName);
        Log.i(Str.TAG, "onCreate:自启动权限 " + res);
        /**
         componentName：组件名称
         newState：组件新的状态，可以设置三个值，分别是如下：
         不可用状态：2：COMPONENT_ENABLED_STATE_DISABLED
         可用状态:1：COMPONENT_ENABLED_STATE_ENABLED
         默认状态:0：COMPONENT_ENABLED_STATE_DEFAULT
         flags:行为标签，值可以是DONT_KILL_APP或者0。 0说明杀死包含该组件的app
         */
        if (res == PackageManager.COMPONENT_ENABLED_STATE_DISABLED || res == PackageManager.COMPONENT_ENABLED_STATE_DEFAULT) {
            // 隐藏应用图标
//            packageManager.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
//                    PackageManager.DONT_KILL_APP);
            /**
             * 跳转到自启动页面
             *
             * 华为 com.huawei.systemmanager/com.huawei.systemmanager.startupmgr.ui.StartupNormalAppListActivity
             * 小米 com.miui.securitycenter/com.miui.permcenter.autostart.AutoStartManagementActivity
             * vivo com.iqoo.secure/.ui.phoneoptimize.AddWhiteListActivity
             * oppo com.coloros.oppoguardelf/com.coloros.powermanager.fuelgaue.PowerUsageModelActivity
             *
             */
            switch (manufacturer) {
                case "OPPO":
                    PermissionUtils.startManagerSettingIntent(context,
                            "com.coloros.oppoguardelf", "com.coloros.powermanager.fuelgaue.PowerUsageModelActivity");
                    break;
            }

        } else if (res == PackageManager.COMPONENT_ENABLED_STATE_ENABLED) {

        }
    }
    public static void permissionCallPhone(Activity activity) {
        if (ContextCompat.checkSelfPermission(activity, Str.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {
            //申请android.permission.CALL_PHONE
            ActivityCompat.requestPermissions(activity, new String[]{Str.CALL_PHONE}, Str.REQUEST_CALL_PHONE);
        }
    }
}
