package com.delelong.diandiandriver.bean;

import android.os.Environment;

import java.io.File;
import java.io.Serializable;

/**
 * Created by Administrator on 2016/9/14.
 */
public class Str implements Serializable {
    public static final String TAG = "BAIDUMAPFORTEST";

//    public static final String URL_ = "192.168.1.27:8080";//本地
    public static final String URL_ = "121.40.142.141:8090";//阿里云
//    public static final String URL_ = "139.196.42.108:8080";//阿里云(新)
//    public static final String URL_ = "admin.dddriver.com";//阿里云(正式版域名，作用同139服务器)

    private static final String URL = "http://" + URL_;

    public static final String URL_WEBSOCKET = "ws://" + URL_ + "/Jfinal/api/websocket?";
    public static final String URL_TUIJIAN_SHARE = URL + "/Jfinal/share";
    public static final String URL_SERVICE_PATH = URL + "/Jfinal";//服务器根目录
    public static final String URL_SERVICE_IMAGEPATH = URL + "/Jfinal/";//图片头地址（本地）
    public static final String URL_UPDATEFILE = URL + "/Jfinal/file";//上传文件（本地）
    private static final String URL_API = URL + "/Jfinal/api";//（本地）

//    public static final String URL_WEBSOCKET = "ws://" + URL_ + "/api/websocket?";
//    private static final String URL_API = URL + "/api";//（正式版）
//    public static final String URL_UPDATEFILE = URL + "/file";//上传文件（本地）
//    public static final String URL_SERVICE_IMAGEPATH = URL + "/";//图片头地址（本地）
//    public static final String URL_SERVICE_PATH = URL + "";//服务器根目录
//    public static final String URL_TUIJIAN_SHARE = URL + "/share";

    public static final String URL_LOGIN = URL_API + "/login";
    public static final String URL_ONLINE = URL_API + "/driver/online";
    public static final String URL_ONLINE_DJ = URL_API + "/driver/djOnline";//代驾类型上线
    public static final String URL_FORGOT = URL_API + "/member/reset/password";
    public static final String URL_SMSCODE = URL_API + "/smscode";//1:注册;2:忘记密码;3:更换手机号;

    public static final String URL_MEMBER = URL_API + "/member";//获取会员信息
    public static final String URL_DRIVER_CARS = URL_API + "/driver/cars";//获取会员信息
    public static final String URL_LOGINOUT = URL_API + "/logout";//注销登陆
    public static final String URL_CHECK_LOGIN = URL_API + "/isLogin";//检查是否登录
    public static final String URL_CHECK_ONLINE = URL_API + "/isOnline";//检查是否上线
    public static final String URL_UPDATE_CARSTATUS = URL_API + "/driver/updateCarStatus";//更新司机状态，防止下线
    public static final String URL_UPDATE_ENDPOINT = URL_API + "/driver/updateEndPoint";//更新终点位置信息
    public static final String URL_UPDATE_DISTANCE = URL_API + "/driver/updateDistance";//订单中途更新等候时间和距离
    public static final String URL_TUIJIAN = URL_API + "/myreferee";//获取我推荐的人
    public static final String URL_LAW = URL_SERVICE_PATH + "/static/html/lowerTextDriver.html";//法律条款
    public static final String URL_LAW_PROVISION = URL_SERVICE_PATH + "/app/provision";//法律信息（更新）
    public static final String URL_COMPANY_INFO = URL_API + "/getSelfCompany";//获取我的公司
    public static final String URL_FANLI_INFO = URL_API + "/driver/fanli";//获取我的公司
    public static final String URL_NOTICE = URL_API + "/notice";//获取我的公告
    public static final String URL_MESSAGE = URL_API + "/message";//获取我的通知
    public static final String URL_UPDATE_MESSAGE = URL_API + "/updateMessage";//更新消息为已读
    public static final String URL_DETAIL_AMOUNT_RULE = URL_SERVICE_PATH + "/www/calrule";//计价规则，webview展示

    public static final String URL_TAKE_ORDER = URL_API + "/driver/grabsingle";//司机接单
    public static final String URL_TAKE_TRAVER_ORDER = URL_API + "/order/getOrder";//司机接单(顺风车)
    public static final String URL_TAKE_ZHUANXIAN_ORDER = URL_API + "/special/grabsingle";//司机接单(专线)
    public static final String URL_ORDER_START = URL_API + "/driver/orderStart";//订单行程开始
    public static final String URL_ORDER_WAIT = URL_API + "/driver/orderWait";//订单行程开始

