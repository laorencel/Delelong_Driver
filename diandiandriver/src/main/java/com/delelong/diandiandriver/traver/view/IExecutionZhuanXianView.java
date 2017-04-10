package com.delelong.diandiandriver.traver.view;

import com.delelong.diandiandriver.base.view.iview.IListView;
import com.delelong.diandiandriver.traver.bean.ExecutionZhuanXianBean;

import java.util.List;

/**
 * Created by Administrator on 2017/3/14.
 */

public interface IExecutionZhuanXianView extends IListView {

    void showExecutionTraver(List<ExecutionZhuanXianBean> traverBeanList);
}