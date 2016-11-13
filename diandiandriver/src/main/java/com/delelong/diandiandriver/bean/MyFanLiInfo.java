package com.delelong.diandiandriver.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/11/4.
 */

public class MyFanLiInfo implements Serializable {

    private double amount;
    private String create_time;

    public MyFanLiInfo(double amount, String create_time) {
        this.amount = amount;
        this.create_time = create_time;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }
}
