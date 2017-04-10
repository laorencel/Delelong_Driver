package com.delelong.diandiandriver.traver.presenter;

import android.util.Log;

import com.delelong.diandiandriver.base.params.BasePageParams;
import com.delelong.diandiandriver.base.presenter.BasePagePresenter;
import com.delelong.diandiandriver.bean.Str;
import com.delelong.diandiandriver.traver.bean.ExecutionZhuanXianBean;
import com.delelong.diandiandriver.traver.view.IExecutionZhuanXianView;

import java.util.List;

/**
 * Created by Administrator on 2017/3/9.
 */

public class ExecutionZhuanXianPresenter extends BasePagePresenter<BasePageParams, ExecutionZhuanXianBean> {
    IExecutionZhuanXianView baseView;

    public ExecutionZhuanXianPresenter(IExecutionZhuanXianView baseView, Class<ExecutionZhuanXianBean> clazz) {
        super(baseView, clazz);
        this.baseView = baseView;
        getModel().setApiInterface(Str.URL_ZHUANXIANLIST_EXECUTION);
        Log.i(Str.TAG, "ExecutionZhuanXianPresenter: "+getModel().getApiInterface());
        setCount(5);
        showProgress(true);
    }

    @Override
    public void responseOk(List<ExecutionZhuanXianBean> list) {
        baseView.showExecutionTraver(list);
    }
}
