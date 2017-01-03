package com.delelong.diandiandriver.order;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.MapView;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.RouteSearch;
import com.delelong.diandiandriver.BaseActivity;
import com.delelong.diandiandriver.R;
import com.delelong.diandiandriver.bean.OrderInfo;
import com.delelong.diandiandriver.bean.Str;
import com.delelong.diandiandriver.listener.MyRouteSearchListener;
import com.delelong.diandiandriver.utils.AMapUtil;
import com.delelong.diandiandriver.utils.ToastUtil;

import static com.delelong.diandiandriver.utils.AMapUtil.getKiloLength;

/**
 * Created by Administrator on 2016/10/27.
 */

public class CreateOrderActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "BAIDUMAPFORTEST";
    Context context;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_order);
        initActionBar();
        setUpMap(savedInstanceState);
        initView();
        setRouteSearchListner();
        initMsg();
    }

    private MapView mMapView = null;
    private AMap aMap = null;

    private void setUpMap(Bundle savedInstanceState) {
        this.context = this;
        //获取地图控件引用
        mMapView = (MapView) findViewById(R.id.mapView);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，实现地图生命周期管理
        mMapView.onCreate(savedInstanceState);
        aMap = mMapView.getMap();
    }

    String city,poiName, poiAddr;
    String adcode = "340104";
    private void initMsg() {
        Bundle bundle = getIntent().getBundleExtra("bundle");
        if (bundle == null) {
            return;
        }
        city = bundle.getString("city");
        adcode = bundle.getString("adcode");
        poiName = bundle.getString("poiName");
        poiAddr = bundle.getString("poiAddr");
        double lati = bundle.getDouble("posi_lati");
        double longi = bundle.getDouble("posi_longi");
        mPositionPoiItem = new PoiItem("11", new LatLonPoint(lati, longi), poiName, poiAddr);
        mDestinationPoiItem = new PoiItem("12", new LatLonPoint(0, 0), "", "");
        if (mPositionPoiItem != null) {
            tv_position.setText(mPositionPoiItem.getTitle());
        }
    }

    TextView tv_time, tv_position, tv_destination;
    EditText edt_phone, edt_name;
    Button btn_confirm;

    private void initView() {
        tv_time = (TextView) findViewById(R.id.tv_time);
        tv_position = (TextView) findViewById(R.id.tv_position);
        tv_destination = (TextView) findViewById(R.id.tv_destination);

        edt_phone = (EditText) findViewById(R.id.edt_phone);
        edt_name = (EditText) findViewById(R.id.edt_name);
        btn_confirm = (Button) findViewById(R.id.btn_confirm);
        setListener();
    }

    private void setListener() {
        tv_time.setOnClickListener(this);
        tv_position.setOnClickListener(this);
        tv_destination.setOnClickListener(this);
        btn_confirm.setOnClickListener(this);
    }

    ImageButton arrow_back;

    private void initActionBar() {
        arrow_back = (ImageButton) findViewById(R.id.arrow_back);
        arrow_back.setOnClickListener(this);
    }

    String phone;
    String name;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.arrow_back:
                finish();
                break;
            case R.id.tv_time:
                break;
            case R.id.tv_position:
                Bundle bundle_posi = getIntentBundle("myPosition");
                intentActivityWithBundleForResult(this, ChooseAddrActivity.class, Str.REQUEST_CHOOSE_ADDR_POSITION, bundle_posi);
                break;
            case R.id.tv_destination:
                Bundle bundle_des = getIntentBundle("destination");
                intentActivityWithBundleForResult(this, ChooseAddrActivity.class, Str.REQUEST_CHOOSE_ADDR_DESTINATION, bundle_des);
                break;
            case R.id.btn_confirm:
                if (mPositionPoiItem == null || mDestinationPoiItem == null) {
                    ToastUtil.show(this, "请先设置起点");
                    return;
                }
                phone = edt_phone.getText().toString();
                name = edt_name.getText().toString();
                if (phone.length() != 11 || !phone.matches("[0-9]*")) {
                    ToastUtil.show(this, "手机号码不符合规则");
                    return;
                }
                /**
                 * no改为预算时间
                 *
                 */
                long duration = 0;
                float distance = 0;
                if (mDrivePath != null) {
                    duration = mDrivePath.getDuration() / 60;
                    distance = mDrivePath.getDistance() / 1000;
                }
                OrderInfo orderInfo = new OrderInfo(1, 1, phone, name, "",
                        "" + duration, "现在", "", "代驾", false, 0, distance, 0,
                        mPositionPoiItem.getLatLonPoint().getLatitude(),
                        mPositionPoiItem.getLatLonPoint().getLongitude(),
                        mDestinationPoiItem.getLatLonPoint().getLatitude(),
                        mDestinationPoiItem.getLatLonPoint().getLongitude(),
                        mPositionPoiItem.getTitle(), mDestinationPoiItem.getTitle(),
                        "司机下单");
                Bundle bundle = new Bundle();
                bundle.putSerializable("orderInfo", orderInfo);
                Intent intent = new Intent();
                intent.putExtra("bundle", bundle);
                setResult(Str.REQUEST_CREATE_ORDER, intent);
                finish();
                break;
        }
    }

    private Bundle getIntentBundle(String choose) {
        Bundle bundle = new Bundle();
        bundle.putString("choose", choose);
        bundle.putString("city", city);
        bundle.putString("adcode", adcode);
        return bundle;
    }

    PoiItem mPositionPoiItem, mDestinationPoiItem;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bundle bundle;
        switch (resultCode) {
            case Str.REQUEST_CHOOSE_ADDR_POSITION://起点
                bundle = data.getBundleExtra("bundle");
                if (bundle == null) {
                    return;
                }
                mPositionPoiItem = bundle.getParcelable("PoiInfo");
                tv_position.setText(mPositionPoiItem.getTitle());
                if (mDestinationPoiItem != null) {
                    LatLonPoint start = mPositionPoiItem.getLatLonPoint();
                    LatLonPoint end = mDestinationPoiItem.getLatLonPoint();
                    if (aMap != null) {
                        routeSearch(start, end, false);
                    }
                }
                break;
            case Str.REQUEST_CHOOSE_ADDR_DESTINATION:
                bundle = data.getBundleExtra("bundle");
                if (bundle == null) {
                    return;
                }
                mDestinationPoiItem = bundle.getParcelable("PoiInfo");
                tv_destination.setText(mDestinationPoiItem.getTitle());
                if (mPositionPoiItem != null) {
                    LatLonPoint start = mPositionPoiItem.getLatLonPoint();
                    LatLonPoint end = mDestinationPoiItem.getLatLonPoint();
                    if (aMap != null) {
                        routeSearch(start, end, false);
                    }
                }
                break;
        }
    }

    private RouteSearch mRouteSearch;
    private MyRouteSearchListener myRouteSearchListener;
    private DrivePath mDrivePath;

    public void routeSearch(LatLonPoint start, LatLonPoint end, boolean showRoute) {
        myRouteSearchListener.setShowRoute(showRoute);
        final RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(
                start, end);
        RouteSearch.DriveRouteQuery routeQuery = null;
        routeQuery = new RouteSearch.DriveRouteQuery(fromAndTo, RouteSearch.DrivingDefault, null, null, "");
        mRouteSearch.calculateDriveRouteAsyn(routeQuery);// 异步路径规划驾车模式查询
    }

    public void setRouteSearchListner() {
        mRouteSearch = new RouteSearch(this);
        myRouteSearchListener = new MyRouteSearchListener(aMap, this);
        mRouteSearch.setRouteSearchListener(myRouteSearchListener);
        myRouteSearchListener.getDrivePathListener(new MyRouteSearchListener.MyDrivePathListener() {
            @Override
            public void getDrivePath(DrivePath drivePath) {

                if (mDrivePath != null) {//根据路径获取里程数等
                    Log.i(TAG, "getDrivePath: " + "预计行程花费 " + AMapUtil.getFriendlyTime((int) drivePath.getDuration())//
                            + " 总距离：" + getKiloLength(drivePath.getDistance()) + "千米");
                    mDrivePath = drivePath;
                }
            }
        });
    }
}
