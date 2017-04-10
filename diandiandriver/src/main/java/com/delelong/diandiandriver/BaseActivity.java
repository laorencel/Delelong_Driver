package com.delelong.diandiandriver;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.model.LatLng;
import com.delelong.diandiandriver.alipay.util.Alipay;
import com.delelong.diandiandriver.base.bean.BaseBean;
import com.delelong.diandiandriver.bean.ADBean;
import com.delelong.diandiandriver.bean.Client;
import com.delelong.diandiandriver.bean.Driver;
import com.delelong.diandiandriver.bean.MyNotificationInfo;
import com.delelong.diandiandriver.bean.OrderInfo;
import com.delelong.diandiandriver.bean.Str;
import com.delelong.diandiandriver.dialog.MyDialogUtils;
import com.delelong.diandiandriver.dialog.MyToastDialog;
import com.delelong.diandiandriver.http.MyAsyncHttpUtils;
import com.delelong.diandiandriver.http.MyBinaryHttpResponseHandler;
import com.delelong.diandiandriver.http.MyHttpHelper;
import com.delelong.diandiandriver.http.MyTextHttpResponseHandler;
import com.delelong.diandiandriver.listener.MyHttpDataListener;
import com.delelong.diandiandriver.listener.MyOrientationListener;
import com.delelong.diandiandriver.listener.MyPayResultInterface;
import com.delelong.diandiandriver.pace.MyAMapLocation;
import com.delelong.diandiandriver.receiver.MyPushNotificationBuilder;
import com.delelong.diandiandriver.utils.AMapCityUtils;
import com.delelong.diandiandriver.utils.ExampleUtil;
import com.delelong.diandiandriver.utils.MyApp;
import com.delelong.diandiandriver.utils.SystemUtils;
import com.delelong.diandiandriver.utils.TipHelper;
import com.delelong.diandiandriver.utils.ToastUtil;
import com.delelong.diandiandriver.view.FullScreenDlgFragment;
import com.delelong.diandiandriver.wxapi.WXPay;
import com.flyco.animation.BaseAnimatorSet;
import com.flyco.animation.BounceEnter.BounceTopEnter;
import com.flyco.animation.SlideExit.SlideBottomExit;
import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.widget.NormalDialog;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.common.primitives.Doubles;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;
import com.loopj.android.http.RequestParams;

import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.jpush.android.api.JPushInterface;
import cz.msebera.android.httpclient.Header;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;


public class BaseActivity extends AppCompatActivity {
    public static final String TAG = "BAIDUMAPFORTEST";
    //    public static final String URL_LOGIN = "http://121.40.142.141:8090/Jfinal/api/login";
    private String registrationId;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    Context mContext;
    private final static float TARGET_HEAP_UTILIZATION = 0.75f;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//禁止横屏
        EventBus.getDefault().register(this);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        mContext = BaseActivity.this;
        setTargetHeapUtilization(TARGET_HEAP_UTILIZATION);
//        initJPush();
//        setWindowBar();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Subscribe
    public void onEventMainThread(BaseBean bean) {

    }

    public static boolean isActivityRunning(Context mContext) {
        ActivityManager am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(100);
        boolean isAppRunning = false;
        Log.i(TAG, "isActivityRunning: " + list.size() + "//" + mContext.getPackageName());
        String MY_PKG_NAME = "com.delelong.diandiandriver";
        for (ActivityManager.RunningTaskInfo info : list) {
            if (info.topActivity.getPackageName().equals(MY_PKG_NAME) || info.baseActivity.getPackageName().equals(MY_PKG_NAME)) {
                isAppRunning = true;
                Log.i(TAG, info.topActivity.getPackageName() + " info.baseActivity.getPackageName()=" + info.baseActivity.getPackageName());
                break;
            }
        }
        return isAppRunning;
    }

