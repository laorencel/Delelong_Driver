package com.example.drawerlayout;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "BAIDUMAPFORTEST";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_driver);

        initView();
    }

    LinearLayout ly_desk03;
    FrameLayout ly_desk02;
    TextView tv_today_detail;
    Button btn_onLine;

    private void initView() {

        ly_desk03 = (LinearLayout) findViewById(R.id.ly_desk03);
        ly_desk02 = (FrameLayout) findViewById(R.id.ly_desk_show);
        tv_today_detail = (TextView) findViewById(R.id.tv_today_detail);
        btn_onLine = (Button) findViewById(R.id.btn_onLine);
        btn_onLine.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_onLine:
                if (ly_desk03.getVisibility() == View.VISIBLE) {
                    ly_desk03.setVisibility(View.INVISIBLE);

                    int[] distance = animDistance(ly_desk02, ly_desk03);
                    setTranslateAnimation(ly_desk02, 0, 0, 0, distance[1]);
                } else {
                    ly_desk03.setVisibility(View.VISIBLE);
                    AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1.0f);
                    alphaAnimation.setDuration(500);
                    alphaAnimation.setFillAfter(true);
                    ly_desk03.setAnimation(alphaAnimation);

                    int[] distance = animDistance(ly_desk02, ly_desk03);
                    setTranslateAnimation(ly_desk02, 0, 0, distance[1], 0);
                }

                break;
        }
    }

    public int[] animDistance(View from, View to) {
        int[] fromView = new int[2];
        int[] toView = new int[2];//to - from
        from.getLocationOnScreen(fromView);
        to.getLocationOnScreen(toView);

        int[] distance = new int[2];
        for (int i = 0; i < fromView.length; i++) {
            distance[i] = toView[i] - fromView[i];
        }
        return distance;
    }

    private void setTranslateAnimation(View view, float fromXDelta, float toXDelta, float fromYDelta, float toYDelta) {
        TranslateAnimation animation = new TranslateAnimation(fromXDelta, toXDelta, fromYDelta, toYDelta);
        //设置动画持续时间
        animation.setDuration(1000);
        //设置动画结束后效果保留
        animation.setFillAfter(true);
        //控制动画先慢后快
        /**
         （Interpolator的实现类):
         LinearInterpolator(匀速）
         AccelerateInterpolator（先慢后快）
         AccelerateDecelerateInterpolator（先慢中快后慢）
         DecelerateInterpolator（先快后慢）
         CycleInterpolator（循环播放，速度为正弦曲线）
         AnticipateInterpolator（先回撤，再匀速向前）
         OvershootInterpolator（超过，拉回）
         BounceInterpolator(回弹）
         */
        animation.setInterpolator(new DecelerateInterpolator());
        view.setAnimation(animation);
    }
}
