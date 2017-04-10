package com.delelong.diandiandriver.traver.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.delelong.diandiandriver.base.bean.BaseBean;

import java.math.BigDecimal;

/**
 * Created by Administrator on 2017/3/18.
 */

public class ZhuanXianCustomerBean extends BaseBean {

    /**
     * 订单ID
     */
    @JSONField(name = "id")
    private BigDecimal id;
    /**
     * 该订单联系人姓名
     */
    @JSONField(name = "name")
    private String name;
    /**
     * 该订单联系方式
     */
    @JSONField(name = "phone")
    private String phone;
    /**
     * 乘客头像
     */
    @JSONField(name = "headPortrait")
    private String headPortrait;
    /**
     * 该订单人数
     */
    @JSONField(name = "people")
    private BigDecimal people;
    /**
     * 该订单价格
     */
    @JSONField(name = "amount")
    private BigDecimal amount;
    /**
     * 是否拼车（1.拼车 0.不拼车）
     */
    @JSONField(name = "type")
    private boolean type;
    /**
     * 订单状态 ( 1;//订单创建 2;//司机接单3;//订单开始5;//订单已支付6;//订单取消4;//到达终点9;//订单完成 7;//司机开始等待)
     */
    @JSONField(name = "status")
    private BigDecimal status;

    public ZhuanXianCustomerBean() {
    }

    public ZhuanXianCustomerBean(BigDecimal id, String name, String phone, String headPortrait, BigDecimal people, BigDecimal amount, boolean type, BigDecimal status) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.headPortrait = headPortrait;
        this.people = people;
        this.amount = amount;
        this.type = type;
        this.status = status;
    }

    public BigDecimal getId() {
        return id;
    }

    public void setId(BigDecimal id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getHeadPortrait() {
        return headPortrait;
    }

    public void setHeadPortrait(String headPortrait) {
        this.headPortrait = headPortrait;
    }

    public BigDecimal getPeople() {
        return people;
    }

    public void setPeople(BigDecimal people) {
        this.people = people;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public boolean getType() {
        return type;
    }

    public void setType(boolean type) {
        this.type = type;
    }

    public BigDecimal getStatus() {
        return status;
    }

    public void setStatus(BigDecimal status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "ZhuanXianCustomerBean{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", headPortrait='" + headPortrait + '\'' +
                ", people=" + people +
                ", amount=" + amount +
                ", type=" + type +
                ", status=" + status +
                '}';
    }
}
