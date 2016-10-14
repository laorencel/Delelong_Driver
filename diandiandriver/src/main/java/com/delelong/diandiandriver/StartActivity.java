package com.delelong.diandiandriver;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.delelong.diandiandriver.bean.Str;
import com.delelong.diandiandriver.http.MyHttpUtils;
import com.delelong.diandiandriver.menuActivity.AdActivity;

import java.io.File;
import java.util.List;

/**
 * Created by Administrator on 2016/8/26.
 */
public class StartActivity extends BaseActivity {

    private static final String TAG = "BAIDUMAPFORTEST";
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    init();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_start);

        setVoice();
        if (!setNetworkDialog(this)) {
        } else {
            toAd();
        }
    }
    AudioManager mAudioManager;
    private void setVoice() {
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mAudioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,
                mAudioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL),
                AudioManager.FLAG_PLAY_SOUND);
        Log.i(TAG, "setVoice: ");
    }

    /**
     * 跳转广告界面
     */
    public void toAd() {
        try {
            new Thread().sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        File adFile = new File(Str.ADIMAGEPATH_START);
        if (adFile.exists()) {
            startActivityForResult(new Intent(this, AdActivity.class), Str.REQUESTADCODE);
            overridePendingTransition(R.anim.in_alpha, R.anim.out_alpha);
        } else {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    handler.sendEmptyMessage(0);
                }
            }, 3000);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Str.REQUESTADCODE) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    handler.sendEmptyMessage(0);
                }
            });
        }
    }

    String phone, pwd;
    boolean firstLogin;

    private void init() {
        SharedPreferences preferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        firstLogin = preferences.getBoolean("firstLogin", true);
        if (firstLogin) {
            startActivity(new Intent(this, LoginActivity.class));
        } else {
            phone = preferences.getString("phone", null);
            pwd = preferences.getString("pwd", null);
            MyHttpUtils myHttpUtils = new MyHttpUtils(this);
            List<String> loginResult = myHttpUtils.login(Str.URL_LOGIN, phone, pwd);

            if (loginResult.get(0).equalsIgnoreCase("OK")) {
                //从服务器获取app更新信息，有新版本提示下载安装，无则直接进入app主界面
                int loginTimes = preferences.getInt("loginTimes",0);
                preferences.edit()
                        .putInt("loginTimes",++loginTimes)
                        .commit();

                startActivity(new Intent(this, DriverActivity.class));
            } else if (loginResult.get(0).equals("ERROR")) {
                Toast.makeText(this, "登陆出错,请重新登陆", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, LoginActivity.class));
            } else if (loginResult.get(0).equals("FAILURE")) {
                startActivity(new Intent(this, LoginActivity.class));
            }
        }
        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
        finish();
    }


}