    public static boolean isServiceWorked(Context context, String serviceName) {
        ActivityManager myManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ArrayList<ActivityManager.RunningServiceInfo> runningService = (ArrayList<ActivityManager.RunningServiceInfo>) myManager.getRunningServices(Integer.MAX_VALUE);
        for (int i = 0; i < runningService.size(); i++) {
            if (runningService.get(i).service.getClassName().toString().equals(serviceName)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isApplicationBackground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(context.getPackageName())) {
                Log.i(TAG, "isApplicationBackground: " + appProcess.importance);
                if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
                        || appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE) {
                    Log.i("前台", appProcess.processName);
                    return false;
                } else {
                    Log.i("后台", appProcess.processName);
                    return true;
                }
            }
        }
        return false;
    }


    public static void setTargetHeapUtilization(float heapUtilization) {
        try {
            Class<?> cls = Class.forName("dalvik.system.VMRuntime");
            Method getRuntime = cls.getMethod("getRuntime");
            Object obj = getRuntime.invoke(null);// obj就是Runtime
            if (obj == null) {
                Log.i(TAG, "setMinHeapSize: obj is null");
            } else {
                System.out.println(obj.getClass().getName());
                Class<?> runtimeClass = obj.getClass();
                Method setTargetHeapUtilization = runtimeClass.getMethod(
                        "setTargetHeapUtilization", float.class);
                setTargetHeapUtilization.invoke(obj, heapUtilization);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            Log.i(TAG, "setMinHeapSize: ClassNotFoundException" + e);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            Log.i(TAG, "setMinHeapSize: NoSuchMethodException" + e);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            Log.i(TAG, "setMinHeapSize: IllegalArgumentException" + e);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            Log.i(TAG, "setMinHeapSize: IllegalAccessException" + e);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            Log.i(TAG, "setMinHeapSize: InvocationTargetException" + e);
        }
    }

    /**
     * 设置通知栏全屏模式
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setSysBar() {
        String version = SystemUtils.getSystemVersion();
        double num = Doubles.tryParse(version.substring(0, 3));
        if (num >= 5) {//android 5.0及以上系统
            Window window = getWindow();
            //设置透明状态栏,这样才能让 ContentView 向上
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            //需要设置这个 flag 才能调用 setStatusBarColor 来设置状态栏颜色
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            //设置状态栏颜色
            window.setStatusBarColor(getResources().getColor(R.color.colorPinChe));

            ViewGroup mContentView = (ViewGroup) findViewById(Window.ID_ANDROID_CONTENT);
            View mChildView = mContentView.getChildAt(0);
            if (mChildView != null) {
                //注意不是设置 ContentView 的 FitsSystemWindows, 而是设置 ContentView 的第一个子 View . 使其不为系统 View 预留空间.
                ViewCompat.setFitsSystemWindows(mChildView, false);
            }
        } else if (num >= 4.5 && num < 5.0) {//android 4.0-5.0系统

            Window window = getWindow();
            ViewGroup mContentView = (ViewGroup) findViewById(Window.ID_ANDROID_CONTENT);

            //首先使 ChildView 不预留空间
            View mChildView = mContentView.getChildAt(0);
            if (mChildView != null) {
                ViewCompat.setFitsSystemWindows(mChildView, false);
            }

            int statusBarHeight = getStatusBarHeight();
            //需要设置这个 flag 才能设置状态栏
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //避免多次调用该方法时,多次移除了 View
            if (mChildView != null && mChildView.getLayoutParams() != null && mChildView.getLayoutParams().height == statusBarHeight) {
                //移除假的 View.
                mContentView.removeView(mChildView);
                mChildView = mContentView.getChildAt(0);
            }
            if (mChildView != null) {
                FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mChildView.getLayoutParams();
                //清除 ChildView 的 marginTop 属性
                if (lp != null && lp.topMargin >= statusBarHeight) {
                    lp.topMargin -= statusBarHeight;
                    mChildView.setLayoutParams(lp);
                }
            }
        }
    }

    /**
     * 设置通知栏着色模式
     */
    public void setWindowBar() {
        String version = SystemUtils.getSystemVersion();
        double num = Doubles.tryParse(version.substring(0, 3));
        //android 5.0及以上系统(如5.1)
        if (num >= 5) {
            setWindowBarAboveFive();
        } else if (num > 4.5 && num < 5.0) {
            setWindowBarBelowFive();
        }
    }

    /**
     * 5.0系统以上 设置状态栏颜色（着色模式）
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void setWindowBarAboveFive() {
        Window window = getWindow();
        //取消设置透明状态栏,使 ContentView 内容不再覆盖状态栏
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        //需要设置这个 flag 才能调用 setStatusBarColor 来设置状态栏颜色
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        //设置状态栏颜色
        window.setStatusBarColor(getResources().getColor(R.color.blankColor));

        ViewGroup mContentView = (ViewGroup) findViewById(Window.ID_ANDROID_CONTENT);
        View mChildView = mContentView.getChildAt(0);
        if (mChildView != null) {
            //注意不是设置 ContentView 的 FitsSystemWindows, 而是设置 ContentView 的第一个子 View . 预留出系统 View 的空间.
            ViewCompat.setFitsSystemWindows(mChildView, true);
        }
        Log.i(TAG, "setWindowBarAboveFive: ");
    }

    /**
     * 5.0系统以下
     */
    public void setWindowBarBelowFive() {
//        Window window = getWindow();
//        ViewGroup mContentView = (ViewGroup) findViewById(Window.ID_ANDROID_CONTENT);
//
//        //First translucent status bar.
//        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        int statusBarHeight = getStatusBarHeight();
//
//        View mChildView = mContentView.getChildAt(0);
//        if (mChildView != null) {
//            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mChildView.getLayoutParams();
//            //如果已经为 ChildView 设置过了 marginTop, 再次调用时直接跳过
//            if (lp != null && lp.topMargin < statusBarHeight && lp.height != statusBarHeight) {
//                //不预留系统空间
//                ViewCompat.setFitsSystemWindows(mChildView, false);
//                lp.topMargin += statusBarHeight;
//                mChildView.setLayoutParams(lp);
//            }
//        }
//
//        View statusBarView = mContentView.getChildAt(0);
//        if (statusBarView != null && statusBarView.getLayoutParams() != null && statusBarView.getLayoutParams().height == statusBarHeight) {
//            //避免重复调用时多次添加 View
//            statusBarView.setBackgroundColor(getResources().getColor(R.color.mainColor));
//            return;
//        }
//        statusBarView = new View(this);
//        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, statusBarHeight);
//        statusBarView.setBackgroundColor(getResources().getColor(R.color.mainColor));
//        //向 ContentView 中添加假 View
//        mContentView.addView(statusBarView, 0, lp);
        Window window = getWindow();
        ViewGroup mContentView = (ViewGroup) findViewById(Window.ID_ANDROID_CONTENT);

//First translucent status bar.
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        int statusBarHeight = getStatusBarHeight();

        View mChildView = mContentView.getChildAt(0);
        if (mChildView != null) {
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mChildView.getLayoutParams();
            //如果已经为 ChildView 设置过了 marginTop, 再次调用时直接跳过
            if (lp != null && lp.topMargin < statusBarHeight && lp.height != statusBarHeight) {
                //不预留系统空间
                ViewCompat.setFitsSystemWindows(mChildView, false);
                lp.topMargin += statusBarHeight;
                mChildView.setLayoutParams(lp);
            }
        }

        View statusBarView = mContentView.getChildAt(0);
        if (statusBarView != null && statusBarView.getLayoutParams() != null && statusBarView.getLayoutParams().height == statusBarHeight) {
            //避免重复调用时多次添加 View
            statusBarView.setBackgroundColor(getResources().getColor(R.color.blankColor));
            return;
        }
        statusBarView = new View(this);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, statusBarHeight);
        statusBarView.setBackgroundColor(getResources().getColor(R.color.blankColor));
//向 ContentView 中添加假 View
        mContentView.addView(statusBarView, 0, lp);
        Log.i(TAG, "setWindowBarBelowFive: ");
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public Bitmap getBitMapFormRes(Context context, int resId) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        opt.inPurgeable = true;//bitmap可回收
        opt.inInputShareable = true;//
        //获取资源图片
        InputStream is = context.getResources().openRawResource(resId);
        return BitmapFactory.decodeStream(is, null, opt);
    }

    public Bitmap getBitMapFormInputStream(Context context, InputStream is) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        opt.inPurgeable = true;//bitmap可回收
        opt.inInputShareable = true;//
        //获取资源图片
        return BitmapFactory.decodeStream(is, null, opt);
    }

    ////////////////////////////////////////////////////////////
    public void PayByAli(String orderInfo, MyPayResultInterface payResultInterface) {
        //测试
        Alipay alipay = new Alipay(BaseActivity.this);
        alipay.payV2(orderInfo, payResultInterface);
    }

    public void PayByWX(String orderInfo) {
        //测试
        WXPay wxPay = new WXPay(BaseActivity.this);
        wxPay.pay(orderInfo);
    }
    //////////////////////////////////////////

    /**
     * 判断网络是否连接
     *
     * @param context
     * @return
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
        } else {
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            if (networkInfo == null) {
            } else {
                return networkInfo.isAvailable();
            }
            //如果仅仅是用来判断网络连接
            //则可以使用 cm.getActiveNetworkInfo().isAvailable();
//            NetworkInfo[] info = cm.getAllNetworkInfo();
//            if (info != null) {
//                for (int i = 0; i < info.length; i++) {
//                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
//                        return true;
//                    }
//                }
//            }
        }
        return false;
    }

    /**
     * 判断网络是否可用，并返回true or false
     * 不可用弹出对话框
     *
     * @param context
     */
    public boolean setNetworkDialog(Context context) {
        boolean isNetworkAvailable = isNetworkAvailable(context);
        if (!isNetworkAvailable) {
            MyDialogUtils dialog = new MyDialogUtils(this);
            dialog.showNetWork();
        }
        return isNetworkAvailable;
    }

    //////////////////////////////////////////////////////////////////////////定位
    /**
     * 定位到我的位置
     */
    /**
     * 定位到我的位置
     */
    public void centerToMyLocation(AMap aMap, AMapLocationClient mLocationClient, MyOrientationListener myOrientationListener, AMapLocation aMapLocation) {
        if (myOrientationListener != null) {
            if (!myOrientationListener.isStarted()) {
                myOrientationListener.start();
            }
        }
        if (mLocationClient != null) {
            if (!mLocationClient.isStarted()) {
                mLocationClient.startLocation();
            }
        }
        if (aMap == null || mLocationClient == null || aMapLocation == null) {
            return;
        }
//        LatLng latLng = new LatLng(myLatitude, myLongitude);
        LatLng latLng = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
//        CameraUpdate update = CameraUpdateFactory.zoomTo(18);
//        aMap.animateCamera(update);
        aMap.animateCamera(CameraUpdateFactory.changeLatLng(latLng));
        mLocationClient.startLocation();
    }

    public <T> void intentActivityForResult(Context context, Class<T> tClass, String key, String value, String city, int requestCode) {
        Intent intent = new Intent(context, tClass);
        intent.putExtra(key, value);
        intent.putExtra("city", city);
        startActivityForResult(intent, requestCode);
    }

    public <T> void intentActivityWithBundleForResult(Context context, Class<T> tClass, int requestCode, Bundle bundle) {
        Intent intent = new Intent(context, tClass);
        intent.putExtra("bundle", bundle);
        startActivityForResult(intent, requestCode);
    }

    /**
     * 带bundle的界面跳转
     *
     * @param context
     * @param tClass
     * @param bundle
     * @param <T>
     */
    public <T> void intentActivityWithBundle(Context context, Class<T> tClass, Bundle bundle) {
        Intent intent = new Intent(context, tClass);
        intent.putExtra("bundle", bundle);
        startActivity(intent);
    }

    /**
     * 将城市json数据存储进数据库（只会存一次）
     */
    public void insert2DB() {
        SharedPreferences preferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        if (preferences.getBoolean("initDB", true)) {
            AMapCityUtils cityUtils = new AMapCityUtils(BaseActivity.this);
            cityUtils.insert2DB();//加载city到数据库
        }
    }

    public boolean notNull(Object... objects) {
        for (Object obj : objects) {
            if (obj == null) {
                return false;
            }
        }
        return true;
    }

    public boolean isNull(Object... objects) {
        for (Object obj : objects) {
            if (obj == null) {
                return true;
            }
        }
        return false;
    }

    public boolean notEmpty(String... objects) {
        for (String obj : objects) {
            if (obj == null || obj.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public boolean isEmpty(String... objects) {
        for (String obj : objects) {
            if (obj == null || obj.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 拨打电话
     *
     * @param callPhone
     */
    public void callPhone(String callPhone) {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + callPhone));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        startActivity(callIntent);
    }
    ///////////////////////////////////////////图片获取与存储

    /**
     * 设置启动页广告图片
     *
     * @param aMapLocation
     */
    public void downloadStartAD(AMapLocation aMapLocation) {
        String adcode;
        if (isNull(aMapLocation)) {
            adcode = "340100";
        } else {
            adcode = !aMapLocation.getAdCode().isEmpty() ? aMapLocation.getAdCode() : "340100";
        }
        MyHttpHelper myHttpHelper = new MyHttpHelper(this);
        RequestParams params = myHttpHelper.getADBeanParams(adcode, 1);
        final MyHttpHelper myFinalHttpHelper = myHttpHelper;
        MyAsyncHttpUtils.get(Str.URL_AD, params, new MyTextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String s, Throwable throwable) {

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String s) {
                ADBean adBean_start = myFinalHttpHelper.getADInfoByJson(s, null);
                if (isNull(adBean_start, adBean_start.getAdInfos())) {
                    return;
                }
                if (adBean_start.getAdInfos().size() < 1) {
                    return;
                }
                SharedPreferences preferences = getSharedPreferences("user", Context.MODE_PRIVATE);
                String last_update_start = preferences.getString("last_update_start", "null");
                ADBean.ADInfo adInfo = adBean_start.getAdInfos().get(0);

                if (adInfo.getLast_update().equals(last_update_start)) {
                    Log.i(TAG, "downloadStartAD: equals");
                    return;
                }
                preferences.edit()
                        .putString("last_update_start", adInfo.getLast_update())
                        .putString("url_start", adInfo.getUrl()).commit();
                File file = new File(Str.ADIMAGEPATH_START);
                if (!file.exists()) {
                    file.mkdirs();
                } else {
                    try {
                        Log.i(TAG, "onSuccess: delete");
                        File[] files = file.listFiles();
                        if (files != null && files.length > 0) {
                            for (File file1 : files) {
                                if (file1 != null) {
                                    file1.delete();
                                    Log.i(TAG, "onSuccess: delete111");
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (adInfo.getPics() == null || adInfo.getPics().size() == 0) {
                    return;
                }
                for (int i = 0; i < adInfo.getPics().size(); i++) {
                    String pic = adInfo.getPics().get(i);
                    file = new File(Str.ADIMAGEPATH_START + File.separator + i + ".JPEG");
                    if (!file.exists()) {
                        try {
                            file.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    final int finalI = i;
                    MyAsyncHttpUtils.get(Str.URL_SERVICE_IMAGEPATH + pic, new MyBinaryHttpResponseHandler() {
                        @Override
                        public void onSuccess(int status, Header[] headers, byte[] bytes) {
                            InputStream inputStream = new ByteArrayInputStream(bytes);
                            if (inputStream == null) {
                                return;
                            }
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            if (inputStream != null) {
                                myFinalHttpHelper.createImage(Str.ADIMAGEPATH_START + File.separator + finalI + ".JPEG", bitmap);
                            }
                        }

                        @Override
                        public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

                        }
                    });
                }
            }
        });

//        SharedPreferences preferences = getSharedPreferences("user", Context.MODE_PRIVATE);
//        String last_update_start = preferences.getString("last_update_start", "null");
//        ADBean adBean_start = null;
//        MyHttpUtils myHttpUtils = new MyHttpUtils(this);
//        if (aMapLocation.getAdCode() != null) {
//            adBean_start = myHttpUtils.getADBeanByGET(Str.URL_AD, aMapLocation.getAdCode(), 1);
//            if (adBean_start == null) {
//                return;
//            }
//        } else {
//            return;
//        }
//
//        ADBean.ADInfo adInfo = adBean_start.getAdInfos().get(0);
//
//        if (adInfo.getLast_update().equals(last_update_start)) {
//            Log.i(TAG, "downloadStartAD: equals");
//            return;
//        }
//        preferences.edit()
//                .putString("last_update_start", adInfo.getLast_update())
//                .putString("url_start", adInfo.getUrl()).commit();
//        File file = new File(Str.ADIMAGEPATH_START);
//        if (!file.exists()) {
//            file.mkdirs();
//        }
//        for (int i = 0; i < adInfo.getPics().size(); i++) {
//            String pic = adInfo.getPics().get(i);
//            file = new File(Str.ADIMAGEPATH_START + i + ".JPEG");
//            if (!file.exists()) {
//                try {
//                    file.createNewFile();
//                    myHttpUtils.createImage(Str.ADIMAGEPATH_START + i + ".JPEG",
//                            myHttpUtils.downloadImage(Str.URL_SERVICE_IMAGEPATH + pic));
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
    }

    public void downloadMainAD(AMapLocation aMapLocation) {
        String adcode;
        if (isNull(aMapLocation)) {
            adcode = "340100";
        } else {
            adcode = !aMapLocation.getAdCode().isEmpty() ? aMapLocation.getAdCode() : "340100";
        }
        MyHttpHelper myHttpHelper = new MyHttpHelper(this);
        RequestParams params = myHttpHelper.getADBeanParams(adcode, 2);
        final MyHttpHelper myFinalHttpHelper = myHttpHelper;
        MyAsyncHttpUtils.get(Str.URL_AD, params, new MyTextHttpResponseHandler() {

            @Override
            public void onFailure(int statusCode, Header[] headers, String s, Throwable throwable) {

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String s) {
                ADBean adBean_main = myFinalHttpHelper.getADInfoByJson(s, null);
                SharedPreferences preferences = getSharedPreferences("user", Context.MODE_PRIVATE);
                String last_update_main = preferences.getString("last_update_main", "null");
                if (isNull(adBean_main) || isNull(adBean_main.getAdInfos())) {
                    return;
                }
                if (adBean_main.getAdInfos().size() < 1) {
                    return;
                }
                ADBean.ADInfo adInfo = adBean_main.getAdInfos().get(0);

                if (adInfo.getLast_update().equals(last_update_main)) {
                    Log.i(TAG, "downloadMainAD: equals");
                    return;
                }
                preferences.edit()
                        .putString("last_update_main", adInfo.getLast_update())
                        .putString("url_main", adInfo.getUrl()).commit();

                File file = new File(Str.ADIMAGEPATH_MAIN);
                if (!file.exists()) {
                    file.mkdirs();
                }
                if (adInfo.getPics() == null || adInfo.getPics().size() == 0) {
                    return;
                }
                for (int i = 0; i < adInfo.getPics().size(); i++) {
                    file = new File(Str.ADIMAGEPATH_MAIN + i + ".JPEG");
                    if (!file.exists()) {
                        try {
                            file.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    String pic = adInfo.getPics().get(i);

                    final int finalI = i;
                    MyAsyncHttpUtils.get(Str.URL_SERVICE_IMAGEPATH + pic, new MyBinaryHttpResponseHandler() {
                        @Override
                        public void onSuccess(int status, Header[] headers, byte[] bytes) {
                            InputStream inputStream = new ByteArrayInputStream(bytes);
                            if (inputStream == null) {
                                return;
                            }
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            if (inputStream != null) {
                                myFinalHttpHelper.createImage(Str.ADIMAGEPATH_MAIN + finalI + ".JPEG", bitmap);
                            }
                        }

                        @Override
                        public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

                        }
                    });
                }
            }
        });

    }

    public void showMainAD(AMapLocation aMapLocation) {
        String adcode;
        if (isNull(aMapLocation)) {
            adcode = "340100";
        } else {
            adcode = !aMapLocation.getAdCode().isEmpty() ? aMapLocation.getAdCode() : "340100";
        }
        MyHttpHelper myHttpHelper = new MyHttpHelper(this);
        RequestParams params = myHttpHelper.getADBeanParams(adcode, 2);
        final MyHttpHelper myFinalHttpHelper = myHttpHelper;
        MyAsyncHttpUtils.get(Str.URL_AD, params, new MyTextHttpResponseHandler() {

            @Override
            public void onFailure(int statusCode, Header[] headers, String s, Throwable throwable) {

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String s) {
                Log.i(TAG, "onSuccess: getADInfoByJson:showMainAD:" + s);
                ADBean adBean_main = myFinalHttpHelper.getADInfoByJson(s, new MyHttpDataListener() {
                    @Override
                    public void toLogin() {

                    }

                    @Override
                    public void onError(Object object) {
                        MyToastDialog.show(mContext, object.toString());
                    }
                });
                SharedPreferences preferences = getSharedPreferences("user", Context.MODE_PRIVATE);
                String last_update_main = preferences.getString("last_update_main", "null");
                if (isNull(adBean_main) || isNull(adBean_main.getAdInfos())) {
                    return;
                }
                if (adBean_main.getAdInfos().size() == 0) {
                    return;
                }
                final ADBean.ADInfo adInfo = adBean_main.getAdInfos().get(0);

                if (adInfo.getLast_update().equals(last_update_main)) {
                    Log.i(TAG, "showMainAD: equals");
                    return;
                }
                preferences.edit()
                        .putString("last_update_main", adInfo.getLast_update())
                        .putString("url_main", adInfo.getUrl()).commit();
                if (adInfo.getPics() == null || adInfo.getPics().size() == 0) {
                    return;
                }
                final List<Bitmap> bitmaps = new ArrayList<Bitmap>();
                for (int i = 0; i < adInfo.getPics().size(); i++) {
                    String pic = adInfo.getPics().get(i);
                    final int finalI = i;
                    MyAsyncHttpUtils.get(Str.URL_SERVICE_IMAGEPATH + pic, new MyBinaryHttpResponseHandler() {
                        @Override
                        public void onSuccess(int status, Header[] headers, byte[] bytes) {
                            InputStream inputStream = null;
                            Bitmap bitmap = null;
                            try {
                                inputStream = new ByteArrayInputStream(bytes);
                                bitmap = BitmapFactory.decodeStream(inputStream);
                                if (bitmap != null) {
                                    bitmaps.add(bitmap);
                                }
                                if (finalI == adInfo.getPics().size() - 1) {
                                    if (bitmaps != null && bitmaps.size() > 0) {
                                        FullScreenDlgFragment.newInstance(BaseActivity.this, bitmaps, 0)
                                                .show(getSupportFragmentManager(), "dialog");
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                try {
                                    if (inputStream != null) {
                                        inputStream.close();
                                        inputStream = null;
                                    }
                                    if (bitmap != null && bitmap.isRecycled()) {
                                        bitmap.recycle();
                                        bitmap = null;
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        @Override
                        public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

                        }
                    });
                }

            }
        });
    }

    public void login(final String phone, final String pwd_edt, final String pwd) {
        if (phone.isEmpty() || pwd.isEmpty()) {
            Toast.makeText(this, "账号或密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        } else {
            if (phone.length() != 11 || pwd_edt.length() < 6) {
                Toast.makeText(this, "号码或密码长度不够", Toast.LENGTH_SHORT).show();
                return;
            } else {
                final ProgressDialog progressDialog = ProgressDialog.show(BaseActivity.this, null, "登录中，请稍候...");
                MyHttpHelper myHttpHelper = new MyHttpHelper(this);
                RequestParams params = myHttpHelper.geLoginParams(phone, pwd);
                final MyHttpHelper myFinalHttpHelper = myHttpHelper;
                MyAsyncHttpUtils.post(Str.URL_LOGIN, params, new MyTextHttpResponseHandler() {
                    @Override
                    public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                        Log.i(TAG, "onFailure: login" + s);
                        if (progressDialog != null && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                        ToastUtil.show(BaseActivity.this, "登录失败！");
                    }

                    @Override
                    public void onSuccess(int i, Header[] headers, String s) {
                        Log.i(TAG, "onSuccess: " + s);
                        if (progressDialog != null && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                        List<String> result = myFinalHttpHelper.resultByJson(s, null);
                        if (result == null) {
                            ToastUtil.show(BaseActivity.this, "点击失败，请重试");
                            return;
                        }
                        if (result.get(0).equals("OK")) {
                            startActivity(new Intent(mContext, DriverActivity.class));
                            overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                            //存储用户(token)、密码(sercet)
                            setPreferences(result, phone, pwd, pwd_edt);
                            //登录后重置请求头
                            MyAsyncHttpUtils.setHeader(MyAsyncHttpUtils.getAsyncHttpHeader());
                            finish();
                        } else if (result.get(0).equals("ERROR")) {
                            if (result.get(1).equalsIgnoreCase("极光推送参数不能为空！")) {
                                try {
                                    Thread.sleep(800);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                login(phone, pwd_edt, pwd);
                            } else {
                                Toast.makeText(mContext, "登陆出错," + result.get(1), Toast.LENGTH_SHORT).show();
                            }
                            return;
                        } else if (result.get(0).equals("FAILURE")) {
                            Toast.makeText(mContext, "登陆失败," + result.get(1), Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                });

//                MyHttpUtils myHttpUtils = new MyHttpUtils(this);
//                List<String> result = myHttpUtils.login(Str.URL_LOGIN, phone, pwd);
            }
        }
    }

    public void setPreferences(List<String> result, String phone, String pwd, String pwd_edt) {
        SharedPreferences preferences = getSharedPreferences("user", Context.MODE_MULTI_PROCESS | Context.MODE_PRIVATE);
        boolean firstLogin = false;
        if (result.size() < 4) {
            return;
        }
        int loginTimes = preferences.getInt("loginTimes", 0);
        preferences.edit()
                .putString("token", result.get(2))
                .putString("sercet", result.get(3))
                .putString("phone", phone)
                .putString("pwd", pwd)
                .putString("pwd_edt", pwd_edt)
                .putInt("loginTimes", ++loginTimes)
                .putBoolean("firstLogin", firstLogin)
                .commit();
    }

    /**
     * 把数据写入文件
     *
     * @param filePath
     * @param fileName
     * @param bitmap
     * @return 文件路径
     */
    public String createImage(String filePath, String fileName, Bitmap bitmap) {
        String path = filePath + fileName;
        FileOutputStream fileOutputStream = null;
        try {
            File fileDir = new File(filePath);
            if (!fileDir.exists()) {
                fileDir.mkdirs();
            }
            File file = new File(path);
            if (!file.exists()) {
                file.createNewFile();
            }
            fileOutputStream = new FileOutputStream(path);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);// 把数据写入文件
            Log.i(TAG, "createImage: " + "保存图片" + fileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (fileOutputStream != null) {
                    fileOutputStream.flush();
                    fileOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        return path;
    }

    /**
     * 获取相机返回的图片
     *
     * @param data
     * @param bitmap
     * @return Bitmap
     */
    public Bitmap getCamera(Intent data, Bitmap bitmap) {
        String sdStatus = Environment.getExternalStorageState();
        if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
            Log.i(TAG, "SD card is not avaiable/writeable right now.");
            return null;
        }
        if (isNull(data, data.getExtras())) {
            return null;
        }
        Bundle bundle = data.getExtras();
        bitmap = (Bitmap) bundle.get("data");// 获取相机返回的数据，并转换为Bitmap图片格式
        return bitmap;
    }

    /**
     * 获取相册返回的图片
     *
     * @param data   Intent
     * @param bitmap
     * @return Bitmap
     */
    public Bitmap getAlbum(Intent data, Bitmap bitmap) {
        if (isNull(data, data.getData())) {
            return null;
        }
        Uri selectedImage = data.getData();
        String[] filePathColumns = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(selectedImage, filePathColumns, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumns[0]);
        String imagePath = cursor.getString(columnIndex);
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        opt.inPurgeable = true;//bitmap可回收
        opt.inInputShareable = true;//
        opt.inSampleSize = computeSampleSize(opt, -1, 128 * 128);
        BitmapFactory.decodeFile(imagePath, opt);
        bitmap = BitmapFactory.decodeFile(imagePath);
        cursor.close();
        return bitmap;
    }

    public static int computeSampleSize(BitmapFactory.Options options,
                                        int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength,
                maxNumOfPixels);
        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }
        return roundedSize;
    }

    private static int computeInitialSampleSize(BitmapFactory.Options options,
                                                int minSideLength, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;
        int lowerBound = (maxNumOfPixels == -1) ? 1 :
                (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == -1) ? 128 :
                (int) Math.min(Math.floor(w / minSideLength),
                        Math.floor(h / minSideLength));
        if (upperBound < lowerBound) {
            // return the larger one when there is no overlapping zone.
            return lowerBound;
        }
        if ((maxNumOfPixels == -1) &&
                (minSideLength == -1)) {
            return 1;
        } else if (minSideLength == -1) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Base Page") // TODO: Define a title for the content shown.
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

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    public void getSystemTime() {
        try {
            DateTime dateTime = new DateTime(Str.TIME_Y, Str.TIME_M, Str.TIME_M, Str.TIME_M, Str.TIME_M, Str.TIME_M, Str.TIME_M);
            if (dateTime != null && dateTime.isBeforeNow()) {
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class MyHeadTask extends AsyncTask<String, Void, Bitmap> {
        ImageView img_head;

        public MyHeadTask(ImageView img_head) {
            this.img_head = img_head;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            URL url_head_portrait = null;
            Bitmap head = null;
            try {
                url_head_portrait = new URL(params[0] + params[1]);
                HttpURLConnection conn = (HttpURLConnection) url_head_portrait.openConnection();
                conn.setConnectTimeout(8000);
                conn.setDoInput(true);
                conn.connect();
                InputStream is = conn.getInputStream();
                head = BitmapFactory.decodeStream(is);
                is.close();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return head;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null) {
                img_head.setImageBitmap(bitmap);
            }
        }
    }

    /////////////////////////////////////////////////////////////////极光推送
    @Override
    protected void onPause() {
        super.onPause();
//        JPushInterface.onPause(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (JPushInterface.isPushStopped(MyApp.getInstance())) {
            Log.i(TAG, "onResume: JPushInterface.isPushStopped(this)");
            JPushInterface.resumePush(MyApp.getInstance());//恢复推送
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        if (mTipHelper != null) {
//            mTipHelper.stopSpeak();
//        }
        if (mMessageReceiver != null) {
            unregisterReceiver(mMessageReceiver);
        }
        EventBus.getDefault().unregister(this);
    }

    /**
     * 收起虚拟键盘
     */
    public void hideSoftInputFromWindow() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
        }
    }

    private void initJPush() {
        JPushInterface.init(this);
        MyPushNotificationBuilder builder = new MyPushNotificationBuilder(this);
        builder.setOrderNotificationBuilder();
//        registerMessageReceiver();
        Log.i(TAG, "initJPush: " + JPushInterface.getRegistrationID(this));
    }

    //for receive customer msg from jpush server
    private MessageReceiver mMessageReceiver;
    public static final String MESSAGE_RECEIVED_ACTION = "com.example.jpushdemo.MESSAGE_RECEIVED_ACTION";
    public static final String KEY_TITLE = "title";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_EXTRAS = "extras";

//    public void registerMessageReceiver() {
//        mMessageReceiver = new MessageReceiver();
//        IntentFilter filter = new IntentFilter();
//        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
//        filter.addAction(MESSAGE_RECEIVED_ACTION);
//        registerReceiver(mMessageReceiver, filter);
//    }

    public class MessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (MESSAGE_RECEIVED_ACTION.equals(intent.getAction())) {
                String messge = intent.getStringExtra(KEY_MESSAGE);
                String extras = intent.getStringExtra(KEY_EXTRAS);
                StringBuilder showMsg = new StringBuilder();
                showMsg.append(KEY_MESSAGE + " : " + messge + "\n");
                if (!ExampleUtil.isEmpty(extras)) {
                    showMsg.append(KEY_EXTRAS + " : " + extras + "\n");
                }
                Log.i(TAG, "onReceive: showMsg:" + showMsg.toString());
            }
        }
    }

    /**
     * @param tClass 目标Class
     * @param <T>
     */
    public <T> void startActivityWithBundle(Class<T> tClass, MyAMapLocation myAMapLocation, Client client) {
        Bundle bundle = new Bundle();
        if (myAMapLocation != null) {
            bundle.putSerializable("myAMapLocation", myAMapLocation);//传递我的位置
        }
        if (client != null) {
            bundle.putSerializable("client", client);
        }
        intentActivityWithBundle(getApplicationContext(), tClass, bundle);
    }
//////////////////////////////////////////////////////////////////////////////////

    /**
     * 计算两个view之间在屏幕的距离
     *
     * @param from
     * @param to
     * @return
     */
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

    /**
     * 为view设置移动动画
     *
     * @param view
     * @param fromXDelta
     * @param toXDelta
     * @param fromYDelta
     * @param toYDelta
     */
    public void setTranslateAnimation(View view, float fromXDelta, float toXDelta, float fromYDelta, float toYDelta) {
        TranslateAnimation animation = new TranslateAnimation(fromXDelta, toXDelta, fromYDelta, toYDelta);
        //设置动画持续时间
        animation.setDuration(600);
        //设置动画结束后效果保留
        animation.setFillAfter(true);
        //控制动画先慢后快
        //LinearInterpolator(匀速）
        //AccelerateInterpolator（先慢后快）
        //AccelerateDecelerateInterpolator（先慢中快后慢）
        //DecelerateInterpolator（先快后慢）
        //CycleInterpolator（循环播放，速度为正弦曲线）
        //AnticipateInterpolator（先回撤，再匀速向前）
        //OvershootInterpolator（超过，拉回）
        //BounceInterpolator(回弹）

        animation.setInterpolator(new BounceInterpolator());
        view.setAnimation(animation);

        final View view1 = view;
        final int toYDelta1 = (int) toYDelta;
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //清除动画
                view1.clearAnimation();
                //重新设置view位置，以响应点击事件
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view1.getLayoutParams();
                params.topMargin = params.topMargin + toYDelta1;
                view1.setLayoutParams(params);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    public void setDecelerateTransAnim(View view, float fromXDelta, float toXDelta, float fromYDelta, float toYDelta) {
        TranslateAnimation translateAnimation = new TranslateAnimation(fromXDelta, toXDelta, fromYDelta, toYDelta);
        translateAnimation.setDuration(500);
        //设置动画结束后效果保留
        translateAnimation.setFillAfter(false);
        //控制动画先快后慢
        translateAnimation.setInterpolator(new DecelerateInterpolator());
        view.setAnimation(translateAnimation);
    }

    /**
     * 为view设置旋转动画（以自身为中心）
     *
     * @param view
     * @param fromDegrees
     * @param toDegrees
     */
    public void setRotateAnimation(View view, float fromDegrees, float toDegrees) {
        RotateAnimation rotateAnimation = new RotateAnimation(fromDegrees, toDegrees,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        //设置动画持续时间
        rotateAnimation.setDuration(600);
        //设置动画结束后效果保留
        rotateAnimation.setFillAfter(true);
        view.setAnimation(rotateAnimation);
    }


    /**
     * 为view设置透明度动画
     *
     * @param view
     * @param fromAlpha
     * @param toAlpha
     */
    public void setAlphaAnimation(View view, float fromAlpha, float toAlpha) {
        AlphaAnimation alphaAnimation = new AlphaAnimation(fromAlpha, toAlpha);
        alphaAnimation.setDuration(500);
        alphaAnimation.setFillAfter(true);
        view.setAnimation(alphaAnimation);
    }

    /**
     * 按屏幕大小（比例）设置LayoutParams(for MapView in Fragment)
     *
     * @param view
     * @param weightScale
     * @param hightScale
     * @return
     */
    public RelativeLayout.LayoutParams setViewParams(View view, int weightScale, int hightScale) {
        WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        int hight = display.getHeight();
        int width = display.getWidth();
        RelativeLayout.LayoutParams params;
        params = (RelativeLayout.LayoutParams) view.getLayoutParams();
        params.height = hight / hightScale;
        params.width = width / weightScale;
        return params;
    }

    //////////////////////////////////////////////////////

    /**
     * 更新adcode
     *
     * @param driver
     * @param aMapLocation
     */
    public void updateAdCode(Driver driver, AMapLocation aMapLocation) {
        if ((!driver.getCompany().equals("null")) || (aMapLocation == null)) {
            return;
        }
        driver.setCompany(aMapLocation.getAdCode());
        final MyHttpHelper myHttpHelper = new MyHttpHelper(this);
        RequestParams params = myHttpHelper.getAdcodeParams(driver.getCompany());
        MyAsyncHttpUtils.get(Str.URL_UPDATEADCODE, params, new MyTextHttpResponseHandler() {
            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                Log.i(TAG, "onFailure:updateAdCode: " + s);
                MyToastDialog.showInfo(BaseActivity.this, "更新所在公司失败，请重新进入软件");
            }

            @Override
            public void onSuccess(int i, Header[] headers, String s) {
                Log.i(TAG, "onSuccess:updateAdCode: " + s);
                if (s.contains("OK")) {
                    Log.i(TAG, "onSuccess: 更新成功");
                } else {
                    List<String> result = myHttpHelper.resultByJson(s, null);
                    if (result != null && result.size() > 1) {
                        MyToastDialog.showInfo(BaseActivity.this, result.get(1));
                    } else {
                        MyToastDialog.showInfo(BaseActivity.this, "更新所在公司失败，请重新进入软件");
                    }
                }
            }
        });
    }

    public void checkAdcode(AMapLocation aMapLocation, Driver driver) {
        if (aMapLocation != null) {
            updateAdCode(driver, aMapLocation);
        } else {
            try {
                new Thread().sleep(5000);//睡眠5秒后递归
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            updateAdCode(driver, aMapLocation);
        }
    }

    /**
     * 注销登陆dialog
     */
    public void NormalDialogCustomAttr(final Activity activity, final boolean isInOrder) {
        BaseAnimatorSet bas_in = new BounceTopEnter();
        BaseAnimatorSet bas_out = new SlideBottomExit();
        Log.i(TAG, "NormalDialogCustomAttr: ");
        if (activity.isFinishing()) {
            return;
        }
        final NormalDialog dialog = new NormalDialog(this);
        dialog.isTitleShow(false)//
                .bgColor(Color.parseColor("#383838"))//
                .cornerRadius(5)//
                .content("是否确定注销登陆?")//
                .contentGravity(Gravity.CENTER)//
                .contentTextColor(Color.parseColor("#ffffff"))//
                .dividerColor(Color.parseColor("#222222"))//
                .btnTextSize(15.5f, 15.5f)//
                .btnTextColor(Color.parseColor("#ffffff"), Color.parseColor("#ffffff"))//
                .btnPressColor(Color.parseColor("#2B2B2B"))//
                .widthScale(0.85f)//
                .showAnim(bas_in)//
                .dismissAnim(bas_out)//
                .show();

        dialog.setOnBtnClickL(
                new OnBtnClickL() {
                    @Override
                    public void onBtnClick() {
                        dialog.dismiss();
                    }
                },
                new OnBtnClickL() {
                    @Override
                    public void onBtnClick() {
                        if (isInOrder) {
                            ToastUtil.show(BaseActivity.this, "您正处于接单状态，请处理完订单再下线");
                            return;
                        }
                        MyAsyncHttpUtils.get(Str.URL_LOGINOUT, new MyTextHttpResponseHandler() {
                            @Override
                            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {

                            }

                            @Override
                            public void onSuccess(int i, Header[] headers, String s) {
                                MyHttpHelper myHttpHelper = new MyHttpHelper(mContext);
                                List<String> loginOutResult = myHttpHelper.resultByJson(s, null);
                                if (loginOutResult == null) {
                                    return;
                                }
                                if (loginOutResult.get(0).equalsIgnoreCase("OK")) {
                                    ToastUtil.show(BaseActivity.this, "注销登陆");
                                    dialog.dismiss();
                                    activity.startActivity(new Intent(activity, LoginActivity.class));
                                    activity.finish();
                                } else {
                                    ToastUtil.show(BaseActivity.this, "注销登陆失败，" + loginOutResult.get(1));
                                }
                            }
                        });
//                        List<String> loginOutResult = myHttpUtils.getLoginOutResultByGET(Str.URL_LOGINOUT);

                    }
                });
    }

    public List<OrderInfo> checkOrders(int count) {
        List<OrderInfo> orderInfos = null;
        if (count == 1) {
            OrderInfo orderInfo1 = getOrderInfoFromReceiver("{\"createTime\":1477189489000,\"reservationAddress\":\"爆爆椒香辣虾(红星路店)\",\"phone\":\"18110932720\",\"ygAmount\":23.00,\"otherCharges\":0.00,\"remark\":\"通过APP下单\",\"no\":\"DELL20161023102449VXBXFT\",\"yhAmount\":0.00,\"setouttime\":1477189489000,\"type\":4,\"nick_name\":\"鱼鱼\",\"id\":515,\"amount\":0.00,\"baseAmount\":0.00,\"distance\":6.20,\"head_portrait\":\"attachment/2016/10/10/dd3e615c-aa68-4811-ac7d-e84930abe24b.jpg\",\"realPay\":0.00,\"remoteFee\":0.00,\"trip\":{\"member\":27,\"startLongitude\":117.291583,\"endLatitude\":31.828436,\"startLatitude\":31.859931,\"endLongitude\":117.264221,\"orderId\":515},\"roadToll\":0.00,\"fromType\":1,\"member\":27,\"status\":1,\"distanceAmount\":0.00,\"pdFlag\":true,\"timeOutAmount\":0.00,\"waitAmount\":0.00,\"serviceType\":1,\"destination\":\"庐州太太(南七店)\",\"payStatus\":6,\"company\":2,\"setOutFlag\":false,\"delFlag\":false}");
            if (orderInfo1 != null) {
                orderInfos = new ArrayList<>();
                orderInfos.add(orderInfo1);
            }
        } else {
            OrderInfo orderInfo1 = getOrderInfoFromReceiver("{\"createTime\":1477189489000,\"reservationAddress\":\"爆爆椒香辣虾(红星路店)\",\"phone\":\"18110932720\",\"ygAmount\":23.00,\"otherCharges\":0.00,\"remark\":\"通过APP下单\",\"no\":\"DELL20161023102449VXBXFT\",\"yhAmount\":0.00,\"setouttime\":1477189489000,\"type\":4,\"nick_name\":\"鱼鱼\",\"id\":515,\"amount\":0.00,\"baseAmount\":0.00,\"distance\":6.20,\"head_portrait\":\"attachment/2016/10/10/dd3e615c-aa68-4811-ac7d-e84930abe24b.jpg\",\"realPay\":0.00,\"remoteFee\":0.00,\"trip\":{\"member\":27,\"startLongitude\":117.291583,\"endLatitude\":31.828436,\"startLatitude\":31.859931,\"endLongitude\":117.264221,\"orderId\":515},\"roadToll\":0.00,\"fromType\":1,\"member\":27,\"status\":1,\"distanceAmount\":0.00,\"pdFlag\":true,\"timeOutAmount\":0.00,\"waitAmount\":0.00,\"serviceType\":1,\"destination\":\"庐州太太(南七店)\",\"payStatus\":6,\"company\":2,\"setOutFlag\":false,\"delFlag\":false}");
            OrderInfo orderInfo2 = getOrderInfoFromReceiver("{\"createTime\":1477192877000,\"reservationAddress\":\"傣妹火锅(站前路店)\",\"phone\":\"18110932720\",\"ygAmount\":23.00,\"otherCharges\":0.00,\"remark\":\"通过APP下单\",\"no\":\"DELL20161023112117DNMIRJ\",\"yhAmount\":0.00,\"setouttime\":1477192877000,\"type\":4,\"nick_name\":\"鱼鱼\",\"id\":516,\"amount\":0.00,\"baseAmount\":0.00,\"distance\":7.60,\"head_portrait\":\"attachment/2016/10/10/dd3e615c-aa68-4811-ac7d-e84930abe24b.jpg\",\"realPay\":0.00,\"remoteFee\":0.00,\"trip\":{\"member\":27,\"startLongitude\":117.311884,\"endLatitude\":31.85519,\"startLatitude\":31.88466,\"endLongitude\":117.256066,\"orderId\":516},\"roadToll\":0.00,\"fromType\":1,\"member\":27,\"status\":1,\"distanceAmount\":0.00,\"pdFlag\":true,\"timeOutAmount\":0.00,\"waitAmount\":0.00,\"serviceType\":1,\"destination\":\"刘一手火锅(三里庵店)\",\"payStatus\":6,\"company\":2,\"setOutFlag\":false,\"delFlag\":false}");
            if (orderInfo1 != null) {
                orderInfos = new ArrayList<>();
                orderInfos.add(orderInfo1);
            }
            if (orderInfo2 != null) {
                if (orderInfos == null) {
                    orderInfos = new ArrayList<>();
                }
                orderInfos.add(orderInfo2);
            }
        }
        return orderInfos;
    }


    public OrderInfo getOrderInfoFromReceiver(String orderMessage) {
        OrderInfo orderInfo = null;
        try {
            JSONObject object = new JSONObject(orderMessage);
            int title = object.has("title") ? !object.getString("title").equalsIgnoreCase("null") ? Ints.tryParse(object.getString("title")) : 1 : 1;
            int status = object.has("status") ? !object.getString("status").equalsIgnoreCase("null") ? Ints.tryParse(object.getString("status")) : 1 : 1;
            int timeOut = object.has("timeOut") ? !object.getString("timeOut").equalsIgnoreCase("null") ? Ints.tryParse(object.getString("timeOut")) : 30 : 30;
            int people = object.has("people") ? !object.getString("people").equalsIgnoreCase("null") ? Ints.tryParse(object.getString("people")) : 0 : 0;
            String phone = object.has("phone") ? object.getString("phone") : "";
            String nick_name = object.has("nick_name") ? object.getString("nick_name") : "";
            String head_portrait = object.has("head_portrait") ? object.getString("head_portrait") : "";
            String no = object.has("no") ? object.getString("no") : "";
            String setouttime = object.has("setouttime") ? object.getString("setouttime") : "";
            String createTime = object.has("createTime") ? object.getString("createTime") : "";
            int type_ = object.has("type") ? !object.getString("type").equalsIgnoreCase("null") ? Ints.tryParse(object.getString("type")) : 4 : 4;
            int serviceType_ = object.has("serviceType") ? !object.getString("serviceType").equalsIgnoreCase("null") ? Ints.tryParse(object.getString("serviceType")) : 1 : 1;
            int orderType = object.has("orderType") ? !object.getString("orderType").equalsIgnoreCase("null") ? Ints.tryParse(object.getString("orderType")) : 1 : 1;
            String type = type2String(type_);//小分类
            String serviceType = serviceType2String(serviceType_);//大分类
            boolean set_out_flag = object.has("setOutFlag") ? !object.getString("setOutFlag").equalsIgnoreCase("null") ? object.getBoolean("setOutFlag") : false : false;
            boolean addAmountFlag = object.has("addFlag") ? !object.getString("addFlag").equalsIgnoreCase("null") ? object.getBoolean("addFlag") : false : false;
            boolean pdFlag = object.has("pdFlag") ? !object.getString("pdFlag").equalsIgnoreCase("null") ? object.getBoolean("pdFlag") : false : false;
            long id = object.has("id") ? !object.getString("id").equalsIgnoreCase("null") ? Longs.tryParse(object.getString("id")) : 0 : 0;
            double distance = object.has("distance") ? !object.getString("distance").equalsIgnoreCase("null") ? Doubles.tryParse(object.getString("distance")) : 0 : 0;
            double yg_amount = object.has("ygAmount") ? !object.getString("ygAmount").equalsIgnoreCase("null") ? Doubles.tryParse(object.getString("ygAmount")) : 0 : 0;
            double addAmount = object.has("addAmount") ? !object.getString("addAmount").equalsIgnoreCase("null") ? Doubles.tryParse(object.getString("addAmount")) : 0 : 0;
            JSONObject trip = object.has("trip") ? object.getJSONObject("trip") : null;
            double startLatitude = 0, startLongitude = 0, endLatitude = 0, endLongitude = 0;
            if (trip != null) {
                startLatitude = trip.has("startLatitude") ? !trip.getString("startLatitude").equalsIgnoreCase("null") ? Doubles.tryParse(trip.getString("startLatitude")) : 0 : 0;
                startLongitude = trip.has("startLongitude") ? !trip.getString("startLongitude").equalsIgnoreCase("null") ? Doubles.tryParse(trip.getString("startLongitude")) : 0 : 0;
                endLatitude = trip.has("endLatitude") ? !trip.getString("endLatitude").equalsIgnoreCase("null") ? Doubles.tryParse(trip.getString("endLatitude")) : 0 : 0;
                endLongitude = trip.has("endLongitude") ? !trip.getString("endLongitude").equalsIgnoreCase("null") ? Doubles.tryParse(trip.getString("endLongitude")) : 0 : 0;
            }
            String reservationAddress = object.has("reservationAddress") ? !object.getString("reservationAddress").equalsIgnoreCase("null") ? object.getString("reservationAddress") : "" : "";
            String destination = object.has("destination") ? !object.getString("destination").equalsIgnoreCase("null") ? object.getString("destination") : "" : "";
            String remark = object.has("remark") ? object.getString("remark") : "";
            orderInfo = new OrderInfo(title, status, phone, nick_name, head_portrait, no, setouttime, type, serviceType, set_out_flag, id, distance,
                    yg_amount, startLatitude, startLongitude, endLatitude, endLongitude, reservationAddress, destination, remark);
            orderInfo.setTimeOut(timeOut);
            orderInfo.setCreateTime(createTime);
            orderInfo.setPdFlag(pdFlag);
            orderInfo.setAddAmountFlag(addAmountFlag);
            orderInfo.setAddAmount(addAmount);
            orderInfo.setPeople(people);
            orderInfo.setOrderType(orderType);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.i(TAG, "getOrderInfoFromReceiver: " + e);
        }
        return orderInfo;
    }

    public MyNotificationInfo getNotificationInfo(String msg) {
        if (msg == null) {
            return null;
        }
        MyNotificationInfo myNotificationInfo = null;
        try {
            JSONObject object = new JSONObject(msg);
            String title = object.has("title") ? object.getString("title") : "";
            String content = object.has("content") ? object.getString("content") : "";
            myNotificationInfo = new MyNotificationInfo(title, content);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return myNotificationInfo;
    }

    /**
     * 大分类
     *
     * @return
     */
    public String serviceType2String(int serviceType_) {
        String stringType = "";
        switch (serviceType_) {
            case 1:
                stringType = "专车";
                break;
            case 2:
                stringType = "代驾";
                break;
            case 3:
                stringType = "出租车";
                break;
            case 4:
                stringType = "快车";
                break;
        }
        return stringType;
    }

    /**
     * 小分类
     *
     * @return
     */
    public String type2String(int type_) {
        String stringServiceType = "";
        switch (type_) {
            case 44:
                stringServiceType = "快车";
                break;
            case 37:
                stringServiceType = "豪华型";
                break;
            case 43:
                stringServiceType = "舒适型";
                break;
            case 40:
                stringServiceType = "代驾";
                break;
            case 42:
                stringServiceType = "出租车";
                break;
        }
        return stringServiceType;
    }

    /**
     * 将时间戳转换为时间格式( MM月dd日 HH时mm分 )
     *
     * @param time
     * @return
     */
    public String getDateToString(long time) {
        Date d = new Date(time);
        SimpleDateFormat sf = new SimpleDateFormat("MM月dd日 HH时mm分");
        return sf.format(d);
    }

    TipHelper mTipHelper;

    public void iflySpeak(String content) {
        if (mTipHelper == null) {
            mTipHelper = new TipHelper(this);
        }
        if (mTipHelper == null) {
            return;
        }
        mTipHelper.Vibrate(500);//震动0.5秒
        mTipHelper.speak(content);
    }

    /**
     * 申请网络访问权限
     */
    public void permissionInternet() {
        if (ContextCompat.checkSelfPermission(this, Str.INTERNET)
                != PackageManager.PERMISSION_GRANTED) {
            //申请android.permission.INTERNET权限
            ActivityCompat.requestPermissions(this, new String[]{Str.INTERNET}, Str.REQUEST_INTERNET);
        }
    }

    public void permissionCallPhone() {
        if (ContextCompat.checkSelfPermission(this, Str.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {
            //申请android.permission.CALL_PHONE
            ActivityCompat.requestPermissions(this, new String[]{Str.CALL_PHONE}, Str.REQUEST_CALL_PHONE);
        }
    }

    /**
     * 申请定位权限
     */
    public void permissionLocation() {
//        Log.i(TAG, "permissionLocation: ");
        if (ContextCompat.checkSelfPermission(this, Str.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
//            Log.i(TAG, "permissionLocation: ACCESS_COARSE_LOCATION");
            //申请android.permission.ACCESS_COARSE_LOCATION权限
            ActivityCompat.requestPermissions(this, new String[]{Str.ACCESS_COARSE_LOCATION}, Str.REQUEST_ACCESS_COARSE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(this, Str.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
//            Log.i(TAG, "permissionLocation: ACCESS_FINE_LOCATION");
            //申请android.permission.ACCESS_FINE_LOCATION权限
            ActivityCompat.requestPermissions(this, new String[]{Str.ACCESS_FINE_LOCATION}, Str.REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    public void permissionExternalStorage() {
//        Log.i(TAG, "permissionExternalStorage: ");
        if (ContextCompat.checkSelfPermission(this, Str.WRITE_EXTERNALSTORAGE)
                != PackageManager.PERMISSION_GRANTED) {
//            Log.i(TAG, "permissionExternalStorage: WRITE_EXTERNALSTORAGE");
            //申请android.permission.ACCESS_COARSE_LOCATION权限
            ActivityCompat.requestPermissions(this, new String[]{Str.WRITE_EXTERNALSTORAGE}, Str.REQUEST_WRITE_EXTERNALSTORAGE);
        }
        if (ContextCompat.checkSelfPermission(this, Str.DELE_CREATE_EXTERNALSTORAGE)
                != PackageManager.PERMISSION_GRANTED) {
//            Log.i(TAG, "permissionExternalStorage: DELE_CREATE_EXTERNALSTORAGE");
            //申请android.permission.ACCESS_FINE_LOCATION权限
            ActivityCompat.requestPermissions(this, new String[]{Str.DELE_CREATE_EXTERNALSTORAGE}, Str.REQUEST_DELE_CREATE_EXTERNALSTORAGE);
        }
    }

    public void permissionWriteExternalStorage() {
//        Log.i(TAG, "permissionExternalStorage: ");
        if (ContextCompat.checkSelfPermission(this, Str.WRITE_EXTERNALSTORAGE)
                != PackageManager.PERMISSION_GRANTED) {
//            Log.i(TAG, "permissionExternalStorage: WRITE_EXTERNALSTORAGE");
            //申请android.permission.ACCESS_COARSE_LOCATION权限
            ActivityCompat.requestPermissions(this, new String[]{Str.WRITE_EXTERNALSTORAGE}, Str.REQUEST_WRITE_EXTERNALSTORAGE);
        }
    }

    public boolean checkPermissionWriteExternalStorage() {
//        Log.i(TAG, "permissionExternalStorage: ");
        if (ContextCompat.checkSelfPermission(this, Str.WRITE_EXTERNALSTORAGE)
                != PackageManager.PERMISSION_GRANTED) {
//            Log.i(TAG, "permissionExternalStorage: WRITE_EXTERNALSTORAGE");
            return false;
        }
//        if (ContextCompat.checkSelfPermission(this, Str.DELE_CREATE_EXTERNALSTORAGE)
//                != PackageManager.PERMISSION_GRANTED) {
//            Log.i(TAG, "permissionExternalStorage: WRITE_EXTERNALSTORAGE");
//            return false;
//        }
        return true;
    }

    public void checkOpenGps() {
        LocationManager locationManager = (LocationManager) this
                .getSystemService(Context.LOCATION_SERVICE);
        // 判断GPS模块是否开启，如果没有则开启
        if (locationManager != null && !locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
            MyDialogUtils myDialogUtils = new MyDialogUtils(this, this);
            myDialogUtils.openGps(Str.REQUEST_OPEN_GPS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        for (int i = 0; i < permissions.length; i++) {
////            Log.i(TAG, "onRequestPermissionsResult:permissions: " + permissions[i]);
//        }
//        for (int i = 0; i < grantResults.length; i++) {
////            Log.i(TAG, "onRequestPermissionsResult:grantResults: " + grantResults[i]);
//        }
    }

    /**
     * 定义圆形bitmap
     *
     * @param bitmap
     * @param size   尺寸为原图的几分之几（如2：表示1/2）
     * @return
     */
    public static Bitmap makeRoundCornerBitmap(Bitmap bitmap, int size) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int left = 0, top = 0, right = width, bottom = height;
        float roundPx = height / 2;
        if (width > height) {
            left = (width - height) / 2;
            top = 0;
            right = left + height;
            bottom = height;
        } else if (height > width) {
            left = 0;
            top = (height - width) / 2;
            right = width;
            bottom = top + width;
            roundPx = width / 2;
        }
        Log.i(TAG, "ps:" + left + ", " + top + ", " + right + ", " + bottom);
        Bitmap output = Bitmap.createBitmap(width / size, height / size, Bitmap.Config.ARGB_8888);//自定义为原尺寸一半
        Canvas canvas = new Canvas(output);
        int color = 0xff424242;
        Paint paint = new Paint();
        Rect rect = new Rect(left / size, top / size, right / size, bottom / size);//定义为原尺寸一半
        RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }
}
