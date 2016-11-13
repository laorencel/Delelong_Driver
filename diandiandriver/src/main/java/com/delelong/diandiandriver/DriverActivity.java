package com.delelong.diandiandriver;

import android.animation.LayoutTransition;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.RouteSearch;
import com.delelong.diandiandriver.bean.Driver;
import com.delelong.diandiandriver.bean.DriverAmount;
import com.delelong.diandiandriver.bean.DriverCarBean;
import com.delelong.diandiandriver.bean.MyNotificationInfo;
import com.delelong.diandiandriver.bean.OrderInfo;
import com.delelong.diandiandriver.bean.Str;
import com.delelong.diandiandriver.dialog.MyDialogUtils;
import com.delelong.diandiandriver.dialog.MyOrderDialog;
import com.delelong.diandiandriver.fragment.DriverMenuFrag;
import com.delelong.diandiandriver.fragment.MyAppUpdate;
import com.delelong.diandiandriver.function.MyFunctionFrag;
import com.delelong.diandiandriver.http.ClientLocationInfo;
import com.delelong.diandiandriver.http.MyHttpUtils;
import com.delelong.diandiandriver.listener.MyOrientationListener;
import com.delelong.diandiandriver.listener.MyRouteSearchListener;
import com.delelong.diandiandriver.menuActivity.MyHistoryOrderActivity;
import com.delelong.diandiandriver.order.CreateOrderActivity;
import com.delelong.diandiandriver.order.MyCheckOrderListener;
import com.delelong.diandiandriver.order.MyOrderActivity;
import com.delelong.diandiandriver.order.MyOrderInterface;
import com.delelong.diandiandriver.pace.MyAMapLocation;
import com.delelong.diandiandriver.receiver.NetReceiver;
import com.delelong.diandiandriver.service.MyWebSocketService;
import com.delelong.diandiandriver.utils.TipHelper;
import com.delelong.diandiandriver.utils.ToastUtil;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by Administrator on 2016/9/21.
 */
