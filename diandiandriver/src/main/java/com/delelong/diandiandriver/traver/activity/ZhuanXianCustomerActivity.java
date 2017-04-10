package com.delelong.diandiandriver.traver.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.delelong.diandiandriver.R;
import com.delelong.diandiandriver.base.activity.MBaseActivity;
import com.delelong.diandiandriver.bean.Str;
import com.delelong.diandiandriver.traver.bean.ExecutionZhuanXianBean;
import com.delelong.diandiandriver.traver.fragment.ZhuanXianCustomerListFragment;

import java.math.BigDecimal;

import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;

/**
 * Created by Administrator on 2017/3/18.
 */

public class ZhuanXianCustomerActivity extends MBaseActivity {
    ExecutionZhuanXianBean traverBean;

    @Subscribe(threadMode = ThreadMode.MainThread, sticky = true)
    public void getTraver(ExecutionZhuanXianBean traverBean) {
        Log.i(Str.TAG, "TraverCustomerActivity:getTraver: " + traverBean);
        this.traverBean = traverBean;

        getSupportFragmentManager().beginTransaction().add(R.id.fl_content, ZhuanXianCustomerListFragment.newInstance(traverBean)).commit();
    }

    @Override
    public View addTitleView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return null;
    }

    @NonNull
    @Override
    public View addCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_zhuanxian_customer, null);
        initView(view);
        if (traverBean!=null){
            initMsg(traverBean);
        }
        return view;
    }

    TextView tv_time, tv_distance, tv_lineName, tv_memberCount;
    ImageButton btn_cancel;
    TextView tv_start_address, tv_end_address;
    LinearLayout ly_pinChe, ly_baoChe, ly_distance;
    TextView amount_pinChe, amount_baoChe;
    LinearLayout ly_nav, ly_start, ly_end;

    private void initView(View view) {
        tv_time = (TextView) view.findViewById(R.id.tv_time);
        tv_lineName = (TextView) view.findViewById(R.id.tv_lineName);
        tv_memberCount = (TextView) view.findViewById(R.id.tv_memberCount);
        btn_cancel = (ImageButton) view.findViewById(R.id.btn_cancel);
        tv_start_address = (TextView) view.findViewById(R.id.tv_start_address);
        tv_end_address = (TextView) view.findViewById(R.id.tv_end_address);
        ly_pinChe = (LinearLayout) view.findViewById(R.id.ly_pinChe);
        ly_baoChe = (LinearLayout) view.findViewById(R.id.ly_baoChe);
        ly_distance = (LinearLayout) view.findViewById(R.id.ly_distance);
        amount_pinChe = (TextView) view.findViewById(R.id.amount_pinChe);
        amount_baoChe = (TextView) view.findViewById(R.id.amount_baoChe);
        tv_distance = (TextView) view.findViewById(R.id.tv_distance);
        ly_nav = (LinearLayout) view.findViewById(R.id.ly_nav);
        ly_start = (LinearLayout) view.findViewById(R.id.ly_start);
        ly_end = (LinearLayout) view.findViewById(R.id.ly_end);
    }

    private void initMsg(ExecutionZhuanXianBean traverBean) {
        tv_start_address
                .setText(traverBean.getStartAddress());
        tv_end_address
                .setText(traverBean.getEndAddress());
        if (traverBean.getRequiredTime() != null && !traverBean.getRequiredTime().isEmpty()) {
            tv_time
                    .setText(Html.fromHtml("<font color='#Fe8a03'>" + traverBean.getRequiredTime() + "</font>"));
        } else {
            tv_time.setVisibility(View.INVISIBLE);
        }

        if (traverBean.getLineName() != null) {
            tv_lineName.setText(traverBean.getLineName().split("专线")[0]);
        } else {
            tv_lineName.setVisibility(View.INVISIBLE);
        }
        if (traverBean.getCount() != null) {
            tv_memberCount
                    .setText(Html.fromHtml("已接 <font color='#Fe8a03'><big>" + traverBean.getCount() + "</big></font> 人"));
        } else {
            tv_memberCount.setVisibility(View.INVISIBLE);
        }

        btn_cancel.setVisibility(View.GONE);
        if (traverBean.getPinCheAmount() != null && !traverBean.getPinCheAmount().equals("") && !traverBean.getPinCheAmount().equals("0")) {
            amount_pinChe
                    .setText(Html.fromHtml(traverBean.getPinCheAmount() + " 元"));
        } else {
            ly_pinChe.setVisibility(View.GONE);
        }
        if (traverBean.getBaoCheAmount() != null && !traverBean.getBaoCheAmount().equals("") && !traverBean.getBaoCheAmount().equals("0")) {
            amount_baoChe
                    .setText(Html.fromHtml(traverBean.getBaoCheAmount() + " 元"));
        } else {
            ly_baoChe.setVisibility(View.GONE);
        }
        if (traverBean.getDistance() != null && traverBean.getDistance().compareTo(new BigDecimal(0)) != 0) {
            tv_distance
                    .setText(Html.fromHtml("<small>约</small>" + " <font color='#Fe8a03'>" + traverBean.getDistance() + "</font> km"));
        } else {
            ly_distance.setVisibility(View.GONE);
        }
    }

    @Override
    public void onActivityStart() {
        setTitle("订单信息");
    }
}