    public static final String URL_MODIFY = URL_API + "/member/update/password";//修改密码（记得密码）
    public static final String URL_REGISTER = URL_API + "/register";//注册
    public static final String URL_MODIFY_PAY_PWD = URL_API + "/tixianmimaxiugai";//重置提现支付密码
    public static final String URL_CHOOSE_BANK = URL_API + "/banks";//获取银行信息
    public static final String URL_TIXIAN = URL_API + "/tixian";//提现
    public static final String URL_TIXIAN_AMOUNT = URL_API + "/tixianjine";//可提现金额
    public static final String URL_TIXIAN_JILU = URL_API + "/tixianjilu";//提现记录
    public static final String URL_TIXIAN_PWD = URL_API + "/yanzhengtxmima";//提现密码验证
    public static final String URL_CAR_BRAND = URL_API + "/driver/car/brands";//获取车辆品牌
    public static final String URL_CAR_BRAND_MODEL = URL_API + "/driver/car/models";//获取车辆品牌
    public static final String URL_UNFINISHED_ORDER = URL_API + "/driver/getOrder";//获取未完成的订单信息
    public static final String URL_CHECK_RESERVATION_ORDER = URL_API + "/driver/hasyuyue";//获取是否有预约订单
    public static final String URL_AD = URL_API + "/ad";//获取广告图片
    public static final String URL_UPDATECLIENT = URL_API + "/member/update";//更新会员信息（GET）
    public static final String URL_UPDATEDRIVER = URL_API + "/member/update";//更新司机信息
    public static final String URL_UPDATEADCODE = URL_API + "/updateCityCode";//更新会员信息（GET）
    public static final String URL_PLUS_CAR = URL_API + "/driver/car/plus";//增加车辆信息
    public static final String URL_GETCARINFO = URL_API + "/member/cars";//获取车辆列表
    public static final String URL_GETDRIVERINFO = URL_API + "/member/drivers";//获取代驾司机列表
    public static final String URL_DRIVER_CREATE_ORDER = URL_API + "/order/drivercreate";//代驾司机创建订单
    public static final String URL_TOTAL_AMOUNT = URL_API + "/order/totalAmount";//计算总价格
    public static final String URL_DRIVER_ARRIVE_AMOUNT = URL_API + "/driver/orderArrived";//计算结算总价格
    public static final String URL_DRIVER_YE_AMOUNT = URL_API + "/driver/amount";//获得司机余额
    public static final String URL_ACCOUNT_INFO = URL_API + "/accountlog";//账户明细
    public static final String URL_RECHARGE_AMOUNT = URL_API + "/rechangeItem";//获得充值项
    public static final String URL_DRIVER_ALIRECHARGE = URL_API + "/order/recharge";//充值
    public static final String URL_DRIVER_WXRECHARGE = URL_API + "/order/wxrecharge";//充值
    public static final String URL_HISTORY_ORDER = URL_API + "/order/myOrder";//历史订单
    public static final String URL_GET_RESERVATION_ORDER = URL_API + "/order/yuyuedingdan";//预约订单详情
    public static final String URL_RESERVATION_ORDER = URL_API + "/driver/getYuyueOrder";//预约订单
    public static final String URL_UPDATE_APP = URL_API + "/update";//app版本升级

    public static final String URL_TRAVERLIST = URL_API + "/order/getCustomerTraver";//获取顺风车列表
    public static final String URL_TRAVERLIST_HISTORY = URL_API + "/order/historyRecord";//获取顺风车列表
    public static final String URL_TRAVER_EXECUTION_CUSTOMER = URL_API + "/order/showCustomer";//获取顺风车行程的乘客信息
    public static final String URL_ZHUANXIAN_EXECUTION_CUSTOMER = URL_API + "/special/getOrder";//获取专车行程的乘客信息
    public static final String URL_ZHUANXIANLIST_EXECUTION = URL_API + "/special/getline";//获取执行中专线列表
    public static final String URL_ZHUANXIANLIST = URL_API + "/special/getSpecialCar";//获取专线列表
    public static final String URL_TRAVER_PUBLISH = URL_API + "/order/traver";//顺风车发布行程
    public static final String URL_TRAVER_PEER = URL_API + "/order/choseCustomer";//确认同行顺风车
    public static final String URL_ZHUANXIAN_PEER = URL_API + "/special/selectline";//确认同行顺风车
    public static final String URL_TRAVER_CANCEL = URL_API + "/order/shunFengCheCancel";//取消顺风车
    public static final String URL_TRAVER_START = URL_API + "/order/orderStart";//顺风车行程开始
    public static final String URL_ZHUANXIAN_START = URL_API + "/special/orderStart";//专线行程开始
    public static final String URL_ZHUANXIAN_ARRIVE = URL_API + "/special/orderArrived";//专线行程结束（结算）
    public static final String URL_TRAVER_ARRIVE = URL_API + "/order/orderArrived";//顺风车行程结束（结算）
    public static final String URL_ZHUANXIAN_CANCEL = URL_API + "/special/deleteline";//取消专线
    public static final String URL_TRAVER_AMOUNT = URL_API + "/order/shunAmount";//顺风车价格
    public static final String URL_CUSTOMER_LOCATION = URL_API + "/order/memberLocation";//获取乘客位置（顺风车、专线）

