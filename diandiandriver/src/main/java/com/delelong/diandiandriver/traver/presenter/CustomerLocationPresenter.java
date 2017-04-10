package com.delelong.diandiandriver.traver.presenter;

import com.amap.api.maps.model.LatLng;
import com.delelong.diandiandriver.base.presenter.BasePresenter;
import com.delelong.diandiandriver.bean.Str;
import com.delelong.diandiandriver.traver.bean.CustomerLocationBean;
import com.delelong.diandiandriver.traver.params.CustomerLocationParams;
import com.delelong.diandiandriver.traver.view.ICustomerLocationView;

/**
 * Created by Administrator on 2017/3/25.
 */

public class CustomerLocationPresenter extends BasePresenter<CustomerLocationParams, CustomerLocationBean> {
    ICustomerLocationView customerLocationView;
    LatLng start;

    public CustomerLocationPresenter(ICustomerLocationView mvpView, Class<CustomerLocationBean> clazz, LatLng start) {
        super(mvpView, clazz);
        this.start = start;
        this.customerLocationView = mvpView;
        getModel().setApiInterface(Str.URL_CUSTOMER_LOCATION);
        showProgress(true);
    }

    @Override
    public void responseOk(CustomerLocationBean customerLocationBean) {
        customerLocationView.setCustomerLocation(start,customerLocationBean);
    }
}
