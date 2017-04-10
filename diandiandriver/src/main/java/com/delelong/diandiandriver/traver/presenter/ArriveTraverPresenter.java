package com.delelong.diandiandriver.traver.presenter;


import com.delelong.diandiandriver.base.bean.BaseBean;
import com.delelong.diandiandriver.base.presenter.BasePresenter;
import com.delelong.diandiandriver.base.view.iview.IView;
import com.delelong.diandiandriver.bean.Str;
import com.delelong.diandiandriver.traver.params.ArriveTraverParams;

/**
 * Created by Administrator on 2017/3/7.
 */

public class ArriveTraverPresenter extends BasePresenter<ArriveTraverParams,BaseBean> {

    public ArriveTraverPresenter(IView mvpView) {
        super(mvpView);
        getModel().setApiInterface(Str.URL_TRAVER_ARRIVE);
    }

    @Override
    public void responseOk(BaseBean baseBean) {

    }
}
