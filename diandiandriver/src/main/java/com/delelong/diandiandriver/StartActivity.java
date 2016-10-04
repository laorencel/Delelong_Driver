package com.delelong.diandiandriver;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.widget.ImageView;
import android.widget.Toast;

import com.delelong.diandiandriver.bean.Str;
import com.delelong.diandiandriver.http.MyHttpUtils;

import java.util.List;

/**
 * Created by Administrator on 2016/8/26.
 */
public class StartActivity extends BaseActivity {

    private static final String TAG = "BAIDUMAPFORTEST";
    Handler handler;
    ImageView img_ad;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_start);
        img_ad = (ImageView) findViewById(R.id.img_ad);

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 0:
//                        task.execute();
                        init();
                        break;
                    case 1:
                        //广告位
//                        MyHeadTask myHeadTask = new MyHeadTask(img_ad);
//                        myHeadTask.execute(Str.URL_HEAD_PORTRAIT, head_portrait);
                        break;
                }
            }
        };
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                handler.sendEmptyMessage(0);
            }
        }, 1000);
    }

    String phone, pwd;
    boolean firstLogin;

    private void init() {
        SharedPreferences preferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        firstLogin = preferences.getBoolean("firstLogin", true);
        if (firstLogin) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        } else {
            phone = preferences.getString("phone", null);
            pwd = preferences.getString("pwd", null);
            MyHttpUtils myHttpUtils = new MyHttpUtils(this);
            List<String> loginResult = myHttpUtils.login(Str.URL_LOGIN, phone, pwd);

            if (loginResult.get(0).equalsIgnoreCase("OK")) {
                //从服务器获取app更新信息，有新版本提示下载安装，无则直接进入app主界面
                startActivity(new Intent(this, DriverActivity.class));
            } else if (loginResult.get(0).equals("ERROR")) {
                Toast.makeText(this, "登陆出错,请重新登陆", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, LoginActivity.class));
            } else if (loginResult.get(0).equals("FAILURE")) {
                startActivity(new Intent(this, LoginActivity.class));
            }
            finish();
        }
    }


}
