package com.delelong.diandiandriver.traver.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.delelong.diandiandriver.R;
import com.delelong.diandiandriver.base.adapter.BaseListAdapter;
import com.delelong.diandiandriver.base.bean.BaseHttpMsg;
import com.delelong.diandiandriver.base.common.utils.NumberPickerUtils;
import com.delelong.diandiandriver.base.common.utils.SnackbarUtil;
import com.delelong.diandiandriver.base.fragment.BasePageFragment;
import com.delelong.diandiandriver.base.params.BasePageParams;
import com.delelong.diandiandriver.base.presenter.BasePagePresenter;
import com.delelong.diandiandriver.bean.AMapCityBean;
import com.delelong.diandiandriver.traver.activity.TraverActivity;
import com.delelong.diandiandriver.traver.adapter.ZhuanXianListAdapter;
import com.delelong.diandiandriver.traver.bean.TraverBean;
import com.delelong.diandiandriver.traver.params.TraverParams;
import com.delelong.diandiandriver.traver.params.ZhuanXianPeerParams;
import com.delelong.diandiandriver.traver.presenter.ZhuanXianPeerPresenter;
import com.delelong.diandiandriver.traver.presenter.ZhuanXianPresenter;
import com.delelong.diandiandriver.traver.view.ITraverView;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by Administrator on 2017/2/26.
 */

public class ZhuanXianPageFragment extends BasePageFragment implements ITraverView {
    private static final int CONFIRM_TRAVER = 1;
    private int mType = TraverActivity.TRAVER_TYPE_SHINEI;
    private TraverActivity traverActivity;
    private AMapCityBean cityBean;

    ZhuanXianPresenter presenter;
    ZhuanXianListAdapter zhuanXianAdapter;
    ZhuanXianPeerPresenter peerPresenter;

    public static ZhuanXianPageFragment newInstance(int type) {
        Bundle args = new Bundle();
        ZhuanXianPageFragment fragment = new ZhuanXianPageFragment();
        args.putInt("type", type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onFragmentStart() {
        traverActivity = (TraverActivity) mActivity;
        cityBean = traverActivity.getCityBean();
        mType = getArguments().getInt("type");
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
        zhuanXianAdapter = new ZhuanXianListAdapter();
        zhuanXianAdapter.setOnItemClickListener(new BaseListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position, Object object) {
                final TraverBean traver = (TraverBean) object;
                AlertDialog.Builder peerBuilder = new AlertDialog.Builder(mActivity)
                        .setTitle("是否确认选择该路线？")
                        .setCancelable(false)//点击返回键或对话框外部时是否消失，默认为true
                        .setNegativeButton("取消", null)//
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                NumberPickerUtils pickerUtils = new NumberPickerUtils(mActivity);
                                pickerUtils.numPickDialog(getResources().getStringArray(R.array.peopleNum));
                                pickerUtils.setUnit("座");
                                pickerUtils.setTitle("座位数选择");
                                pickerUtils.setOnNumPickListener(new NumberPickerUtils.OnNumPickListener() {
                                    @Override
                                    public void onNumPicked(int position, String str) {
                                        ZhuanXianPeerParams peerParams = new ZhuanXianPeerParams(traver.getId(), new BigDecimal(position + 1));
                                        if (peerPresenter == null) {
                                            peerPresenter = new ZhuanXianPeerPresenter(ZhuanXianPageFragment.this);
                                        }
                                        peerPresenter.accessServer(peerParams);
                                    }
                                });
                            }
                        });
                peerBuilder.create().show();
            }
        });
        return zhuanXianAdapter;
    }

    @Override
    public BasePagePresenter setPresenter() {
        presenter = new ZhuanXianPresenter(this, TraverBean.class);
        return presenter;
    }

    @Override
    public BasePageParams setParams() {
        return new TraverParams(1, 10, new BigDecimal(2), new BigDecimal(mType), cityBean.getAdcode(), null);
    }

    @Override
    public void showTraver(List<TraverBean> traverBeanList) {
        setData(traverBeanList);
    }

    @Override
    public void showSucceed(BaseHttpMsg httpMsg) {
        if (peerPresenter != null) {
            if (httpMsg.getApi().equals(peerPresenter.getModel().getApiInterface())) {
                SnackbarUtil.LongSnackbar(baseRefreshLayout, httpMsg.getMsg()).show();
            }
        }
    }
}
