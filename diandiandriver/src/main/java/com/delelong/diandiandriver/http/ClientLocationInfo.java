package com.delelong.diandiandriver.http;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/9/14.
 */
public class ClientLocationInfo implements Serializable {

    private String longitude;//经度
    private String latitude;//纬度
    private String speed;//速度
    private String orientation;//方向
    private int type;//定位类型(4:GPS;2::网络和其他;)(AMapLocation.getLocationType)
    private float accuracy;//定位精度 单位:米

    public ClientLocationInfo(String longitude, String latitude, String speed, String orientation,int type,float accuracy) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.speed = speed;
        this.orientation = orientation;
        this.type = type;
        this.accuracy = accuracy;
    }

    public ClientLocationInfo() {
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    public String getOrientation() {
        return orientation;
    }

    public void setOrientation(String orientation) {
        this.orientation = orientation;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public float getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(float accuracy) {
        this.accuracy = accuracy;
    }

    @Override
    public String toString() {
        return "ClientLocationInfo{" +
                "longitude='" + longitude + '\'' +
                ", latitude='" + latitude + '\'' +
                ", speed='" + speed + '\'' +
                ", orientation='" + orientation + '\'' +
                ", type=" + type +
                ", accuracy=" + accuracy +
                '}';
    }
}
