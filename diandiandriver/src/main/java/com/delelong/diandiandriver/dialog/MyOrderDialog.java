package com.delelong.diandiandriver.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;
import com.delelong.diandiandriver.R;
import com.delelong.diandiandriver.bean.OrderInfo;
import com.delelong.diandiandriver.utils.TipHelper;
import com.delelong.diandiandriver.view.WiperSwitch;
import com.google.common.primitives.Longs;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2016/10/16.
 */

public class MyOrderDialog implements View.OnClickListener, WiperSwitch.OnSlipperChangedListener {
    private static final String TAG = "BAIDUMAPFORTEST";
    public static final int HIDE_ORDERDIALOG = 18;//取消显示订单dialog
    public static final int SPEAK = 19;//取消显示订单dialog
    Context context;
    Dialog dialog;
    OrderInfo orderInfo;
    Activity activity;
    AMapLocation mAMapLocation;
    TipHelper tipHelper;
    public Handler dialogHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case HIDE_ORDERDIALOG:
                    dismiss();
                    break;
                case SPEAK:
                    if (speak_continue) {
                        if (orderInfo != null) {
                            String content;
                            if (orderInfo.getDestination() == null || orderInfo.getDestination().equalsIgnoreCase("")) {
                                content = orderInfo.getServiceType() + "订单。距离您" + dstr + "公里，从" + orderInfo.getReservationAddress() + "出发";
                            } else {
                                content = orderInfo.getServiceType() + "订单。距离您" + dstr + "公里，从" + orderInfo.getReservationAddress() + "出发，到" + orderInfo.getDestination();
                            }
                            speak(content);
                        }
                    }
                    break;
            }
        }
    };
    boolean speak_continue;

    public MyOrderDialog(Context context, Activity activity, AMapLocation aMapLocation, OrderInfo orderInfo) {
        this.context = context;
        this.activity = activity;
        mAMapLocation = aMapLocation;
        this.orderInfo = orderInfo;
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
    }

    OrderInterface orderInterface;
    ImageView img_cancel;
    WiperSwitch wiper_take;
    TextView tv_order_type, tv_order_time, tv_order_des;
    TextView tv_reservationAddress, tv_destination;
    int mTimeOut = 30 * 1000;
    String dstr;

    public void show(int timeOut, OrderInterface orderInterface) {
        mTimeOut = timeOut * 1000;
        this.orderInterface = orderInterface;
        dialog.show();
//        activity = (DriverActivity) dialog.getOwnerActivity();//回调路线距离
        tipHelper = new TipHelper(activity);

        Window window = dialog.getWindow();
        window.setContentView(R.layout.dialog_take_order);
        speak("");
        initView(window);
//        setRouteSearchListner();
//        routeSearch(false);
        if (orderInfo != null && mAMapLocation != null) {
            LatLng start11 = new LatLng(orderInfo.getStartLatitude(), orderInfo.getStartLongitude());
            LatLng endLat11 = new LatLng(mAMapLocation.getLatitude(), mAMapLocation.getLongitude());
            double distance = AMapUtils.calculateLineDistance(start11, endLat11);//千米
            int distance_time = (int) (distance / 300);
            DecimalFormat fnum = new DecimalFormat("#####0.00");
            dstr = fnum.format(distance / 1000);
            tv_order_des.setText(Html.fromHtml("距离客户约 <big><font color='#Fe8a03'> " + dstr + " 千米</font></big>,大约需要 <big><font color='#Fe8a03'>" +
                    distance_time + "</font></big> 分钟"));
        }

        dialogHandler.sendEmptyMessageDelayed(HIDE_ORDERDIALOG, mTimeOut);
        speak_continue = true;
        dialogHandler.sendEmptyMessageDelayed(SPEAK, 2000);
    }

    private void initView(Window window) {
        img_cancel = (ImageView) window.findViewById(R.id.img_cancel);
        wiper_take = (WiperSwitch) window.findViewById(R.id.wiper_take);
        wiper_take.setDrawable(R.drawable.img_order_on, R.drawable.img_order_off, R.drawable.img_order_slipper);
        tv_order_type = (TextView) window.findViewById(R.id.tv_order_type);
        tv_order_time = (TextView) window.findViewById(R.id.tv_order_time);
        tv_order_des = (TextView) window.findViewById(R.id.tv_order_des);
        tv_reservationAddress = (TextView) window.findViewById(R.id.tv_reservationAddress);
        tv_destination = (TextView) window.findViewById(R.id.tv_destination);

        tv_order_type.setText(orderInfo.getServiceType());
        if (orderInfo.isSet_out_flag()) {
            tv_order_time.setText(getDateToString(Longs.tryParse(orderInfo.getSetouttime())));
        } else {
            tv_order_time.setText("现在");
        }
        tv_reservationAddress.setText(orderInfo.getReservationAddress());
        tv_destination.setText(orderInfo.getDestination());
        //timeOut
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
            dismiss();
        }
    }

    private void speak(String content) {
        if (tipHelper == null) {
            tipHelper = new TipHelper(activity);
        }
        if (tipHelper != null) {
            tipHelper.speak(content);
        }
    }

    public String getDateToString(long time) {
        Date d = new Date(time);
        SimpleDateFormat sf = new SimpleDateFormat("MM月dd日 HH时mm分");
        return sf.format(d);
    }

    public boolean isShowing() {
        return dialog.isShowing();
    }

    public void dismiss() {
        if (speak_continue) {
            speak_continue = false;
            if (tipHelper != null) {
                tipHelper.stopSpeak();
            }
            if (dialogHandler.hasMessages(SPEAK)) {
                dialogHandler.removeMessages(SPEAK);
            }
            dialogHandler.removeCallbacksAndMessages(null);
            dialogHandler = null;
        }
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    /**
     * 根据起始点规划路径（显示路径图）
     * //
     */
//    public void routeSearch(boolean showRoute) {
//        LatLonPoint start = new LatLonPoint(mAMapLocation.getLatitude(), mAMapLocation.getLongitude());
//        LatLonPoint end = new LatLonPoint(orderInfo.getStartLatitude(), orderInfo.getStartLongitude());
//        myRouteSearchListener.setShowRoute(showRoute);
//        final RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(start, end);
//        // 第一个参数表示路径规划的起点和终点，第二个参数表示驾车模式，第三个参数表示途经点，第四个参数表示避让区域，第五个参数表示避让道路
//        RouteSearch.DriveRouteQuery routeQuery = new RouteSearch.DriveRouteQuery(fromAndTo, RouteSearch.DrivingDefault, null, null, "");
//        mRouteSearch.calculateDriveRouteAsyn(routeQuery);// 异步路径规划驾车模式查询
//    }
//
//    private RouteSearch mRouteSearch;
//    private MyRouteSearchListener myRouteSearchListener;
//    private DrivePath mDrivePath;
//
//    public void setRouteSearchListner() {
//        mRouteSearch = new RouteSearch(context);
//        myRouteSearchListener = new MyRouteSearchListener(aMap, context);
//        mRouteSearch.setRouteSearchListener(myRouteSearchListener);
//        myRouteSearchListener.getDrivePathListener(new MyRouteSearchListener.MyDrivePathListener() {
//            @Override
//            public void getDrivePath(DrivePath drivePath) {
//                mDrivePath = drivePath;
//                if (mDrivePath != null) {//根据路径获取里程数等
//                    Log.i(TAG, "getDrivePath: " + "预计行程花费 " + AMapUtil.getFriendlyTime((int) drivePath.getDuration())
//                            + " 总距离：" + AMapUtil.getKiloLength(drivePath.getDistance()) + "千米");
//                    tv_order_des.setText(Html.fromHtml("距离客户 <big><font color='#Fe8a03'> " + AMapUtil.getKiloLength(drivePath.getDistance()) + "千米</font></big>,大约需要 <big><font color='#Fe8a03'>" + AMapUtil.getFriendlyTime((int) drivePath.getDuration()) + "</font></big>"));
//                }
//            }
//        });
//    }

    public interface OrderInterface {
        void take(boolean take);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_cancel:
                dismiss();
                break;
        }
    }
}
