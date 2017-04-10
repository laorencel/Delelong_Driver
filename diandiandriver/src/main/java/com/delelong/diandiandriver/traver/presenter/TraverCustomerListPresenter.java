package com.delelong.diandiandriver.traver.presenter;

import com.delelong.diandiandriver.base.presenter.BaseListPresenter;
import com.delelong.diandiandriver.bean.Str;
import com.delelong.diandiandriver.traver.bean.TraverCustomerBean;
import com.delelong.diandiandriver.traver.params.TraverCustomerListParams;
import com.delelong.diandiandriver.traver.view.IExecutionCustomerView;

import java.util.List;

/**
 * Created by Administrator on 2017/3/17.
 */

public class TraverCustomerListPresenter extends BaseListPresenter<TraverCustomerListParams,TraverCustomerBean> {
    IExecutionCustomerView baseView;
    public TraverCustomerListPresenter(IExecutionCustomerView mvpView,Class<TraverCustomerBean> clazz) {
        super(mvpView,clazz);
        this.baseView = mvpView;
        getModel().setApiInterface(Str.URL_TRAVER_EXECUTION_CUSTOMER);
    }

    @Override
    public void responseOk(List<TraverCustomerBean> customerBeen) {
        baseView.showExecutionCustomer(customerBeen);
    }
}
