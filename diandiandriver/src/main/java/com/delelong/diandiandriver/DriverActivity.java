package com.delelong.diandiandriver;

import android.animation.LayoutTransition;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
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
import com.delelong.diandiandriver.bean.OrderInfo;
import com.delelong.diandiandriver.bean.Str;
import com.delelong.diandiandriver.bean.TraverOrderBean;
import com.delelong.diandiandriver.bean.ZhuanXianOrderBean;
import com.delelong.diandiandriver.dialog.MyDialogUtils;
import com.delelong.diandiandriver.dialog.MyOrderDialog;
import com.delelong.diandiandriver.dialog.MyProgressDialog;
import com.delelong.diandiandriver.dialog.MyToastDialog;
import com.delelong.diandiandriver.dialog.MyTraverOrderDialog;
import com.delelong.diandiandriver.dialog.MyZhuanXianOrderDialog;
import com.delelong.diandiandriver.fragment.DriverMenuFrag;
import com.delelong.diandiandriver.fragment.MyAppUpdate;
import com.delelong.diandiandriver.function.MyFunctionActivity;
import com.delelong.diandiandriver.http.ClientLocationInfo;
import com.delelong.diandiandriver.http.MyAsyncHttpUtils;
import com.delelong.diandiandriver.http.MyHttpHelper;
import com.delelong.diandiandriver.http.MyProgTextHttpResponseHandler;
import com.delelong.diandiandriver.http.MyTextHttpResponseHandler;
import com.delelong.diandiandriver.listener.MyHttpDataListener;
import com.delelong.diandiandriver.listener.MyOrientationListener;
import com.delelong.diandiandriver.listener.MyRouteSearchListener;
import com.delelong.diandiandriver.menuActivity.MyHistoryOrderActivity;
import com.delelong.diandiandriver.menuActivity.MyReservationOrderActivity;
import com.delelong.diandiandriver.order.CreateOrderActivity;
import com.delelong.diandiandriver.order.MyCheckOrderListener;
import com.delelong.diandiandriver.order.MyOrderActivity;
import com.delelong.diandiandriver.receiver.NetReceiver;
import com.delelong.diandiandriver.service.CoreService;
import com.delelong.diandiandriver.traver.activity.ExecutionTraverActivity;
import com.delelong.diandiandriver.traver.activity.TraverActivity;
import com.delelong.diandiandriver.utils.MD5;
import com.delelong.diandiandriver.utils.MyApp;
import com.delelong.diandiandriver.utils.TipHelper;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;
import com.loopj.android.http.RequestParams;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cn.jpush.android.api.JPushInterface;
import cz.msebera.android.httpclient.Header;
import de.greenrobot.event.EventBus;

/**
 * Created by Administrator on 2016/9/21.
 */
public class DriverActivity extends BaseActivity implements View.OnClickListener,
        MyDialogUtils.MyDialogInterface, MyCheckOrderListener, MyHttpDataListener,
        LocationSource, AMapLocationListener, SwipeRefreshLayout.OnRefreshListener {

    ExecutorService threadPool = Executors.newCachedThreadPool();

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
    public static final int REFRESH_AMOUNT = 18;//持续更新状态，防止下线
    public static final int INIT_DRIVER = 19;//获取司机个人信息
    public static final int INIT_DRIVER_CAR_BEAN = 20;//获取司机个人信息
    public static final int INIT_AMOUNT_MSG = 21;//获取司机余额收入信息
    public static final int CHECK_TO_ONLINE = 22;//检测并上线
    public static final int ONLINE = 23;//上线
    public static final int CHECK_DRIVER_CAR = 24;//检测司机车辆
    public static final int SHOW_ORDER = 25;//显示新订单
    public static final int CREATE_ORDER_BY_DRIVER = 26;//司机创建新订单
    public static final int CHECK_RESERVATION_ORDER = 27;//检测是否有预约订单
    private static final int CLICKABLE_DELAYED = 120;//点击后延迟恢复可点击状态
    public static final int CHECK_IS_ONLINE = 28;//检查是否已经上线
    public static final int CHECK_JPUSH = 30;//检查是否已经上线
    public static final int SHOW_TRAVER_ORDER = 31;//显示顺风车新订单
    public static final int SHOW_ZHUANXIAN_ORDER = 32;//显示专线新订单


    private static final long CLICKABLE_DELAYED_TIMEMILIS = 3500;//点击后延迟时间
    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATE_CARSTATUS:
                    if (updateStatus) {
                        updateStatus();
                        sendEmptyMsgDelayToHandlerByExecutor(UPDATE_CARSTATUS, 55000);
                    }
                    break;
                case CHECK_JPUSH:
//                    checkJpush();
//                    sendEmptyMsgDelayToHandlerByExecutor(CHECK_JPUSH, 40000);
                    break;
                case REFRESH_AMOUNT:
                    initAmountMsg();
                    break;
                case ONLINE_TIME:
                    if (online) {
                        onlineTime = onlineTime.plusMinutes(1);
                        setOnlineTime(12, "上线时间\n" + onlineTime.toString("HH:mm"));
                        if (online) {
                            if (!handler.hasMessages(ONLINE_TIME)) {
                                sendEmptyMsgDelayToHandlerByExecutor(ONLINE_TIME, 60000);
                            }
                        }
                    }
                    break;
                case INIT_FRAG://加载fragment和地图view
                    initFrag();
                    setMyRouteSearchListener();
                    initMapView();
                    break;
                case CHECK_ORDER:
                    checkInOrder();
                    break;
                case CHECK_RESERVATION_ORDER:
                    checkReservationOrder();
                    break;
                case CHECK_ADCODE:
                    updateAdcode();
                    break;
                case CHECK_AD_UPDATE:
                    permissionExternalStorage();
                    download();
                    break;
                case CHECK_APP_UPDATE:
//                    if (preferences != null && (preferences.getInt("updatetime", 3) % 3 == 0)) {
                    //每三次进入app检查一次更新
                    permissionExternalStorage();
                    MyAppUpdate myAppUpdate = new MyAppUpdate(DriverActivity.this);
                    myAppUpdate.checkUpdate();
//                    }
                    break;
                case UPDATE_LOCATION:
                    upDateLocation(mAMapLocation, updateLocLatlng);
                    if (updateLoc) {
                        sendEmptyMsgDelayToHandlerByExecutor(UPDATE_LOCATION, 15000);
                    }
                    break;
                case INIT_DRIVER:
                    initDriver();
                    break;
                case INIT_DRIVER_CAR_BEAN:
                    initDriverCarBean();
                    break;
                case INIT_AMOUNT_MSG:
                    initAmountMsg();
                    break;
                case CHECK_TO_ONLINE:
                    checkToOnLine();
                    break;
                case CHECK_IS_ONLINE:
                    CheckIsOnline();
                    break;
                case ONLINE:
                    onLine();
                    break;
                case CHECK_DRIVER_CAR:
                    checkDriverCar();
                    break;
                case SHOW_ORDER:
                    String orderMessage = (String) msg.obj;
                    showOrder(orderMessage);
                    break;
                case SHOW_TRAVER_ORDER:
                    String traverOrderMessage = (String) msg.obj;
                    showTraverOrder(traverOrderMessage);
                    break;
                case SHOW_ZHUANXIAN_ORDER:
                    String zhuanXianOrderMessage = (String) msg.obj;
                    showZhuanXianOrder(zhuanXianOrderMessage);
                    break;
                case CREATE_ORDER_BY_DRIVER:
                    OrderInfo orderInfo = (OrderInfo) msg.obj;
                    createOrderByDriver(orderInfo);
                    break;
                case CLICKABLE_DELAYED:
                    View view = (View) msg.obj;
                    view.setClickable(true);
                    showProgress(false);
                    break;
            }
        }
    };
