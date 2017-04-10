package com.delelong.diandiandriver.traver.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.delelong.diandiandriver.base.bean.BaseBean;

import java.math.BigDecimal;

/**
 * Created by Administrator on 2017/3/17.
 */

public class TraverCustomerBean extends BaseBean {

    /**
     * 乘客id
     */
    @JSONField(name = "id")
    private BigDecimal id;
    /**
     * 乘客真实姓名
     */
    @JSONField(name = "real_name")
    private String realName;
    /**
     * 乘客昵称
     */
    @JSONField(name = "nick_name")
    private String nickName;
    /**
     * 乘客手机号
     */
    @JSONField(name = "phone")
    private String phone;
    /**
     * 乘客头像地址
     */
    @JSONField(name = "head_portrait")
    private String headPortrait;
    /**
     * 备注信息
     */
    private String note;
    /**
     * 该订单人数
     */
    @JSONField(name = "people")
    private BigDecimal peopleNum;
    /**
     * 该订单价格
     */
    @JSONField(name = "money")
    private BigDecimal amount;
    /**
     * 是否拼车：0：包车；1：拼车；2：寄货
     */
    @JSONField(name = "pd")
    private BigDecimal pd;

    public TraverCustomerBean() {
    }

    public TraverCustomerBean(BigDecimal id, String realName, String nickName, String phone, String headPortrait, String note, BigDecimal peopleNum, BigDecimal amount, BigDecimal pd) {
        this.id = id;
        this.realName = realName;
        this.nickName = nickName;
        this.phone = phone;
        this.headPortrait = headPortrait;
        this.note = note;
        this.peopleNum = peopleNum;
        this.amount = amount;
        this.pd = pd;
    }

    public BigDecimal getId() {
        return id;
    }

    public void setId(BigDecimal id) {
        this.id = id;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
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

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public BigDecimal getPeopleNum() {
        return peopleNum;
    }

    public void setPeopleNum(BigDecimal peopleNum) {
        this.peopleNum = peopleNum;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getPd() {
        return pd;
    }

    public void setPd(BigDecimal pd) {
        this.pd = pd;
    }

    @Override
    public String toString() {
        return "TraverCustomerBean{" +
                "id=" + id +
                ", realName='" + realName + '\'' +
                ", nickName='" + nickName + '\'' +
                ", phone='" + phone + '\'' +
                ", headPortrait='" + headPortrait + '\'' +
                ", note='" + note + '\'' +
                ", peopleNum=" + peopleNum +
                ", amount=" + amount +
                ", pd=" + pd +
                '}';
    }
}
