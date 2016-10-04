package com.delelong.diandiandriver.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.LatLng;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.delelong.diandiandriver.DriverActivity;
import com.delelong.diandiandriver.R;
import com.delelong.diandiandriver.bean.CarInfo;
import com.delelong.diandiandriver.listener.MyFragCameraChangeListener;
import com.delelong.diandiandriver.listener.MyOrientationListener;
import com.delelong.diandiandriver.numberPicker.ToastUtil;

import java.util.List;

/**
 * Created by Administrator on 2016/9/22.
 */
public class AMapFrag extends Fragment implements View.OnClickListener, LocationSource, AMapLocationListener {

    private static final String TAG = "BAIDUMAPFORTEST";
    private static AMapFrag fragment=null;
    public static final int POSITION=0;

    private MapView mapView;
    private AMap aMap;
    private View view;

    public static Fragment newInstance(){
        if(fragment==null){
            synchronized(AMapFrag.class){
                if(fragment==null){
                    fragment=new AMapFrag();
                }
            }
        }
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setUpMap(inflater,container,savedInstanceState);
        initView();
        return view;
    }

    ImageView img_map_top02;//展开地图箭头
    TextView tv_map_top02;//显示当前位置地名
    ImageButton loc;
    private void initView() {
        img_map_top02 = (ImageView) view.findViewById(R.id.img_map_top02);
        tv_map_top02 = (TextView) view.findViewById(R.id.tv_map_top02);
        loc = (ImageButton) view.findViewById(R.id.loc);
        img_map_top02.setOnClickListener(this);
        loc.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.img_map_top02:
                getFragmentManager().popBackStack();
                break;
            case R.id.loc:
                ((DriverActivity)getActivity()).centerToMyLocation(aMap, mLocationClient,
                        myOrientationListener, mAMapLocation.getLatitude(), mAMapLocation.getLongitude());
                break;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        //首次进入开启定位，以后返回界面可以由centerToMyLocation重新开启定位
        if (isFirstIn) {
            if (aMap != null) {
                aMap.setMyLocationEnabled(true);
                if (mLocationClient != null){
                    mLocationClient.startLocation();
                    if (!mLocationClient.isStarted()) {
                        mLocationClient.startLocation();
                        //开启方向传感器
                        myOrientationListener.start();
                    }
                }
            }
        }

    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    /**
     * 方法必须重写
     * map的生命周期方法
     */
    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }
    @Override
    public void onStop() {
        super.onStop();
        //停止定位
//        aMap.setMyLocationEnabled(false);
        if (mLocationClient != null) {
            mLocationClient.stopLocation();
        }
        //关闭方向传感器
        myOrientationListener.stop();
    }

    /**
     * 方法必须重写
     * map的生命周期方法
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     * map的生命周期方法
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
//        myCameraChangeListener = null;
        myOrientationListener = null;
        if (null != mLocationClient) {
            mLocationClient.onDestroy();
        }
        deactivate();
    }

    /**
     * 初始化view，AMap
     * @param inflater
     * @param container
     * @param savedInstanceState
     */
    private void setUpMap(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.include_driver_map, container,false);
            mapView = (MapView) view.findViewById(R.id.mapView);
            mapView.onCreate(savedInstanceState);

