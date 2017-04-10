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
import com.delelong.diandiandriver.base.params.BasePageParams;
import com.delelong.diandiandriver.base.presenter.BaseListPresenter;
import com.delelong.diandiandriver.bean.Str;
import com.delelong.diandiandriver.navi.NativeDialog;
import com.delelong.diandiandriver.traver.adapter.ExecutionCustomerListAdapter;
import com.delelong.diandiandriver.traver.bean.CustomerLocationBean;
import com.delelong.diandiandriver.traver.bean.ExecutionTraverBean;
import com.delelong.diandiandriver.traver.bean.TraverCustomerBean;
import com.delelong.diandiandriver.traver.params.CustomerLocationParams;
import com.delelong.diandiandriver.traver.params.TraverCustomerListParams;
import com.delelong.diandiandriver.traver.presenter.CustomerLocationPresenter;
import com.delelong.diandiandriver.traver.presenter.TraverCustomerListPresenter;
import com.delelong.diandiandriver.traver.view.ICustomerLocationView;
import com.delelong.diandiandriver.traver.view.IExecutionCustomerView;

import java.math.BigDecimal;
import java.util.List;

import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;

/**
 * Created by Administrator on 2017/3/16.
 */

public class TraverCustomerListFragment extends BaseListFragment implements IExecutionCustomerView, ICustomerLocationView {
    ExecutionTraverBean traverBean;
    AMapLocation mAMapLocation;
    TraverCustomerListPresenter customerListPresenter;
    ExecutionCustomerListAdapter customerListAdapter;
    CustomerLocationPresenter locationPresenter;

    public static TraverCustomerListFragment newInstance(ExecutionTraverBean traverBean) {
        Bundle args = new Bundle();
        TraverCustomerListFragment fragment = new TraverCustomerListFragment();
        args.putSerializable("traverBean", traverBean);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onFragmentStart() {
        traverBean = (ExecutionTraverBean) getArguments().getSerializable("traverBean");
        Log.i(Str.TAG, "onFragmentStart: " + traverBean);
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
        customerListAdapter = new ExecutionCustomerListAdapter();
        customerListAdapter.setOnChildItemClickListener(new ExecutionCustomerListAdapter.OnChildItemClickListener() {
            @Override
            public void onChildItemClick(View view, int position, BigDecimal amountType, Object bean) {
                TraverCustomerBean customerBean = (TraverCustomerBean) bean;
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
                                locationPresenter = new CustomerLocationPresenter(TraverCustomerListFragment.this, CustomerLocationBean.class, start);
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
        return customerListPresenter = new TraverCustomerListPresenter(this, TraverCustomerBean.class);
    }

    @Override
    public BasePageParams setParams() {
        return new TraverCustomerListParams(traverBean.getId(), traverBean.getPd());
    }

    @Override
    public void showExecutionCustomer(List<TraverCustomerBean> customerBeanList) {
        setData(customerBeanList);
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
