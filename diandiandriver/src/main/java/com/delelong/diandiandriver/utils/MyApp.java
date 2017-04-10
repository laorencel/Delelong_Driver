package com.delelong.diandiandriver.utils;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.delelong.diandiandriver.bean.Str;
import com.delelong.diandiandriver.http.MyAsyncHttpUtils;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by Administrator on 2016/8/11.
 */
public class MyApp extends MultiDexApplication {

    private static final String TAG = "Baidumapfortest";
    private static MyApp myApp;

    @Override
    public void onCreate() {
        super.onCreate();
        myApp = this;
//        LeakCanary.install(this);
        JPushInterface.init(this);
        Log.i(TAG, "onCreate:JPushInterface.getRegistrationID: "+JPushInterface.getRegistrationID(myApp));
        Log.i(TAG, "Str.URL_LOGIN: "+ Str.URL_LOGIN);

        UMShareAPI.get(this);
        MyAsyncHttpUtils.setHeader(MyAsyncHttpUtils.getAsyncHttpHeader());
    }
    //各个平台的配置，建议放在全局Application或者程序入口
    {
        PlatformConfig.setWeixin("wxc98901c7b1969502", "b4e8f62642b1a1354fdca27140c8499e");
        //豆瓣RENREN平台目前只能在服务器端配置
        PlatformConfig.setQQZone("1105749047", "1105749047");
//        PlatformConfig.setSinaWeibo("3921700954", "04b48b094faeb16683c32669824ebdad","http://sns.whalecloud.com");
//        PlatformConfig.setYixin("yxc0614e80c9304c11b0391514d09f13bf");
//        PlatformConfig.setTwitter("3aIN7fuF685MuZ7jtXkQxalyi", "MK6FEYG63eWcpDFgRYw4w9puJhzDl0tyuqWjZ3M7XJuuG7mMbO");
//        PlatformConfig.setAlipay("2015111700822536");
//        PlatformConfig.setLaiwang("laiwangd497e70d4", "d497e70d4c3e4efeab1381476bac4c5e");
//        PlatformConfig.setPinterest("1439206");
//        PlatformConfig.setKakao("e4f60e065048eb031e235c806b31c70f");
//        PlatformConfig.setDing("dingoalmlnohc0wggfedpk");
//        PlatformConfig.setVKontakte("5764965","5My6SNliAaLxEm3Lyd9J");
//        PlatformConfig.setDropbox("oz8v5apet3arcdy","h7p2pjbzkkxt02a");

    }
    public static MyApp getInstance() {
        return myApp;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
