package com.delelong.diandiandriver;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.delelong.diandiandriver.bean.Driver;
import com.delelong.diandiandriver.bean.Str;
import com.delelong.diandiandriver.fragment.AMapFrag;
import com.delelong.diandiandriver.fragment.DriverMenuFrag;
import com.delelong.diandiandriver.fragment.MyAppUpdate;
import com.delelong.diandiandriver.function.ChooseCarBrandActivity;
import com.delelong.diandiandriver.function.MyFunctionFrag;
import com.delelong.diandiandriver.http.MyHttpUtils;
import com.google.common.primitives.Ints;

import org.joda.time.DateTime;

import java.util.List;

/**
 * Created by Administrator on 2016/9/21.
 */
public class DriverActivity extends BaseActivity implements View.OnClickListener, AMapFrag.MyLocationInterface {

    private static final String TAG = "BAIDUMAPFORTEST";
    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    if (online){
                        onlineTime = onlineTime.plusMinutes(1);
                        setOnlineTime(12,"上线时间\n" + onlineTime.toString("HH:mm"));
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_driver);

        initMsg();
        initView();
        checkUpdate();
    }

    DateTime onlineTime;
    SharedPreferences preferences;
    MyHttpUtils myHttpUtils;
    Driver driver;
    AMapLocation aMapLocation;

    private void initMsg() {
        onlineTime = new DateTime();

        preferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        myHttpUtils = new MyHttpUtils(this);
        driver = myHttpUtils.getDriverByGET(Str.URL_MEMBER);
//        myHttpUtils.getCarBrands(Str.URL_CAR_BRAND, 1, 15);
//        myHttpUtils.getCarModelsByBrand(Str.URL_CAR_BRAND_MODEL,1, 1, 15);
    }

    /**
     * 检查更新（每3次） 另开线程
     */
    private void checkUpdate() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if ((preferences.getInt("updatetime", 3) % 3 == 0)) {
                    //每三次进入app检查一次更新
                    MyAppUpdate myAppUpdate = new MyAppUpdate(DriverActivity.this);
                    myAppUpdate.checkUpdate();
                }
                if (aMapLocation == null) {
                    try {
                        new Thread().sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                if (driver != null) {
                    if (driver.getCompany().equals("null")) {
                        //如果adcode为"null"（表示未设置过）
                        checkAdcode(aMapLocation, driver);
                    }
                } else {
                    driver = myHttpUtils.getDriverByGET(Str.URL_MEMBER);
                    if (driver.getCompany().equals("null")) {
                        checkAdcode(aMapLocation, driver);
                    }
                }
                download();
            }
        }).start();
    }

    private void download() {
        downloadStartAD(aMapLocation);
        downloadMainAD(aMapLocation);
    }

    public DrawerLayout drawerly;
    RelativeLayout menu_left, menu_right;//左右菜单布局

    ImageView img_menu, img_function;//actionbar
    LinearLayout ly_sum_yesterday, ly_sum_today;//昨日、今日收入点击事件
    TextView tv_sum_yesterday, tv_sum_today;//昨日、今日收入

    LinearLayout ly_desk_detail;//收入明细（可隐藏）
    TextView tv_today_detail;//收入明细（可隐藏）
    LinearLayout ly_desk_show;//显示、隐藏收入明细
    ImageView img_desk_show;

    Button btn_createOrder, btn_onLine, btn_backToCity;//创建订单、上线、返城按钮

    RelativeLayout rl_showMap;//显示地图布局
    ImageView img_showMap;//显示地图箭头

    RelativeLayout rl_map;//地图界面fragment

    private void initView() {
        drawerly = (DrawerLayout) findViewById(R.id.drawerly);

        menu_left = (RelativeLayout) findViewById(R.id.menu_left);
        menu_right = (RelativeLayout) findViewById(R.id.menu_right);

        img_menu = (ImageView) findViewById(R.id.img_menu);
        img_function = (ImageView) findViewById(R.id.img_function);

        ly_sum_yesterday = (LinearLayout) findViewById(R.id.ly_sum_yesterday);
        ly_sum_today = (LinearLayout) findViewById(R.id.ly_sum_today);
        tv_sum_yesterday = (TextView) findViewById(R.id.tv_sum_yesterday);
        tv_sum_today = (TextView) findViewById(R.id.tv_sum_today);

        ly_desk_detail = (LinearLayout) findViewById(R.id.ly_desk_detail);
        tv_today_detail = (TextView) findViewById(R.id.tv_today_detail);
        ly_desk_show = (LinearLayout) findViewById(R.id.ly_desk_show);
        img_desk_show = (ImageView) findViewById(R.id.img_desk_show);

        btn_createOrder = (Button) findViewById(R.id.btn_createOrder);
        btn_onLine = (Button) findViewById(R.id.btn_onLine);
        btn_backToCity = (Button) findViewById(R.id.btn_backToCity);

        rl_showMap = (RelativeLayout) findViewById(R.id.rl_showMap);
        img_showMap = (ImageView) findViewById(R.id.img_showMap);

        rl_map = (RelativeLayout) findViewById(R.id.rl_map);

        initFrag();
        initListener();
    }

    FragmentManager fragmentManager;
    AMapFrag aMapFrag;
    MyFunctionFrag functionFrag;
    DriverMenuFrag menuFrag;

    private void initFrag() {
        aMapFrag = (AMapFrag) AMapFrag.newInstance();
        functionFrag = new MyFunctionFrag();
        menuFrag = new DriverMenuFrag();
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(R.id.rl_map, aMapFrag, "aMapFrag")
                .add(R.id.menu_right, functionFrag, "functionFrag")
                .add(R.id.menu_left, menuFrag, "menuFrag")
                .addToBackStack("null")
                .hide(functionFrag)
                .hide(aMapFrag)
                .commit();
    }

    private void initListener() {
        img_menu.setOnClickListener(this);
        img_function.setOnClickListener(this);
        ly_sum_yesterday.setOnClickListener(this);
        ly_sum_today.setOnClickListener(this);
        img_desk_show.setOnClickListener(this);
        btn_createOrder.setOnClickListener(this);
        btn_onLine.setOnClickListener(this);
        btn_backToCity.setOnClickListener(this);
        rl_showMap.setOnClickListener(this);

    }

    boolean online;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_menu:
                //左侧菜单
                drawerly.openDrawer(Gravity.LEFT);
                break;
            case R.id.img_function:
                //右侧功能菜单
                showFrag(functionFrag);
                break;
            case R.id.ly_sum_yesterday:
                //昨日收入

                break;
            case R.id.ly_sum_today:
                //今日收入
                startActivity(new Intent(this, ChooseCarBrandActivity.class));
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
                online = !online;//上线、下线
                if (online){//加快显示
                    setOnlineTime(12,"上线时间\n" + "00:00");
                }
                List<String> result = myHttpUtils.onlineApp(Str.URL_ONLINE, 51, online);//上线
                if (online) {//上线重置时间
                    onlineTime = new DateTime(onlineTime.getYear(), onlineTime.getMonthOfYear(),
                            onlineTime.getDayOfMonth(), 0, 0, 0);
                    onlineTime = onlineTime.plusMinutes(Ints.tryParse(result.get(2)));//当天累计上线时间
                    setOnlineTime(12,"上线时间\n" + onlineTime.toString("HH:mm"));
                    sendEmptyMessage(0);
                } else {
                    setOnlineTime(20,"上线");
                }
                break;
            case R.id.btn_backToCity:
                //返城
                myHttpUtils.createImage(Str.ADIMAGEPATH, myHttpUtils.downloadImage("http://p3.so.qhmsg.com/bdr/_240_/t013763a5b2c5fceb98.jpg"));
                break;
            case R.id.rl_showMap:
                //动画
