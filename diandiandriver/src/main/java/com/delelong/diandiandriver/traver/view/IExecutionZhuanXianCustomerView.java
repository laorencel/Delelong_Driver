package com.delelong.diandiandriver.traver.view;

import com.delelong.diandiandriver.base.view.iview.IListView;
import com.delelong.diandiandriver.traver.bean.ZhuanXianCustomerBean;

import java.util.List;

/**
 * Created by Administrator on 2017/2/26.
 */

public interface IExecutionZhuanXianCustomerView extends IListView {

    void showExecutionZhuanXianCustomer(List<ZhuanXianCustomerBean> traverBeanList);
}
