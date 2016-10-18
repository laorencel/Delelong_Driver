package com.delelong.diandiandriver.listener;

import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;

/**
 * Created by Administrator on 2016/9/21.
 */
public class MyDrawerLayoutListener implements DrawerLayout.DrawerListener {

    private static final String TAG = "BAIDUMAPFORTEST";
    DrawerLayout mDrawerLayout;

    public MyDrawerLayoutListener(DrawerLayout mDrawerLayout) {
        this.mDrawerLayout = mDrawerLayout;
    }

    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) {

    }
    //侧边栏弹出之后也无法使用滑动来关闭，只能通过点击空白区域来关闭侧边栏
    @Override
    public void onDrawerOpened(View drawerView) {
        Log.i(TAG, "onDrawerOpened: ");
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);//打开手势滑动
    }

    @Override
    public void onDrawerClosed(View drawerView) {
        Log.i(TAG, "onDrawerClosed: ");
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);//关闭手势滑动
    }

    @Override
    public void onDrawerStateChanged(int newState) {

    }
}
