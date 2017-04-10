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
import com.delelong.diandiandriver.navi.NativeDialog;
import com.delelong.diandiandriver.traver.activity.TraverCustomerActivity;
import com.delelong.diandiandriver.traver.adapter.ExecutionTraverListAdapter;
import com.delelong.diandiandriver.traver.bean.ExecutionTraverBean;
import com.delelong.diandiandriver.traver.params.ArriveTraverParams;
import com.delelong.diandiandriver.traver.params.CancelTraverParams;
import com.delelong.diandiandriver.traver.params.ExecutionTraverParams;
import com.delelong.diandiandriver.traver.params.StartTraverParams;
import com.delelong.diandiandriver.traver.presenter.ArriveTraverPresenter;
import com.delelong.diandiandriver.traver.presenter.CancelTraverPresenter;
import com.delelong.diandiandriver.traver.presenter.ExecutionTraverPresenter;
import com.delelong.diandiandriver.traver.presenter.StartTraverPresenter;
import com.delelong.diandiandriver.traver.view.IExecutionTraverView;

import java.math.BigDecimal;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by Administrator on 2017/3/13.
 */

public class ExecutionTraverPageFragment extends BasePageFragment implements IExecutionTraverView {
    private ExecutionTraverPresenter traverPresenter;
    private ExecutionTraverListAdapter traverListAdapter;
    private CancelTraverPresenter cancelPresenter;
    private StartTraverPresenter startPresenter;
    private ArriveTraverPresenter arrivePresenter;
    private int removeItemPosition;

    public static ExecutionTraverPageFragment newInstance() {
        ExecutionTraverPageFragment fragment = new ExecutionTraverPageFragment();
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
        traverListAdapter = new ExecutionTraverListAdapter();
        traverListAdapter.setOnChildItemClickListener(new ExecutionTraverListAdapter.OnChildItemClickListener() {
            @Override
            public void onChildItemClick(View view, final int position, BigDecimal amountType, Object bean) {
                final ExecutionTraverBean traverBean = (ExecutionTraverBean) bean;
                Log.i(Str.TAG, "traverAdapter:onChildItemClick: " + position + "//" + amountType + traverBean.toString());
                switch (view.getId()) {
                    case R.id.ly_content://进入乘客列表
                        Log.i(Str.TAG, "onChildItemClick: ly_content");
                        if (traverBean.getPd() != null) {
                            EventBus.getDefault().postSticky(traverBean);
                            startActivity(new Intent(mContext, TraverCustomerActivity.class));
                        }
                        break;
                    case R.id.ly_nav://进入导航
                        Log.i(Str.TAG, "onChildItemClick: ly_nav");
                        LatLng start = null, end = null;
                        if (traverBean.getStartLatitude() != null && traverBean.getStartLongitude() != null) {
                            start = new LatLng(traverBean.getStartLatitude().doubleValue(), traverBean.getStartLongitude().doubleValue());
                        }
                        if (traverBean.getEndLatitude() != null && traverBean.getEndLongitude() != null) {
                            end = new LatLng(traverBean.getEndLatitude().doubleValue(), traverBean.getEndLongitude().doubleValue());
                        }
                        if (start != null && end != null) {
                            NativeDialog msgDialog = new NativeDialog(mActivity, start, end);
                            msgDialog.show();
                        } else {
                            SnackbarUtil.LongSnackbar(baseRefreshLayout, "暂未获取到位置信息").show();
                        }
                        break;
                    case R.id.ly_start://开始行程
                        Log.i(Str.TAG, "onChildItemClick: ly_start");
//                        AlertDialog.Builder startBuilder = new AlertDialog.Builder(mActivity,R.style.AlertDialogTheme)
                        AlertDialog.Builder startBuilder = new AlertDialog.Builder(mActivity)
                                .setTitle("确认出发？")
                                .setCancelable(false)//点击返回键或对话框外部时是否消失，默认为true
                                .setNegativeButton("取消", null)//
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        //参数：dialog-The dialog that received the click.；which-The button that was clicked
                                        if (startPresenter == null) {
                                            startPresenter = new StartTraverPresenter(ExecutionTraverPageFragment.this);
                                        }
                                        startPresenter.accessServer(new StartTraverParams(traverBean.getId(), traverBean.getPd()));
                                    }
                                });
                        startBuilder.create().show();
                        break;
                    case R.id.ly_end://结束行程
                        Log.i(Str.TAG, "onChildItemClick: ly_end");
                        AlertDialog.Builder endBuilder = new AlertDialog.Builder(mActivity)
                                .setTitle("确认结束行程？")
                                .setCancelable(false)//点击返回键或对话框外部时是否消失，默认为true
                                .setNegativeButton("取消", null)//
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        //参数：dialog-The dialog that received the click.；which-The button that was clicked
                                        if (arrivePresenter == null) {
                                            arrivePresenter = new ArriveTraverPresenter(ExecutionTraverPageFragment.this);
                                        }
                                        arrivePresenter.accessServer(
                                                new ArriveTraverParams(traverBean.getId(), traverBean.getPd(), new BigDecimal(-1)));
                                    }
                                });
                        endBuilder.create().show();
                        break;
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
                                            cancelPresenter = new CancelTraverPresenter(ExecutionTraverPageFragment.this);
                                        }
                                        if (traverBean.getId() != null) {
                                            cancelPresenter.accessServer(new CancelTraverParams(traverBean.getId(), traverBean.getPd()));
                                        }
                                    }
                                });
                        cancelBuilder.create().show();
                        break;
                    default:
                        break;
                }
            }
        });
        return traverListAdapter;
    }

    @Override
    public BasePagePresenter setPresenter() {
        traverPresenter = new ExecutionTraverPresenter(this, ExecutionTraverBean.class);
        return traverPresenter;
    }

    @Override
    public BasePageParams setParams() {
        return new ExecutionTraverParams(1, 5, new BigDecimal(2));
    }

    @Override
    public void showExecutionTraver(List<ExecutionTraverBean> traverBeanList) {
        setData(traverBeanList);
    }
}
