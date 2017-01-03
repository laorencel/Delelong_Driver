package com.delelong.diandiandriver.menuActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.delelong.diandiandriver.BaseActivity;
import com.delelong.diandiandriver.R;
import com.delelong.diandiandriver.bean.MyHistoryOrderInfo;
import com.delelong.diandiandriver.bean.OrderInfo;
import com.delelong.diandiandriver.bean.Str;
import com.delelong.diandiandriver.dialog.MyDialogUtils;
import com.delelong.diandiandriver.http.MyAsyncHttpUtils;
import com.delelong.diandiandriver.http.MyHttpHelper;
import com.delelong.diandiandriver.http.MyProgTextHttpResponseHandler;
import com.delelong.diandiandriver.listener.MyHttpDataListener;
import com.delelong.diandiandriver.order.MyOrderActivity;
import com.delelong.diandiandriver.utils.ToastUtil;
import com.loopj.android.http.RequestParams;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cz.msebera.android.httpclient.Header;


/**
 * Created by Administrator on 2016/10/29.
 */

public class MyReservationOrderActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private static final String TAG = "BAIDUMAPFORTEST";
    private static final int ORDER = 11;
    ExecutorService threadPool = Executors.newFixedThreadPool(3);
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case ORDER:
                    int orderId = (int) msg.obj;
                    getReservationOrder(orderId);
                    break;
            }
        }
    };
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
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_reservation_order);
        initActionBar();
        setHistoryAdapter();
    }

    MyHttpHelper mHttpHelper;
    List<MyHistoryOrderInfo> mHistoryOrderInfos;
    MyHistoryOrderAdapter myHistoryOrderAdapter;

    private void setHistoryAdapter() {
        mHistoryOrderInfos = null;
        MyAsyncHttpUtils.post(Str.URL_GET_RESERVATION_ORDER, new MyProgTextHttpResponseHandler(this) {
            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                Log.i(TAG, "onFailure:setHistoryAdapter: "+s);
            }

            @Override
            public void onSuccess(int i, Header[] headers, String s) {
                Log.i(TAG, "onSuccess:setHistoryAdapter: "+s);
                if (mHttpHelper==null){
                    mHttpHelper = new MyHttpHelper(MyReservationOrderActivity.this);
                }
                mHistoryOrderInfos = mHttpHelper.getHistoryOrderInfoByJson(s, new MyHttpDataListener() {
                    @Override
                    public void toLogin() {
                        ToastUtil.show(MyReservationOrderActivity.this, "未登陆，请重新登陆");
                    }

                    @Override
                    public void onError(Object object) {
                        ToastUtil.show(MyReservationOrderActivity.this, object.toString());
                    }
                });
                if (mHistoryOrderInfos == null || mHistoryOrderInfos.size() == 0) {
                    Log.i(TAG, "setHistoryAdapter: null");
                    tv_no_info.setVisibility(View.VISIBLE);
                    lv_history_order.setVisibility(View.GONE);
                    return;
                }
                tv_no_info.setVisibility(View.GONE);
                lv_history_order.setVisibility(View.VISIBLE);
//        if (myHistoryOrderAdapter == null) {
                myHistoryOrderAdapter = new MyHistoryOrderAdapter(MyReservationOrderActivity.this, mHistoryOrderInfos);
                lv_history_order.setAdapter(myHistoryOrderAdapter);
//        } else {
                myHistoryOrderAdapter.notifyDataSetChanged();
//        }
                lv_history_order.setOnItemClickListener(MyReservationOrderActivity.this);
            }
        });
