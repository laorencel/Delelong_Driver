package com.delelong.diandiandriver.traver.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.delelong.diandiandriver.R;
import com.delelong.diandiandriver.base.adapter.BaseListAdapter;
import com.delelong.diandiandriver.base.bean.BaseHttpMsg;
import com.delelong.diandiandriver.base.common.utils.ActivityUtils;
import com.delelong.diandiandriver.base.common.utils.PermissionUtils;
import com.delelong.diandiandriver.base.common.utils.SnackbarUtil;
import com.delelong.diandiandriver.base.fragment.BasePageFragment;
import com.delelong.diandiandriver.base.params.BasePageParams;
import com.delelong.diandiandriver.base.presenter.BasePagePresenter;
import com.delelong.diandiandriver.bean.AMapCityBean;
import com.delelong.diandiandriver.bean.Str;
import com.delelong.diandiandriver.traver.activity.TraverActivity;
import com.delelong.diandiandriver.traver.adapter.TraverListAdapter;
import com.delelong.diandiandriver.traver.bean.TraverBean;
import com.delelong.diandiandriver.traver.params.PeerParams;
import com.delelong.diandiandriver.traver.params.TraverParams;
import com.delelong.diandiandriver.traver.presenter.PeerPresenter;
import com.delelong.diandiandriver.traver.presenter.TraverPresenter;
import com.delelong.diandiandriver.traver.view.ITraverView;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by Administrator on 2017/2/26.
 */

public class TraverPageFragment extends BasePageFragment implements ITraverView {

    private static final int CONFIRM_TRAVER = 1;
    private int mType = TraverActivity.TRAVER_TYPE_SHINEI;
    private TraverActivity traverActivity;
    private AMapCityBean cityBean;

    TraverPresenter traverPresenter;
    TraverListAdapter traverAdapter;
    PeerPresenter peerPresenter;

    public static TraverPageFragment newInstance(int type) {
        Bundle args = new Bundle();
        TraverPageFragment fragment = new TraverPageFragment();
        args.putInt("type", type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onFragmentStart() {
        traverActivity = (TraverActivity) mActivity;
        cityBean = traverActivity.getCityBean();
        mType = getArguments().getInt("type");
        traverAdapter.setType(mType);
    }

    @Override
    public void load() {
        if (!hasLoad) {
            onRefresh();
            hasLoad = !hasLoad;
        }
    }

    @Override
    public void onRefresh() {
        super.onRefresh();
    }

    @NonNull
    @Override
    public RecyclerView.LayoutManager setLayoutManager() {
        return new LinearLayoutManager(mActivity);
    }

    @Override
    public BaseListAdapter setAdapter() {
        traverAdapter = new TraverListAdapter();
        traverAdapter.setOnChildItemClickListener(new TraverListAdapter.OnChildItemClickListener() {
            @Override
            public void onChildItemClick(View view, int position, final BigDecimal amountType, Object bean) {
                final TraverBean traver = (TraverBean) bean;
                switch (view.getId()) {
                    case R.id.phone:
                        Log.i(Str.TAG, "onChildItemClick: phone");
                        if (traver.getPhone() != null) {
                            PermissionUtils.permissionCallPhone(mActivity);
                            ActivityUtils.callPhone(mActivity, traver.getPhone());
                        }
                        break;
                    default:
                        AlertDialog.Builder peerBuilder = new AlertDialog.Builder(mActivity)
                                .setTitle("是否确认同行？")
                                .setCancelable(false)//点击返回键或对话框外部时是否消失，默认为true
                                .setNegativeButton("取消", null)//
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (peerPresenter == null) {
                                            peerPresenter = new PeerPresenter(TraverPageFragment.this);
                                        }
                                        peerPresenter.accessServer(new PeerParams(traver.getId(), traver.getMemberId(), amountType));
                                    }
                                });
                        peerBuilder.create().show();
                        break;
                }
                Log.i(Str.TAG, "traverAdapter:onChildItemClick: " + view.getId() + "//" + position + "//" + amountType + traver.toString() + traver.getId() + traver.getName() + traver.getPhone());
            }
        });
        return traverAdapter;
    }

    @Override
    public BasePagePresenter setPresenter() {
        traverPresenter = new TraverPresenter(this, TraverBean.class);
        return traverPresenter;
    }

    @Override
    public BasePageParams setParams() {
        return new TraverParams(1, 10, new BigDecimal(2), new BigDecimal(mType), cityBean.getAdcode(), null);
    }

    @Override
    public void showSucceed(BaseHttpMsg httpMsg) {
        if (peerPresenter != null) {
            if (httpMsg.getApi().equals(peerPresenter.getModel().getApiInterface())) {
                SnackbarUtil.LongSnackbar(baseRefreshLayout, httpMsg.getMsg()).show();
            }
        }
    }

    @Override
    public void showTraver(List<TraverBean> traverBeanList) {
        setData(traverBeanList);
    }
}
