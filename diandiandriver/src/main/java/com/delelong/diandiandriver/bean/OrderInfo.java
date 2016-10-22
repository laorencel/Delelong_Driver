package com.delelong.diandiandriver.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/10/16.
 */

public class OrderInfo implements Serializable{

    private int title;//自定义消息的类型
    private String phone;//手机号
    private String nick_name;//昵称
    private String head_portrait;//头像地址
    private String no;//订单号
    private String setouttime;//预约时间
    private String type;//订单类型
    private String serviceType;//服务项（小分类）
    private boolean set_out_flag;//是否是预约
    private long id;//主键
    private double distance;//订单距离
    private double yg_amount;//预算费用
    private double startLatitude;
    private double startLongitude;
    private double endLatitude;
    private double endLongitude;
    private String reservationAddress;//预约地址(地址信息不是adcode)
    private String destination;//目的地(地址信息不是adcode)
    private String remark;//备注

    public OrderInfo() {
    }

    /**
     *
     * @param title 自定义消息的类型
     * @param phone 手机号
     * @param nick_name 昵称
     * @param no 订单号
     * @param setouttime 预约时间
     * @param type 订单类型
     * @param serviceType 服务项（小分类）
     * @param set_out_flag 是否是预约
     * @param id 主键
     * @param distance 订单距离
     * @param yg_amount 预算费用
     * @param startLatitude
     * @param startLongitude
     * @param endLatitude
     * @param endLongitude
     * @param reservationAddress 预约地址(地址信息不是adcode)
     * @param destination 目的地(地址信息不是adcode)
     */
    public OrderInfo(int title, String phone, String nick_name,String head_portrait, String no, String setouttime, String type, String serviceType, boolean set_out_flag, long id, double distance, double yg_amount, double startLatitude, double startLongitude, double endLatitude, double endLongitude, String reservationAddress, String destination,String remark) {
        this.title = title;
        this.phone = phone;
        this.nick_name = nick_name;
        this.head_portrait = head_portrait;
        this.no = no;
        this.setouttime = setouttime;
        this.type = type;
        this.serviceType = serviceType;
        this.set_out_flag = set_out_flag;
        this.id = id;
        this.distance = distance;
        this.yg_amount = yg_amount;
        this.startLatitude = startLatitude;
        this.startLongitude = startLongitude;
        this.endLatitude = endLatitude;
        this.endLongitude = endLongitude;
        this.reservationAddress = reservationAddress;
        this.destination = destination;
        this.remark = remark;
    }

    public int getTitle() {
        return title;
    }

    public void setTitle(int title) {
        this.title = title;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getNick_name() {
        return nick_name;
    }

    public void setNick_name(String nick_name) {
        this.nick_name = nick_name;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getSetouttime() {
        return setouttime;
    }

    public void setSetouttime(String setouttime) {
        this.setouttime = setouttime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public boolean isSet_out_flag() {
        return set_out_flag;
    }

    public void setSet_out_flag(boolean set_out_flag) {
        this.set_out_flag = set_out_flag;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getYg_amount() {
        return yg_amount;
    }

    public void setYg_amount(double yg_amount) {
        this.yg_amount = yg_amount;
    }

    public double getStartLatitude() {
        return startLatitude;
    }

    public void setStartLatitude(double startLatitude) {
        this.startLatitude = startLatitude;
    }

    public double getStartLongitude() {
        return startLongitude;
    }

    public void setStartLongitude(double startLongitude) {
        this.startLongitude = startLongitude;
    }

    public double getEndLatitude() {
        return endLatitude;
    }

    public void setEndLatitude(double endLatitude) {
        this.endLatitude = endLatitude;
    }

    public double getEndLongitude() {
        return endLongitude;
    }

    public void setEndLongitude(double endLongitude) {
        this.endLongitude = endLongitude;
    }

    public String getReservationAddress() {
        return reservationAddress;
    }

    public void setReservationAddress(String reservationAddress) {
        this.reservationAddress = reservationAddress;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getHead_portrait() {
        return head_portrait;
    }

    public void setHead_portrait(String head_portrait) {
        this.head_portrait = head_portrait;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public String toString() {
        return "OrderInfo{" +
                "title=" + title +
                ", phone='" + phone + '\'' +
                ", nick_name='" + nick_name + '\'' +
                ", head_portrait='" + head_portrait + '\'' +
                ", no='" + no + '\'' +
                ", setouttime='" + setouttime + '\'' +
                ", type='" + type + '\'' +
                ", serviceType='" + serviceType + '\'' +
                ", set_out_flag=" + set_out_flag +
                ", id=" + id +
                ", distance=" + distance +
                ", yg_amount=" + yg_amount +
                ", startLatitude=" + startLatitude +
                ", startLongitude=" + startLongitude +
                ", endLatitude=" + endLatitude +
                ", endLongitude=" + endLongitude +
                ", reservationAddress='" + reservationAddress + '\'' +
                ", destination='" + destination + '\'' +
                ", remark='" + remark + '\'' +
                '}';
    }
}
