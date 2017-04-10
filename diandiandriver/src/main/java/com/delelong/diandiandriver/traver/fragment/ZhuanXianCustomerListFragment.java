package com.delelong.diandiandriver.traver.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.amap.api.location.AMapLocation;
import com.amap.api.maps.model.LatLng;
import com.delelong.diandiandriver.R;
import com.delelong.diandiandriver.base.adapter.BaseListAdapter;
import com.delelong.diandiandriver.base.bean.BaseHttpMsg;
import com.delelong.diandiandriver.base.common.utils.ActivityUtils;
import com.delelong.diandiandriver.base.common.utils.PermissionUtils;
import com.delelong.diandiandriver.base.common.utils.SnackbarUtil;
import com.delelong.diandiandriver.base.fragment.BaseListFragment;
import com.delelong.diandiandriver.base.params.BaseParams;
import com.delelong.diandiandriver.base.presenter.BaseListPresenter;
import com.delelong.diandiandriver.bean.Str;
import com.delelong.diandiandriver.navi.NativeDialog;
import com.delelong.diandiandriver.traver.adapter.ExecutionZhuanXianCustomerListAdapter;
import com.delelong.diandiandriver.traver.bean.CustomerLocationBean;
import com.delelong.diandiandriver.traver.bean.ExecutionZhuanXianBean;
import com.delelong.diandiandriver.traver.bean.ZhuanXianCustomerBean;
import com.delelong.diandiandriver.traver.params.CustomerLocationParams;
import com.delelong.diandiandriver.traver.presenter.CustomerLocationPresenter;
import com.delelong.diandiandriver.traver.presenter.ZhuanXianCustomerListPresenter;
import com.delelong.diandiandriver.traver.view.ICustomerLocationView;
import com.delelong.diandiandriver.traver.view.IExecutionZhuanXianCustomerView;

import java.math.BigDecimal;
import java.util.List;

import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;

/**
 * Created by Administrator on 2017/3/18.
 */

public class ZhuanXianCustomerListFragment extends BaseListFragment implements IExecutionZhuanXianCustomerView, ICustomerLocationView {
    ExecutionZhuanXianBean traverBean;
    AMapLocation mAMapLocation;
    ZhuanXianCustomerListPresenter customerListPresenter;
    ExecutionZhuanXianCustomerListAdapter customerListAdapter;
    CustomerLocationPresenter locationPresenter;

    public static ZhuanXianCustomerListFragment newInstance(ExecutionZhuanXianBean traverBean) {
        Bundle args = new Bundle();
        ZhuanXianCustomerListFragment fragment = new ZhuanXianCustomerListFragment();
        args.putSerializable("traverBean", traverBean);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onFragmentStart() {
        traverBean = (ExecutionZhuanXianBean) getArguments().getSerializable("traverBean");
        Log.i(Str.TAG, "ZhuanXianCustomerListFragment:onFragmentStart: " + traverBean);
    }

    @Override
    public void load() {
        if (!hasLoad) {
            onRefresh();
            hasLoad = !hasLoad;
        }
    }

    @Subscribe(threadMode = ThreadMode.MainThread, sticky = true)
    public void getAMapLocation(AMapLocation aMapLocation) {
        mAMapLocation = aMapLocation;
    }

    @Override
    public void showSucceed(BaseHttpMsg httpMsg) {

    }

    @NonNull
    @Override
    public RecyclerView.LayoutManager setLayoutManager() {
        return new LinearLayoutManager(mContext);
    }

    @Override
    public BaseListAdapter setAdapter() {
        customerListAdapter = new ExecutionZhuanXianCustomerListAdapter();
        customerListAdapter.setOnChildItemClickListener(new ExecutionZhuanXianCustomerListAdapter.OnChildItemClickListener() {
            @Override
            public void onChildItemClick(View view, int position, BigDecimal amountType, Object bean) {
                ZhuanXianCustomerBean customerBean = (ZhuanXianCustomerBean) bean;
                Log.i(Str.TAG, "onChildItemClick: " + customerBean);
                switch (view.getId()) {
                    case R.id.phone:
                        if (customerBean.getPhone() != null) {
                            PermissionUtils.permissionCallPhone(mActivity);
                            ActivityUtils.callPhone(mActivity, customerBean.getPhone());
                        }
                        break;
                    case R.id.tv_nav:
                        if (customerBean.getId() != null) {
                            if (locationPresenter == null) {
                                LatLng start = null;
                                if (mAMapLocation != null) {
                                    start = new LatLng(mAMapLocation.getLatitude(), mAMapLocation.getLongitude());
                                }
                                locationPresenter = new CustomerLocationPresenter(
                                        ZhuanXianCustomerListFragment.this, CustomerLocationBean.class, start);
                            }
                            locationPresenter.accessServer(new CustomerLocationParams(customerBean.getId()));
                        }
                        break;
                }
            }
        });
        return customerListAdapter;
    }

    @Override
    public BaseListPresenter setPresenter() {
        customerListPresenter = new ZhuanXianCustomerListPresenter(ZhuanXianCustomerListFragment.this, ZhuanXianCustomerBean.class);
        return customerListPresenter;
    }

    @Override
    public BaseParams setParams() {
        return new BaseParams();
    }

    @Override
    public void showExecutionZhuanXianCustomer(List<ZhuanXianCustomerBean> traverBeanList) {
//        List<ZhuanXianCustomerBean> traverBeanLists = new ArrayList<>();
//        traverBeanLists.addAll(traverBeanList);
        setData(traverBeanList);
    }

    @Override
    public void setCustomerLocation(LatLng start, CustomerLocationBean customerLocation) {
        LatLng end = null;
        if (customerLocation.getLatitude() != null && customerLocation.getLongitude() != null) {
            end = new LatLng(customerLocation.getLatitude().doubleValue(), customerLocation.getLongitude().doubleValue());
        }
        if (end != null) {
            try {
                NativeDialog msgDialog = new NativeDialog(mActivity, start, end);
                msgDialog.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            SnackbarUtil.LongSnackbar(baseRefreshLayout, "暂未获取到位置信息").show();
        }
    }
}
