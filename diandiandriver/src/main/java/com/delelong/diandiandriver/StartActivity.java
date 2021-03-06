package com.delelong.diandiandriver;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.delelong.diandiandriver.bean.Str;
import com.delelong.diandiandriver.http.MyAsyncHttpUtils;
import com.delelong.diandiandriver.http.MyHttpHelper;
import com.delelong.diandiandriver.http.MyHttpUtils;
import com.delelong.diandiandriver.http.MyTextHttpResponseHandler;
import com.delelong.diandiandriver.start.MyStartViewPagerActivity;

import java.io.File;
import java.util.List;

import cz.msebera.android.httpclient.Header;

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
                case 1:
                    toAd();
                    break;
            }
        }
    };

    MyHttpUtils myHttpUtils;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_start);
        initDB();
        myHttpUtils = new MyHttpUtils(this);

        permissionToAd();
    }



    private void permissionToAd() {
        if (!setNetworkDialog(this)) {
        } else {
            if (checkPermissionWriteExternalStorage()) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        handler.sendEmptyMessage(1);
                    }
                });
            } else {
                permissionWriteExternalStorage();
            }
        }
    }

    boolean isCheckedDB;

    private void initDB() {
        if (!isCheckedDB) {
            permissionExternalStorage();
            SharedPreferences preferences1 = getSharedPreferences("user", Context.MODE_PRIVATE);
            if (preferences1.getBoolean("initDB", true)) {
                Log.i(TAG, "handleMessage: initDB");
                insert2DB();
            }
            isCheckedDB = !isCheckedDB;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Str.REQUEST_WRITE_EXTERNALSTORAGE) {
            if (grantResults != null && grantResults.length > 0) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.i(TAG, "onRequestPermissionsResult: 111");
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            handler.sendEmptyMessage(1);
                        }
                    });
                } else {
                    Log.i(TAG, "onRequestPermissionsResult: 222");
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            handler.sendEmptyMessage(0);
                        }
                    });
                }
            } else {
                Log.i(TAG, "onRequestPermissionsResult: 333");
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        handler.sendEmptyMessage(0);
                    }
                });
            }
        }
    }

    AudioManager mAudioManager;

    private void setVoice(boolean show) {
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if (mAudioManager != null) {
            if (show) {
                //// 音乐音量
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                        mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC),
                        AudioManager.FLAG_SHOW_UI);
            } else {
                //// 音乐音量
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                        mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC),
                        AudioManager.FLAG_PLAY_SOUND);
            }
            // // 提示声音音量
            mAudioManager.setStreamVolume(AudioManager.STREAM_ALARM,
                    mAudioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM),
                    AudioManager.FLAG_PLAY_SOUND);
            // // 铃声音量
            mAudioManager.setStreamVolume(AudioManager.STREAM_RING,
                    mAudioManager.getStreamMaxVolume(AudioManager.STREAM_RING),
                    AudioManager.FLAG_PLAY_SOUND);
        }
    }

    /**
     * 跳转广告界面
     */
    public void toAd() {
        if (myHttpHelper == null) {
            myHttpHelper = new MyHttpHelper(StartActivity.this);
        }
        MyAsyncHttpUtils.get(Str.URL_CHECK_ONLINE, new MyTextHttpResponseHandler() {
            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                super.onFailure(i, headers, s, throwable);
                Log.i(TAG, "onFailure:toAd " + s);
                startActivity(new Intent(StartActivity.this, LoginActivity.class));
                overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                finish();
            }

            @Override
            public void onSuccess(int i, Header[] headers, String s) {
                super.onSuccess(i, headers, s);
                Log.i(TAG, "onSuccess:toAd " + s);
                List<String> resultForStatus = myHttpHelper.resultByJson(s, null);
                boolean show = true;
                if (resultForStatus == null) {
                    startActivity(new Intent(StartActivity.this, LoginActivity.class));
                    overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                    finish();
                } else {
                    if (resultForStatus.size() != 0) {
                        if (resultForStatus.get(0).equalsIgnoreCase("OK")) {
                            show = false;
                            startActivity(new Intent(StartActivity.this, DriverActivity.class));
                            overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                            finish();
                        } else {
                            File adFile = new File(Str.ADIMAGEPATH_START);
                            if (adFile.exists()) {
                                startActivityForResult(new Intent(StartActivity.this, MyStartViewPagerActivity.class), Str.REQUESTADCODE);
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
                    } else {
                        startActivity(new Intent(StartActivity.this, LoginActivity.class));
                        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                        finish();
                    }
                }
                setVoice(show);
            }
        });


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

    boolean firstLogin;
    MyHttpHelper myHttpHelper;

    private void init() {
        SharedPreferences preferences = getSharedPreferences("user", Context.MODE_MULTI_PROCESS | Context.MODE_PRIVATE);
        firstLogin = preferences.getBoolean("firstLogin", true);
        if (firstLogin) {
            startActivity(new Intent(this, LoginActivity.class));
            overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
            finish();
        } else {
            if (myHttpHelper == null) {
                myHttpHelper = new MyHttpHelper(StartActivity.this);
            }
            MyAsyncHttpUtils.get(Str.URL_CHECK_LOGIN, new MyTextHttpResponseHandler() {
                @Override
                public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                    super.onFailure(i, headers, s, throwable);
                    Log.i(TAG, "onFailure:Str.URL_CHECK_LOGIN " + s);
                    startActivity(new Intent(StartActivity.this, LoginActivity.class));
                    overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                    finish();
                }

                @Override
                public void onSuccess(int i, Header[] headers, String s) {
                    super.onSuccess(i, headers, s);
                    Log.i(TAG, "onSuccess:Str.URL_CHECK_LOGIN: " + Str.URL_CHECK_LOGIN + "//" + s);
                    List<String> resultForStatus = myHttpHelper.resultByJson(s, null);
                    if (resultForStatus == null) {
                        startActivity(new Intent(StartActivity.this, LoginActivity.class));
                    } else {
                        if (resultForStatus.size() != 0) {
                            if (resultForStatus.get(0).equalsIgnoreCase("OK")) {
                                startActivity(new Intent(StartActivity.this, DriverActivity.class));
                            } else {
                                startActivity(new Intent(StartActivity.this, LoginActivity.class));
                            }
                        } else {
                            startActivity(new Intent(StartActivity.this, LoginActivity.class));
                        }
                    }
                    overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                    finish();
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }
    }
}
