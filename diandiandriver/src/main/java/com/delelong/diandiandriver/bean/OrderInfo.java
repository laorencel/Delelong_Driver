package com.delelong.diandiandriver.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/10/16.
 */

public class OrderInfo implements Serializable{

    private int title;//自定义消息的类型
    private int status;//订单状态
    private int timeOut;//订单超时时间，设置dialog多少秒自动消失
    private int car_id;//如未完成的订单会有carId
    private int waitMinutes;//等待时间（包括未完成订单）
    private int people;//用车人数
    private String phone;//手机号
    private String nick_name;//昵称
    private String head_portrait;//头像地址
    private String no;//订单号
    private String setouttime;//预约时间
    private String createTime;//订单创建时间
    private int orderType = 1;//订单类型：1：顺风车、专线之外的订单；2：顺风车；3：专线
    private String type;//订单类型
    private String serviceType;//服务项（小分类）
    private boolean pdFlag;//是否是拼车单
    private boolean addAmountFlag;//是否是加价订单
    private boolean set_out_flag;//是否是预约
    private long id;//主键
    private double distance;//订单距离
    private double realDistance;//订单实际距离
    private double yg_amount;//预算费用
    private double addAmount;//订单加价金额
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
     * @param distance 订单距离（千米）
     * @param yg_amount 预算费用
     * @param startLatitude
     * @param startLongitude
     * @param endLatitude
     * @param endLongitude
     * @param reservationAddress 预约地址(地址信息不是adcode)
     * @param destination 目的地(地址信息不是adcode)
     */
    public OrderInfo(int title, int status,String phone, String nick_name,String head_portrait, String no, String setouttime, String type, String serviceType, boolean set_out_flag, long id, double distance, double yg_amount, double startLatitude, double startLongitude, double endLatitude, double endLongitude, String reservationAddress, String destination,String remark) {
        this.title = title;
        this.status = status;
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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(int timeOut) {
        this.timeOut = timeOut;
    }

    public int getPeople() {
        return people;
    }

    public void setPeople(int people) {
        this.people = people;
    }

    public int getCar_id() {
        return car_id;
    }

    public int getWaitMinutes() {
        return waitMinutes;
    }

    public void setWaitMinutes(int waitMinutes) {
        this.waitMinutes = waitMinutes;
    }

    public void setCar_id(int car_id) {
        this.car_id = car_id;
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

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
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

    public boolean isPdFlag() {
        return pdFlag;
    }

    public void setPdFlag(boolean pdFlag) {
        this.pdFlag = pdFlag;
    }

    public boolean isAddAmountFlag() {
        return addAmountFlag;
    }

    public void setAddAmountFlag(boolean addAmountFlag) {
        this.addAmountFlag = addAmountFlag;
    }

    public double getAddAmount() {
        return addAmount;
    }

    public void setAddAmount(double addAmount) {
        this.addAmount = addAmount;
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

    public double getRealDistance() {
        return realDistance;
    }

    public void setRealDistance(double realDistance) {
        this.realDistance = realDistance;
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

    public int getOrderType() {
        return orderType;
    }

    public void setOrderType(int orderType) {
        this.orderType = orderType;
    }

    @Override
    public String toString() {
        return "OrderInfo{" +
                "title=" + title +
                ", status=" + status +
                ", timeOut=" + timeOut +
                ", car_id=" + car_id +
                ", waitMinutes=" + waitMinutes +
                ", people=" + people +
                ", phone='" + phone + '\'' +
                ", nick_name='" + nick_name + '\'' +
                ", head_portrait='" + head_portrait + '\'' +
                ", no='" + no + '\'' +
                ", setouttime='" + setouttime + '\'' +
                ", createTime='" + createTime + '\'' +
                ", type='" + type + '\'' +
                ", serviceType='" + serviceType + '\'' +
                ", pdFlag=" + pdFlag +
                ", addAmountFlag=" + addAmountFlag +
                ", set_out_flag=" + set_out_flag +
                ", id=" + id +
                ", distance=" + distance +
                ", realDistance=" + realDistance +
                ", yg_amount=" + yg_amount +
                ", addAmount=" + addAmount +
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