            mapView.setLayoutParams(setViewParams(mapView,1,2));
            if (aMap == null) {
                aMap = mapView.getMap();
            }
        }else {
            if (view.getParent() != null) {
                ((ViewGroup) view.getParent()).removeView(view);
            }
        }

        setUpMap();
    }
    public RelativeLayout.LayoutParams setViewParams(View view, int weightScale, int hightScale) {
        WindowManager wm = (WindowManager)this.getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        int hight = display.getHeight();
        int width = display.getWidth();
        RelativeLayout.LayoutParams params;
        params= (RelativeLayout.LayoutParams) view.getLayoutParams();
        params.height=hight/hightScale;
        params.width=width/weightScale;
        return params;
    }



    private Context context;
    private AMapLocationClient mLocationClient;
    private AMapLocationClientOption mLocationOption;
    private LocationSource.OnLocationChangedListener mListener;
    private float mCurrentX;
    private MyOrientationListener myOrientationListener;
    AMapLocation mAMapLocation;
    /**
     * 初始化地图、定位
     */
    private void setUpMap() {
        this.context = getContext();

        //定义一个UiSettings对象
        UiSettings mUiSettings = aMap.getUiSettings();
        mUiSettings.setZoomControlsEnabled(false);//是否允许显示缩放按钮。
        mUiSettings.setCompassEnabled(false);//指南针可用不可用。
        mUiSettings.setRotateGesturesEnabled(false);//是否允许通过手势来旋转。
        mUiSettings.setScaleControlsEnabled(false);//设置比例尺功能是否可用

        aMap.animateCamera(CameraUpdateFactory.zoomTo( 15.0f), 1000, null);
        aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
        aMap.setLocationSource(this);// 设置定位监听
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false


        setMyOrientationListener();
        setMyCameraChangeListener();
    }

    private void setMyOrientationListener() {
        //设置方向监听
        myOrientationListener = new MyOrientationListener(context);
        myOrientationListener.setmOnOritationListener(new MyOrientationListener.OnOritationListener() {
            @Override
            public void onOritationChanged(float x) {
                mCurrentX = x;
            }
        });
    }

    private MyFragCameraChangeListener myCameraChangeListener;
    List<PoiItem> pois;
    LatLng centerOfMap;
    private List<CarInfo> carInfos;
    String city;
    /**
     * 地图移动状态监听
     */
    private void setMyCameraChangeListener() {
        myCameraChangeListener = new MyFragCameraChangeListener( context);
        aMap.setOnCameraChangeListener(myCameraChangeListener);

        myCameraChangeListener.getGeoCodeResultListener(new MyFragCameraChangeListener.GeoCodeResulutListener() {
            @Override
            public void getReverseGeoCodeResult(RegeocodeResult regeocodeResult) {
                pois = regeocodeResult.getRegeocodeAddress().getPois();
                //当前中心点所在城市
                city = regeocodeResult.getRegeocodeAddress().getCity();
            }

            @Override
            public void getLatlng(LatLng center) {
                centerOfMap = center;
                LatLng leftTop = new LatLng(centerOfMap.latitude - 0.000015, centerOfMap.longitude - 0.000015);//半径2公里
                LatLng rightBottom = new LatLng(centerOfMap.latitude + 0.000015, centerOfMap.longitude + 0.000015);
//                carInfos = myHttpUtils.getCarInfos(Str.URL_GETCARINFO, leftTop, rightBottom);//获取车辆列表
                //测试
//                List<CarInfo> list = new ArrayList<CarInfo>();
//                for (int i = 0; i < 10; i++) {
//                    CarInfo carInfo = new CarInfo();
//                    carInfo.setLatitude(centerOfMap.latitude+i/100);
//                    carInfo.setLongitude(centerOfMap.longitude+i/100);
//                    carInfo.setOrientation(i*10);
//                    list.add(carInfo);
//                }
//                if (carInfos.size() >= 1) {
//                    //显示车辆
//                    addCars(carInfos);
//                }
            }
        });
    }
    private boolean isFirstIn = true;
    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (mListener != null && aMapLocation != null) {
            if (aMapLocation != null
                    && aMapLocation.getErrorCode() == 0) {
                mListener.onLocationChanged(aMapLocation);// 显示系统小蓝点
                aMap.setMyLocationRotateAngle(mCurrentX);

                //判断是否上传位置
//                if (mAMapLocation != null) {
////                    upDateLocation();
//                }
                mAMapLocation = aMapLocation;

                tv_map_top02.setText(mAMapLocation.getPoiName());
//                myAMapLocation = new MyAMapLocation(mAMapLocation.getCountry(), mAMapLocation.getProvince(),
//                        mAMapLocation.getCity(), mAMapLocation.getDistrict(), mAMapLocation.getAddress(), mAMapLocation.getAdCode());
                city = mAMapLocation.getCity();
            } else {
                String errText = "定位失败," + aMapLocation.getErrorCode() + ": " + aMapLocation.getErrorInfo();
                ToastUtil.show("errText");
            }
        }
    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mListener = onLocationChangedListener;
        if (mLocationClient == null) {
            mLocationClient = new AMapLocationClient(context);
            mLocationOption = new AMapLocationClientOption();
            //设置定位监听
            mLocationClient.setLocationListener(this);
            //设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            mLocationOption.setInterval(5000);
            mLocationClient.setLocationOption(mLocationOption);
//            aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);

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
}