//
//    boolean checkJpushs;
//
//    private void checkJpush() {
//        if (checkJpushs) {
//            if (JPushInterface.isPushStopped(MyApp.getInstance())) {
//                JPushInterface.resumePush(MyApp.getInstance());
//            }
//        } else {
//            JPushInterface.stopPush(MyApp.getInstance());
//        }
//        checkJpushs = !checkJpushs;
//    }

    private void sendEmptyMsgToHandlerByExecutor(final int what) {
        threadPool.submit(new Runnable() {
            @Override
            public void run() {
                handler.sendEmptyMessage(what);
            }
        });
    }

    private void sendEmptyMsgDelayToHandlerByExecutor(final int what, final long delayMillis) {
        threadPool.submit(new Runnable() {
            @Override
            public void run() {
                handler.sendEmptyMessageDelayed(what, delayMillis);
            }
        });
    }

    private void sendMsgToHandlerByExecutor(final int what, final Object object) {
        threadPool.submit(new Runnable() {
            @Override
            public void run() {
                Message message = handler.obtainMessage();
                message.what = what;
                message.obj = object;
                handler.sendMessage(message);
            }
        });
    }

    private void sendMsgDelayToHandlerByExecutor(final int what, final Object object, final long delayMillis) {
        threadPool.submit(new Runnable() {
            @Override
            public void run() {
                Message message = handler.obtainMessage();
                message.what = what;
                message.obj = object;
                handler.sendMessageDelayed(message, delayMillis);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Str.REQUEST_WRITE_EXTERNALSTORAGE || requestCode == Str.REQUEST_DELE_CREATE_EXTERNALSTORAGE) {
            if (grantResults != null && grantResults.length > 0) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    download();
                }
            }
        }
    }

    DriverActivity driverActivity;
    boolean updateStatus = true;
    boolean updateLoc = true;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    private void updateStatus() {
        MyAsyncHttpUtils.post(Str.URL_UPDATE_CARSTATUS, new MyTextHttpResponseHandler() {
            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                Log.i(TAG, "onFailure:updateStatus: " + s);
                MyToastDialog.show(mContext, "抱歉，服务器开小差啦~");
            }

            @Override
            public void onSuccess(int i, Header[] headers, String s) {
                Log.i(TAG, "onSuccess:updateStatus: " + s);
                List<String> resultForStatus = myHttpHelper.resultByJson(s, new MyHttpDataListener() {
                    @Override
                    public void toLogin() {
                        MyToastDialog.show(mContext, "未登录");
                    }

                    @Override
                    public void onError(Object object) {
                        MyToastDialog.show(mContext, object.toString());
                    }
                });
                if (resultForStatus == null || resultForStatus.size() == 0) {
                    return;
                }
                if (resultForStatus.get(0).equalsIgnoreCase("OK")) {
                    if (resultForStatus.get(1).equalsIgnoreCase("true")) {
                    } else {//下线状态，重新上线
                        online = false;
                        autoOnline = true;
                        checkDriverType();
//                onLine();
                    }
                } else if (resultForStatus.get(0).equalsIgnoreCase("NOAUTH")) {
                    MyToastDialog.show(mContext, resultForStatus.get(1));
//                    startActivity(new Intent(context, LoginActivity.class));
//                    finish();
                } else {
                    MyToastDialog.show(mContext, resultForStatus.get(1));
//                    startActivity(new Intent(context, LoginActivity.class));
//                    finish();
                }
            }
        });
    }

    boolean hasChecked;//已经检查过

    /**
     * 进入界面检查是否之前有未处理结束的订单
     */
    private void checkInOrder() {
//        if (mOrderInfos == null) {
        MyAsyncHttpUtils.post(Str.URL_UNFINISHED_ORDER, new MyProgTextHttpResponseHandler(mActivity) {
            @Override
            public void onFailure(int statusCode, Header[] headers, String s, Throwable throwable) {

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String s) {
                Log.i(TAG, "onSuccess:getOrderInfosByJson: " + s);
                List<OrderInfo> orderInfoList = myHttpHelper.getOrderInfosByJson(s, DriverActivity.this);
                if (orderInfoList == null || orderInfoList.size() == 0) {
                    mOrderInfos = null;
                    inOrder = false;
                    isInOrder(inOrder);
                    return;
                }
                if (orderInfoList != null && orderInfoList.size() != 0) {//如果在处理订单状态
                    for (int i = 0; i < orderInfoList.size(); i++) {
                        OrderInfo orderInfo = orderInfoList.get(i);
                        int orderStatus = orderInfo.getStatus();
                        //订单状态 ( 1;//订单创建 2;//司机接单3;//订单开始5;//订单已支付
                        // 6;//订单取消4;//到达终点9;//订单完成 7;//司机开始等待)
                        if (orderStatus == 2 || orderStatus == 3 || orderStatus == 4 || orderStatus == 7) {
                            mOrderInfos = null;
                            if (mOrderInfos == null) {
                                mOrderInfos = new ArrayList<>();
                            }
                            mOrderInfos.add(orderInfoList.get(0));
                            speak("您有未处理完订单，请完成订单");
                            if (!mOrderInfos.get(0).getServiceType().equals("代驾")) {
                                if (orderInfoList.size() == 2) {//如果为2个乘客
                                    mOrderInfos.add(orderInfoList.get(1));
                                }
                            }
                            inOrder = true;//改变接单状态
                            isInOrder(inOrder);

                            if (!online) {//如未上线，强制上线
                                final List<OrderInfo> finalOrderInfoList = orderInfoList;
                                MyAsyncHttpUtils.get(Str.URL_MEMBER, new MyTextHttpResponseHandler() {
                                    @Override
                                    public void onFailure(int i, Header[] headers, String s, Throwable throwable) {

                                    }

                                    @Override
                                    public void onSuccess(int i, Header[] headers, String s) {
                                        driver = myHttpHelper.getDriverByJson(s, DriverActivity.this);
                                        if (driver == null) {
                                            return;
                                        }
                                        if (online) {
                                            offLineTimeMillis = System.currentTimeMillis();
                                        } else {
                                            onLineTimeMillis = System.currentTimeMillis();
                                        }
                                        RequestParams params;
                                        if (finalOrderInfoList.get(0).getServiceType().contains("代驾")) {//代驾
                                            driver.setType("2");
                                            DJOnLine(false);
//                                                result = myHttpUtils.onlineAppDJ(Str.URL_ONLINE_DJ, !online, onLineTimeMillis, offLineTimeMillis);//上线
                                        } else {
                                            driver.setType("1");
                                            otherOnLine(finalOrderInfoList.get(0).getCar_id(), false);
//                                                List<String> result   = myHttpUtils.onlineApp(Str.URL_ONLINE, finalOrderInfoList.get(0).getCar_id(), !online, onLineTimeMillis, offLineTimeMillis);//上线
                                        }

                                    }
                                });
//                                        driver = myHttpUtils.getDriverByGET(Str.URL_MEMBER);
                            }

                        }
                    }
                }
            }
        });
//        }
    }

    private void checkReservationOrder() {
        MyAsyncHttpUtils.post(Str.URL_CHECK_RESERVATION_ORDER, new MyTextHttpResponseHandler() {
            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                super.onFailure(i, headers, s, throwable);
                Log.i(TAG, "onFailure: " + s);
            }

            @Override
            public void onSuccess(int i, Header[] headers, String s) {
                super.onSuccess(i, headers, s);
                Log.i(TAG, "onSuccess:checkReservationOrder: " + s);
                List<String> resultCheckReservation = myHttpHelper.resultByJson(s, DriverActivity.this);
                if (resultCheckReservation != null && resultCheckReservation.size() > 0) {
                    if (resultCheckReservation.get(0).equalsIgnoreCase("OK")) {
                        if (resultCheckReservation.get(1).equalsIgnoreCase("true")) {
                            MyToastDialog.show(mContext, "您有预约订单，请按时处理");
                            startActivity(new Intent(DriverActivity.this, MyReservationOrderActivity.class));
                        } else {
                            MyToastDialog.show(mContext, "暂无预约订单");
                        }
                    }
                }
            }
        });
    }

    boolean autoOnline;//自动上线

    private void setOnLineView(List<String> result, boolean speak) {
        if (result == null) {
            return;
        }
        if (result.get(0).equalsIgnoreCase("OK")) {
            online = !online;//上线、下线
//            speechSynthesizer.startSpeaking("开始接单啦 ", mySynthesizerListener);
            if (online) {//上线重置时间
                if (speak) {
                    if (!autoOnline) {
                        speak("开始接单啦，快去赚钱吧");
                    }
                    autoOnline = false;
                }
                resetOnlineTime(result);
//                if (!driver.getType().equals("2")) {
//                    updateStatus = true;
//                    sendEmptyMsgToHandlerByExecutor(UPDATE_CARSTATUS);
//                }
            } else {
                setOnlineTime(20, "上线");
                updateStatus = false;
                driver.setType("");
                mDriverCar = null;
                if (handler.hasMessages(UPDATE_CARSTATUS)) {
                    handler.removeMessages(UPDATE_CARSTATUS);
                }
                if (speak) {
                    if (!autoOnline) {
                        speak("停止接单啦，享受生活吧");
                    }
                    autoOnline = false;
                }
            }
        } else if (result.get(0).equalsIgnoreCase("ERROR")) {
            driver.setType("");
            mDriverCar = null;
//            ToastUtil.show(this, result.get(1));
            MyToastDialog.show(mContext, result.get(1));
            return;
        }
    }

    Context context;
    NetReceiver mNetReceiver = new NetReceiver();
    IntentFilter mNetFilter = new IntentFilter();
    MyHttpHelper myHttpHelper;
    DriverActivity mActivity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_driver);
        setUpMap(savedInstanceState);
        setUpMap();
        mActivity = this;
        context = this;
        myHttpHelper = new MyHttpHelper(context);
        speak(" ");
        permissionLocation();
        checkOpenGps();
        initMsg();
        initView();


        registerMessageReceiver();
        mNetFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mNetReceiver, mNetFilter);//监听网络连接
