package com.delelong.diandiandriver.traver.view;

import com.delelong.diandiandriver.base.view.iview.IListView;
import com.delelong.diandiandriver.traver.bean.TraverCustomerBean;

import java.util.List;

/**
 * Created by Administrator on 2017/2/26.
 */

public interface IExecutionCustomerView extends IListView {

    void showExecutionCustomer(List<TraverCustomerBean> traverBeanList);
}
