package com.delelong.diandiandriver.traver.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.delelong.diandiandriver.base.bean.BaseBean;

import java.math.BigDecimal;

/**
 * Created by Administrator on 2017/2/26.
 */

public class ExecutionZhuanXianBean extends BaseBean {

    /**
     * 行程id
     */
    @JSONField(name = "id")
    private BigDecimal id;
    /**
     * 路线名称（如："机场专线"）
     */
    @JSONField(name = "lineName")
    private String lineName;
    /**
     * 出发城市
     */
    @JSONField(name = "reservationCity")
    private String startCity;
    /**
     * 出发具体位置
     */
    @JSONField(name = "reservationAddress")
    private String startAddress;
    /**
     * 终点城市
     */
    @JSONField(name = "destinationCity")
    private String endCity;
    /**
     * 终点具体位置
     */
    @JSONField(name = "destination")
    private String endAddress;
    /**
     * 起点纬度
     */
    @JSONField(name = "startLatitude")
    private BigDecimal startLatitude;
    /**
     * 起点经度
     */
    @JSONField(name = "startLongitude")
    private BigDecimal startLongitude;
    /**
     * 终点纬度
     */
    @JSONField(name = "endLatitude")
    private BigDecimal endLatitude;
    /**
     * 终点经度
     */
    @JSONField(name = "endLongitude")
    private BigDecimal endLongitude;
    /**
     * 下单时间
     */
    @JSONField(name = "createTime")
    private String createTime;
    /**
     * 拼车价格
     */
    @JSONField(name = "pinCheAmount")
    private BigDecimal pinCheAmount;
    /**
     * 包车价格
     */
    @JSONField(name = "amount")
    private BigDecimal baoCheAmount;
    /**
     * 寄货价格
     */
    @JSONField(name = "jiHuoAmount")
    private BigDecimal jiHuoAmount;
    /**
     * 距离
     */
    @JSONField(name = "distance")
    private BigDecimal distance;
    /**
     * 大约用时
     */
    @JSONField(name = "requiredTime")
    private String requiredTime;

    /**
     * 订单状态(1:创建行程 2：执行中行程 3：结束行程 4：取消行程)
     */
    @JSONField(name = "status")
    private BigDecimal status;
    /**
     * 接单状态（0：接单;1:未接单）
     */
    @JSONField(name = "orderStatus")
    private BigDecimal orderStatus;
    /**
     * 专线状态（0：专线禁用;1:专线可用）
     */
    @JSONField(name = "lineStatus")
    private BigDecimal lineStatus;
    /**
     * 已接人数
     */
    private BigDecimal count;
    public ExecutionZhuanXianBean() {
    }

    public ExecutionZhuanXianBean(BigDecimal id, String lineName, String startCity, String startAddress, String endCity, String endAddress, BigDecimal startLatitude, BigDecimal startLongitude, BigDecimal endLatitude, BigDecimal endLongitude, String createTime, BigDecimal pinCheAmount, BigDecimal baoCheAmount, BigDecimal jiHuoAmount, BigDecimal distance, String requiredTime, BigDecimal status, BigDecimal orderStatus, BigDecimal lineStatus, BigDecimal count) {
        this.id = id;
        this.lineName = lineName;
        this.startCity = startCity;
        this.startAddress = startAddress;
        this.endCity = endCity;
        this.endAddress = endAddress;
        this.startLatitude = startLatitude;
        this.startLongitude = startLongitude;
        this.endLatitude = endLatitude;
        this.endLongitude = endLongitude;
        this.createTime = createTime;
        this.pinCheAmount = pinCheAmount;
        this.baoCheAmount = baoCheAmount;
        this.jiHuoAmount = jiHuoAmount;
        this.distance = distance;
        this.requiredTime = requiredTime;
        this.status = status;
        this.orderStatus = orderStatus;
        this.lineStatus = lineStatus;
        this.count = count;
    }

    public BigDecimal getId() {
        return id;
    }