//        mHistoryOrderInfos = myHttpUtils.getHistoryOrder(Str.URL_HISTORY_ORDER, type, serviceType);
    }

    TextView tv_no_info;
    ListView lv_history_order;
    ImageView arrow_back;

    private void initActionBar() {
        arrow_back = (ImageView) findViewById(R.id.arrow_back);
        arrow_back.setOnClickListener(this);
        tv_no_info = (TextView) findViewById(R.id.tv_no_info);
        lv_history_order = (ListView) findViewById(R.id.lv_history_order);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.arrow_back:
                finish();
                break;
        }
    }

    public static final int HANDLE_RESERVATION_ORDER = 10;
    MyDialogUtils myDialogUtils;

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mHistoryOrderInfos != null) {
            final MyHistoryOrderInfo historyOrderInfo = mHistoryOrderInfos.get(position);
            if (historyOrderInfo != null) {
                if (myDialogUtils == null) {
                    myDialogUtils = new MyDialogUtils(MyReservationOrderActivity.this);
                }
                if (!myDialogUtils.isShowing()) {
                    myDialogUtils.handleReservationOrder(HANDLE_RESERVATION_ORDER, new MyDialogUtils.MyDialogInterface() {
                        @Override
                        public void chooseDriverCar(int requestCode, int position) {

                        }

                        @Override
                        public void sure(int requestCode, String arg0) {
                            if (requestCode == HANDLE_RESERVATION_ORDER) {
                                if (arg0.equalsIgnoreCase("1")) {//开始处理订单
                                    sendMsgToHandlerByExecutor(ORDER,historyOrderInfo.getOrderId());
                                } else if (arg0.equalsIgnoreCase("2")) {//拨打客户电话
                                    callPhone(historyOrderInfo.getPhone());
                                }
                            }
                        }

                        @Override
                        public void sure(int requestCode, Object object) {

                        }
                    });
                }
            }
        }
    }
    private void getReservationOrder(int orderId){
        if (mHttpHelper==null){
            mHttpHelper = new MyHttpHelper(MyReservationOrderActivity.this);
        }
        RequestParams params = mHttpHelper.getReservationOrderParams(orderId);
        MyAsyncHttpUtils.post(Str.URL_RESERVATION_ORDER,params, new MyProgTextHttpResponseHandler(MyReservationOrderActivity.this) {
            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                super.onFailure(i, headers, s, throwable);
                Log.i(TAG, "onFailure: "+s);
            }

            @Override
            public void onSuccess(int i, Header[] headers, String s) {
                super.onSuccess(i, headers, s);
                Log.i(TAG, "onSuccess:getReservationOrder: "+s);
               OrderInfo orderInfo = mHttpHelper.getCreatedOrderInfosByJson(s, new MyHttpDataListener() {
                    @Override
                    public void toLogin() {
                        ToastUtil.show(MyReservationOrderActivity.this, "未登陆，请重新登陆");
                    }

                    @Override
                    public void onError(Object object) {

                    }
                });
                if (orderInfo!=null){
                    toMyOrderAcitivity(orderInfo);
                    finish();
                }
            }
        });
    }
    private void toMyOrderAcitivity(OrderInfo orderInfo_0) {
        Bundle bundle = new Bundle();
        if (orderInfo_0 != null) {
            bundle.putSerializable("orderInfo_0", orderInfo_0);
        }
        Intent intent = new Intent(this, MyOrderActivity.class);
        intent.putExtra("bundle", bundle);
        startActivity(intent);
    }
    class MyHistoryOrderAdapter extends BaseAdapter {

        Context context;
        List<MyHistoryOrderInfo> historyOrderInfos;

        MyHistoryOrderAdapter(Context context, List<MyHistoryOrderInfo> mHistoryOrderInfos) {
            this.context = context;
            historyOrderInfos = mHistoryOrderInfos;
        }

        @Override
        public int getCount() {
            if (historyOrderInfos == null) {
                return 0;
            } else {
                return historyOrderInfos.size();
            }
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            MyHistoryOrderInfo historyOrderInfo = historyOrderInfos.get(position);
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.lv_reservation_order, null);
                holder = new ViewHolder();
                holder.tv_orderNo = (TextView) convertView.findViewById(R.id.tv_orderNo);
                holder.tv_real_pay = (TextView) convertView.findViewById(R.id.tv_real_pay);
                holder.tv_create_time = (TextView) convertView.findViewById(R.id.tv_create_time);
                holder.tv_reservation_address = (TextView) convertView.findViewById(R.id.tv_reservation_address);
                holder.tv_destination = (TextView) convertView.findViewById(R.id.tv_destination);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            if (historyOrderInfo == null) {
                return convertView;
            }
            holder.tv_orderNo.setText(historyOrderInfo.getOrderNo());
//            holder.tv_real_pay.setText("￥ " + historyOrderInfo.getReal_pay());
            holder.tv_create_time.setText("预约时间：" + historyOrderInfo.getSetouttime());
            holder.tv_reservation_address.setText("从 " + historyOrderInfo.getReservation_address());
            holder.tv_destination.setText("到 " + historyOrderInfo.getDestination());

            return convertView;
        }

        class ViewHolder {
            TextView tv_orderNo, tv_real_pay, tv_create_time, tv_reservation_address, tv_destination;
        }
    }

}
