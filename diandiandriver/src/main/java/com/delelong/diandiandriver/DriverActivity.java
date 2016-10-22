package com.delelong.diandiandriver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.maps.AMap;
import com.delelong.diandiandriver.bean.Driver;
import com.delelong.diandiandriver.bean.DriverCarBean;
import com.delelong.diandiandriver.bean.OrderInfo;
import com.delelong.diandiandriver.bean.Str;
import com.delelong.diandiandriver.dialog.MyDialogUtils;
import com.delelong.diandiandriver.dialog.MyOrderDialog;
import com.delelong.diandiandriver.fragment.AMapFrag;
import com.delelong.diandiandriver.fragment.DriverMenuFrag;
import com.delelong.diandiandriver.fragment.MyAppUpdate;
import com.delelong.diandiandriver.fragment.OrderFrag;
import com.delelong.diandiandriver.function.MyFunctionFrag;
import com.delelong.diandiandriver.http.MyHttpUtils;
import com.delelong.diandiandriver.listener.MySpeechListener;
import com.delelong.diandiandriver.listener.MySynthesizerListener;
import com.delelong.diandiandriver.order.MyOrderInterface;
import com.delelong.diandiandriver.utils.TipHelper;
import com.delelong.diandiandriver.utils.ToastUtil;
import com.google.common.primitives.Ints;
import com.iflytek.cloud.speech.SpeechSynthesizer;
import com.iflytek.cloud.speech.SpeechUser;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/9/21.
 */
public class DriverActivity extends BaseActivity implements View.OnClickListener, AMapFrag.MyLocationInterface, MyDialogUtils.MyDialogInterface {

    private static final String TAG = "BAIDUMAPFORTEST";
    private static final int REQEST_CHOOSE_CAR = 0;
    private static final int ONLINE_TIME = 10;//添加路径
    private static final int INIT_FRAG = 11;//添加路径
    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case ONLINE_TIME:
                    if (online) {
                        onlineTime = onlineTime.plusMinutes(1);
                        setOnlineTime(12, "上线时间\n" + onlineTime.toString("HH:mm"));
                    }
                    break;
                case INIT_FRAG:
                    initFrag();
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
    DriverCarBean driverCarBean;
    AMapLocation aMapLocation;
    AMap mAMap;
    MyDialogUtils myDialog;
    DriverCarBean.DriverCar mDriverCar;//(多辆车)司机车辆信息
    SpeechSynthesizer speechSynthesizer;
    MySynthesizerListener mySynthesizerListener;

    public SpeechSynthesizer getSpeechSynthesizer() {
        return speechSynthesizer;
    }

    public void setSpeechSynthesizer(SpeechSynthesizer speechSynthesizer) {
        this.speechSynthesizer = speechSynthesizer;
    }

    public MySynthesizerListener getMySynthesizerListener() {
        return mySynthesizerListener;
    }

    public void setMySynthesizerListener(MySynthesizerListener mySynthesizerListener) {
        this.mySynthesizerListener = mySynthesizerListener;
    }

    private void initMsg() {
        onlineTime = new DateTime();
        preferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        myDialog = new MyDialogUtils(this);
        myHttpUtils = new MyHttpUtils(this);
        driver = myHttpUtils.getDriverByGET(Str.URL_MEMBER);
        driverCarBean = myHttpUtils.getDriverCars(Str.URL_DRIVER_CARS);

        SpeechUser.getUser().login(this, null, null, "appid=5806cdd4", new MySpeechListener());
        mySynthesizerListener = new MySynthesizerListener();
        speechSynthesizer = getMySpeechSynthesizer();
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
    RelativeLayout rl_order;//订单界面fragment

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
        rl_order = (RelativeLayout) findViewById(R.id.rl_order);

        handler.sendEmptyMessage(INIT_FRAG);//加载fragment
        initListener();
    }

    FragmentManager fragmentManager;
    AMapFrag aMapFrag;
    MyFunctionFrag functionFrag;
    DriverMenuFrag menuFrag;
    OrderFrag orderFrag;

