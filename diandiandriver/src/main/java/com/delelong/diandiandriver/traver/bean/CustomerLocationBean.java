package com.delelong.diandiandriver.traver.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.delelong.diandiandriver.base.bean.BaseBean;

import java.math.BigDecimal;

/**
 * Created by Administrator on 2017/3/25.
 */

public class CustomerLocationBean extends BaseBean {

    /**
     *
     *
     */
    @JSONField(name = "recive_time")
    private String recive_time;
    /**
     *
     */
    @JSONField(name = "member")
    private BigDecimal member;
    /**
     *
     */
    @JSONField(name = "id")
    private BigDecimal id;
    /**
     *
     */
    @JSONField(name = "type")
    private BigDecimal type;
    /**
     * 方向
     */
    @JSONField(name = "orientation")
    private BigDecimal orientation;
    /**
     * 速度
     */
    @JSONField(name = "speed")
    private BigDecimal speed;
    /**
     * 纬度
     */
    @JSONField(name = "latitude")
    private BigDecimal latitude;
    /**
     * 经度
     */
    @JSONField(name = "longitude")
    private BigDecimal longitude;
    /**
     * 精度
     */
    @JSONField(name = "accuracy")
    private BigDecimal accuracy;

    public CustomerLocationBean() {
    }

    public CustomerLocationBean(String recive_time, BigDecimal member, BigDecimal id, BigDecimal type, BigDecimal orientation, BigDecimal speed, BigDecimal latitude, BigDecimal longitude, BigDecimal accuracy) {
        this.recive_time = recive_time;
        this.member = member;
        this.id = id;
        this.type = type;
        this.orientation = orientation;
        this.speed = speed;
        this.latitude = latitude;
        this.longitude = longitude;
        this.accuracy = accuracy;
    }

    public String getRecive_time() {
        return recive_time;
    }

    public void setRecive_time(String recive_time) {
        this.recive_time = recive_time;
    }

    public BigDecimal getMember() {
        return member;
    }

    public void setMember(BigDecimal member) {
        this.member = member;
    }

    public BigDecimal getId() {
        return id;
    }

    public void setId(BigDecimal id) {
        this.id = id;
    }

    public BigDecimal getType() {
        return type;
    }

    public void setType(BigDecimal type) {
        this.type = type;
    }

    public BigDecimal getOrientation() {
        return orientation;
    }

    public void setOrientation(BigDecimal orientation) {
        this.orientation = orientation;
    }

    public BigDecimal getSpeed() {
        return speed;
    }

    public void setSpeed(BigDecimal speed) {
        this.speed = speed;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public BigDecimal getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(BigDecimal accuracy) {
        this.accuracy = accuracy;
    }

    @Override
    public String toString() {
        return "CustomerLocationBean{" +
                "recive_time='" + recive_time + '\'' +
                ", member=" + member +
                ", id=" + id +
                ", type=" + type +
                ", orientation=" + orientation +
                ", speed=" + speed +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", accuracy=" + accuracy +
                '}';
    }
}