//        mServiceIntents = new Intent(this, MyWebSocketService.class);
//        startService(mServiceIntents);//websocket获取推送消息

        if (JPushInterface.isPushStopped(MyApp.getInstance())) {
            JPushInterface.resumePush(MyApp.getInstance());//恢复推送
        }

        boolean b = BaseActivity.isServiceWorked(MyApp.getInstance(), "com.delelong.diandiandriver.service.CoreService");
        if (!b) {
            Intent service = new Intent(MyApp.getInstance(), CoreService.class);
            startService(service);
            Log.i(TAG, "Start CoreService");
        }

        sendEmptyMsgToHandlerByExecutor(INIT_FRAG);
        sendEmptyMsgToHandlerByExecutor(CHECK_IS_ONLINE);
        sendEmptyMsgDelayToHandlerByExecutor(CHECK_APP_UPDATE, 30000);
        sendEmptyMsgDelayToHandlerByExecutor(CHECK_ORDER, 3000);
//        sendEmptyMsgDelayToHandlerByExecutor(CHECK_JPUSH, 40000);

        Intent orderIntent = getIntent();
        getOrderIntent(orderIntent);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);//注释掉，让其不再保存Fragment的状态，达到其随着Activity一起被回收的效果！
    }

    //    public Intent mServiceIntents;
    DateTime onlineTime;
    SharedPreferences preferences;
    //    MyHttpUtils myHttpUtils;
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
//        myHttpUtils = new MyHttpUtils(this, driverActivity);
        mTipHelper = new TipHelper(this);
        sendEmptyMsgToHandlerByExecutor(INIT_DRIVER);
        sendEmptyMsgDelayToHandlerByExecutor(INIT_DRIVER_CAR_BEAN, 1000);
    }

    DriverAmount mDriverAmount;

    private void initDriver() {
        MyAsyncHttpUtils.get(Str.URL_MEMBER, new MyTextHttpResponseHandler() {
            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {

            }

            @Override
            public void onSuccess(int i, Header[] headers, String s) {
                Log.i(TAG, "onSuccess:getDriverByJson: " + s);
                if (driver == null) {
                    driver = myHttpHelper.getDriverByJson(s, DriverActivity.this);
                }
            }
        });
    }

    private void initDriverCarBean() {
        //        driverCarBean = myHttpUtils.getDriverCars(Str.URL_DRIVER_CARS);
        MyAsyncHttpUtils.post(Str.URL_DRIVER_CARS, new MyTextHttpResponseHandler() {
            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {

            }

            @Override
            public void onSuccess(int i, Header[] headers, String s) {
                if (driverCarBean == null) {
//                    Log.i(TAG, "onSuccess:DriverCarsByJson: " + s);
                    driverCarBean = myHttpHelper.DriverCarsByJson(s, DriverActivity.this);
                }
            }
        });
    }

    private void updateAdcode() {
        MyAsyncHttpUtils.get(Str.URL_MEMBER, new MyTextHttpResponseHandler() {
            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {

            }

            @Override
            public void onSuccess(int i, Header[] headers, String s) {
                if (driver == null) {
                    driver = myHttpHelper.getDriverByJson(s, DriverActivity.this);
                }
                if (driver != null) {
                    if (driver.getCompany().equalsIgnoreCase("null")) {
                        //如果adcode为1（表示未设置过）
                        updateAdCode(driver, mAMapLocation);
                    }
                }
            }
        });

    }

    private void download() {
        downloadStartAD(mAMapLocation);
//        downloadMainAD(mAMapLocation);
        showMainAD(mAMapLocation);
    }

    LayoutTransition transition;
    public DrawerLayout drawerly;
    SwipeRefreshLayout ly_refresh;
    RelativeLayout menu_left;//左右菜单布局

    LinearLayout ly_desk;//切换界面大小，显示 隐藏地图
    RelativeLayout rl_desk;
    ImageView img_menu, img_function;//actionbar
    LinearLayout ly_sum, ly_sum_yesterday, ly_sum_today;//昨日、今日收入点击事件
    TextView tv_sum_yesterday, tv_sum_today;//昨日、今日收入

    LinearLayout ly_desk02, ly_desk_detail;//收入明细（可隐藏）
    TextView tv_today_detail;//收入明细（可隐藏）
    LinearLayout ly_desk_show;//显示、隐藏收入明细
    ImageView img_desk_show;

    LinearLayout ly_traver;
    TextView tv_traver;
    Button btn_createOrder, btn_onLine, btn_backToCity;//创建订单、上线、返城按钮

    private void initView() {
        transition = new LayoutTransition();
        drawerly = (DrawerLayout) findViewById(R.id.drawerly);
        drawerly.setLayoutTransition(transition);

        ly_refresh = (SwipeRefreshLayout) findViewById(R.id.ly_refresh);
        ly_refresh.setLayoutTransition(transition);
        ly_refresh.setOnRefreshListener(this);

        menu_left = (RelativeLayout) findViewById(R.id.menu_left);

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

        ly_traver = (LinearLayout) findViewById(R.id.ly_traver);
        tv_traver = (TextView) findViewById(R.id.tv_traver);

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

//        initAmountMsg();
        sendEmptyMsgToHandlerByExecutor(INIT_AMOUNT_MSG);
        initListener();
    }

    private void initAmountMsg() {
        MyAsyncHttpUtils.post(Str.URL_DRIVER_YE_AMOUNT, new MyTextHttpResponseHandler() {
            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                ly_refresh.setRefreshing(false);//停止刷新
            }

            @Override
            public void onSuccess(int i, Header[] headers, String s) {
                Log.i(TAG, "onSuccess: getDriverYeAmountByJson:" + s);
                ly_refresh.setRefreshing(false);//停止刷新
                mDriverAmount = myHttpHelper.getDriverYeAmountByJson(s, DriverActivity.this);
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
        });
//        mDriverAmount = myHttpUtils.getDriverYeAmount(Str.URL_DRIVER_YE_AMOUNT);

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
            try {
                tv_order_time.setText(getDateToString(Longs.tryParse(orderInfo.getSetouttime())));
            } catch (Exception e) {
                e.printStackTrace();
                tv_order_time.setText(orderInfo.getSetouttime());
            }
        } else {
            tv_order_time.setText("现在");
        }
        tv_order_resAddr.setText(Html.fromHtml("从 <font color='#ffff'>" + orderInfo.getReservationAddress() + "</font>"));
        if (orderInfo.getDestination() != null) {
            tv_order_desAddr.setText(Html.fromHtml("到 <font color='#ffff'>" + orderInfo.getDestination() + "</font>"));
        }
    }


    FragmentManager fragmentManager;
    DriverMenuFrag menuFrag;

    private void initFrag() {
        menuFrag = new DriverMenuFrag();
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(R.id.menu_left, menuFrag, "menuFrag")
                .addToBackStack("null")
                .commitAllowingStateLoss();
    }

    private void initListener() {
        img_menu.setOnClickListener(this);
        img_function.setOnClickListener(this);
        ly_sum_yesterday.setOnClickListener(this);
        ly_sum_today.setOnClickListener(this);
        img_desk_show.setOnClickListener(this);
        ly_traver.setOnClickListener(this);
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
                try {
                    drawerly.openDrawer(Gravity.LEFT);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.ly_traver:
                //顺风车|专线
                Intent traverIntent = new Intent(this, TraverActivity.class);
//                Bundle traverBundle = ActivityUtils.getIntentBundle("traver", city, mAMapLocation);
//                traverIntent.putExtra("bundle", traverBundle);
                EventBus.getDefault().postSticky(mAMapLocation);
                startActivity(traverIntent);
                break;
            case R.id.img_function:
                //右侧功能菜单
                Bundle functionBundle = new Bundle();
                functionBundle.putString("adcode", mAMapLocation.getAdCode());
                functionBundle.putString("city", mAMapLocation.getCity());
                Intent functionIntent = new Intent(this, MyFunctionActivity.class);
                functionIntent.putExtra("bundle", functionBundle);
                startActivity(functionIntent);
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
//                    ToastUtil.show(this, "请先上线");
                    MyToastDialog.show(mContext, "请先上线");
                    return;
                }
                if (!driver.getType().equals("2")) {
//                    ToastUtil.show(this, "只有代驾司机可创建订单");
                    MyToastDialog.show(mContext, "只有代驾司机可创建订单");
                    return;
                }
                if (mOrderInfos != null && mOrderInfos.size() > 0) {
                    MyToastDialog.show(mContext, "请先完成已有订单！");
                    return;
                }
                Bundle bundle = new Bundle();
                bundle.putString("city", city);
                bundle.putString("adcode", mAMapLocation.getAdCode());
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
                clickableDelayed(btn_onLine);
                if (online) {
                    DJOnLine(true);//下线
                } else {
                    sendEmptyMsgToHandlerByExecutor(CHECK_TO_ONLINE);
                }
                break;
            case R.id.btn_backToCity:
                //预约订单
                if (mOrderInfos != null && mOrderInfos.size() > 0) {
                    MyToastDialog.show(mContext, "请先完成已有订单！");
                    return;
                }
                clickableDelayed(btn_backToCity);
                sendEmptyMsgToHandlerByExecutor(CHECK_RESERVATION_ORDER);
                break;
            case R.id.img_map_top02:
                //显示地图布局
                showMap = !showMap;
                if (showMap) {
                    ly_desk02.setVisibility(View.GONE);
                    ly_desk.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1.0f));
                    setRotateAnimation(img_map_top02, 0, 180);//箭头旋转
                } else {
                    ly_desk02.setVisibility(View.VISIBLE);
                    ly_desk.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 10.0f));
                    setRotateAnimation(img_map_top02, 180, 0);//箭头旋转
                }
                break;
            case R.id.loc:
                //定位
                centerToMyLocation(aMap, mLocationClient, myOrientationListener, mAMapLocation);
                break;
        }
    }

    private void checkToOnLine() {
        if (driver == null) {
            MyAsyncHttpUtils.get(Str.URL_MEMBER, new MyTextHttpResponseHandler() {
                @Override
                public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
//                            ToastUtil.show(mContext, "暂未获取到司机信息");
                    MyToastDialog.show(mContext, "暂未获取到司机信息");
                }

                @Override
                public void onSuccess(int i, Header[] headers, String s) {
                    if (driver == null) {
                        Log.i(TAG, "onSuccess:getDriverByJson: " + s);
                        driver = myHttpHelper.getDriverByJson(s, DriverActivity.this);
                        if (driver != null) {
                            checkDriverType();
                        }
                    }
                }
            });
        } else {
            checkDriverType();
        }
    }

    private void CheckIsOnline() {
        MyAsyncHttpUtils.get(Str.URL_CHECK_ONLINE, new MyTextHttpResponseHandler() {
            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
            }

            @Override
            public void onSuccess(int i, Header[] headers, String s) {
                Log.i(TAG, "onSuccess:CheckIsOnline: " + s);
                List<String> resultForStatus = myHttpHelper.resultByJson(s, null);
                if (resultForStatus != null) {
                    if (resultForStatus.size() > 0) {
                        if (resultForStatus.get(0).equalsIgnoreCase("OK")) {
                            setOnLineView(resultForStatus, false);
                        }
                    }
                }

            }
        });
    }

    boolean showMap;

    private void toCreateOrder() {

    }

    /**
     * 上线确定接单类型
     */
    private void checkDriverType() {
        Log.i(TAG, "checkDriverType: " + driver + "//" + driver.getTypes());
        if (driver.getType().equals("")) {
            if (driver.getTypes().size() == 1) {
                if (driver.getTypes().get(0).equals("2")) {
                    driver.setType("2");//代驾
                    btn_createOrder.setVisibility(View.VISIBLE);
                } else {
                    driver.setType("1");
                }
//                onLine();
                if (handler != null && handler.hasMessages(UPDATE_CARSTATUS)) {
                    handler.removeMessages(UPDATE_CARSTATUS);
//                    Log.i(TAG, "checkDriverType: ");
                }
                sendEmptyMsgToHandlerByExecutor(ONLINE);
                return;
            } else {//类型不止一个
                for (int i = 0; i < driver.getTypes().size(); i++) {
                    if (driver.getTypes().get(i).equals("2")) {
                        if (myDialog == null) {
                            myDialog = new MyDialogUtils(this);
                        }
                        myDialog.chooseOnLineType(REQEST_CHOOSE_ONLINE_TYPE, this);
                        return;
                    } else {
                        driver.setType("1");
                    }
                }
//                onLine();//不为代驾
                if (handler != null && handler.hasMessages(UPDATE_CARSTATUS)) {
                    handler.removeMessages(UPDATE_CARSTATUS);
                    Log.i(TAG, "checkDriverType: 000");
                }
                sendEmptyMsgToHandlerByExecutor(ONLINE);
                return;
            }
        } else {
//            onLine();
            if (handler != null && handler.hasMessages(UPDATE_CARSTATUS)) {
                handler.removeMessages(UPDATE_CARSTATUS);
                Log.i(TAG, "checkDriverType: 111");
            }
            sendEmptyMsgToHandlerByExecutor(ONLINE);
        }
    }

    private void setDaiJiaButtonVisibility(Driver driver) {
        if (driver.getType() != null && driver.getType().equals("2")) {
            btn_createOrder.setVisibility(View.VISIBLE);
        }
    }

    private void checkDriverCar() {
        MyAsyncHttpUtils.post(Str.URL_DRIVER_CARS, new MyTextHttpResponseHandler() {
            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {

            }

            @Override
            public void onSuccess(int i, Header[] headers, String s) {
//                Log.i(TAG, "onSuccess: DriverCarsByJson:" + s);
                if (driverCarBean == null) {
                    driverCarBean = myHttpHelper.DriverCarsByJson(s, DriverActivity.this);
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
                        myDialog.chooseDriverCars(driverCarBean, REQEST_CHOOSE_CAR, DriverActivity.this);
                        return;
                    } else {
                        mDriverCar = driverCarBean.getDriverCars().get(0);
                        sendEmptyMsgToHandlerByExecutor(ONLINE);
                    }
                }
            }
        });

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
        List<String> result;
        if (online) {
            offLineTimeMillis = System.currentTimeMillis();
        } else {
            onLineTimeMillis = System.currentTimeMillis();
        }
        RequestParams params = null;
        if (driver.getType().equals("2")) {
            DJOnLine(true);
//            result = myHttpUtils.onlineAppDJ(Str.URL_ONLINE_DJ, !online, onLineTimeMillis, offLineTimeMillis);//上线
        } else {
            if (mDriverCar == null) {
//                checkDriverCar();
                sendEmptyMsgToHandlerByExecutor(CHECK_DRIVER_CAR);
                return;
            }
            if (mDriverCar == null) {
                return;
            }
            otherOnLine(mDriverCar.getId(), true);
//            result = myHttpUtils.onlineApp(Str.URL_ONLINE, mDriverCar.getId(), !online, onLineTimeMillis, offLineTimeMillis);//上线
        }
    }

    private void DJOnLine(final boolean speak) {
        RequestParams params = myHttpHelper.getDJOnLineParams(!online, onLineTimeMillis, offLineTimeMillis);
        MyAsyncHttpUtils.post(Str.URL_ONLINE_DJ, params, new MyProgTextHttpResponseHandler(mActivity) {
            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                Log.i(TAG, "DJOnLine:onFailure: " + s);
            }

            @Override
            public void onSuccess(int i, Header[] headers, String s) {
                Log.i(TAG, "DJOnLine:onSuccess: " + s);
                List<String> result = myHttpHelper.resultByJson(s, DriverActivity.this);
                setOnLineView(result, speak);
            }
        });
    }

    private void otherOnLine(int driverCarId, final boolean speak) {
//        mDriverCar.setId(driverCarId);
        RequestParams params = myHttpHelper.getOnLineParams(driverCarId, !online, onLineTimeMillis, offLineTimeMillis);
        Log.i(TAG, "otherOnLine: onLineTimeMillis:" + online + "/" + onLineTimeMillis + "/offLineTimeMillis/" + offLineTimeMillis);
        MyAsyncHttpUtils.post(Str.URL_ONLINE, params, new MyProgTextHttpResponseHandler(mActivity) {
            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                Log.i(TAG, "onFailure:otherOnLine: " + s);
            }

            @Override
            public void onSuccess(int i, Header[] headers, String s) {
                Log.i(TAG, "onSuccess: otherOnLine" + s);
                List<String> result = myHttpHelper.resultByJson(s, DriverActivity.this);
                setOnLineView(result, speak);
            }
        });
    }

    private void resetOnlineTime(List<String> result) {
        onlineTime = new DateTime(onlineTime.getYear(), onlineTime.getMonthOfYear(),
                onlineTime.getDayOfMonth(), 0, 0, 0);
        onlineTime = onlineTime.plusMinutes(Ints.tryParse(result.get(2)));//当天累计上线时间
        setOnlineTime(12, "上线时间\n" + onlineTime.toString("HH:mm"));
        if (online) {
//            handler.sendEmptyMessageDelayed(ONLINE_TIME, 60000);
            sendEmptyMsgDelayToHandlerByExecutor(ONLINE_TIME, 60000);
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
        if (!online) {
            if (handler.hasMessages(ONLINE_TIME)) {
                handler.removeMessages(ONLINE_TIME);
            }
        }
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
        if (myOrientationListener.isStarted()) {
            myOrientationListener.stop();
        }
        myOrientationListener = null;
        if (null != mLocationClient) {
            mLocationClient.onDestroy();
        }
        deactivate();
        if (aMap != null) {
            aMap = null;
        }
        if (mTipHelper != null) {
            mTipHelper.stopSpeak();
        }
//        if (online) {
//            if (online) {
//                offLineTimeMillis = System.currentTimeMillis();
//            } else {
//                onLineTimeMillis = System.currentTimeMillis();
//            }
////            online = false;
//            if (driver.getType().equals("2")) {
//                RequestParams params = myHttpHelper.getDJOnLineParams(!online, onLineTimeMillis, offLineTimeMillis);
//                MyAsyncHttpUtils.post(Str.URL_ONLINE_DJ, params, new MyProgTextHttpResponseHandler(mActivity) {
//                    @Override
//                    public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
//
//                    }
//
//                    @Override
//                    public void onSuccess(int i, Header[] headers, String s) {
////                        Log.i(TAG, "onSuccess: resultByJson:" + s);
//                    }
//                });
////                DJOnLine(false);
////                myHttpUtils.onlineAppDJ(Str.URL_ONLINE_DJ, online, onLineTimeMillis, offLineTimeMillis);//下线
//            } else {
//                RequestParams params = myHttpHelper.getOnLineParams(mDriverCar.getId(), !online, onLineTimeMillis, offLineTimeMillis);
//                MyAsyncHttpUtils.post(Str.URL_ONLINE, params, new MyProgTextHttpResponseHandler(mActivity) {
//                    @Override
//                    public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
//
//                    }
//
//                    @Override
//                    public void onSuccess(int i, Header[] headers, String s) {
//                    }
//                });
////                otherOnLine(mDriverCar.getId(), false);
////                myHttpUtils.onlineApp(Str.URL_ONLINE, mDriverCar.getId(), online, onLineTimeMillis, offLineTimeMillis);//下线
//            }
//        }

//        if (mServiceIntents != null) {
//            stopService(mServiceIntents);
//        }
        if (mNetReceiver != null) {
            unregisterReceiver(mNetReceiver);
            mNetReceiver = null;
        }
        if (threadPool != null && !threadPool.isShutdown()) {
            threadPool.shutdownNow();
        }
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            if (handler.hasMessages(ONLINE_TIME)) {
                handler.removeMessages(ONLINE_TIME);
            }
            if (handler.hasMessages(INIT_FRAG)) {
                handler.removeMessages(INIT_FRAG);
            }
            if (handler.hasMessages(CHECK_ORDER)) {
                handler.removeMessages(CHECK_ORDER);
            }
            if (handler.hasMessages(CHECK_ADCODE)) {
                handler.removeMessages(CHECK_ADCODE);
            }
            if (handler.hasMessages(CHECK_AD_UPDATE)) {
                handler.removeMessages(CHECK_AD_UPDATE);
            }
            if (handler.hasMessages(CHECK_APP_UPDATE)) {
                handler.removeMessages(CHECK_APP_UPDATE);
            }
            if (handler.hasMessages(REFRESH_AMOUNT)) {
                handler.removeMessages(REFRESH_AMOUNT);
            }
            if (handler.hasMessages(UPDATE_CARSTATUS)) {
                handler.removeMessages(UPDATE_CARSTATUS);
                updateStatus = false;
            }
            if (handler.hasMessages(UPDATE_LOCATION)) {
                updateLoc = false;
                handler.removeMessages(UPDATE_LOCATION);
            }
            handler = null;
        }
        if (orderMessageReceiver != null) {
            unregisterReceiver(orderMessageReceiver);
            orderMessageReceiver = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        initAmountMsg();
        sendEmptyMsgToHandlerByExecutor(INIT_AMOUNT_MSG);
        mMapView.onResume();
        if (mLocationClient != null) {
            mLocationClient.startLocation();
        }
    }

    long exitTime = 0;// 退出时间

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - exitTime) > 3000) {
                Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
                return false;
            } else {
                //结束
//                finish();
                // 返回桌面操作
//                Intent home = new Intent(Intent.ACTION_MAIN);
//                home.addCategory(Intent.CATEGORY_HOME);
//                startActivity(home);
                moveTaskToBack(true);
            }
        } else if (keyCode == KeyEvent.KEYCODE_HOME) {
            moveTaskToBack(true);
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void chooseDriverCar(int requestCode, int position) {
        if (requestCode == REQEST_CHOOSE_CAR) {
            mDriverCar = driverCarBean.getDriverCars().get(position);
            sendEmptyMsgToHandlerByExecutor(ONLINE);
        }
    }

    @Override
    public void sure(int requestCode, String arg0) {
        if (requestCode == REQEST_CHOOSE_ONLINE_TYPE) {
            String onlineType = arg0;//2:代驾，1：其他
//            Log.i(TAG, "sure: " + onlineType);
            driver.setType(onlineType);
            if (onlineType.equals("2")) {
                btn_createOrder.setVisibility(View.VISIBLE);
            }
//            onLine();
            sendEmptyMsgToHandlerByExecutor(ONLINE);
        } else if (requestCode == Str.REQUEST_LOGIN) {
            if (arg0 != null) {
                String[] loginInfo = arg0.split(";");
                if (loginInfo.length == 2) {
                    String phone, pwd_edt, pwd;
                    phone = loginInfo[0];
                    pwd_edt = loginInfo[1];
                    pwd = MD5.getMD5Str(pwd_edt);
                    login(phone, pwd_edt, pwd);
                }
            }
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

    @Override
    public void toLogin() {
        if (myDialog == null) {
            myDialog = new MyDialogUtils(mContext);
        }
        myDialog.login(Str.REQUEST_LOGIN, this);
    }

    @Override
    public void onError(Object object) {

    }

    @Override
    public void onRefresh() {
//        handler.sendEmptyMessage(REFRESH_AMOUNT);
//        sendEmptyMsgToHandlerByExecutor(REFRESH_AMOUNT);
        Log.i(TAG, "onRefresh: 11111");
        sendEmptyMsgToHandlerByExecutor(INIT_AMOUNT_MSG);
    }

    public class OrderMessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
//            Log.i(TAG, "onReceive: intent.getAction()" + intent.getAction());
            getOrderIntent(intent);
        }
    }

    private void getOrderIntent(Intent intent) {
        if (Str.ORDER_MESSAGE_RECEIVED_ACTION.equals(intent.getAction())) {
            String orderTitle = intent.getStringExtra(Str.KEY_ORDER_TITLE);
            String orderMessage = intent.getStringExtra(Str.KEY_ORDER_MESSAGE);//订单消息
            String orderExtras = intent.getStringExtra(Str.KEY_ORDER_EXTRA);//可能为空
            Log.i(TAG, "DriverActivity:onReceive:orderTitle: " + orderTitle + "//" + orderMessage);
            if (orderTitle.equals("1")) {//订单消息
                if (inOrder) {
                    return;//已经接单并且有2单在处理，则不提示有新订单
                }
//                    Log.i(TAG, "onReceive: "+orderMessage);
                sendMsgToHandlerByExecutor(SHOW_ORDER, orderMessage);
            } else if (orderTitle.equals("7")) {//顺风车新订单
                if (orderMessage != null) {
                    sendMsgToHandlerByExecutor(SHOW_TRAVER_ORDER, orderMessage);
                }
            } else if (orderTitle.equals("12")) {//专线新订单
                if (orderMessage != null) {
                    sendMsgToHandlerByExecutor(SHOW_ZHUANXIAN_ORDER, orderMessage);
                }
            } else if (orderTitle.equals("4")) {//取消订单
                if (orderMessage == null) {
                    return;
                }
                OrderInfo canceledOrderInfo = getOrderInfoFromReceiver(orderMessage);
                if (isNull(canceledOrderInfo)) {
                    return;
                }
                //订单类型：1：顺风车、专线之外的订单；2：顺风车；3：专线
                if (canceledOrderInfo.getOrderType() != 1) {
                    String title = "";
                    if (canceledOrderInfo.getOrderType() == 2) {
                        title = "您的顺风车订单已取消";
                    } else if (canceledOrderInfo.getOrderType() == 3) {
                        title = "您的专线订单已取消";
                    }
                    AlertDialog.Builder cancelBuilder = new AlertDialog.Builder(mActivity)
                            .setTitle(title)
                            .setCancelable(false)//点击返回键或对话框外部时是否消失，默认为true
                            .setPositiveButton("确定", null);
                    cancelBuilder.create().show();
                    speak("订单已取消,订单号：" + canceledOrderInfo.getId());
                    return;
                }
                if (isNull(mOrderInfos)) {
                    return;
                }
                if (mOrderInfos.size() > 1) {
                    for (int i = 0; i < mOrderInfos.size(); i++) {
                        OrderInfo orderInfo = mOrderInfos.get(i);
                        if (orderInfo.getId() == canceledOrderInfo.getId()) {
                            mOrderInfos.remove(i);
                        }
                    }
                    if (notNull(mOrderInfos)) {
                        if (mOrderInfos.size() > 0) {

                        } else {
                            onOrderCanceled();
                        }
                    } else {
                        onOrderCanceled();
                    }
                    speak("订单已取消,订单号：" + canceledOrderInfo.getId());
                } else if (mOrderInfos.size() == 1) {
                    onOrderCanceled();
                    speak("订单已取消,订单号：" + canceledOrderInfo.getId());
                }
            } else if (orderTitle.equals("6")) {//推送语音
                if (orderMessage != null) {
                    speak("您有新的消息：" + orderMessage);
                }
            } else if (orderTitle.equals("11")) {//推送信息
                if (orderMessage != null) {
                    AlertDialog.Builder cancelBuilder = new AlertDialog.Builder(mActivity)
                            .setTitle("您有新的消息")
                            .setMessage(orderMessage)
                            .setCancelable(false)//点击返回键或对话框外部时是否消失，默认为true
                            .setPositiveButton("确定", null);
                    cancelBuilder.create().show();
                }
            }
        }
    }

    public void onOrderCanceled() {
        mOrderInfos = null;
        inOrder = false;
        isInOrder(inOrder);
    }

    boolean inOrder;

    private void speak(String content) {
        if (mTipHelper == null) {
            Log.i(TAG, "speak: mTipHelper == null");
            mTipHelper = new TipHelper(this);
        }
        if (mTipHelper == null) {
            Log.i(TAG, "speak: mTipHelper == null:222");
            return;
        }
        mTipHelper.Vibrate(500);//震动0.5秒
        mTipHelper.speak(content);
    }

    public void clickableDelayed(View view) {
        view.setClickable(false);
        showProgress(true);
        sendMsgDelayToHandlerByExecutor(CLICKABLE_DELAYED, view, CLICKABLE_DELAYED_TIMEMILIS);
    }

    MyProgressDialog mProgressDialog;

    public void showProgress(boolean showProg) {
        if (showProg) {
            if (mProgressDialog == null) {
                mProgressDialog = new MyProgressDialog(context);
            }
            if (!mProgressDialog.isShowing()) {
                mProgressDialog.show();
            }
        } else {
            if (mProgressDialog != null) {
                if (mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }
            }
        }
    }

    public void showOrder(String orderMessage) {
        try {
            mOrderInfo = getOrderInfoFromReceiver(orderMessage);
            Log.i(TAG, "showOrder: " + mOrderInfo);
            if (mOrderInfo == null) {
                return;
            }

            long timeOut = System.currentTimeMillis() - Longs.tryParse(mOrderInfo.getSetouttime());
//            Log.i(TAG, "showOrder:timeOut: " + timeOut);
            if (timeOut / (1000 * 60) > 5) {//超过5分钟不显示
//                Log.i(TAG, "showOrder:timeOut/(1000*60): " + timeOut / (1000 * 60));
                return;
            }

            final MyOrderDialog orderDialog = new MyOrderDialog(this, this, mAMapLocation, mOrderInfo);
            orderDialog.show(mOrderInfo.getTimeOut(), new MyOrderDialog.OrderInterface() {
                @Override
                public void take(boolean take) {
                    if (take) {
                        RequestParams params = myHttpHelper.getTakeOrderParams(mOrderInfo.getId());
                        MyAsyncHttpUtils.post(Str.URL_TAKE_ORDER, params, new MyTextHttpResponseHandler() {
                            @Override
                            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                                Log.i(TAG, "showOrder:onFailure: " + s);
                            }

                            @Override
                            public void onSuccess(int i, Header[] headers, String s) {
                                Log.i(TAG, "showOrder:onSuccess: " + s);
                                List<String> orderResult = myHttpHelper.resultByJson(s, DriverActivity.this);
                                if (orderResult == null) {
                                    return;
                                }
                                if (orderResult.get(0).equalsIgnoreCase("OK")) {
                                    if (mOrderInfo.isSet_out_flag()) {
                                        speak("接单成功,可点击预约订单详情页查看");
                                    } else {
                                        speak("接单成功 ");
                                        if (mOrderInfos == null) {//接单后才添加到接单列表
                                            mOrderInfos = new ArrayList<>();
                                            mOrderInfos.add(mOrderInfo);
                                        }
//                                mFanChengInfo = mOrderInfos.get(0);
                                        toMyOrderAcitivityForResult(mOrderInfos.get(0), null);

                                        inOrder = true;
                                        isInOrder(inOrder);
                                    }
                                } else {
                                    speak("抢单失败 ");
                                }
                            }
                        });
//                    List<String> orderResult = myHttpUtils.takeOrder(Str.URL_TAKE_ORDER, mOrderInfo.getId());
                    }
                }
            });
//            speak("来新订单了 ");
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, "showOrder: " + e);
        }
    }

    private void showTraverOrder(String traverOrderMessage) {
        final TraverOrderBean traverOrderBean = JSON.parseObject(traverOrderMessage, TraverOrderBean.class);
        if (traverOrderBean == null) {
            return;
        }
        MyTraverOrderDialog traverOrderDialog = new MyTraverOrderDialog(DriverActivity.this, traverOrderBean);
        traverOrderDialog.show(new MyTraverOrderDialog.OrderInterface() {
            @Override
            public void take(boolean take) {
                if (take) {
                    RequestParams params = myHttpHelper.getTakeTraverOrderParams(traverOrderBean.getId());
                    MyAsyncHttpUtils.post(Str.URL_TAKE_TRAVER_ORDER, params, new MyTextHttpResponseHandler() {
                        @Override
                        public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                            Log.i(TAG, "showTraverOrder:onFailure: " + s);
                        }

                        @Override
                        public void onSuccess(int i, Header[] headers, String s) {
                            Log.i(TAG, "showTraverOrder:onSuccess: " + s);
                            List<String> orderResult = myHttpHelper.resultByJson(s, DriverActivity.this);
                            if (orderResult == null) {
                                return;
                            }
                            if (orderResult.get(0).equalsIgnoreCase("OK")) {
                                AlertDialog.Builder peerBuilder = new AlertDialog.Builder(mActivity)
                                        .setTitle("接单成功")
                                        .setMessage("是否进入顺风车界面查看？")
                                        .setCancelable(false)//点击返回键或对话框外部时是否消失，默认为true
                                        .setNegativeButton("取消", null)//
                                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                startActivity(new Intent(DriverActivity.this, ExecutionTraverActivity.class));
                                            }
                                        });
                                peerBuilder.create().show();
                                speak("接单成功 ");
                            } else {
                                speak("接单失败 ");
                            }
                        }
                    });
                }
            }
        });
    }

    private void showZhuanXianOrder(String zhuanXianOrderMessage) {
        final ZhuanXianOrderBean zhuanXianOrderBean = JSON.parseObject(zhuanXianOrderMessage, ZhuanXianOrderBean.class);
        if (zhuanXianOrderBean == null) {
            return;
        }
        MyZhuanXianOrderDialog zhuanXianOrderDialog = new MyZhuanXianOrderDialog(DriverActivity.this, zhuanXianOrderBean);
        zhuanXianOrderDialog.show(new MyZhuanXianOrderDialog.OrderInterface() {
            @Override
            public void take(boolean take) {
                if (take) {
                    RequestParams params = myHttpHelper.getTakeTraverOrderParams(zhuanXianOrderBean.getId());
                    MyAsyncHttpUtils.post(Str.URL_TAKE_ZHUANXIAN_ORDER, params, new MyTextHttpResponseHandler() {
                        @Override
                        public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                            Log.i(TAG, "showTraverOrder:onFailure: " + s);
                        }

                        @Override
                        public void onSuccess(int i, Header[] headers, String s) {
                            Log.i(TAG, "showTraverOrder:onSuccess: " + s);
                            List<String> orderResult = myHttpHelper.resultByJson(s, DriverActivity.this);
                            if (orderResult == null) {
                                return;
                            }
                            if (orderResult.get(0).equalsIgnoreCase("OK")) {
                                AlertDialog.Builder peerBuilder = new AlertDialog.Builder(mActivity)
                                        .setTitle("接单成功")
                                        .setMessage("是否进入专线界面查看？")
                                        .setCancelable(false)//点击返回键或对话框外部时是否消失，默认为true
                                        .setNegativeButton("取消", null)//
                                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                startActivity(new Intent(DriverActivity.this, ExecutionTraverActivity.class));
                                            }
                                        });
                                peerBuilder.create().show();
                                speak("接单成功 ");
                            } else {
                                speak("接单失败 ");
                            }
                        }
                    });
                }
            }
        });

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
        Log.i(TAG, "toMyOrderAcitivityForResult: 111");
        startActivityForResult(intent, Str.REQUEST_ORDER_ACTIVITY);
        Log.i(TAG, "toMyOrderAcitivityForResult: 222");
    }

