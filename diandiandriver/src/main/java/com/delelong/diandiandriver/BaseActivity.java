package com.delelong.diandiandriver;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.model.LatLng;
import com.delelong.diandiandriver.bean.ADBean;
import com.delelong.diandiandriver.bean.Client;
import com.delelong.diandiandriver.bean.Driver;
import com.delelong.diandiandriver.bean.MyNotificationInfo;
import com.delelong.diandiandriver.bean.OrderInfo;
import com.delelong.diandiandriver.bean.Str;
import com.delelong.diandiandriver.dialog.MyDialogUtils;
import com.delelong.diandiandriver.http.MyHttpUtils;
import com.delelong.diandiandriver.listener.MyOrientationListener;
import com.delelong.diandiandriver.pace.MyAMapLocation;
import com.delelong.diandiandriver.utils.ExampleUtil;
import com.delelong.diandiandriver.utils.SystemUtils;
import com.delelong.diandiandriver.utils.TipHelper;
import com.delelong.diandiandriver.utils.ToastUtil;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.jpush.android.api.JPushInterface;


public class BaseActivity extends AppCompatActivity {
    private static final String TAG = "BAIDUMAPFORTEST";
    //    public static final String URL_LOGIN = "http://121.40.142.141:8090/Jfinal/api/login";
    private String registrationId;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//禁止横屏
        getSupportActionBar().hide();
        initJPush();
//        setWindowBar();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
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

    ////////////////////////////////////////////////////////////

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
    public void centerToMyLocation(AMap aMap, AMapLocationClient mLocationClient, MyOrientationListener myOrientationListener, double myLatitude, double myLongitude) {
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
        if (aMap == null || mLocationClient == null || myOrientationListener == null) {
            return;
        }
        LatLng latLng = new LatLng(myLatitude, myLongitude);
//        CameraUpdate update = CameraUpdateFactory.zoomTo(18);
//        aMap.animateCamera(update);
        aMap.animateCamera(CameraUpdateFactory.changeLatLng(latLng));
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
     * @return 获取手机序列号
     */
    public String getSerialNumber() {
        String serial = null;
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class);
            serial = (String) get.invoke(c, "ro.serialno");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return serial;
    }
    ///////////////////////////////////////////图片获取与存储

