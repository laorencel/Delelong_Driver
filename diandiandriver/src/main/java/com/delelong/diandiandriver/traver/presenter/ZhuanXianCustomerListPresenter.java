package com.delelong.diandiandriver.traver.presenter;

import com.delelong.diandiandriver.base.params.BaseParams;
import com.delelong.diandiandriver.base.presenter.BaseListPresenter;
import com.delelong.diandiandriver.bean.Str;
import com.delelong.diandiandriver.traver.bean.ZhuanXianCustomerBean;
import com.delelong.diandiandriver.traver.view.IExecutionZhuanXianCustomerView;

import java.util.List;

/**
 * Created by Administrator on 2017/3/17.
 */

public class ZhuanXianCustomerListPresenter extends BaseListPresenter<BaseParams,ZhuanXianCustomerBean> {
    IExecutionZhuanXianCustomerView baseView;
    public ZhuanXianCustomerListPresenter(IExecutionZhuanXianCustomerView mvpView, Class<ZhuanXianCustomerBean> clazz) {
        super(mvpView,clazz);
        this.baseView = mvpView;
        getModel().setApiInterface(Str.URL_ZHUANXIAN_EXECUTION_CUSTOMER);
    }

    @Override
    public void responseOk(List<ZhuanXianCustomerBean> customerBeen) {
        baseView.showExecutionZhuanXianCustomer(customerBeen);
    }
}
