package com.delelong.diandiandriver.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/10/27.
 */

public class DriverAmount implements Serializable {

    private double ye;//账户余额
    private double today;//今天收入
    private double yesterday;//昨天收入
    private double month;//本月收入

    public DriverAmount(double ye, double today, double yesterday, double month) {
        this.ye = ye;
        this.today = today;
        this.yesterday = yesterday;
        this.month = month;
    }

    public double getYe() {
        return ye;
    }

    public void setYe(double ye) {
        this.ye = ye;
    }

    public double getToday() {
        return today;
    }

    public void setToday(double today) {
        this.today = today;
    }

    public double getYesterday() {
        return yesterday;
    }

    public void setYesterday(double yesterday) {
        this.yesterday = yesterday;
    }

    public double getMonth() {
        return month;
    }

    public void setMonth(double month) {
        this.month = month;
    }
}
