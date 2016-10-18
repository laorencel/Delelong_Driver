package com.delelong.diandiandriver.dialog;

import android.app.Dialog;
import android.content.Context;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.maps.AMap;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.RouteSearch;
import com.delelong.diandiandriver.DriverActivity;
import com.delelong.diandiandriver.R;
import com.delelong.diandiandriver.bean.OrderInfo;
import com.delelong.diandiandriver.listener.MyRouteSearchListener;
import com.delelong.diandiandriver.utils.AMapUtil;
import com.delelong.diandiandriver.view.WiperSwitch;
import com.google.common.primitives.Longs;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2016/10/16.
 */

public class MyOrderDialog implements View.OnClickListener, WiperSwitch.OnSlipperChangedListener {
    private static final String TAG = "BAIDUMAPFORTEST";
    Context context;
    Dialog dialog;
    OrderInfo orderInfo;
    DriverActivity activity;
    AMap aMap;
    AMapLocation mAMapLocation;

    public MyOrderDialog(Context context, AMap aMap, AMapLocation aMapLocation,OrderInfo orderInfo) {
        this.context = context;
        this.aMap = aMap;
        mAMapLocation = aMapLocation;
        this.orderInfo = orderInfo;
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
    }

    OrderInterface orderInterface;
    ImageView img_cancel;
    WiperSwitch wiper_take;
    TextView tv_order_time, tv_order_des;
    TextView tv_reservationAddress, tv_destination;

    public void show(OrderInterface orderInterface) {
        this.orderInterface = orderInterface;
        dialog.show();
        activity = (DriverActivity) dialog.getOwnerActivity();//回调路线距离

        Window window = dialog.getWindow();
        window.setContentView(R.layout.dialog_take_order);

        initView(window);
        setRouteSearchListner();
        routeSearch(false);
    }

    private void initView(Window window) {
        img_cancel = (ImageView) window.findViewById(R.id.img_cancel);
        wiper_take = (WiperSwitch) window.findViewById(R.id.wiper_take);
        wiper_take.setDrawable(R.drawable.img_order_on, R.drawable.img_order_off, R.drawable.img_order_slipper);
        tv_order_time = (TextView) window.findViewById(R.id.tv_order_time);
        tv_order_des = (TextView) window.findViewById(R.id.tv_order_des);
        tv_reservationAddress = (TextView) window.findViewById(R.id.tv_reservationAddress);
        tv_destination = (TextView) window.findViewById(R.id.tv_destination);

        Log.i(TAG, "getSetouttime: " + getDateToString(Longs.tryParse(orderInfo.getSetouttime())));
        Log.i(TAG, "getSetouttime: " + orderInfo);
        if (orderInfo.isSet_out_flag()) {
            tv_order_time.setText(getDateToString(Longs.tryParse(orderInfo.getSetouttime())));
        } else {
            tv_order_time.setText("现在");
        }
        tv_reservationAddress.setText(orderInfo.getReservationAddress());
        tv_destination.setText(orderInfo.getDestination());
        setListener();
    }

    private void setListener() {
        img_cancel.setOnClickListener(this);
        wiper_take.setOnSlipperChangedListener(this);
    }

    @Override
    public void OnChanged(WiperSwitch wiperSwitch, boolean checkState) {
        if (checkState) {
            orderInterface.take(checkState);
            dialog.dismiss();
        }
    }


    public String getDateToString(long time) {
        Date d = new Date(time);
        SimpleDateFormat sf = new SimpleDateFormat("MM月dd日 HH时mm分");
        return sf.format(d);
    }

    /**
     * 根据起始点规划路径（显示路径图）
     */
    public void routeSearch(boolean showRoute) {
        LatLonPoint start = new LatLonPoint(mAMapLocation.getLatitude(), mAMapLocation.getLongitude());
        LatLonPoint end = new LatLonPoint(orderInfo.getStartLatitude(), orderInfo.getStartLongitude());
        myRouteSearchListener.setShowRoute(showRoute);
        final RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(start, end);
        // 第一个参数表示路径规划的起点和终点，第二个参数表示驾车模式，第三个参数表示途经点，第四个参数表示避让区域，第五个参数表示避让道路
        RouteSearch.DriveRouteQuery routeQuery = new RouteSearch.DriveRouteQuery(fromAndTo, RouteSearch.DrivingDefault, null, null, "");
        mRouteSearch.calculateDriveRouteAsyn(routeQuery);// 异步路径规划驾车模式查询
    }

    private RouteSearch mRouteSearch;
    private MyRouteSearchListener myRouteSearchListener;
    private DrivePath mDrivePath;

    public void setRouteSearchListner() {
        mRouteSearch = new RouteSearch(context);
        myRouteSearchListener = new MyRouteSearchListener(aMap, context);
        mRouteSearch.setRouteSearchListener(myRouteSearchListener);
        myRouteSearchListener.getDrivePathListener(new MyRouteSearchListener.MyDrivePathListener() {
            @Override
            public void getDrivePath(DrivePath drivePath) {
                mDrivePath = drivePath;
                if (mDrivePath != null) {//根据路径获取里程数等
                    Log.i(TAG, "getDrivePath: "+"预计行程花费 " + AMapUtil.getFriendlyTime((int) drivePath.getDuration())
                            + " 总距离：" + AMapUtil.getKiloLength(drivePath.getDistance()) + "千米");
                    tv_order_des.setText(Html.fromHtml("距离客户 <big><font color='#Fe8a03'> " + AMapUtil.getKiloLength(drivePath.getDistance()) + "千米</font></big>,大约需要 <big><font color='#Fe8a03'>"+AMapUtil.getFriendlyTime((int) drivePath.getDuration())+"</font></big>"));
                }
            }
        });
    }

    public interface OrderInterface {
        void take(boolean take);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_cancel:
                dialog.dismiss();
                break;
        }
    }
}
