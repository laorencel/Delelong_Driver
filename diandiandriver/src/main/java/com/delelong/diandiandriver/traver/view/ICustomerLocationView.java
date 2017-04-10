package com.delelong.diandiandriver.traver.view;

import com.amap.api.maps.model.LatLng;
import com.delelong.diandiandriver.base.view.iview.IView;
import com.delelong.diandiandriver.traver.bean.CustomerLocationBean;

/**
 * Created by Administrator on 2017/3/25.
 */

public interface ICustomerLocationView extends IView {

    void setCustomerLocation(LatLng start, CustomerLocationBean customerLocation);
}
