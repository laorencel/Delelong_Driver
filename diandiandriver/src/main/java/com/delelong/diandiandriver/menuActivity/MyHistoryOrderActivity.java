package com.delelong.diandiandriver.menuActivity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.delelong.diandiandriver.BaseActivity;
import com.delelong.diandiandriver.R;
import com.delelong.diandiandriver.bean.DriverAmount;
import com.delelong.diandiandriver.bean.MyHistoryOrderInfo;
import com.delelong.diandiandriver.bean.Str;
import com.delelong.diandiandriver.http.MyAsyncHttpUtils;
import com.delelong.diandiandriver.http.MyHttpHelper;
import com.delelong.diandiandriver.http.MyProgTextHttpResponseHandler;
import com.delelong.diandiandriver.listener.MyHttpDataListener;
import com.delelong.diandiandriver.utils.ToastUtil;
import com.loopj.android.http.RequestParams;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;


/**
 * Created by Administrator on 2016/10/29.
 */

public class MyHistoryOrderActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "BAIDUMAPFORTEST";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_history_order);
        initActionBar();
        initView();
        initMsg();
    }

    //    MyHttpUtils myHttpUtils;
    MyHttpHelper mHttpHelper;
    DriverAmount mDriverAmount;
    List<MyHistoryOrderInfo> mHistoryOrderInfos;
    MyHistoryOrderAdapter myHistoryOrderAdapter;

    private void initMsg() {
//        myHttpUtils = new MyHttpUtils(this);
        mHttpHelper = new MyHttpHelper(this);
        MyAsyncHttpUtils.post(Str.URL_DRIVER_YE_AMOUNT, new MyProgTextHttpResponseHandler(this) {
            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {

            }

            @Override
            public void onSuccess(int i, Header[] headers, String s) {
                mDriverAmount = mHttpHelper.getDriverYeAmountByJson(s, null);
                if (mDriverAmount != null) {
                    tv_myBalance.setText(mDriverAmount.getYe() + " 元");
                }
            }
        });
//        mDriverAmount = myHttpUtils.getDriverYeAmount(Str.URL_DRIVER_YE_AMOUNT);

        setHistoryAdapter(timeType, serviceType);
    }

    /**
     * @param type        //日期类型 1:今天;2:昨天;3:上月;4:本月;
     * @param serviceType //serviceType	Number	服务类型就是大项
     */
    private void setHistoryAdapter(int type, int serviceType) {
        mHistoryOrderInfos = null;
        RequestParams params = mHttpHelper.getHistoryOrderParams(type, serviceType);
        MyAsyncHttpUtils.post(Str.URL_HISTORY_ORDER, params, new MyProgTextHttpResponseHandler(this) {
            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {

            }

            @Override
            public void onSuccess(int i, Header[] headers, String s) {
                mHistoryOrderInfos = mHttpHelper.getHistoryOrderInfoByJson(s, new MyHttpDataListener() {
                    @Override
                    public void toLogin() {
                        ToastUtil.show(MyHistoryOrderActivity.this,"未登陆，请重新登陆");
                    }

                    @Override
                    public void onError(Object object) {

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
                myHistoryOrderAdapter = new MyHistoryOrderAdapter(MyHistoryOrderActivity.this, mHistoryOrderInfos);
                lv_history_order.setAdapter(myHistoryOrderAdapter);
//        } else {
                myHistoryOrderAdapter.notifyDataSetChanged();
//        }
            }
        });
//        mHistoryOrderInfos = myHttpUtils.getHistoryOrder(Str.URL_HISTORY_ORDER, type, serviceType);
    }

    TextView tv_myBalance, tv_choose_date, tv_no_info;
    TextView tv_zhuanChe, tv_daiJia, tv_chuZuChe, tv_kuaiChe;
    ListView lv_history_order;

    private void initView() {
        tv_myBalance = (TextView) findViewById(R.id.tv_myBalance);
        tv_choose_date = (TextView) findViewById(R.id.tv_choose_date);

        tv_zhuanChe = (TextView) findViewById(R.id.tv_zhuanChe);
        tv_daiJia = (TextView) findViewById(R.id.tv_daiJia);
        tv_chuZuChe = (TextView) findViewById(R.id.tv_chuZuChe);
        tv_kuaiChe = (TextView) findViewById(R.id.tv_kuaiChe);
        tv_zhuanChe.setTextColor(Color.parseColor("#1BC47A"));

        lv_history_order = (ListView) findViewById(R.id.lv_history_order);
        tv_no_info = (TextView) findViewById(R.id.tv_no_info);
        tv_choose_date.setOnClickListener(this);
        tv_zhuanChe.setOnClickListener(this);
        tv_daiJia.setOnClickListener(this);
        tv_chuZuChe.setOnClickListener(this);
        tv_kuaiChe.setOnClickListener(this);
        setPopListView();
    }

    private android.widget.ListView listView;
    private MyPopListAdapter myPopListAdapter;
    private PopupWindow window;
    private List<String> list = new ArrayList<String>();

    private void setPopListView() {
        list.add("今天");
        list.add("昨天");
        list.add("上月");
        list.add("本月");
        listView = new android.widget.ListView(this);
        //隐藏滚动条
        listView.setVerticalScrollBarEnabled(false);
        myPopListAdapter = new MyPopListAdapter(this);
        listView.setAdapter(myPopListAdapter);
        //设置listview的点击事件
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i(TAG, "onItemClick: " + position);
                window.dismiss();
                timeType = position + 1;
                setHistoryAdapter(timeType, serviceType);
            }
        });
    }

    int timeType = 4;

    private void setPopWindow() {
        if (window == null) {
            //创建PopupWindow
            window = new PopupWindow(listView, tv_choose_date.getWidth() * 2, 300);
        }

        window.setFocusable(true);
        //设置背景图片
//        window.setBackgroundDrawable(new BitmapDrawable());
        //设置外部点击消失
        window.setOutsideTouchable(true);
        window.showAsDropDown(tv_choose_date, 0, 0);
    }

    ImageView arrow_back;

    private void initActionBar() {
        arrow_back = (ImageView) findViewById(R.id.arrow_back);
        arrow_back.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.arrow_back:
                finish();
                break;
            case R.id.tv_choose_date:
                setPopWindow();
                break;
            case R.id.tv_zhuanChe:
                changeSum(tv_zhuanChe);
                setHistoryAdapter(timeType, serviceType);
                break;
            case R.id.tv_daiJia:
                changeSum(tv_daiJia);
                setHistoryAdapter(timeType, serviceType);
                break;
            case R.id.tv_chuZuChe:
                changeSum(tv_chuZuChe);
                setHistoryAdapter(timeType, serviceType);
                break;
            case R.id.tv_kuaiChe:
                changeSum(tv_kuaiChe);
                setHistoryAdapter(timeType, serviceType);
                break;
        }
    }

    int serviceType = 1;

    private void changeSum(View view) {
        switch (view.getId()) {
            case R.id.tv_zhuanChe:
                serviceType = 1;
                tv_zhuanChe.setTextColor(Color.parseColor("#1BC47A"));
                tv_daiJia.setTextColor(Color.parseColor("#ffffff"));//白色
                tv_chuZuChe.setTextColor(Color.parseColor("#ffffff"));//绿色
                tv_kuaiChe.setTextColor(Color.parseColor("#ffffff"));//绿色
                break;
            case R.id.tv_daiJia:
                serviceType = 2;
                tv_zhuanChe.setTextColor(Color.parseColor("#ffffff"));
                tv_daiJia.setTextColor(Color.parseColor("#1BC47A"));//白色
                tv_chuZuChe.setTextColor(Color.parseColor("#ffffff"));//绿色
                tv_kuaiChe.setTextColor(Color.parseColor("#ffffff"));//绿色
                break;
            case R.id.tv_chuZuChe:
                serviceType = 3;
                tv_zhuanChe.setTextColor(Color.parseColor("#ffffff"));
                tv_daiJia.setTextColor(Color.parseColor("#ffffff"));//白色
                tv_chuZuChe.setTextColor(Color.parseColor("#1BC47A"));//绿色
                tv_kuaiChe.setTextColor(Color.parseColor("#ffffff"));//绿色
                break;
            case R.id.tv_kuaiChe:
                serviceType = 4;
                tv_zhuanChe.setTextColor(Color.parseColor("#ffffff"));
                tv_daiJia.setTextColor(Color.parseColor("#ffffff"));//白色
                tv_chuZuChe.setTextColor(Color.parseColor("#ffffff"));//绿色
                tv_kuaiChe.setTextColor(Color.parseColor("#1BC47A"));//绿色
                break;
        }
    }

    class MyHistoryOrderAdapter extends BaseAdapter {

        Context context;
        List<MyHistoryOrderInfo> historyOrderInfos;

        MyHistoryOrderAdapter(Context context, List<MyHistoryOrderInfo> mHistoryOrderInfos) {
            this.context = context;
            historyOrderInfos = mHistoryOrderInfos;
        }

        //        private int mChildCount = 0;
//
//        @Override
//        public void notifyDataSetChanged() {
//            mChildCount = getCount();
//            super.notifyDataSetChanged();
//        }
//
//        public int getItemPosition(Object object)   {
//            if ( mChildCount > 0) {
//                mChildCount --;
//                return POSITION_NONE;
//            }
//            return super.getItemPosition(object);
//        }
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
                convertView = LayoutInflater.from(context).inflate(R.layout.lv_history_order, null);
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
            holder.tv_real_pay.setText("￥ " + historyOrderInfo.getReal_pay());
            holder.tv_create_time.setText(historyOrderInfo.getCreate_time());
            holder.tv_reservation_address.setText("从 " + historyOrderInfo.getReservation_address());
            holder.tv_destination.setText("到 " + historyOrderInfo.getDestination());

            return convertView;
        }

        class ViewHolder {
            TextView tv_orderNo, tv_real_pay, tv_create_time, tv_reservation_address, tv_destination;
        }
    }

    class MyPopListAdapter extends BaseAdapter {
        Context context;

        MyPopListAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {

            return list.size();
        }

        @Override
        public Object getItem(int position) {

            return list.get(position);
        }

        @Override
        public long getItemId(int position) {

            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            popViewHolder holder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.lv_pop_history, null);
                holder = new popViewHolder();
                holder.tv_pop_history = (TextView) convertView.findViewById(R.id.tv_pop_history);
                convertView.setTag(holder);
            } else {
                holder = (popViewHolder) convertView.getTag();
            }

            holder.tv_pop_history.setText(list.get(position));
//            tv_pop_history.setOnClickListener(new View.OnClickListener() {
//                //删除事件
//                @Override
//                public void onClick(View v) {
//                    //更新adapter
//
//                        window.dismiss();
//                }
//            });
            return convertView;
        }

        class popViewHolder {
            TextView tv_pop_history;
        }

    }

}
