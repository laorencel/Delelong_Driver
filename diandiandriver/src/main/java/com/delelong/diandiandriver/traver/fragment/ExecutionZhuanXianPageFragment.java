package com.delelong.diandiandriver.traver.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.amap.api.maps.model.LatLng;
import com.delelong.diandiandriver.R;
import com.delelong.diandiandriver.base.adapter.BaseListAdapter;
import com.delelong.diandiandriver.base.bean.BaseHttpMsg;
import com.delelong.diandiandriver.base.common.utils.SnackbarUtil;
import com.delelong.diandiandriver.base.fragment.BasePageFragment;
import com.delelong.diandiandriver.base.params.BasePageParams;
import com.delelong.diandiandriver.base.presenter.BasePagePresenter;
import com.delelong.diandiandriver.bean.Str;
import com.delelong.diandiandriver.navi.Location;
import com.delelong.diandiandriver.navi.NativeDialog;
import com.delelong.diandiandriver.traver.activity.ZhuanXianCustomerActivity;
import com.delelong.diandiandriver.traver.adapter.ExecutionZhuanXianListAdapter;
import com.delelong.diandiandriver.traver.bean.ExecutionZhuanXianBean;
import com.delelong.diandiandriver.traver.params.ArriveZhuanXianParams;
import com.delelong.diandiandriver.traver.params.CancelTraverParams;
import com.delelong.diandiandriver.traver.params.StartZhuanXianParams;
import com.delelong.diandiandriver.traver.presenter.ArriveZhuanXianPresenter;
import com.delelong.diandiandriver.traver.presenter.CancelTraverPresenter;
import com.delelong.diandiandriver.traver.presenter.ExecutionZhuanXianPresenter;
import com.delelong.diandiandriver.traver.presenter.StartZhuanXianPresenter;
import com.delelong.diandiandriver.traver.view.IExecutionZhuanXianView;

import java.math.BigDecimal;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by Administrator on 2017/3/14.
 */

public class ExecutionZhuanXianPageFragment extends BasePageFragment implements IExecutionZhuanXianView {
    private ExecutionZhuanXianPresenter executionPresenter;
    private ExecutionZhuanXianListAdapter traverListAdapter;
    private CancelTraverPresenter cancelPresenter;
    private StartZhuanXianPresenter startPresenter;
    private ArriveZhuanXianPresenter arrivePresenter;
    private int removeItemPosition;

    public static ExecutionZhuanXianPageFragment newInstance() {
        ExecutionZhuanXianPageFragment fragment = new ExecutionZhuanXianPageFragment();
        return fragment;
    }

    @Override
    public void onFragmentStart() {
    }

    @Override
    public void load() {
        if (!hasLoad) {
            onRefresh();
            hasLoad = !hasLoad;
        }
    }

    @Override
    public void showSucceed(BaseHttpMsg httpMsg) {
        if (cancelPresenter != null) {
            if (httpMsg.getApi().equals(cancelPresenter.getModel().getApiInterface())) {
                SnackbarUtil.LongSnackbar(baseRefreshLayout, httpMsg.getMsg()).show();
//                traverListAdapter.removeItem(removeItemPosition);
                onRefresh();
            }
        }
        if (startPresenter != null) {
            if (httpMsg.getApi().equals(startPresenter.getModel().getApiInterface())) {
                SnackbarUtil.LongSnackbar(baseRefreshLayout, httpMsg.getMsg()).show();
            }
        }
        if (arrivePresenter != null) {
            if (httpMsg.getApi().equals(arrivePresenter.getModel().getApiInterface())) {
                SnackbarUtil.LongSnackbar(baseRefreshLayout, httpMsg.getMsg()).show();
            }
        }
    }

    @NonNull
    @Override
    public RecyclerView.LayoutManager setLayoutManager() {
        return new LinearLayoutManager(mActivity);
    }