    public void setId(BigDecimal id) {
        this.id = id;
    }

    public String getLineName() {
        return lineName;
    }

    public void setLineName(String lineName) {
        this.lineName = lineName;
    }

    public String getStartCity() {
        return startCity;
    }

    public void setStartCity(String startCity) {
        this.startCity = startCity;
    }

    public String getStartAddress() {
        return startAddress;
    }

    public void setStartAddress(String startAddress) {
        this.startAddress = startAddress;
    }

    public String getEndCity() {
        return endCity;
    }

    public void setEndCity(String endCity) {
        this.endCity = endCity;
    }

    public String getEndAddress() {
        return endAddress;
    }

    public void setEndAddress(String endAddress) {
        this.endAddress = endAddress;
    }

    public BigDecimal getStartLatitude() {
        return startLatitude;
    }

    public void setStartLatitude(BigDecimal startLatitude) {
        this.startLatitude = startLatitude;
    }

    public BigDecimal getStartLongitude() {
        return startLongitude;
    }

    public void setStartLongitude(BigDecimal startLongitude) {
        this.startLongitude = startLongitude;
    }

    public BigDecimal getEndLatitude() {
        return endLatitude;
    }

    public void setEndLatitude(BigDecimal endLatitude) {
        this.endLatitude = endLatitude;
    }

    public BigDecimal getEndLongitude() {
        return endLongitude;
    }

    public void setEndLongitude(BigDecimal endLongitude) {
        this.endLongitude = endLongitude;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public BigDecimal getPinCheAmount() {
        return pinCheAmount;
    }

    public void setPinCheAmount(BigDecimal pinCheAmount) {
        this.pinCheAmount = pinCheAmount;
    }

    public BigDecimal getBaoCheAmount() {
        return baoCheAmount;
    }

    public void setBaoCheAmount(BigDecimal baoCheAmount) {
        this.baoCheAmount = baoCheAmount;
    }

    public BigDecimal getJiHuoAmount() {
        return jiHuoAmount;
    }

    public void setJiHuoAmount(BigDecimal jiHuoAmount) {
        this.jiHuoAmount = jiHuoAmount;
    }

    public BigDecimal getDistance() {
        return distance;
    }

    public void setDistance(BigDecimal distance) {
        this.distance = distance;
    }

    public String getRequiredTime() {
        return requiredTime;
    }

    public void setRequiredTime(String requiredTime) {
        this.requiredTime = requiredTime;
    }

    public BigDecimal getStatus() {
        return status;
    }

    public void setStatus(BigDecimal status) {
        this.status = status;
    }

    public BigDecimal getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(BigDecimal orderStatus) {
        this.orderStatus = orderStatus;
    }

    public BigDecimal getLineStatus() {
        return lineStatus;
    }

    public void setLineStatus(BigDecimal lineStatus) {
        this.lineStatus = lineStatus;
    }

    public BigDecimal getCount() {
        return count;
    }

    public void setCount(BigDecimal count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "ExecutionZhuanXianBean{" +
                "id=" + id +
                ", lineName='" + lineName + '\'' +
                ", startCity='" + startCity + '\'' +
                ", startAddress='" + startAddress + '\'' +
                ", endCity='" + endCity + '\'' +
                ", endAddress='" + endAddress + '\'' +
                ", startLatitude=" + startLatitude +
                ", startLongitude=" + startLongitude +
                ", endLatitude=" + endLatitude +
                ", endLongitude=" + endLongitude +
                ", createTime='" + createTime + '\'' +
                ", pinCheAmount=" + pinCheAmount +
                ", baoCheAmount=" + baoCheAmount +
                ", jiHuoAmount=" + jiHuoAmount +
                ", distance=" + distance +
                ", requiredTime='" + requiredTime + '\'' +
                ", status=" + status +
                ", orderStatus=" + orderStatus +
                ", lineStatus=" + lineStatus +
                ", count=" + count +
                '}';
    }
}