    public static final String STORAGEFILEPATH = Environment.getExternalStorageDirectory().getAbsolutePath();//文件本地地址
    public static final String FILEPATH = STORAGEFILEPATH + File.separator + "DianDianDriver" + File.separator;//文件本地地址
    public static final String FILEIMAGEPATH = FILEPATH + "Image";//文件本地地址
    public static final String FILEIMAGEQRCODEPATH = "qrCode.jpg";//推荐二维码文件本地地址
    public static final String ADIMAGEPATH_MAIN = FILEIMAGEPATH + File.separator + "Main" + File.separator;//主界面广告文件本地地址（父目录）
    public static final String ADIMAGEPATH_START = FILEIMAGEPATH + File.separator + "Start";//启动页广告文件本地地址（父目录）

    public static final String URL_UPDATELOCATION = URL_API + "/member/update/location";//更新位置
    public static final String URL_UPDATELOCATION_DRIVER = URL_API + "/driver/update/location";//更新位置
    public static final String URL_DRIVERENROLL = URL_SERVICE_PATH + "/driver/enroll";//司机报名页面
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
    public static final String APP_ID_WX = "wxd79e00cf03529e97";
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
    public static final int REQUEST_LOGIN = 110;//DIALOG 登陆
    public static final int REQUEST_CREATE_ORDER = 113;//创建订单选起点
    public static final int REQUEST_CHOOSE_ADDR_POSITION = 111;//创建订单选起点
    public static final int REQUEST_CHOOSE_ADDR_DESTINATION = 112;//创建订单选终点
    public static final int REQUEST_ORDER_ACTIVITY = 114;//打开orderActivity
    public static final int REQUEST_OPEN_GPS = 120;//打开gps
    public static final int TIME_Y = 2017;
    public static final int TIME_M = 9;
    public static final int REQUESTCHOOSEBANK = 14;//选择银行
    public static final int REQUESTCONTACT = 15;//选择通讯录联系人
    /**
     * js打开系统文件请求码
     */
    public static final int FILECHOOSER_RESULTCODE = 10000;
    public static final String MESSAGE_RECEIVED_ACTION = "com.example.jpushdemo.MESSAGE_RECEIVED_ACTION";//receiver服务
    public static final String URL_LIANCHENG = "http://www.52liancheng.com";//链城网
    public static final String VERSION_URL = "http://223.26.63.3/mobilesafe/update.json";
    public static final int SQL_TABLE_VERSION = 1;
    public static final String SQL_TABLE_NAME = "city";
    public static final String SQL_DB_NAME = "dbcity";
    public static final String AMAP_AREA = "dele_area.json";
    public static final String ORDER_MESSAGE_RECEIVED_ACTION = "com.delelong.diandiandriver.ORDER_MESSAGE_RECEIVED_ACTION";
    public static final String WXPAY_RESULT_ACTION = "com.delelong.diandian.WXPAY_RESULT_ACTION";
    public static final String KEY_ORDER_MESSAGE = "order_message";
    public static final String KEY_ORDER_EXTRA = "order_extra";
    public static final String KEY_ORDER_TITLE = "order_title";

    public static final String INTERNET = "android.permission.INTERNET";
    public static final int REQUEST_INTERNET = 10;
    public static final String CALL_PHONE = "android.permission.CALL_PHONE";
    public static final String WRITE_EXTERNALSTORAGE = "android.permission.WRITE_EXTERNAL_STORAGE";
    public static final int REQUEST_WRITE_EXTERNALSTORAGE = 14;
    public static final String DELE_CREATE_EXTERNALSTORAGE = "android.permission.MOUNT_UNMOUNT_FILESYSTEMS";
    public static final int REQUEST_DELE_CREATE_EXTERNALSTORAGE = 15;
    public static final int REQUEST_CALL_PHONE = 11;
    public static final String ACCESS_COARSE_LOCATION = "android.permission.ACCESS_COARSE_LOCATION";
    public static final int REQUEST_ACCESS_COARSE_LOCATION = 12;
    public static final String ACCESS_FINE_LOCATION = "android.permission.ACCESS_FINE_LOCATION";
    public static final int REQUEST_ACCESS_FINE_LOCATION = 13;
    public static final int REQUEST_CONFIRM_AMOUNT = 20;

    public static final String SEARCH_GASSTATION = "加油站";
    public static final String SEARCH_MARKET = "便利店";
    public static final String SEARCH_HOTEL = "星级酒店";
    public static final String SEARCH_WC = "公厕";
    public static final String SEARCH_FOOD = "美食";
    public static final String SEARCH_BAR = "酒吧";
    public static final String SEARCH_KTV = "KTV";
}