    @Override
    public BaseListAdapter setAdapter() {
        traverListAdapter = new ExecutionZhuanXianListAdapter();
        traverListAdapter.setOnChildItemClickListener(new ExecutionZhuanXianListAdapter.OnChildItemClickListener() {
            @Override
            public void onChildItemClick(View view, final int position, BigDecimal amountType, Object bean) {
                final ExecutionZhuanXianBean traverBean = (ExecutionZhuanXianBean) bean;
                Log.i(Str.TAG, "traverAdapter:onChildItemClick: " + position + "//" + amountType + traverBean.toString());
                switch (view.getId()) {
                    case R.id.btn_cancel://取消订单
                        AlertDialog.Builder cancelBuilder = new AlertDialog.Builder(mActivity)
                                .setTitle("确认取消行程？")
                                .setCancelable(false)//点击返回键或对话框外部时是否消失，默认为true
                                .setNegativeButton("取消", null)//
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        //参数：dialog-The dialog that received the click.；which-The button that was clicked
                                        removeItemPosition = position;
                                        if (cancelPresenter == null) {
                                            cancelPresenter = new CancelTraverPresenter(ExecutionZhuanXianPageFragment.this);
                                        }
                                        if (traverBean.getId() != null) {
                                            cancelPresenter.getModel().setApiInterface(Str.URL_ZHUANXIAN_CANCEL);
                                            cancelPresenter.accessServer(new CancelTraverParams(traverBean.getId(), null));
                                        }
                                    }
                                });
                        cancelBuilder.create().show();
                        Log.i(Str.TAG, "onChildItemClick: tv_cancel" + traverBean.getId());
                        break;
                }
                //专线状态(1.专线可用 0.专线禁用)
                if (traverBean.getLineStatus().compareTo(new BigDecimal(1)) == 0) {
                    switch (view.getId()) {
                        case R.id.ly_content:
                            //进入乘客界面
                            EventBus.getDefault().postSticky(traverBean);
                            startActivity(new Intent(mContext, ZhuanXianCustomerActivity.class));
                            break;
                        case R.id.ly_nav:
                            LatLng start = null, end = null;
                            Location nav_start = null, nav_end = null;
                            if (traverBean.getStartLatitude() != null && traverBean.getStartLongitude() != null) {
//                                nav_start = new Location(traverBean.getStartLatitude().doubleValue(), traverBean.getStartLongitude().doubleValue());
                                start = new LatLng(traverBean.getStartLatitude().doubleValue(), traverBean.getStartLongitude().doubleValue());
                            }
                            if (traverBean.getEndLatitude() != null && traverBean.getEndLongitude() != null) {
//                                nav_end = new Location(traverBean.getEndLatitude().doubleValue(), traverBean.getEndLongitude().doubleValue());
                                end = new LatLng(traverBean.getEndLatitude().doubleValue(), traverBean.getEndLongitude().doubleValue());
                            }
                            if (start != null && end != null) {
                                NativeDialog msgDialog = new NativeDialog(mActivity, start, end);
                                msgDialog.show();
                            } else {
                                SnackbarUtil.LongSnackbar(baseRefreshLayout, "暂未获取到位置信息").show();
                            }
                            break;
                        case R.id.ly_start:
                            AlertDialog.Builder startBuilder = new AlertDialog.Builder(mActivity)
                                    .setTitle("确认出发？")
                                    .setCancelable(false)//点击返回键或对话框外部时是否消失，默认为true
                                    .setNegativeButton("取消", null)//
                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            //参数：dialog-The dialog that received the click.；which-The button that was clicked
                                            if (startPresenter == null) {
                                                startPresenter = new StartZhuanXianPresenter(ExecutionZhuanXianPageFragment.this);
                                            }
                                            startPresenter.accessServer(new StartZhuanXianParams(traverBean.getId(), traverBean.getOrderStatus()));
                                        }
                                    });
                            startBuilder.create().show();
                            break;
                        case R.id.ly_end:
                            AlertDialog.Builder endBuilder = new AlertDialog.Builder(mActivity)
                                    .setTitle("确认结束行程？")
                                    .setCancelable(false)//点击返回键或对话框外部时是否消失，默认为true
                                    .setNegativeButton("取消", null)//
                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            //参数：dialog-The dialog that received the click.；which-The button that was clicked
                                            if (arrivePresenter == null) {
                                                arrivePresenter = new ArriveZhuanXianPresenter(ExecutionZhuanXianPageFragment.this);
                                            }
                                            arrivePresenter.accessServer(
                                                    new ArriveZhuanXianParams(traverBean.getId(), traverBean.getOrderStatus(), new BigDecimal(-1)));
                                        }
                                    });
                            endBuilder.create().show();
                            break;
                        default:
                            break;
                    }
                } else {
                    switch (view.getId()) {
                        case R.id.btn_cancel:
                            //取消订单
                            break;
                        default:
                            SnackbarUtil.LongSnackbar(baseRefreshLayout, "专线不可用！").show();
                            break;
                    }
                }
            }
        });

        return traverListAdapter;
    }

    @Override
    public BasePagePresenter setPresenter() {
        executionPresenter = new ExecutionZhuanXianPresenter(ExecutionZhuanXianPageFragment.this, ExecutionZhuanXianBean.class);
        return executionPresenter;
    }

    @Override
    public BasePageParams setParams() {
        return new BasePageParams(1, 5);
    }

    @Override
    public void showExecutionTraver(List<ExecutionZhuanXianBean> traverBeanList) {
        setData(traverBeanList);
    }
}
