package com.delelong.diandiandriver.traver.presenter;


import com.delelong.diandiandriver.base.bean.BaseBean;
import com.delelong.diandiandriver.base.presenter.BasePresenter;
import com.delelong.diandiandriver.base.view.iview.IView;
import com.delelong.diandiandriver.bean.Str;
import com.delelong.diandiandriver.traver.params.StartTraverParams;

/**
 * Created by Administrator on 2017/3/7.
 */

public class StartTraverPresenter extends BasePresenter<StartTraverParams,BaseBean> {

    public StartTraverPresenter(IView mvpView) {
        super(mvpView);
        getModel().setApiInterface(Str.URL_TRAVER_START);
    }

    @Override
    public void responseOk(BaseBean baseBean) {

    }
}
