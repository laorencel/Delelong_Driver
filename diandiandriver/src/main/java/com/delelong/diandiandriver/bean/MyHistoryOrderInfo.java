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
    private String setouttime; //预约订单出发时间
    private String status; //订单的状态
    private String phone; //订单的状态
    private String real_name; //订单的状态
    private String pay_status;//订单支付状态
    private String real_pay;//订单金额
    private String reservation_address;//起点
    private String destination;//终点
    private String distance;//距离
    private String remote_fee;//远程费
    private String road_toll;//过路费
    private String other_charges;//其他费用

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

    public String getSetouttime() {
        return setouttime;
    }

    public void setSetouttime(String setouttime) {
        this.setouttime = setouttime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getReal_name() {
        return real_name;
    }

    public void setReal_name(String real_name) {
        this.real_name = real_name;
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

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getRemote_fee() {
        return remote_fee;
    }

    public void setRemote_fee(String remote_fee) {
        this.remote_fee = remote_fee;
    }

    public String getRoad_toll() {
        return road_toll;
    }

    public void setRoad_toll(String road_toll) {
        this.road_toll = road_toll;
    }

    public String getOther_charges() {
        return other_charges;
    }

    public void setOther_charges(String other_charges) {
        this.other_charges = other_charges;
    }
}
