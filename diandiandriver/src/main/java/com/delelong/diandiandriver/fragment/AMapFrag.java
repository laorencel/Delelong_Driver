package com.delelong.diandiandriver.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amap.api.maps.AMap;
import com.amap.api.maps.MapView;
import com.delelong.diandiandriver.R;

/**
 * Created by Administrator on 2016/9/22.
 */
public class AMapFrag extends Fragment {

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


//    View view;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        view = inflater.inflate(R.layout.include_driver_map,container,false);
        if (view == null) {
            Log.i("sys", "MF onCreateView() null");
            view = inflater.inflate(R.layout.include_driver_map, null);
            mapView = (MapView) view.findViewById(R.id.mapView);
            mapView.onCreate(savedInstanceState);
            if (aMap == null) {
                aMap = mapView.getMap();
            }
        }else {
            if (view.getParent() != null) {
                ((ViewGroup) view.getParent()).removeView(view);
            }
        }
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
//        ((MainActivity) activity).onSectionAttached(Constants.MAP_FRAGMENT);
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onResume() {
        Log.i("sys", "mf onResume");
        super.onResume();
        mapView.onResume();
    }

    /**
     * 方法必须重写
     * map的生命周期方法
     */
    @Override
    public void onPause() {
        Log.i("sys", "mf onPause");
        super.onPause();
        mapView.onPause();
    }

    /**
     * 方法必须重写
     * map的生命周期方法
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.i("sys", "mf onSaveInstanceState");
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     * map的生命周期方法
     */
    @Override
    public void onDestroy() {
        Log.i("sys", "mf onDestroy");
        super.onDestroy();
        mapView.onDestroy();
    }
}
//    private Context context;
//    private MapView mMapView = null;
//    private AMap aMap = null;
//    private AMapLocationClient mLocationClient;
//    private AMapLocationClientOption mLocationOption;
//    private LocationSource.OnLocationChangedListener mListener;
//    private float mCurrentX;
//    private MyOrientationListener myOrientationListener;
//
//    /**
//     * 初始化地图、定位
//     *
//     * @param savedInstanceState
//     */
//    private void setUpMap(Bundle savedInstanceState) {
//        this.context = this;
//        //获取地图控件引用
//        mMapView = (MapView) findViewById(R.id.map);
//        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，实现地图生命周期管理
//        mMapView.onCreate(savedInstanceState);
//        aMap = mMapView.getMap();
//
//        //定义一个UiSettings对象
//        UiSettings mUiSettings = aMap.getUiSettings();
//        mUiSettings.setZoomControlsEnabled(false);//是否允许显示缩放按钮。
//        mUiSettings.setCompassEnabled(false);//指南针可用不可用。
//        mUiSettings.setRotateGesturesEnabled(false);//是否允许通过手势来旋转。
//        mUiSettings.setScaleControlsEnabled(false);//设置比例尺功能是否可用
//
//        //改写箭头样式
////        myLocationStyle = new MyLocationStyle();
////        myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.drawable.navi_map_gps_locked1));
////        myLocationStyle.strokeColor(Color.argb(0, 0, 0, 0));// 设置圆形的边框颜色
////        myLocationStyle.radiusFillColor(Color.argb(0, 0, 0, 0));// 设置圆形的填充颜色
////        aMap.setMyLocationStyle(myLocationStyle);
//
//        CameraUpdate update = CameraUpdateFactory.zoomTo(15);
//        aMap.animateCamera(update);
//        aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
////        aMap.setLocationSource(this);// 设置定位监听
//        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
//
//        //设置方向监听
//        myOrientationListener = new MyOrientationListener(context);
//        myOrientationListener.setmOnOritationListener(new MyOrientationListener.OnOritationListener() {
//            @Override
//            public void onOritationChanged(float x) {
//                mCurrentX = x;
//            }
//        });
//    }

