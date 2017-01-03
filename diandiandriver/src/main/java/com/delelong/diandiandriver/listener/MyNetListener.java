package com.delelong.diandiandriver.listener;

/**
 * Created by Administrator on 2016/12/9.
 */

public interface MyNetListener {
    /**
     * true:有网络连接
     *
     * @param onNet
     */
    void onNet(boolean onNet);

    /**
     * true：wifi环境
     *
     * @param onWifi
     */
    void onWifi(boolean onWifi);
}
