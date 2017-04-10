package com.delelong.diandiandriver.base.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/3/17.
 */

public class BaseHttpMsg extends BaseBean implements Serializable{

    private String api;

    private String msg;

    public BaseHttpMsg() {
    }

    public BaseHttpMsg(String api, String msg) {
        this.api = api;
        this.msg = msg;
    }

    public String getApi() {
        return api;
    }

    public void setApi(String api) {
        this.api = api;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "BaseHttpMsg{" +
                "api='" + api + '\'' +
                ", msg='" + msg + '\'' +
                '}';
    }
}
