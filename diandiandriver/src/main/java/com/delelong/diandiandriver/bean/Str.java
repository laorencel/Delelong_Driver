package com.delelong.diandiandriver.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/9/14.
 */
public class Str implements Serializable {


//    private static final String URL_API = "http://121.40.142.141:8090/Jfinal/api";
    private static final String URL_API = "http://192.168.4.110:8080/Jfinal/api";//（本地）

//    public static final String URL_UPDATEFILE = "http://121.40.142.141:8090/Jfinal/file";//上传文件
    public static final String URL_UPDATEFILE = "http://192.168.4.110:8080/Jfinal/file";//上传文件（本地）
//    public static final String URL_SERVICE_IMAGEPATH = "http://121.40.142.141:8090/Jfinal/";//图片头地址
    public static final String URL_SERVICE_IMAGEPATH = "http://192.168.4.110:8080/Jfinal/";//图片头地址（本地）

    public static final String URL_LOGIN = URL_API+"/login";
    public static final String URL_ONLINE = URL_API+"/driver/online";
    public static final String URL_FORGOT = URL_API+"/member/reset/password";
    public static final String URL_SMSCODE = URL_API+"/smscode";//1:注册;2:忘记密码;3:更换手机号;

    public static final String URL_MEMBER = URL_API+"/member";//获取会员信息
    public static final String URL_DRIVER_CARS = URL_API+"/driver/cars";//获取会员信息
    public static final String URL_LOGINOUT = URL_API+"/logout";//注销登陆

    public static final String URL_MODIFY = URL_API+"/update/password";//修改密码（记得密码）
    public static final String URL_REGISTER = URL_API+"/register";//注册
    public static final String URL_CAR_BRAND = URL_API+"/driver/car/brands";//获取车辆品牌
    public static final String URL_CAR_BRAND_MODEL = URL_API+"/driver/car/models";//获取车辆品牌
    public static final String URL_AD = URL_API+"/ad";//获取广告图片
    public static final String URL_UPDATECLIENT = URL_API+"/member/update";//更新会员信息（GET）
    public static final String URL_UPDATEADCODE = URL_API+"/updateCityCode";//更新会员信息（GET）
    public static final String URL_PLUS_CAR = URL_API+"/driver/car/plus";//增加车辆信息
    public static final String URL_GETCARINFO = URL_API+"/member/cars";//获取车辆列表
    public static final String URL_GETDRIVERINFO = URL_API+"/member/drivers";//获取代驾司机列表
    public static final String FILEPATH = "/sdcard/DianDian/Image/";//文件本地地址
    public static final String ADIMAGEPATH = "/sdcard/DianDian/Image/adimage.jpg";//广告文件本地地址
    public static final String ADIMAGEPATH_START = "/sdcard/DianDian/Image/Start/";//启动页广告文件本地地址（父目录）
    public static final String ADIMAGEPATH_MAIN = "/sdcard/DianDian/Image/Main/";//主界面广告文件本地地址（父目录）
    public static final String URL_UPDATELOCATION = URL_API+"/member/update/location";//更新位置
    public static final String URL_UPDATELOCATION_DRIVER = URL_API+"/driver/update/location";//更新位置

    /**
     * 验证码类型(1:注册;2:忘记密码;3:更换手机号;)
     */
    public static final String VERIFICATION_TYPE_REGISTER = "1";
    public static final String VERIFICATION_TYPE_RESET = "2";
    public static final String VERIFICATION_TYPE_CHANGEPHONE = "3";
    /**
     * appType：请求的类型，1:表示司机端;2:表示普通会员
     */
    public static final String APPTYPE_CLIENT = "1";
    /**
     * devicetype：设备序类型，1:android;2:ios
     */
    public static final String DEVICE_TYPE = "1";
    /**
     * 请求码
     */
    public static final int RESULTNOCHOICECODE = 0;
    public static final int REQUESTCODECAMERA = 1;//相机requestCode
    public static final int REQUESTCODEALBUM = 2;//相册requestCode
    public static final int REQUESTHOMECODE = 3;//设置家庭地址requestCode
    public static final int REQUESTCOMPANYCODE = 4;//设置公司地址相册requestCode
    public static final int REQUESTPOSITIONCODE = 5;//设置起点requestCode
    public static final int REQUESTDESTINATIONCODE = 6;//设置终点requestCode
    public static final int REQUESTADCODE = 7;//设置终点requestCode
    public static final int REQUESTBRANDCODE = 8;//选择车辆brand requestCode
    public static final int REQUESTMODELCODE = 9;//选择车辆brand requestCode
    /**
     * js打开系统文件请求码
     */
    public static final int FILECHOOSER_RESULTCODE = 10000;

    public static final String MESSAGE_RECEIVED_ACTION = "com.example.jpushdemo.MESSAGE_RECEIVED_ACTION";//receiver服务
    public static final String URL_LIANCHENG = "http://www.52liancheng.com";//链城网
    public static final String URL_DRIVERENROLL = "http://192.168.4.110:8080/Jfinal/driver/enroll";//司机报名页面
    public static final String VERSION_URL = "http://223.26.63.3/mobilesafe/update.json";
    public static final String SQL_TABLE_NAME = "city.db";
    public static final String ORDER_MESSAGE_RECEIVED_ACTION = "com.delelong.diandiandriver.ORDER_MESSAGE_RECEIVED_ACTION";
    public static final String KEY_ORDER_MESSAGE = "order_message";
    public static final String KEY_ORDER_EXTRA = "order_extra";
}
