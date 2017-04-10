package com.delelong.diandiandriver.receiver;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.delelong.diandiandriver.BaseActivity;
import com.delelong.diandiandriver.DriverActivity;
import com.delelong.diandiandriver.MainActivity;
import com.delelong.diandiandriver.R;
import com.delelong.diandiandriver.bean.Str;
import com.delelong.diandiandriver.service.MyPushService;
import com.delelong.diandiandriver.utils.ExampleUtil;
import com.google.common.primitives.Ints;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import cn.jpush.android.api.JPushInterface;


/**
 * Created by Administrator on 2016/9/9.
 */

/**
 * 自定义接收器
 * <p/>
 * 如果不定义这个 Receiver，则：
 * 1) 默认用户会打开主界面
 * 2) 接收不到自定义消息
 */
public class MyJPushReceiver extends BroadcastReceiver {

    private static final String TAG = "BAIDUMAPFORTEST";
    boolean firstLogin;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            boolean c = BaseActivity.isServiceWorked(context, "com.delelong.diandiandriver.service.MyPushService");
            if (!c) {
                Intent service = new Intent(context, MyPushService.class);
                context.startService(service);
                Log.i(TAG, "Start MyPushService");
            }
        }
        SharedPreferences preferences = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        firstLogin = preferences.getBoolean("firstLogin", true);

        Bundle bundle = intent.getExtras();
        Log.i(TAG, "[MyReceiver] onReceive - " + intent.getAction() + ", extras: " + printBundle(bundle));

        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
            String registrationId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
            if (firstLogin) {
                preferences.edit().putString("registrationId", registrationId);
                Log.i(TAG, "[MyReceiver] putString: ");
            }
            Log.i(TAG, "[MyReceiver] 接收Registration Id : " + registrationId);
            //send the Registration Id to your server...
        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
            Log.i(TAG, "[MyReceiver] 接收到推送下来的自定义消息: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));
            String title = bundle.getString(JPushInterface.EXTRA_TITLE);
            int titleInt = Ints.tryParse(title);
//            Log.i(TAG, "onReceive:DriverActivity.class.getName():" + DriverActivity.class.getName());
            if (titleInt > 0) {//收到订单消息
                Intent orderIntent = sendOrderMessage(context, bundle);
                if (BaseActivity.isActivityRunning(context)) {
                    Log.i(TAG, "onReceive: 111");
                    context.sendBroadcast(orderIntent);
                    //应用跳转到前台
//                    Intent mainIntent = new Intent(context, DriverActivity.class);
//                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                    Log.i(TAG, "restartDriverActivity: 2222");
//                    context.startActivity(mainIntent);
                } else {
                    Log.i(TAG, "onReceive: 222");
                    orderIntent.setClass(context, DriverActivity.class);
                    orderIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(orderIntent);
                }
            }
        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            Log.i(TAG, "[MyReceiver] 接收到推送下来的通知");
            int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
            String alert = bundle.getString(JPushInterface.EXTRA_ALERT);
            Log.i(TAG, "[MyReceiver] 接收到推送下来的通知的消息: " + alert);

        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            Log.i(TAG, "[MyReceiver] 用户点击打开了通知");

            //打开自定义的Activity
            Intent i = new Intent(context, DriverActivity.class);
            i.putExtras(bundle);
            //i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(i);

        } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
            Log.i(TAG, "[MyReceiver] 用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
            //在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity， 打开一个网页等..

        } else if (JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
            boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
            Log.i(TAG, "[MyReceiver]" + intent.getAction() + " connected state change to " + connected);
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (!connected) {
                //手机系统原因，退到后台会有推送限制
                Log.i(TAG, "onReceive: resumePush");
                JPushInterface.init(context);

                Intent broadcastIntent = new Intent(context, MyNotificationReceiver.class);
                PendingIntent pendingIntent = PendingIntent.
                        getBroadcast(context, 0, broadcastIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
                builder.setContentTitle("点点服务")
                        .setTicker("点点服务发送的通知")
                        .setContentText("连接已断开，点击尝试重新连接")
                        .setContentIntent(pendingIntent)
                        .setSmallIcon(R.drawable.logo);
                notificationManager.notify(ID_JPUSH_DISCONNECT, builder.build());
            } else {
                notificationManager.cancel(ID_JPUSH_DISCONNECT);
            }
        } else {
            Log.i(TAG, "[MyReceiver] Unhandled intent - " + intent.getAction());
        }
    }

    public static final int FLAG_JPUSH_DISCONNECT = 1;
    public static final int ID_JPUSH_DISCONNECT = 11;

    // 打印所有的 intent extra 数据
    private static String printBundle(Bundle bundle) {
        StringBuilder sb = new StringBuilder();
        for (String key : bundle.keySet()) {
            if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
            } else if (key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
            } else if (key.equals(JPushInterface.EXTRA_EXTRA)) {
                if (bundle.getString(JPushInterface.EXTRA_EXTRA).isEmpty()) {
                    Log.i(TAG, "This message has no Extra data");
                    continue;
                }

                try {
                    JSONObject json = new JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA));
                    Iterator<String> it = json.keys();

                    while (it.hasNext()) {
                        String myKey = it.next().toString();
                        sb.append("\nkey:" + key + ", value: [" +
                                myKey + " - " + json.optString(myKey) + "]");
                    }
                } catch (JSONException e) {
                    Log.i(TAG, "Get message extra JSON error!");
                }
            } else {
                sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
            }
        }
        return sb.toString();
    }

    //send msg to MainActivity
    private void processCustomMessage(Context context, Bundle bundle) {
        String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
        String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
        Intent msgIntent = new Intent(MainActivity.MESSAGE_RECEIVED_ACTION);
        msgIntent.putExtra(MainActivity.KEY_MESSAGE, message);
        if (!ExampleUtil.isEmpty(extras)) {
            try {
                JSONObject extraJson = new JSONObject(extras);
                if (null != extraJson && extraJson.length() > 0) {
                    msgIntent.putExtra(MainActivity.KEY_EXTRAS, extras);
                }
            } catch (JSONException e) {

            }
        }
        context.sendBroadcast(msgIntent);
    }

    private Intent sendOrderMessage(Context context, Bundle bundle) {
        String title = bundle.getString(JPushInterface.EXTRA_TITLE);
        String orderMessage = bundle.getString(JPushInterface.EXTRA_MESSAGE);
        String orderExtras = bundle.getString(JPushInterface.EXTRA_EXTRA);
        Intent orderIntent = new Intent(Str.ORDER_MESSAGE_RECEIVED_ACTION);
        orderIntent.putExtra(Str.KEY_ORDER_TITLE, title);
        orderIntent.putExtra(Str.KEY_ORDER_MESSAGE, orderMessage);
        if (!ExampleUtil.isEmpty(orderExtras)) {
            try {
                JSONObject extraJson = new JSONObject(orderExtras);
                if (null != extraJson && extraJson.length() > 0) {
                    orderIntent.putExtra(Str.KEY_ORDER_EXTRA, orderExtras);
                }
            } catch (JSONException e) {

            }
        }
        return orderIntent;
//        context.sendBroadcast(orderIntent);
    }

}