    private void initFrag() {
        aMapFrag = (AMapFrag) AMapFrag.newInstance();
        functionFrag = new MyFunctionFrag();
        menuFrag = new DriverMenuFrag();
        orderFrag = (OrderFrag) OrderFrag.newInstance();
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(R.id.rl_map, aMapFrag, "aMapFrag")
                .add(R.id.menu_right, functionFrag, "functionFrag")
                .add(R.id.menu_left, menuFrag, "menuFrag")
                .add(R.id.rl_order, orderFrag, "orderFrag")
                .addToBackStack("null")
                .hide(functionFrag)
                .hide(aMapFrag)
                .hide(orderFrag)
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
                drawerly.openDrawer(GravityCompat.START);
                break;
            case R.id.img_function:
                //右侧功能菜单
                showFrag(functionFrag);
                break;
            case R.id.ly_sum_yesterday:
                //昨日收入
                showFrag(orderFrag);
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
                //选择接单车辆

                if (driverCarBean.getDriverCars().size() == 0) {
                    myDialog.showAddDriverCar();
                    return;
                }
                if (mDriverCar == null) {
                    if (driverCarBean.getDriverCars().size() > 1) {
                        myDialog.chooseDriverCars(driverCarBean, REQEST_CHOOSE_CAR, this);
                        return;
                    } else {
                        mDriverCar = driverCarBean.getDriverCars().get(0);
                    }
                }

                onLine();
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

    private void onLine() {
        online = !online;//上线、下线
        if (online) {//加快显示
            setOnlineTime(12, "上线时间\n" + "00:00");
        }
        if (!online) {//已经上线，并且在接单中，不能下线
            if (inOrder) {
                ToastUtil.show(this, "您正处于接单状态，请处理完订单再下线");
                return;
            }
        }
        List<String> result = myHttpUtils.onlineApp(Str.URL_ONLINE, mDriverCar.getId(), online);//上线
        if (result.get(0).equalsIgnoreCase("OK")) {
            speechSynthesizer.startSpeaking("开始接单啦 ", mySynthesizerListener);
        }
        if (online) {//上线重置时间
            onlineTime = new DateTime(onlineTime.getYear(), onlineTime.getMonthOfYear(),
                    onlineTime.getDayOfMonth(), 0, 0, 0);
            onlineTime = onlineTime.plusMinutes(Ints.tryParse(result.get(2)));//当天累计上线时间
            setOnlineTime(12, "上线时间\n" + onlineTime.toString("HH:mm"));
            sendEmptyMessage(ONLINE_TIME, 60000);
        } else {
            setOnlineTime(20, "上线");
        }
    }

    /**
     * 设置上线按钮 大小、文本
     *
     * @param textSize
     * @param text
     */
    private void setOnlineTime(float textSize, String text) {
        btn_onLine.setTextSize(textSize);
        btn_onLine.setText(text);
    }

    /**
     * 1分钟更新一次上线时间
     *
     * @param what
     */
    public void sendEmptyMessage(final int what, final long delayMillis) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                handler.sendEmptyMessage(what);
                if (online) {
                    handler.postDelayed(this, delayMillis);//1分钟更新一次
                }
            }
        }, delayMillis);
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
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .addToBackStack("null").show(fragment).commit();
    }

    public void hideFrag(Fragment fragment) {
        fragmentManager.beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                .hide(fragment).commit();
    }

    @Override
    protected void onDestroy() {
        if (online) {
            online = false;
            myHttpUtils.onlineApp(Str.URL_ONLINE, 51, online);//下线
        }
        if (handler.hasMessages(ONLINE_TIME)) {
            handler.removeMessages(ONLINE_TIME);
        }
        if (handler.hasMessages(INIT_FRAG)) {
            handler.removeMessages(INIT_FRAG);
        }
        super.onDestroy();
    }

    private boolean isTwice = false;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (fragmentManager.findFragmentByTag("functionFrag").isVisible()) {
//                fragmentManager.beginTransaction().hide(functionFrag).commit();
                hideFrag(functionFrag);
                return false;
            }
            if (fragmentManager.findFragmentByTag("aMapFrag").isVisible()) {
                hideFrag(aMapFrag);
                return false;
            }
            if (fragmentManager.findFragmentByTag("orderFrag").isVisible()) {
                hideFrag(orderFrag);
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

    @Override
    public void chooseDriverCar(int requestCode, int position) {
        if (requestCode == REQEST_CHOOSE_CAR) {
            mDriverCar = driverCarBean.getDriverCars().get(position);
            onLine();
        }
    }

    @Override
    public void sure(int requestCode, String arg0) {

    }

    @Override
    public void sure(int requestCode, Object object) {

    }

    OrderInfo mOrderInfo;
    List<OrderInfo> mOrderInfos;
    OrderMessageReceiver orderMessageReceiver;

    public void registerMessageReceiver() {
        orderMessageReceiver = new OrderMessageReceiver();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction(Str.ORDER_MESSAGE_RECEIVED_ACTION);
        registerReceiver(orderMessageReceiver, filter);
    }

    public class OrderMessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Str.ORDER_MESSAGE_RECEIVED_ACTION.equals(intent.getAction())) {
                String orderTitle = intent.getStringExtra(Str.KEY_ORDER_TITLE);
                if (orderTitle.equals("1")) {
                    //订单消息
                    String orderMessage = intent.getStringExtra(Str.KEY_ORDER_MESSAGE);
                    String orderExtras = intent.getStringExtra(Str.KEY_ORDER_EXTRA);//可能为空
                    TipHelper helper = new TipHelper(DriverActivity.this);
                    helper.Vibrate(500);//震动0.5秒
                    showOrder(orderMessage);
                }
            }
        }
    }

    boolean inOrder;

    public void showOrder(String orderMessage) {
        mOrderInfo = getOrderInfo(orderMessage);

        final MyOrderDialog orderDialog = new MyOrderDialog(this, mAMap, aMapLocation, mOrderInfo);
        orderDialog.show(new MyOrderDialog.OrderInterface() {
            @Override
            public void take(boolean take) {
                if (take) {
                    if (mOrderInfos == null) {//接单后才添加到接单列表
                        mOrderInfos = new ArrayList<>();
                        mOrderInfos.add(mOrderInfo);
                    } else {
                        mOrderInfos.add(mOrderInfo);
                    }
                    List<String> orderResult = myHttpUtils.takeOrder(Str.URL_TAKE_ORDER, mOrderInfo.getId());
                    if (orderResult.get(0).equalsIgnoreCase("OK")) {
                        speechSynthesizer.startSpeaking("接单成功 ", mySynthesizerListener);
                        showFrag(orderFrag);
                        if (inOrder) {
                            //已经接单了
                            orderFrag.addOrderInfo(mOrderInfos.get(1));
                        } else {
                            orderFrag.setmOrderInfo(mOrderInfos.get(0));
                        }
                        inOrder = true;
                    } else {
                        speechSynthesizer.startSpeaking("接单失败 ", mySynthesizerListener);
                    }
                }
            }
        });
        speechSynthesizer.startSpeaking("来新订单了 ", mySynthesizerListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Str.REQUEST_CONFIRM_AMOUNT) {
            if (data.getStringExtra("result").equalsIgnoreCase("OK")) {//完成所有订单
//            orderFrag.end();
                if (!orderFrag.isHidden()) {
                    hideFrag(orderFrag);
                }
            } else if (data.getStringExtra("result").equalsIgnoreCase("OK_Two")) {//
                int position = data.getIntExtra("position",999);
                mOrderInterface.positionFromMain(position);
            } else if (data.getStringExtra("result").equalsIgnoreCase("NO")) {//取消结算，无操作

            }
        }
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

    @Override
    public void getAMap(AMap aMap) {
        mAMap = aMap;
    }

    /**
     * 传递aMapLocation给其他fragment
     */
    MyActivityLocationInterface myActivityLocInterface;

    public void setMyActivityLocationInterface(MyActivityLocationInterface myActivityLocInterface) {
        this.myActivityLocInterface = myActivityLocInterface;
    }

    public interface MyActivityLocationInterface {
        void getMyLocation(AMapLocation aMapLocation);
    }
    MyOrderInterface mOrderInterface;
    public void setMyOrderInterface(MyOrderInterface myOrderInterface){
        this.mOrderInterface = myOrderInterface;
    }
}
