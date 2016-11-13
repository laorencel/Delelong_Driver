package com.delelong.diandiandriver.function;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.delelong.diandiandriver.BaseActivity;
import com.delelong.diandiandriver.R;
import com.delelong.diandiandriver.bean.MyNotificationInfo;

/**
 * Created by Administrator on 2016/11/4.
 */

public class MyNotificationActivity extends BaseActivity implements View.OnClickListener {

    TextView tv_notification;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        initActionBar();
        tv_notification = (TextView) findViewById(R.id.tv_notification);
        SharedPreferences preferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        String notification = preferences.getString("notification", "");
        if (notification != null && !notification.equalsIgnoreCase("")) {
            MyNotificationInfo myNotificationInfo = getNotificationInfo(notification);
            if (myNotificationInfo!= null&&myNotificationInfo.getContent()!=null&&!myNotificationInfo.getContent().equalsIgnoreCase("")){
//                speak("您有新的通知：" + orderMessage);
                tv_notification.setText(myNotificationInfo.getContent());
            }
        }else {
            tv_notification.setText("暂无通知");
        }
    }

    ImageButton arrow_back;

    private void initActionBar() {
        arrow_back = (ImageButton) findViewById(R.id.arrow_back);
        arrow_back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.arrow_back:
                finish();
                break;
        }
    }
}