//                setDecelerateTransAnim(img_showMap, 0, 0, 0, 20);
                //显示地图布局
                showFrag(aMapFrag);
                break;
        }
    }

    /**
     * 设置上线按钮 大小、文本
     * @param textSize
     * @param text
     */
    private void setOnlineTime(float textSize,String text){
        btn_onLine.setTextSize(textSize);
        btn_onLine.setText(text);
    }
    /**
     * 1分钟更新一次上线时间
     * @param what
     */
    private void sendEmptyMessage(final int what){
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                handler.sendEmptyMessage(what);
                handler.postDelayed(this, 60000);//1分钟更新一次
            }
        }, 60000);
    }

    float transAnimDis;//位移动画距离

    /**
     * 设置收入明细动画
     */
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

    public void showFrag(Fragment fragment) {
        fragmentManager.beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                .addToBackStack("null").show(fragment).commit();
    }

    public void hideFrag(Fragment fragment) {
        fragmentManager.beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                .hide(fragment).commit();
    }

    private boolean isTwice = false;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (drawerly.isDrawerOpen(Gravity.LEFT) || drawerly.isDrawerOpen(Gravity.RIGHT)) {
                drawerly.closeDrawers();
                return false;
            }
            if (fragmentManager.findFragmentByTag("functionFrag").isVisible()) {
//                fragmentManager.beginTransaction().hide(functionFrag).commit();
                hideFrag(functionFrag);
                return false;
            }
            if (fragmentManager.findFragmentByTag("aMapFrag").isVisible()) {
                fragmentManager.beginTransaction().hide(aMapFrag).commit();

                return false;
            }

            if (isTwice) {
                finish();
            } else {
                isTwice = !isTwice;
                Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isTwice = false;
                    }
                }, 3000);
                return false;
            }
        }
        return super.onKeyDown(keyCode, event);
    }


    /**
     * 回调当前位置
     *
     * @param aMapLocation
     */
    @Override
    public void getMyLocation(AMapLocation aMapLocation) {
        if (aMapLocation != null) {
            this.aMapLocation = aMapLocation;//从AMapFrag获取
            myActivityLocInterface.getMyLocation(aMapLocation);//传给MyFunctionFrag
        }
    }

    /**
     * 传递aMapLocation给其他fragment
     */
    MyActivityLocationInterface myActivityLocInterface;

    public void getMyActivityLocationInterface(MyActivityLocationInterface myActivityLocInterface) {
        this.myActivityLocInterface = myActivityLocInterface;
    }

    public interface MyActivityLocationInterface {
        void getMyLocation(AMapLocation aMapLocation);
    }
}
