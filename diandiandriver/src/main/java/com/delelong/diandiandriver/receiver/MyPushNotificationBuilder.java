package com.delelong.diandiandriver.receiver;

import android.content.Context;

import com.delelong.diandiandriver.R;

import cn.jpush.android.api.CustomPushNotificationBuilder;
import cn.jpush.android.api.JPushInterface;

/**
 * Created by Administrator on 2016/10/16.
 */

public class MyPushNotificationBuilder  {

    private Context context;
    public MyPushNotificationBuilder(Context context) {
        this.context = context;
    }

    public CustomPushNotificationBuilder setOrderNotificationBuilder(){
        CustomPushNotificationBuilder builder = new
                CustomPushNotificationBuilder(context,
                R.layout.builder_notitfication_order,R.id.icon,R.id.title,R.id.text);
        // 指定最顶层状态栏小图标
        builder.statusBarDrawable = R.mipmap.ic_diandian;
        // 指定下拉状态栏时显示的通知图标
        builder.layoutIconDrawable = R.mipmap.ic_diandian;
        JPushInterface.setPushNotificationBuilder(2,builder);
        return builder;
    }
}