//    private OrderInfo mFanChengInfo;//返程信息

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        initAmountMsg();
        Log.i(TAG, "onActivityResult: 1111");
        sendEmptyMsgToHandlerByExecutor(INIT_AMOUNT_MSG);
        if (data == null) {
            return;
        }
        if (resultCode == Str.REQUEST_ORDER_ACTIVITY) {
            if (data.getStringExtra("result").equalsIgnoreCase("OK")) {//完成所有订单
                mOrderInfos = null;
                inOrder = false;
                isInOrder(inOrder);
                speak("完成服务");
            } else if (data.getStringExtra("result").equalsIgnoreCase("OK_Two")) {//
                int position = data.getIntExtra("position", 999);
                if (position < mOrderInfos.size()) {
                    mOrderInfos.remove(position);
                }
//                mOrderInterface.positionFromMain(position);//传递第几位乘客订单完成
                setLy_orderMsg(mOrderInfos.get(0));//刷新主界面订单布局数据
                speak("完成服务");
            } else if (data.getStringExtra("result").equalsIgnoreCase("NO")) {//取消结算，无操作

            }
            sendEmptyMsgToHandlerByExecutor(CHECK_ORDER);
        } else if (resultCode == Str.REQUEST_CREATE_ORDER) {//创建订单
            Bundle bundle = data.getBundleExtra("bundle");
            OrderInfo orderInfo = (OrderInfo) bundle.getSerializable("orderInfo");
//            Log.i(TAG, "driverCreateOrder: " + orderInfo);
            if (orderInfo == null) {
                return;
            }
            sendMsgToHandlerByExecutor(CREATE_ORDER_BY_DRIVER, orderInfo);
        }
    }

    private void createOrderByDriver(OrderInfo orderInfo) {
        if (mOrderInfos == null) {
            mOrderInfos = new ArrayList<>();
        }
        if (mOrderInfos.size() == 0) {
            RequestParams params = myHttpHelper.getCreateOrderParams(orderInfo);
            final OrderInfo finalOrderInfo = orderInfo;
            MyAsyncHttpUtils.post(Str.URL_DRIVER_CREATE_ORDER, params, new MyProgTextHttpResponseHandler(mActivity) {
                @Override
                public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
//                    Log.i(TAG, "onFailure: " + s);
                }

                @Override
                public void onSuccess(int i, Header[] headers, String s) {
//                    Log.i(TAG, "onSuccess:getCreatedOrderInfosByJson:// " + s);
                    OrderInfo createOrderInfo = myHttpHelper.getCreatedOrderInfosByJson(s, DriverActivity.this);
//                    Log.i(TAG, "onSuccess:getCreatedOrderInfosByJson:/// " + createOrderInfo);
                    if (createOrderInfo != null) {
                        mOrderInfos.add(createOrderInfo);
                        inOrder = true;
                        isInOrder(inOrder);
                        toMyOrderAcitivityForResult(mOrderInfos.get(0), null);
//                        mFanChengInfo = mOrderInfos.get(0);
                    } else {
                        MyToastDialog.show(mContext, "创建失败，请重试");
                    }
                }
            });
//                OrderInfo createOrderInfo = myHttpUtils.driverCreateOrder(Str.URL_DRIVER_CREATE_ORDER, orderInfo);
        } else {
            speak("有未处理完的订单，请先处理完订单！");
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
        myOrientationListener = new MyOrientationListener(MyApp.getInstance());
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
    TextView tv_map_top01, tv_map_top02;//显示当前位置地名
    ImageButton loc;

    private void initMapView() {
        rl_map = (RelativeLayout) findViewById(R.id.rl_map);
        img_map_top02 = (ImageView) findViewById(R.id.img_map_top02);
        tv_map_top01 = (TextView) findViewById(R.id.tv_map_top01);
        tv_map_top02 = (TextView) findViewById(R.id.tv_map_top02);
        loc = (ImageButton) findViewById(R.id.loc);
        img_map_top02.setOnClickListener(this);
        loc.setOnClickListener(this);
    }

    private boolean isFirstIn = true;
    private LatLng startLat, endLat;
    AMapLocation mAMapLocation;
    LatLng updateLocLatlng;
    String city;

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

                if (notEmpty(aMapLocation.getAddress(), aMapLocation.getPoiName(), aMapLocation.getAdCode())) {
                    mAMapLocation = aMapLocation;
                }
                if (notEmpty(aMapLocation.getAddress())) {
                    mAMapLocation.setAddress(aMapLocation.getAddress());
                }
                if (notEmpty(aMapLocation.getPoiName())) {
                    mAMapLocation.setPoiName(aMapLocation.getPoiName());
                }
                if (notEmpty(aMapLocation.getAdCode())) {
                    mAMapLocation.setAdCode(aMapLocation.getAdCode());
                }
                mAMapLocation.setLatitude(aMapLocation.getLatitude());
                mAMapLocation.setLongitude(aMapLocation.getLongitude());
                mAMapLocation.setLocationType(aMapLocation.getLocationType());
                mAMapLocation.setSpeed(aMapLocation.getSpeed());

//                myAMapLocation = new MyAMapLocation(aMapLocation.getCountry(), aMapLocation.getProvince(),
//                        aMapLocation.getCity(), aMapLocation.getDistrict(), aMapLocation.getAddress(), aMapLocation.getAdCode());
                if (aMapLocation.getCity() != null && !aMapLocation.getCity().equals("")) {
                    city = aMapLocation.getCity();
                }
                String addr = "";
                if (!aMapLocation.getPoiName().isEmpty()) {
                    addr = aMapLocation.getPoiName();
                } else {
                    if (!aMapLocation.getAddress().isEmpty()) {
                        addr = aMapLocation.getAddress();
                    }
                }
                if (!addr.isEmpty()) {
//                    tv_map_top01.setVisibility(View.VISIBLE);
                    tv_map_top02.setText(addr);
                } else {
//                    tv_map_top01.setVisibility(View.GONE);
                }
//                tv_map_top02.setText(!aMapLocation.getPoiName().isEmpty() ? aMapLocation.getPoiName() : !aMapLocation.getAddress().isEmpty()?aMapLocation.getAddress():"");
                //首次进入定位到我的位置
                if (isFirstIn) {
                    mAMapLocation = aMapLocation;
                    centerToMyLocation(aMap, mLocationClient, myOrientationListener, mAMapLocation);
                    aMap.moveCamera(CameraUpdateFactory.zoomTo(18.0f));

                    double latitude_0 = mAMapLocation.getLatitude();
                    double longitude_0 = mAMapLocation.getLongitude();
                    startLat = new LatLng(latitude_0, longitude_0);
//                    initDriver();
                    sendEmptyMsgToHandlerByExecutor(INIT_DRIVER);
                    sendEmptyMsgDelayToHandlerByExecutor(CHECK_ADCODE, 1000);//上传adcode
                    sendEmptyMsgDelayToHandlerByExecutor(CHECK_AD_UPDATE, 3000);//广告需要定位
                    updateLoc = true;
                    sendEmptyMsgDelayToHandlerByExecutor(UPDATE_LOCATION, 10000);
                    isFirstIn = false;
                } else {//判断是否上传位置
//                    myActivityLocInterface.getMyLocation(mAMapLocation);
                }
            } else {
//                String errText = "定位失败," + aMapLocation.getErrorCode() + ": " + aMapLocation.getErrorInfo();
//                Log.e("AmapErr", errText);
//                Toast.makeText(this, errText, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void upDateLocation(AMapLocation mAMapLocation, LatLng updateLocLatlng) {
        if (mAMapLocation == null || updateLocLatlng == null) {
            return;
        }
        ClientLocationInfo locationInfo;
//        if (inOrder){
        locationInfo = new ClientLocationInfo(updateLocLatlng.longitude + "",
                updateLocLatlng.latitude + "", mAMapLocation.getSpeed() + "", mCurrentX + "",
                mAMapLocation.getLocationType(), mAMapLocation.getAccuracy());
//        }else {
//            locationInfo = new ClientLocationInfo(117.86424637 + "",
//                    31.60880267 + "", mAMapLocation.getSpeed() + "", mCurrentX + "",
//                    mAMapLocation.getLocationType(), mAMapLocation.getAccuracy());
//        }
        RequestParams params = myHttpHelper.getupDateLocationParams(locationInfo);
        MyAsyncHttpUtils.post(Str.URL_UPDATELOCATION_DRIVER, params, new MyTextHttpResponseHandler() {
            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {

            }

            @Override
            public void onSuccess(int i, Header[] headers, String s) {
//                Log.i(TAG, "onSuccess: upDateLocation:" + s);
            }
        });
//        List<String> list = myHttpUtils.upDateLocation(Str.URL_UPDATELOCATION_DRIVER, locationInfo);
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
//    MyActivityLocationInterface myActivityLocInterface;
//
//    public void setMyActivityLocationInterface(MyActivityLocationInterface myActivityLocInterface) {
//        this.myActivityLocInterface = myActivityLocInterface;
//    }
//
//    public interface MyActivityLocationInterface {
//        void getMyLocation(AMapLocation aMapLocation);
//    }

//    MyOrderInterface mOrderInterface;
//
//    public void setMyOrderInterface(MyOrderInterface myOrderInterface) {
//        this.mOrderInterface = myOrderInterface;
//    }

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