public class DriverActivity extends BaseActivity implements View.OnClickListener,
        MyDialogUtils.MyDialogInterface, MyCheckOrderListener, LocationSource,
        AMapLocationListener {
    private static final String TAG = "BAIDUMAPFORTEST";
    private static final int REQEST_CHOOSE_CAR = 0;
    private static final int REQEST_CHOOSE_ONLINE_TYPE = 1;//选择上线类型
    private static final int REQEST_LOGIN = 2;//登陆
    private static final int ONLINE_TIME = 10;//添加路径
    private static final int INIT_FRAG = 11;//添加路径
    public static final int CHECK_ORDER = 12;//检查是否有未完成订单
    public static final int CHECK_ADCODE = 13;//检查ADCODE
    public static final int CHECK_AD_UPDATE = 14;//检查广告图片
    public static final int CHECK_APP_UPDATE = 15;//检查软件更新
    public static final int UPDATE_LOCATION = 16;//上传位置
    public static final int UPDATE_CARSTATUS = 17;//持续更新状态，防止下线
    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATE_CARSTATUS:
                    if (updateStatus) {
                        updateStatus();
                        handler.sendEmptyMessageDelayed(UPDATE_CARSTATUS, 60000);//1分钟更新一次
                    }
                    break;
                case ONLINE_TIME:
                    if (online) {
                        onlineTime = onlineTime.plusMinutes(1);
                        setOnlineTime(12, "上线时间\n" + onlineTime.toString("HH:mm"));
                        if (online) {
                            if (!handler.hasMessages(ONLINE_TIME)) {
                                Log.i(TAG, "handleMessage: ONLINE_TIME");
                                handler.sendEmptyMessageDelayed(ONLINE_TIME, 60000);//1分钟更新一次
                            }
                        }
                    }
                    break;
                case INIT_FRAG://加载fragment和地图view
                    initFrag();
                    setUpMap();
                    setMyRouteSearchListener();
                    initMapView();
                    break;
                case CHECK_ORDER:
                    checkInOrder();
                    break;
                case CHECK_ADCODE:
                    updateAdcode();
                    break;
                case CHECK_AD_UPDATE:
//                    download();
                    break;
                case CHECK_APP_UPDATE:
                    if (preferences != null && (preferences.getInt("updatetime", 3) % 3 == 0)) {
                        //每三次进入app检查一次更新
                        MyAppUpdate myAppUpdate = new MyAppUpdate(DriverActivity.this);
                        myAppUpdate.checkUpdate();
                    }
                    break;
                case UPDATE_LOCATION:
//                    if (updateLoc) {
//                        Log.i(TAG, "handleMessage: UPDATE_LOCATION");
//                        upDateLocation();
                    updateLoc = true;
                    upDateLocation(mAMapLocation,updateLocLatlng);
                    if (updateLoc) {
                        handler.sendEmptyMessageDelayed(UPDATE_LOCATION, 60000);
                    }
//                    }
                    break;
            }
        }
    };
    DriverActivity driverActivity;
    boolean updateStatus = true;
    boolean updateLoc = true;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    private void updateStatus() {
        if (myHttpUtils == null) {
            myHttpUtils = new MyHttpUtils(this);
        }
        List<String> resultForStatus = myHttpUtils.updateCarStatus(Str.URL_UPDATE_CARSTATUS);
        if (resultForStatus == null) {
            return;
        }
        if (resultForStatus.get(0).equalsIgnoreCase("OK")) {
            if (resultForStatus.get(1).equalsIgnoreCase("true")) {
            } else {//下线状态，重新上线
                checkDriverType();
//                onLine();
            }
        } else if (resultForStatus.get(0).equalsIgnoreCase("NOAUTH")) {
            ToastUtil.show(this, resultForStatus.get(1) + " \n请重新登陆");
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        } else {
            ToastUtil.show(this, resultForStatus.get(1) + " \n请重新登陆");
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }

    boolean hasChecked;//已经检查过

    /**
     * 进入界面检查是否之前有未处理结束的订单
     */
    private void checkInOrder() {
        Log.i(TAG, "checkInOrder: ");
        if (mOrderInfos == null) {
            List<OrderInfo> orderInfoList = myHttpUtils.getUnfinishedOrderInfos(Str.URL_UNFINISHED_ORDER);
            if (orderInfoList == null) {
                return;
            }
            if (orderInfoList != null && orderInfoList.size() != 0) {//如果在处理订单状态
                for (int i = 0; i < orderInfoList.size(); i++) {
                    OrderInfo orderInfo = orderInfoList.get(i);
                    int orderStatus = orderInfo.getStatus();
                    //订单状态 ( 1;//订单创建 2;//司机接单3;//订单开始5;//订单已支付
                    // 6;//订单取消4;//到达终点9;//订单完成 7;//司机开始等待)
                    if (orderStatus == 2 || orderStatus == 3 || orderStatus == 4 || orderStatus == 7) {
                        if (mOrderInfos == null) {
                            mOrderInfos = new ArrayList<>();
                        }
                        mOrderInfos.add(orderInfoList.get(0));
//                orderFrag.setmOrderInfo(mOrderInfos.get(0));
                        speak("您有未处理完订单，请完成订单");
                        if (!mOrderInfos.get(0).getServiceType().equals("代驾")) {
                            if (orderInfoList.size() == 2) {//如果为2个乘客
                                mOrderInfos.add(orderInfoList.get(1));
//                        orderFrag.addOrderInfo(mOrderInfos.get(1));
                            }
                        }
                        inOrder = true;//改变接单状态
                        isInOrder(inOrder);

                        if (!online) {//如未上线，强制上线
                            List<String> result = null;
                            if (myHttpUtils == null) {
                                myHttpUtils = new MyHttpUtils(this);
                            }
                            if (driver == null) {
                                driver = myHttpUtils.getDriverByGET(Str.URL_MEMBER);
                            }
                            if (driver == null) {
                                return;
                            }
                            if (online) {
                                offLineTimeMillis = System.currentTimeMillis();
                            } else {
                                onLineTimeMillis = System.currentTimeMillis();
                            }
                            if (orderInfoList.get(0).getServiceType().contains("代驾")) {//代驾
                                driver.setType("2");
                                result = myHttpUtils.onlineAppDJ(Str.URL_ONLINE_DJ, !online, onLineTimeMillis, offLineTimeMillis);//上线
                            } else {
                                driver.setType("1");
                                result = myHttpUtils.onlineApp(Str.URL_ONLINE, orderInfoList.get(0).getCar_id(), !online, onLineTimeMillis, offLineTimeMillis);//上线
                            }
                            if (result == null) {
                                return;
                            }

                            if (result.get(0).equalsIgnoreCase("OK")) {
                                online = !online;//上线、下线
                                if (online) {//上线重置时间
                                    resetOnlineTime(result);
                                    if (!driver.getType().equals("2")) {
                                        updateStatus = true;
                                        handler.sendEmptyMessage(UPDATE_CARSTATUS);
//                                sendEmptyMessageUpdateStatus(UPDATE_CARSTATUS, 60000);
                                    }
                                } else {
                                    setOnlineTime(20, "上线");
                                    updateStatus = false;
                                    if (handler.hasMessages(UPDATE_CARSTATUS)) {
                                        handler.removeMessages(UPDATE_CARSTATUS);
                                    }
                                }
                            } else if (result.get(0).equalsIgnoreCase("ERROR")) {
                                ToastUtil.show(this, result.get(1));
                                return;
                            }
                        }
                    }
                }
//                speechSynthesizer.startSpeaking("您有未处理完的订单，点击继续处理 ", mySynthesizerListener);
            }
        }
    }

    Context context;
    NetReceiver mNetReceiver = new NetReceiver();
    IntentFilter mNetFilter = new IntentFilter();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_driver);
        setUpMap(savedInstanceState);
        context = this;

        speak(" ");
        permissionLocation();
        checkOpenGps();
        initMsg();
        initView();

        mNetFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mNetReceiver, mNetFilter);//监听网络连接
        mServiceIntents = new Intent(this, MyWebSocketService.class);
        startService(mServiceIntents);//websocket获取推送消息

        handler.sendEmptyMessage(INIT_FRAG);//加载fragment
        handler.sendEmptyMessageDelayed(CHECK_APP_UPDATE, 30000);
        handler.sendEmptyMessageDelayed(CHECK_ORDER, 10000);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    public Intent mServiceIntents;
    DateTime onlineTime;
    SharedPreferences preferences;
    MyHttpUtils myHttpUtils;
    Driver driver;
    DriverCarBean driverCarBean;
    //    AMapLocation aMapLocation;
    AMap mAMap;
    MyDialogUtils myDialog;
    DriverCarBean.DriverCar mDriverCar;//(多辆车)司机车辆信息
    TipHelper mTipHelper;

    private void initMsg() {
        driverActivity = this;
        onlineTime = new DateTime();
        preferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        myDialog = new MyDialogUtils(this);
        Log.i(TAG, "initMsg: driverActivity:" + driverActivity);
        myHttpUtils = new MyHttpUtils(this, driverActivity);
        mTipHelper = new TipHelper(this);
        initDriver();
    }

    DriverAmount mDriverAmount;

    private void initDriver() {
        driver = myHttpUtils.getDriverByGET(Str.URL_MEMBER);
        driverCarBean = myHttpUtils.getDriverCars(Str.URL_DRIVER_CARS);
    }

    private void updateAdcode() {
        if (driver == null) {
            driver = myHttpUtils.getDriverByGET(Str.URL_MEMBER);
        }
        if (driver != null) {
            if (driver.getCompany().equalsIgnoreCase("null")) {
                //如果adcode为1（表示未设置过）
                Log.i(TAG, "updateAdcode: ");
                updateAdCode(driver, mAMapLocation);
            }
        }
    }

    private void download() {
        downloadStartAD(mAMapLocation);
        downloadMainAD(mAMapLocation);
    }

    LayoutTransition transition;
    public DrawerLayout drawerly;
    RelativeLayout menu_left, menu_right;//左右菜单布局

    LinearLayout ly_desk;//切换界面大小，显示 隐藏地图
    RelativeLayout rl_desk;
    ImageView img_menu, img_function;//actionbar
    LinearLayout ly_sum, ly_sum_yesterday, ly_sum_today;//昨日、今日收入点击事件
    TextView tv_sum_yesterday, tv_sum_today;//昨日、今日收入

    LinearLayout ly_desk02, ly_desk_detail;//收入明细（可隐藏）
    TextView tv_today_detail;//收入明细（可隐藏）
    LinearLayout ly_desk_show;//显示、隐藏收入明细
    ImageView img_desk_show;

    Button btn_createOrder, btn_onLine, btn_backToCity;//创建订单、上线、返城按钮

    RelativeLayout rl_order;//订单界面fragment

    private void initView() {
        transition = new LayoutTransition();
        drawerly = (DrawerLayout) findViewById(R.id.drawerly);
        drawerly.setLayoutTransition(transition);

        menu_left = (RelativeLayout) findViewById(R.id.menu_left);
        menu_right = (RelativeLayout) findViewById(R.id.menu_right);

        ly_desk = (LinearLayout) findViewById(R.id.ly_desk);
        ly_desk.setLayoutTransition(transition);
        rl_desk = (RelativeLayout) findViewById(R.id.rl_desk);
        rl_desk.setLayoutTransition(transition);
        img_menu = (ImageView) findViewById(R.id.img_menu);
        img_function = (ImageView) findViewById(R.id.img_function);

        ly_sum = (LinearLayout) findViewById(R.id.ly_sum);
        ly_sum.setLayoutTransition(transition);
        ly_sum_yesterday = (LinearLayout) findViewById(R.id.ly_sum_yesterday);
        ly_sum_today = (LinearLayout) findViewById(R.id.ly_sum_today);
        tv_sum_yesterday = (TextView) findViewById(R.id.tv_sum_yesterday);
        tv_sum_today = (TextView) findViewById(R.id.tv_sum_today);

        ly_desk02 = (LinearLayout) findViewById(R.id.ly_desk02);
        ly_desk02.setLayoutTransition(transition);
        ly_desk_detail = (LinearLayout) findViewById(R.id.ly_desk_detail);
        tv_today_detail = (TextView) findViewById(R.id.tv_today_detail);
        ly_desk_show = (LinearLayout) findViewById(R.id.ly_desk_show);
        img_desk_show = (ImageView) findViewById(R.id.img_desk_show);

        btn_createOrder = (Button) findViewById(R.id.btn_createOrder);
        btn_onLine = (Button) findViewById(R.id.btn_onLine);
        btn_onLine.setClickable(false);
        btn_backToCity = (Button) findViewById(R.id.btn_backToCity);

        rl_order = (RelativeLayout) findViewById(R.id.rl_order);

        initAmountMsg();
        initListener();
    }

    private void initAmountMsg() {
        if (myHttpUtils == null) {
            myHttpUtils = new MyHttpUtils(this);
        }
        mDriverAmount = myHttpUtils.getDriverYeAmount(Str.URL_DRIVER_YE_AMOUNT);
        if (mDriverAmount != null) {
            tv_sum_yesterday.setText("￥ " + mDriverAmount.getYesterday());
            tv_sum_today.setText("￥ " + mDriverAmount.getToday());
            if (mDriverAmount.getToday() != 0) {
                tv_today_detail.setText("今日收入：" + mDriverAmount.getToday() + "\n继续努力哦");
            } else {
                tv_today_detail.setText("今天暂无收入\n还需努力哦");
            }
        }
    }

    LinearLayout ly_order;//显示正在进行中的订单
    TextView tv_order_type, tv_order_time;//订单类型，时间（预约）
    TextView tv_order_resAddr, tv_order_desAddr;//订单起始点

    private void initInOrderView() {
        ly_order = (LinearLayout) findViewById(R.id.ly_order);
        tv_order_type = (TextView) findViewById(R.id.tv_order_type);
        tv_order_time = (TextView) findViewById(R.id.tv_order_time);
        tv_order_resAddr = (TextView) findViewById(R.id.tv_order_resAddr);
        tv_order_desAddr = (TextView) findViewById(R.id.tv_order_desAddr);
        ly_order.setOnClickListener(this);
    }

    private void setLy_orderMsg(OrderInfo orderInfo) {
        tv_order_type.setText(orderInfo.getType());
        if (orderInfo.isSet_out_flag()) {
            tv_order_time.setText(getDateToString(Longs.tryParse(orderInfo.getSetouttime())));
        } else {
            tv_order_time.setText("现在");
        }
        tv_order_resAddr.setText(Html.fromHtml("从 <font color='#ffff'>" + orderInfo.getReservationAddress() + "</font>"));
        if (orderInfo.getDestination() != null) {
            tv_order_desAddr.setText(Html.fromHtml("到 <font color='#ffff'>" + orderInfo.getDestination() + "</font>"));
        }
    }


    FragmentManager fragmentManager;
    MyFunctionFrag functionFrag;
    DriverMenuFrag menuFrag;
