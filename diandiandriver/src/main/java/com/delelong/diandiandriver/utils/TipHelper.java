package com.delelong.diandiandriver.utils;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Vibrator;

/**
 * Created by Administrator on 2016/10/16.
 */

/**
 * 信息提示类
 */
public class TipHelper {
    SharedPreferences preferences;
    Activity activity;
    public TipHelper(Activity activity) {
        this.activity = activity;
        preferences = activity.getSharedPreferences("user", Context.MODE_PRIVATE);
    }

    /**
     *
     * @param milliseconds 震动的时长，单位是毫秒
     */
    public void Vibrate( long milliseconds) {
        boolean vibrateEnable = preferences.getBoolean("vibrate",false);
        if (vibrateEnable){
            Vibrator vib = (Vibrator) activity.getSystemService(Service.VIBRATOR_SERVICE);
            vib.vibrate(milliseconds);
        }
    }

    /**
     *
     * @param pattern 自定义震动模式 。数组中数字的含义依次是静止的时长，震动时长，静止时长，震动时长。。。时长的单位是毫秒
     * @param isRepeat 是否反复震动，如果是true，反复震动，如果是false，只震动一次
     */
    public void Vibrate(long[] pattern,boolean isRepeat) {
        boolean vibrateEnable = preferences.getBoolean("vibrate",false);
        if (vibrateEnable){
            Vibrator vib = (Vibrator) activity.getSystemService(Service.VIBRATOR_SERVICE);
            vib.vibrate(pattern, isRepeat ? 1 : -1);
        }
    }
}
