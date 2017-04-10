package com.delelong.diandiandriver.jpushtip.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.delelong.diandiandriver.DriverActivity;
import com.delelong.diandiandriver.R;
import com.delelong.diandiandriver.base.activity.MBaseActivity;
import com.delelong.diandiandriver.base.common.utils.ActivityUtils;
import com.delelong.diandiandriver.view.AutoSplitTextView;

/**
 * Created by Administrator on 2017/3/15.
 */

public class JPushTipActivity extends MBaseActivity{
    @Override
    public View addTitleView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return null;
    }

    AutoSplitTextView tv_tip;
    @NonNull
    @Override
    public View addCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_jpush_tip,null);
        tv_tip = (AutoSplitTextView) view.findViewById(R.id.tv_tip);
        tv_tip.setText(getText(R.string.jpushTip));
        return view;
    }

    @Override
    public void onActivityStart() {
        setTitle("解决无法收到推送订单办法");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //若没有找到运行的task，用户结束了task或被系统释放，则重新启动mainactivity
//        Intent resultIntent = new Intent(context, DriverActivity.class);
//                Intent resultIntent = new Intent(this, JPushTipActivity.class);
//                resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                Log.i(Str.TAG, "restartDriverActivity: 2222");
//                startActivity(resultIntent);
                ActivityUtils.startActivityIfAlive(this, DriverActivity.class);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            ActivityUtils.startActivityIfAlive(this, DriverActivity.class);
        }
        return super.onKeyDown(keyCode, event);
    }
}
