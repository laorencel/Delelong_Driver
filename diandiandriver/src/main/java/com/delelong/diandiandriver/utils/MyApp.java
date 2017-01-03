package com.delelong.diandiandriver.utils;

import android.support.multidex.MultiDexApplication;

import com.squareup.leakcanary.LeakCanary;

/**
 * Created by Administrator on 2016/8/11.
 */
public class MyApp extends MultiDexApplication {

    private static MyApp myApp;
    @Override
    public void onCreate() {
        super.onCreate();
        myApp = this;
        LeakCanary.install(this);
    }
    public static MyApp getInstance(){
        return myApp;
    }
}
