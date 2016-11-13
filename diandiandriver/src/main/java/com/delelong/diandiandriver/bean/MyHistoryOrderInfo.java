package com.delelong.diandiandriver.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2016/10/29.
 */

public class MyHistoryOrderInfo implements Serializable {

    private int orderId; //订单主键
    private String orderNo; //订单号
    private String create_time; //订单时间
    private String status; //订单的状态
    private String pay_status;//订单支付状态
    private String real_pay;//订单金额
    private String reservation_address;//起点
    private String destination;//终点

    public MyHistoryOrderInfo(int orderId, String orderNo, String create_time, String status, String pay_status, String real_pay, String reservation_address, String destination) {
        this.orderId = orderId;
        this.orderNo = orderNo;
        this.create_time = create_time;
        this.status = status;
        this.pay_status = pay_status;
        this.real_pay = real_pay;
        this.reservation_address = reservation_address;
        this.destination = destination;
    }
    private List<String > httpStatus;

    public List<String> getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(List<String> httpStatus) {
        this.httpStatus = httpStatus;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPay_status() {
        return pay_status;
    }

    public void setPay_status(String pay_status) {
        this.pay_status = pay_status;
    }

    public String getReal_pay() {
        return real_pay;
    }

    public void setReal_pay(String real_pay) {
        this.real_pay = real_pay;
    }

    public String getReservation_address() {
        return reservation_address;
    }

    public void setReservation_address(String reservation_address) {
        this.reservation_address = reservation_address;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }
}