    /**
     * 设置启动页广告图片
     *
     * @param aMapLocation
     */
    public void downloadStartAD(AMapLocation aMapLocation) {
        SharedPreferences preferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        String last_update_start = preferences.getString("last_update_start", "null");
        ADBean adBean_start = null;
        MyHttpUtils myHttpUtils = new MyHttpUtils(this);
        if (aMapLocation.getAdCode() != null) {
            adBean_start = myHttpUtils.getADBeanByGET(Str.URL_AD, aMapLocation.getAdCode(), 1);
            if (adBean_start == null) {
                return;
            }
        } else {
            return;
        }

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
        }
        for (int i = 0; i < adInfo.getPics().size(); i++) {
            String pic = adInfo.getPics().get(i);
            file = new File(Str.ADIMAGEPATH_START + i + ".JPEG");
            if (!file.exists()) {
                try {
                    file.createNewFile();
                    myHttpUtils.createImage(Str.ADIMAGEPATH_START + i + ".JPEG",
                            myHttpUtils.downloadImage(Str.URL_SERVICE_IMAGEPATH + pic));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void downloadMainAD(AMapLocation aMapLocation) {
        SharedPreferences preferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        String last_update_main = preferences.getString("last_update_main", "null");
        MyHttpUtils myHttpUtils = new MyHttpUtils(this);
        ADBean adBean_main = myHttpUtils.getADBeanByGET(Str.URL_AD, aMapLocation.getAdCode(), 2);
        if (adBean_main == null) {
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
        for (int i = 0; i < adInfo.getPics().size(); i++) {
            file = new File(Str.ADIMAGEPATH_MAIN + i + ".JPEG");
            if (!file.exists()) {
                try {
                    file.createNewFile();
                    String pic = adInfo.getPics().get(i);
                    myHttpUtils.createImage(Str.ADIMAGEPATH_MAIN + i + ".JPEG",
                            myHttpUtils.downloadImage(Str.URL_SERVICE_IMAGEPATH + pic));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
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
            fileOutputStream = new FileOutputStream(path);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);// 把数据写入文件
            Log.i(TAG, "createImage: " + "保存图片" + fileName);
        } catch (FileNotFoundException e) {
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
        Uri selectedImage = data.getData();
        String[] filePathColumns = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(selectedImage, filePathColumns, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumns[0]);
        String imagePath = cursor.getString(columnIndex);
        bitmap = BitmapFactory.decodeFile(imagePath);
        cursor.close();
        return bitmap;
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
        JPushInterface.onPause(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        JPushInterface.onResume(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mMessageReceiver != null) {
            unregisterReceiver(mMessageReceiver);
        }
    }

    private void initJPush() {
        JPushInterface.init(this);
//        MyPushNotificationBuilder builder = new MyPushNotificationBuilder(this);
//        builder.setOrderNotificationBuilder();
        registerMessageReceiver();
        Log.i(TAG, "initJPush: " + JPushInterface.getRegistrationID(this));
    }


    //for receive customer msg from jpush server
    private MessageReceiver mMessageReceiver;
    public static final String MESSAGE_RECEIVED_ACTION = "com.example.jpushdemo.MESSAGE_RECEIVED_ACTION";
    public static final String KEY_TITLE = "title";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_EXTRAS = "extras";

    public void registerMessageReceiver() {
        mMessageReceiver = new MessageReceiver();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction(MESSAGE_RECEIVED_ACTION);
        registerReceiver(mMessageReceiver, filter);
    }

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
        MyHttpUtils myHttpUtils = new MyHttpUtils(this);
        driver.setCompany(aMapLocation.getAdCode());
        Log.i(TAG, "updateAdCode: "+aMapLocation.getAdCode()+"//"+driver.getCompany());
        myHttpUtils.setAdcodeByGET(Str.URL_UPDATEADCODE, driver);
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
                        MyHttpUtils myHttpUtils = new MyHttpUtils(BaseActivity.this);
                        List<String> loginOutResult = myHttpUtils.getLoginOutResultByGET(Str.URL_LOGINOUT);
                        if (loginOutResult == null) {
                            return;
                        }
                        if (loginOutResult.get(0).equalsIgnoreCase("OK")) {
                            ToastUtil.show(BaseActivity.this, "注销登陆");
                            dialog.dismiss();
                            activity.startActivity(new Intent(activity, LoginActivity.class));
                            activity.finish();
                        } else {
                            ToastUtil.show(BaseActivity.this, "注销登陆失败，请重试一次");
                        }
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
            int title = object.has("title") ? Ints.tryParse(object.getString("title")) : 1;
            int status = object.has("status") ? Ints.tryParse(object.getString("status")) : 1;
            int timeOut = object.has("timeOut") ? Ints.tryParse(object.getString("timeOut")) : 30;
            String phone = object.has("phone") ? object.getString("phone") : "";
            String nick_name = object.has("nick_name") ? object.getString("nick_name") : "";
            String head_portrait = object.has("head_portrait") ? object.getString("head_portrait") : "";
            String no = object.has("no") ? object.getString("no") : "";
            String setouttime = object.has("setouttime") ? object.getString("setouttime") : "";
            int type_ = object.has("type") ? Ints.tryParse(object.getString("type")) : 4;
            int serviceType_ = object.has("serviceType") ? Ints.tryParse(object.getString("serviceType")) : 1;
            String type = type2String(type_);//小分类
            String serviceType = serviceType2String(serviceType_);//大分类
            boolean set_out_flag = object.has("setOutFlag") ? object.getBoolean("setOutFlag") : false;
            long id = object.has("id") ? Longs.tryParse(object.getString("id")) : 0;
            double distance = object.has("distance") ? Doubles.tryParse(object.getString("distance")) : 0;
            double yg_amount = object.has("ygAmount") ? Doubles.tryParse(object.getString("ygAmount")) : 0;
            JSONObject trip = object.getJSONObject("trip");
            double startLatitude = trip.has("startLatitude") ? Doubles.tryParse(trip.getString("startLatitude")) : 0;
            double startLongitude = trip.has("startLongitude") ? Doubles.tryParse(trip.getString("startLongitude")) : 0;
            double endLatitude = trip.has("endLatitude") ? Doubles.tryParse(trip.getString("endLatitude")) : 0;
            double endLongitude = trip.has("endLongitude") ? Doubles.tryParse(trip.getString("endLongitude")) : 0;
            String reservationAddress = object.has("reservationAddress") ? object.getString("reservationAddress") : "";
            String destination = object.has("destination") ? object.getString("destination") : "";
            String remark = object.has("remark") ? object.getString("remark") : "";
            orderInfo = new OrderInfo(title, status, phone, nick_name, head_portrait, no, setouttime, type, serviceType, set_out_flag, id, distance,
                    yg_amount, startLatitude, startLongitude, endLatitude, endLongitude, reservationAddress, destination, remark);
            orderInfo.setTimeOut(timeOut);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return orderInfo;
    }

    public MyNotificationInfo getNotificationInfo(String msg){
        if (msg == null){
            return null;
        }
        MyNotificationInfo myNotificationInfo = null;
        try {
            JSONObject object = new JSONObject(msg);
            String title = object.has("title")?object.getString("title"):"";
            String content = object.has("content")?object.getString("content"):"";
            myNotificationInfo = new MyNotificationInfo(title,content);
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
        Log.i(TAG, "permissionLocation: ");
        if (ContextCompat.checkSelfPermission(this, Str.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "permissionLocation: ACCESS_COARSE_LOCATION");
            //申请android.permission.ACCESS_COARSE_LOCATION权限
            ActivityCompat.requestPermissions(this, new String[]{Str.ACCESS_COARSE_LOCATION}, Str.REQUEST_ACCESS_COARSE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(this, Str.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "permissionLocation: ACCESS_FINE_LOCATION");
            //申请android.permission.ACCESS_FINE_LOCATION权限
            ActivityCompat.requestPermissions(this, new String[]{Str.ACCESS_FINE_LOCATION}, Str.REQUEST_ACCESS_FINE_LOCATION);
        }
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
        Log.i(TAG, "onRequestPermissionsResult: ");
//        Log.i(TAG, "onRequestPermissionsResult: "+permissions[0]);
//        Log.i(TAG, "onRequestPermissionsResult: "+grantResults[0]);
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