//    OrderFrag orderFrag;

    private void initFrag() {
        functionFrag = new MyFunctionFrag();
        menuFrag = new DriverMenuFrag();
//        orderFrag = (OrderFrag) OrderFrag.newInstance();
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(R.id.menu_right, functionFrag, "functionFrag")
                .add(R.id.menu_left, menuFrag, "menuFrag")
                .addToBackStack("null")
                .hide(functionFrag)
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
//                startActivity(new Intent(this, MainActivity.class));
                break;
            case R.id.ly_sum_yesterday:
                //昨日收入
                startActivity(new Intent(this, MyHistoryOrderActivity.class));
                break;
            case R.id.ly_sum_today:
                //今日收入
                startActivity(new Intent(this, MyHistoryOrderActivity.class));
                break;
            case R.id.ly_order:
                //显示正在进行的订单
//                showFrag(orderFrag);
                if (mOrderInfos == null) {
                    return;
                }
                if (mOrderInfos.size() == 1) {
                    toMyOrderAcitivityForResult(mOrderInfos.get(0), null);
                } else {
                    toMyOrderAcitivityForResult(mOrderInfos.get(0), mOrderInfos.get(1));
                }
                break;
            case R.id.img_desk_show:
                //显示中间收入明细
                showSumDetail();
                break;
            case R.id.btn_createOrder:
                //创建订单
                if (mAMapLocation == null) {
                    return;
                }
                if (!online) {
                    ToastUtil.show(this, "请先上线");
                    return;
                }
                Bundle bundle = new Bundle();
                Log.i(TAG, "toCreateOrder: " + mAMapLocation.getCity());
                Log.i(TAG, "toCreateOrder: " + mAMapLocation.getPoiName());
                Log.i(TAG, "toCreateOrder: " + mAMapLocation.getAddress());
                Log.i(TAG, "toCreateOrder: " + mAMapLocation.getLatitude());
                Log.i(TAG, "toCreateOrder: " + mAMapLocation.getLongitude());
                bundle.putString("city", mAMapLocation.getCity());
                bundle.putString("poiName", mAMapLocation.getPoiName());
                bundle.putString("poiAddr", mAMapLocation.getAddress());
                bundle.putDouble("posi_lati", mAMapLocation.getLatitude());
                bundle.putDouble("posi_longi", mAMapLocation.getLongitude());
                Intent intent = new Intent(this, CreateOrderActivity.class);
                intent.putExtra("bundle", bundle);
                startActivityForResult(intent, Str.REQUEST_CREATE_ORDER);
                break;
            case R.id.btn_onLine:
                //上线
                checkDriverType();
                break;
            case R.id.btn_backToCity:
                //返城
                ly_desk02.setVisibility(View.GONE);
                ly_desk.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1.0f));
                showMap = !showMap;
                break;
            case R.id.img_map_top02:
                //显示地图布局
                showMap = !showMap;
                if (showMap) {
                    ly_desk02.setVisibility(View.GONE);
                    ly_desk.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1.0f));
                } else {
                    ly_desk02.setVisibility(View.VISIBLE);
                    ly_desk.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 10.0f));
                }
                break;
            case R.id.loc:
                //定位
                centerToMyLocation(aMap, mLocationClient, myOrientationListener, mAMapLocation.getLatitude(), mAMapLocation.getLongitude());
                break;
        }
    }

    boolean showMap;

    private void toCreateOrder() {

    }

    /**
     * 上线确定接单类型
     */
    private void checkDriverType() {
        if (driver.getType().equals("")) {
            Log.i(TAG, "checkDriverType: " + driver.getTypes().size());
            if (driver.getTypes().size() == 1) {
                if (driver.getTypes().get(0).equals("2")) {
                    driver.setType("2");//代驾
                    btn_createOrder.setVisibility(View.VISIBLE);
                } else {
                    driver.setType("1");
                }
                onLine();
                return;
            } else {//类型不止一个
                for (int i = 0; i < driver.getTypes().size(); i++) {
                    if (driver.getTypes().get(i).equals("2")) {
                        Log.i(TAG, "checkDriverType: ");
                        myDialog.chooseOnLineType(REQEST_CHOOSE_ONLINE_TYPE, this);
                        return;
                    } else {
                        driver.setType("1");
                    }
                }
                onLine();//不为代驾
                return;
            }
        } else {
            onLine();
        }
    }

    private void setDaiJiaButtonVisibility(Driver driver) {
        if (driver.getType() != null && driver.getType().equals("2")) {
            btn_createOrder.setVisibility(View.VISIBLE);
        }
    }

    private void checkDriverCar() {
        if (driverCarBean == null) {
            driverCarBean = myHttpUtils.getDriverCars(Str.URL_DRIVER_CARS);
        }
        if (driverCarBean == null) {
            return;
        }
        if (driverCarBean.getDriverCars().size() == 0) {
            if (driver != null && driver.getType().equals("2")) {

            } else if (driver != null && driver.getType().equals("1")) {
                myDialog.showAddDriverCar();
            }
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
    }

    public boolean isInOrder() {
        return inOrder;
    }

    private long onLineTimeMillis;
    private long offLineTimeMillis;

    private void onLine() {
        if (online) {//已经上线，并且在接单中，不能下线
            if (inOrder) {
                speak("您正处于接单状态，请处理完订单再下线");
                return;
            }
        }
        if (driver == null) {
            return;
        }
        Log.i(TAG, "onLine: getType" + driver.getType());
        List<String> result;
        if (online) {
            offLineTimeMillis = System.currentTimeMillis();
        } else {
            onLineTimeMillis = System.currentTimeMillis();
        }
        if (driver.getType().equals("2")) {
            Log.i(TAG, "onLine: 2");
            result = myHttpUtils.onlineAppDJ(Str.URL_ONLINE_DJ, !online, onLineTimeMillis, offLineTimeMillis);//上线
        } else {
            Log.i(TAG, "onLine: 1");
            if (mDriverCar == null) {
                checkDriverCar();
                return;
            }
            if (mDriverCar == null) {
                return;
            }
            result = myHttpUtils.onlineApp(Str.URL_ONLINE, mDriverCar.getId(), !online, onLineTimeMillis, offLineTimeMillis);//上线
        }
        if (result == null) {
            return;
        }
        if (result.get(0).equalsIgnoreCase("OK")) {
            online = !online;//上线、下线
//            speechSynthesizer.startSpeaking("开始接单啦 ", mySynthesizerListener);
            if (online) {//上线重置时间
                speak("开始接单啦，快去赚钱吧");
                resetOnlineTime(result);
                if (!driver.getType().equals("2")) {
                    updateStatus = true;
                    handler.sendEmptyMessage(UPDATE_CARSTATUS);
//                    sendEmptyMessageUpdateStatus(UPDATE_CARSTATUS, 60000);
                }
            } else {
                setOnlineTime(20, "上线");
                updateStatus = false;
                if (handler.hasMessages(UPDATE_CARSTATUS)) {
                    handler.removeMessages(UPDATE_CARSTATUS);
                }
                speak("停止接单啦，享受生活吧");
            }
        } else if (result.get(0).equalsIgnoreCase("ERROR")) {
            driver.setType("");
            ToastUtil.show(this, result.get(1));
            return;
        }
    }

    private void resetOnlineTime(List<String> result) {
        onlineTime = new DateTime(onlineTime.getYear(), onlineTime.getMonthOfYear(),
                onlineTime.getDayOfMonth(), 0, 0, 0);
        onlineTime = onlineTime.plusMinutes(Ints.tryParse(result.get(2)));//当天累计上线时间
        setOnlineTime(12, "上线时间\n" + onlineTime.toString("HH:mm"));
//        sendEmptyMessage(ONLINE_TIME, 60000);
        handler.sendEmptyMessageDelayed(ONLINE_TIME, 60000);
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
        super.onDestroy();
        mMapView.onDestroy();
//        mCameraChangeListener = null;
        myOrientationListener = null;
        if (null != mLocationClient) {
            mLocationClient.onDestroy();
        }
        deactivate();
        Log.i(TAG, "onDestroy: ");
        if (online) {
            Log.i(TAG, "onDestroy: 111");
            if (online) {
                offLineTimeMillis = System.currentTimeMillis();
            } else {
                onLineTimeMillis = System.currentTimeMillis();
            }
            online = false;
            if (driver.getType().equals("2")) {
                myHttpUtils.onlineAppDJ(Str.URL_ONLINE_DJ, online, onLineTimeMillis, offLineTimeMillis);//下线
            } else {
                myHttpUtils.onlineApp(Str.URL_ONLINE, mDriverCar.getId(), online, onLineTimeMillis, offLineTimeMillis);//下线
            }
        }

        if (mServiceIntents != null) {
            stopService(mServiceIntents);
        }
        if (mNetReceiver != null) {
            unregisterReceiver(mNetReceiver);
            mNetReceiver = null;
        }

        handler.removeCallbacksAndMessages(null);
        if (handler.hasMessages(ONLINE_TIME)) {
            Log.i(TAG, "onDestroy: ONLINE_TIME");
            handler.removeMessages(ONLINE_TIME);
        }
        if (handler.hasMessages(INIT_FRAG)) {
            Log.i(TAG, "onDestroy: INIT_FRAG");
            handler.removeMessages(INIT_FRAG);
        }
        if (handler.hasMessages(CHECK_ORDER)) {
            Log.i(TAG, "onDestroy: CHECK_ORDER");
            handler.removeMessages(CHECK_ORDER);
        }
        if (handler.hasMessages(CHECK_ADCODE)) {
            Log.i(TAG, "onDestroy: CHECK_ADCODE");
            handler.removeMessages(CHECK_ADCODE);
        }
        if (handler.hasMessages(CHECK_AD_UPDATE)) {
            Log.i(TAG, "onDestroy: CHECK_AD_UPDATE");
            handler.removeMessages(CHECK_AD_UPDATE);
        }
        if (handler.hasMessages(CHECK_APP_UPDATE)) {
            Log.i(TAG, "onDestroy: CHECK_APP_UPDATE");
            handler.removeMessages(CHECK_APP_UPDATE);
        }
        if (handler.hasMessages(UPDATE_CARSTATUS)) {
            Log.i(TAG, "onDestroy: UPDATE_CARSTATUS");
            handler.removeMessages(UPDATE_CARSTATUS);
            updateStatus = false;
        }
        if (handler.hasMessages(UPDATE_LOCATION)) {
            Log.i(TAG, "onDestroy: UPDATE_CARSTATUS");
            updateLoc = false;
            handler.removeMessages(UPDATE_LOCATION);
        }

        handler = null;
        if (orderMessageReceiver != null) {
            unregisterReceiver(orderMessageReceiver);
            orderMessageReceiver = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initAmountMsg();
        mMapView.onResume();
        if (mLocationClient != null) {
            mLocationClient.startLocation();
        }
        if (JPushInterface.isPushStopped(this)) {
            Log.i(TAG, "onResume: JPushInterface.isPushStopped(this)");
            JPushInterface.init(this);
        }
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
        if (requestCode == REQEST_CHOOSE_ONLINE_TYPE) {
            String onlineType = arg0;//2:代驾，1：其他
            Log.i(TAG, "sure: " + onlineType);
            driver.setType(onlineType);
            if (onlineType.equals("2")) {
                btn_createOrder.setVisibility(View.VISIBLE);
            }
            onLine();
        }
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

    @Override
    public void isInOrder(boolean inOrder) {
        if (inOrder) {
            if (ly_order == null) {
                initInOrderView();
            }
            ly_sum.setVisibility(View.GONE);
            ly_desk02.setVisibility(View.GONE);
            ly_order.setVisibility(View.VISIBLE);
            btn_backToCity.setVisibility(View.GONE);//返程按钮不可见
            setLy_orderMsg(mOrderInfos.get(0));
        } else {
            ly_sum.setVisibility(View.VISIBLE);
            ly_desk02.setVisibility(View.VISIBLE);
            if (ly_order != null) {
                ly_order.setVisibility(View.GONE);
            }
        }
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Driver Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (isFirstIn) {
            if (aMap != null) {
                aMap.setMyLocationEnabled(true);
                if (mLocationClient != null) {
                    if (!mLocationClient.isStarted()) {
                        mLocationClient.startLocation();
                    }
                }
                if (myOrientationListener != null) {
                    if (!myOrientationListener.isStarted()) {
                        //开启方向传感器
                        myOrientationListener.start();
                    }
                }
            }
        }
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();
//停止定位
//        aMap.setMyLocationEnabled(false);
//        if (mLocationClient != null) {
//            mLocationClient.stopLocation();
//        }
//        //关闭方向传感器
//        if (myOrientationListener != null) {
//            if (myOrientationListener.isStarted()) {
//                myOrientationListener.stop();
//            }
//        }
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mListener = onLocationChangedListener;
        if (mLocationClient == null) {
            mLocationClient = new AMapLocationClient(this);
            mLocationOption = new AMapLocationClientOption();
            //设置定位监听
            mLocationClient.setLocationListener(this);
            //设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            mLocationOption.setInterval(5000);
            mLocationClient.setLocationOption(mLocationOption);

            mLocationClient.startLocation();
        }
    }

    @Override
    public void deactivate() {
        mListener = null;
        if (mLocationClient != null) {
            mLocationClient.stopLocation();
            mLocationClient.onDestroy();
        }
        mLocationClient = null;
    }

    public class OrderMessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Str.ORDER_MESSAGE_RECEIVED_ACTION.equals(intent.getAction())) {
                String orderTitle = intent.getStringExtra(Str.KEY_ORDER_TITLE);
                String orderMessage = intent.getStringExtra(Str.KEY_ORDER_MESSAGE);//订单消息
                String orderExtras = intent.getStringExtra(Str.KEY_ORDER_EXTRA);//可能为空
                if (orderTitle.equals("1")) {//订单消息
                    if (inOrder) {
                        return;//已经接单并且有2单在处理，则不提示有新订单
                    }
                    showOrder(orderMessage);
                } else if (orderTitle.equals("4")) {//通知推送
                    if (orderMessage != null) {
                        MyNotificationInfo myNotificationInfo = getNotificationInfo(orderMessage);
                        if (myNotificationInfo != null && myNotificationInfo.getContent() != null && !myNotificationInfo.getContent().equalsIgnoreCase("")) {
                            speak("您有新的通知：" + orderMessage);
                        }
                        preferences.edit().putString("notification", orderMessage).commit();
                    }
                }
            }
        }
    }

    boolean inOrder;

    private void speak(String content) {
        if (mTipHelper == null) {
            mTipHelper = new TipHelper(this);
        }
        if (mTipHelper == null) {
            return;
        }
        mTipHelper.Vibrate(500);//震动0.5秒
        mTipHelper.speak(content);
    }

    public void showOrder(String orderMessage) {
        mOrderInfo = getOrderInfoFromReceiver(orderMessage);
        Log.i(TAG, "showOrder: " + mOrderInfo);
        if (mOrderInfo == null) {
            return;
        }
        final MyOrderDialog orderDialog = new MyOrderDialog(this, this, mAMapLocation, mOrderInfo);
        orderDialog.show(mOrderInfo.getTimeOut(), new MyOrderDialog.OrderInterface() {
            @Override
            public void take(boolean take) {
                if (take) {
                    List<String> orderResult = myHttpUtils.takeOrder(Str.URL_TAKE_ORDER, mOrderInfo.getId());
                    if (orderResult == null) {
                        return;
                    }
                    if (orderResult.get(0).equalsIgnoreCase("OK")) {
                        speak("接单成功 ");
                        if (mOrderInfos == null) {//接单后才添加到接单列表
                            mOrderInfos = new ArrayList<>();
                            mOrderInfos.add(mOrderInfo);
                        }
                        mFanChengInfo = mOrderInfos.get(0);
//                        showFrag(orderFrag);
                        toMyOrderAcitivityForResult(mOrderInfos.get(0), null);

//                        if (inOrder) {
//                            //已经接单了
//                            orderFrag.addOrderInfo(mOrderInfos.get(1));
//                        } else {
//                            orderFrag.setmOrderInfo(mOrderInfos.get(0));
//                        }
                        inOrder = true;
                        isInOrder(inOrder);
                    } else {
                        speak("抢单失败 ");
//                        speechSynthesizer.startSpeaking("接单失败 ", mySynthesizerListener);
                    }
                }
            }
        });
        speak("来新订单了 ");
    }

    private void toMyOrderAcitivityForResult(OrderInfo orderInfo_0, OrderInfo orderInfo_1) {
        Bundle bundle = new Bundle();
        if (orderInfo_0 != null) {
            bundle.putSerializable("orderInfo_0", orderInfo_0);
        }
        if (orderInfo_1 != null) {
            bundle.putSerializable("orderInfo_1", orderInfo_1);
        }
        Intent intent = new Intent(this, MyOrderActivity.class);
        intent.putExtra("bundle", bundle);
        startActivityForResult(intent, Str.REQUEST_ORDER_ACTIVITY);
    }

    private OrderInfo mFanChengInfo;//返程信息

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, "onActivityResult: requestCode:" + requestCode + "resultCode:" + resultCode);
        if (data == null) {
            return;
        }
        if (resultCode == Str.REQUEST_ORDER_ACTIVITY) {
            if (data.getStringExtra("result").equalsIgnoreCase("OK")) {//完成所有订单
//                mOrderInterface.orderEnd(true);//传递结束通知
                mFanChengInfo = mOrderInfos.get(0);
                mOrderInfos = null;
                inOrder = false;
                isInOrder(inOrder);
                btn_backToCity.setVisibility(View.VISIBLE);//返程按钮可见
                speak("完成服务");
            } else if (data.getStringExtra("result").equalsIgnoreCase("OK_Two")) {//
                int position = data.getIntExtra("position", 999);
                mOrderInfos.remove(position);
//                mOrderInterface.positionFromMain(position);//传递第几位乘客订单完成
                setLy_orderMsg(mOrderInfos.get(0));//刷新主界面订单布局数据
                speak("完成服务");
            } else if (data.getStringExtra("result").equalsIgnoreCase("NO")) {//取消结算，无操作

            }
            checkInOrder();
        } else if (resultCode == Str.REQUEST_CREATE_ORDER) {//创建订单
            Bundle bundle = data.getBundleExtra("bundle");
            OrderInfo orderInfo = (OrderInfo) bundle.getSerializable("orderInfo");

            Log.i(TAG, "driverCreateOrder: " + orderInfo);

            if (orderInfo == null) {
                return;
            }
            if (mOrderInfos == null) {
                mOrderInfos = new ArrayList<>();
            }
            if (mOrderInfos.size() == 0) {
                OrderInfo createOrderInfo = myHttpUtils.driverCreateOrder(Str.URL_DRIVER_CREATE_ORDER, orderInfo);
                if (createOrderInfo != null) {
                    if (createOrderInfo.getEndLatitude() == 0) {
                        createOrderInfo.setStartLatitude(orderInfo.getStartLatitude());
                        createOrderInfo.setStartLongitude(orderInfo.getStartLongitude());
                        createOrderInfo.setEndLatitude(orderInfo.getEndLatitude());
                        createOrderInfo.setEndLongitude(orderInfo.getEndLongitude());
                    }
                    if (createOrderInfo.getReservationAddress() == null || createOrderInfo.getReservationAddress().equalsIgnoreCase("")) {
                        createOrderInfo.setReservationAddress(orderInfo.getReservationAddress());
                    }
                    if (createOrderInfo.getDestination() == null || createOrderInfo.getDestination().equalsIgnoreCase("")) {
                        createOrderInfo.setDestination(orderInfo.getDestination());
                    }
                    if (createOrderInfo.getDistance() == 0) {
                        createOrderInfo.setDistance(orderInfo.getDistance());
                    }
                    mOrderInfos.add(createOrderInfo);
//                    orderFrag.setmOrderInfo(mOrderInfos.get(0));
                    inOrder = true;
                    isInOrder(inOrder);
//                    showFrag(orderFrag);
                    toMyOrderAcitivityForResult(mOrderInfos.get(0), null);
                    mFanChengInfo = mOrderInfos.get(0);
                } else {
                    ToastUtil.show(this, "创建失败，请重试");
                }
            } else {
                speak("有未处理完的订单，请先处理完订单！");
            }

        }
    }


    private MapView mMapView = null;
    private AMap aMap = null;
    private AMapLocationClient mLocationClient;
    private AMapLocationClientOption mLocationOption;
    private LocationSource.OnLocationChangedListener mListener;
    private float mCurrentX;
    private MyOrientationListener myOrientationListener;

    /**
     * 初始化地图、定位
     *
     * @param savedInstanceState
     */
    private void setUpMap(Bundle savedInstanceState) {
        this.context = this;
        //获取地图控件引用
        mMapView = (MapView) findViewById(R.id.mapView);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，实现地图生命周期管理
        mMapView.onCreate(savedInstanceState);
        aMap = mMapView.getMap();
    }

    private MyLocationStyle myLocationStyle;

    private void setUpMap() {
        //定义一个UiSettings对象
        UiSettings mUiSettings = aMap.getUiSettings();
        mUiSettings.setZoomControlsEnabled(false);//是否允许显示缩放按钮。
        mUiSettings.setCompassEnabled(false);//指南针可用不可用。
        mUiSettings.setRotateGesturesEnabled(false);//是否允许通过手势来旋转。
        mUiSettings.setScaleControlsEnabled(false);//设置比例尺功能是否可用

        aMap.moveCamera(CameraUpdateFactory.zoomTo(18.0f));
        aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
        aMap.setLocationSource(this);// 设置定位监听
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false

        myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.drawable.navi_map_gps_locked));
        myLocationStyle.strokeColor(Color.argb(0, 0, 0, 0));// 设置圆形的边框颜色
        myLocationStyle.radiusFillColor(Color.argb(0, 0, 0, 0));// 设置圆形的填充颜色
        aMap.setMyLocationStyle(myLocationStyle);
        //设置方向监听
        myOrientationListener = new MyOrientationListener(context);
        myOrientationListener.setmOnOritationListener(new MyOrientationListener.OnOritationListener() {
            @Override
            public void onOritationChanged(float x) {
                mCurrentX = x;
            }
        });
    }

    private RouteSearch mRouteSearch;
    private MyRouteSearchListener myRouteSearchListener;
    private DrivePath mDrivePath;

    /**
     * 获取驾车路线信息
     */
    private void setMyRouteSearchListener() {
        mRouteSearch = new RouteSearch(context);
        myRouteSearchListener = new MyRouteSearchListener(aMap, context);
        mRouteSearch.setRouteSearchListener(myRouteSearchListener);
        myRouteSearchListener.getDrivePathListener(new MyRouteSearchListener.MyDrivePathListener() {
            @Override
            public void getDrivePath(DrivePath drivePath) {
                mDrivePath = drivePath;
                if (mDrivePath != null && mAMapLocation != null) {//根据路径获取里程数等
//                    Toast.makeText(context, "预计行程花费 " + AMapUtil.getFriendlyTime((int) drivePath.getDuration())
//                            + " 总距离：" + AMapUtil.getKiloLength(drivePath.getDistance()) + "千米", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    RelativeLayout rl_map;
    ImageView img_map_top02;//展开地图箭头
    TextView tv_map_top02;//显示当前位置地名
    ImageButton loc;

    private void initMapView() {
        rl_map = (RelativeLayout) findViewById(R.id.rl_map);
        img_map_top02 = (ImageView) findViewById(R.id.img_map_top02);
        tv_map_top02 = (TextView) findViewById(R.id.tv_map_top02);
        loc = (ImageButton) findViewById(R.id.loc);
        img_map_top02.setOnClickListener(this);
        loc.setOnClickListener(this);
    }
//    WindowManager wm;Display display;FrameLayout.LayoutParams params;
//    public RelativeLayout.LayoutParams setViewParams(View view, int weightScale, int hightScale) {
//        wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
//        display = wm.getDefaultDisplay();
//        int hight = display.getHeight();
//        int width = display.getWidth();
//        params = (FrameLayout.LayoutParams) view.getLayoutParams();
//        params.height = hight;
//        if (hightScale == 2){
//            params.width = width / weightScale;
//        }else {
//            params.width = 10;
//        }
//        return params;
//    }

    private boolean isFirstIn = true;
    private LatLng startLat, endLat;
    public MyAMapLocation myAMapLocation;
    AMapLocation mAMapLocation;
    LatLng updateLocLatlng;

    /**
     * 定位
     *
     * @param aMapLocation
     */
    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (mListener != null && aMapLocation != null) {
            if (aMapLocation != null
                    && aMapLocation.getErrorCode() == 0) {
                mListener.onLocationChanged(aMapLocation);// 显示系统小蓝点
                aMap.setMyLocationRotateAngle(mCurrentX);

                double endLat_lati = aMapLocation.getLatitude();
                double endLat_longi = aMapLocation.getLongitude();
                updateLocLatlng = new LatLng(endLat_lati, endLat_longi);

                mAMapLocation = aMapLocation;
                myAMapLocation = new MyAMapLocation(mAMapLocation.getCountry(), mAMapLocation.getProvince(),
                        mAMapLocation.getCity(), mAMapLocation.getDistrict(), mAMapLocation.getAddress(), mAMapLocation.getAdCode());
                tv_map_top02.setText(mAMapLocation.getPoiName());
                //首次进入定位到我的位置
                if (isFirstIn) {
                    centerToMyLocation(aMap, mLocationClient, myOrientationListener, mAMapLocation.getLatitude(), mAMapLocation.getLongitude());
                    double latitude_0 = mAMapLocation.getLatitude();
                    double longitude_0 = mAMapLocation.getLongitude();
                    startLat = new LatLng(latitude_0, longitude_0);
                    if (driver == null) {
                        driver = myHttpUtils.getDriverByGET(Str.URL_MEMBER);
                    }
                    handler.sendEmptyMessageDelayed(CHECK_ADCODE, 6000);//上传adcode
                    handler.sendEmptyMessageDelayed(CHECK_AD_UPDATE, 60000);//广告需要定位
                    updateLoc = true;
                    handler.sendEmptyMessageDelayed(UPDATE_LOCATION, 10000);
                    isFirstIn = false;
                } else {//判断是否上传位置
                    myActivityLocInterface.getMyLocation(mAMapLocation);
//                    if (updateLoc) {
//                        upDateLocation(aMapLocation);
//                        updateLoc = false;
//                    }

                }
            } else {
                String errText = "定位失败," + aMapLocation.getErrorCode() + ": " + aMapLocation.getErrorInfo();
                Log.e("AmapErr", errText);
//                Toast.makeText(this, errText, Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 上传会员位置(判断与原位置相差10米)
     */
//    private void upDateLocation(boolean upDate) {
//        if (mAMapLocation == null) {
//            return;
//        }
//        endLat = new LatLng(mAMapLocation.getLatitude(), mAMapLocation.getLongitude());
//        float distance = 0;
//        if (startLat != null && endLat != null) {
//            distance = AMapUtils.calculateLineDistance(startLat, endLat);
//        }
//        if (distance > 10) {
//            Log.i(TAG, "upDateLocation: ");
//            startLat = new LatLng(endLat.latitude, endLat.longitude);
//            //上传位置
//            upDateLocation();
//        }
//    }
    private void upDateLocation(AMapLocation mAMapLocation, LatLng updateLocLatlng) {
        if (mAMapLocation == null || updateLocLatlng == null) {
            return;
        }
        ClientLocationInfo locationInfo = new ClientLocationInfo(updateLocLatlng.longitude + "",
                updateLocLatlng.latitude + "", mAMapLocation.getSpeed() + "", mCurrentX + "", mAMapLocation.getLocationType());
        List<String> list = myHttpUtils.upDateLocation(Str.URL_UPDATELOCATION_DRIVER, locationInfo);
    }
    /**
     * 回调当前位置
     *
     //     * @param aMapLocation
     */
//    @Override
//    public void getMyLocation(AMapLocation aMapLocation) {
//        if (aMapLocation != null) {
//            this.aMapLocation = aMapLocation;//从AMapFrag获取
//            if (!hasChecked) {
//
//            }
//            myActivityLocInterface.getMyLocation(aMapLocation);//传给MyFunctionFrag
//        }
//    }

//    @Override
//    public void getAMap(AMap aMap) {
//        mAMap = aMap;
//    }
//
//    @Override
//    public void getClientLocationInfo(ClientLocationInfo locationInfo) {
//        List<String> list = myHttpUtils.upDateLocation(Str.URL_UPDATELOCATION_DRIVER, locationInfo);
//        Log.i(TAG, "getClientLocationInfo: upDateLocation");
//        ToastUtil.show(this, "driver updateLocation");
//    }

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

    public void setMyOrderInterface(MyOrderInterface myOrderInterface) {
        this.mOrderInterface = myOrderInterface;
    }

//    /**
//     * 1分钟更新一次上线时间
//     *
//     * @param what
//     */
//    public void sendEmptyMessage(final int what, final long delayMillis) {
//        handler.post(new Runnable() {
//            @Override
//            public void run() {
//                handler.sendEmptyMessage(what);
//            }
//        });
//    }

//    /**
//     * 持续更新使在线
//     *
//     * @param what
//     * @param delayMillis
//     */
//    public void sendEmptyMessageUpdateStatus(final int what, final long delayMillis) {
//        handler.post(new Runnable() {
//            @Override
//            public void run() {
//                handler.sendEmptyMessage(what);
//            }
//        });
//    }
}
