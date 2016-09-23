package com.delelong.diandiandriver;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.delelong.diandiandriver.fragment.AMapFrag;

/**
 * Created by Administrator on 2016/9/21.
 */
public class DriverActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "BAIDUMAPFORTEST";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_driver);
        initView();
    }

    DrawerLayout drawerly;
    RelativeLayout menu_left, menu_right;//左右菜单布局

    ImageView img_head, img_menu;//actionbar
    LinearLayout ly_sum_yesterday, ly_sum_today;//昨日、今日收入点击事件
    TextView tv_sum_yesterday, tv_sum_today;//昨日、今日收入

    LinearLayout ly_desk_detail;//收入明细（可隐藏）
    TextView tv_today_detail;//收入明细（可隐藏）
    FrameLayout ly_desk_show;//显示、隐藏收入明细
    ImageView img_desk_show;

    Button btn_createOrder, btn_onLine, btn_backToCity;//创建订单、上线、返城按钮

    RelativeLayout rl_showMap;//显示地图布局
    ImageView img_showMap;//显示地图箭头

    RelativeLayout rl_map;//地图界面fragment

    private void initView() {
        drawerly = (DrawerLayout) findViewById(R.id.drawerly);
        menu_left = (RelativeLayout) findViewById(R.id.menu_left);
        menu_right = (RelativeLayout) findViewById(R.id.menu_right);


        img_head = (ImageView) findViewById(R.id.img_head);
        img_menu = (ImageView) findViewById(R.id.img_menu);

        ly_sum_yesterday = (LinearLayout) findViewById(R.id.ly_sum_yesterday);
        ly_sum_today = (LinearLayout) findViewById(R.id.ly_sum_today);
        tv_sum_yesterday = (TextView) findViewById(R.id.tv_sum_yesterday);
        tv_sum_today = (TextView) findViewById(R.id.tv_sum_today);

        ly_desk_detail = (LinearLayout) findViewById(R.id.ly_desk_detail);
        tv_today_detail = (TextView) findViewById(R.id.tv_today_detail);
        ly_desk_show = (FrameLayout) findViewById(R.id.ly_desk_show);
        img_desk_show = (ImageView) findViewById(R.id.img_desk_show);

        btn_createOrder = (Button) findViewById(R.id.btn_createOrder);
        btn_onLine = (Button) findViewById(R.id.btn_onLine);
        btn_backToCity = (Button) findViewById(R.id.btn_backToCity);

        rl_showMap = (RelativeLayout) findViewById(R.id.rl_showMap);
        img_showMap = (ImageView) findViewById(R.id.img_showMap);

        rl_map = (RelativeLayout) findViewById(R.id.rl_map);

        initFrag();
        initListener();
        initMsg();
    }

    private void initFrag() {
        aMapFrag = (AMapFrag) AMapFrag.newInstance();
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().add(R.id.rl_map, aMapFrag, "aMapFrag").addToBackStack("null").hide(aMapFrag).commit();
    }

    private void initMsg() {

    }

    private void initListener() {
        img_head.setOnClickListener(this);
        img_menu.setOnClickListener(this);
        ly_sum_yesterday.setOnClickListener(this);
        ly_sum_today.setOnClickListener(this);
        img_desk_show.setOnClickListener(this);
        btn_createOrder.setOnClickListener(this);
        btn_onLine.setOnClickListener(this);
        btn_backToCity.setOnClickListener(this);
        rl_showMap.setOnClickListener(this);


    }

    FragmentManager fragmentManager;
    AMapFrag aMapFrag;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_head:
                //左侧菜单

                break;
            case R.id.img_menu:
                //右侧功能菜单

                break;
            case R.id.ly_sum_yesterday:
                //昨日收入

                break;
            case R.id.ly_sum_today:
                //今日收入

                break;
            case R.id.img_desk_show:
                //显示中间收入明细
                showSumDetail();
                break;
            case R.id.btn_createOrder:
                //创建订单
                break;
            case R.id.btn_onLine:
                //上线

                break;
            case R.id.btn_backToCity:
                //返城
                break;
            case R.id.rl_showMap:
                //动画
                setDecelerateTransAnim(img_showMap,0,0,0,20);
                //显示地图布局
                fragmentManager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).addToBackStack("null").show(aMapFrag).commit();
                break;
        }
    }

    float transAnimDis;//位移动画距离
    private void showSumDetail() {
        if (ly_desk_detail.getVisibility() == View.VISIBLE) {//隐藏收入明细
            ly_desk_detail.setVisibility(View.INVISIBLE);

            int[] distance = animDistance(ly_desk_show, ly_desk_detail);
            transAnimDis = distance[1];
            setAlphaAnimation(ly_desk_detail, 1.0f, 0);//透明度
            setTranslateAnimation(ly_desk_show, 0, 0, 0, distance[1]);//位移
            setRotateAnimation(img_desk_show, 0, 180);//箭头旋转
        } else {//显示收入明细
            ly_desk_detail.setVisibility(View.VISIBLE);
            setAlphaAnimation(ly_desk_detail, 0, 1.0f);
            setTranslateAnimation(ly_desk_show, 0, 0, 0, -transAnimDis);
            setRotateAnimation(img_desk_show, 180, 0);
        }
    }

}
