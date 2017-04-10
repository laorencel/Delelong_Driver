package com.delelong.diandiandriver.traver.params;

import com.alibaba.fastjson.annotation.JSONField;
import com.delelong.diandiandriver.base.params.BaseParams;

import java.math.BigDecimal;

/**
 * Created by Administrator on 2017/3/9.
 */

public class ArriveTraverParams extends BaseParams {
    /**
     * 乘客行程路线id
     */
    @JSONField(name = "id")
    private BigDecimal id;

    /**
     * (司机端 0：包车 1：拼车 2：寄货)
     */
    @JSONField(name = "pd")
    private BigDecimal pd;
    /**
     * 支付类型(1:支付宝;2:代收;3:余额;4:微信)
     */
    @JSONField(name = "payType")
    private BigDecimal payType;

    public ArriveTraverParams() {
    }

    public ArriveTraverParams(BigDecimal id, BigDecimal pd, BigDecimal payType) {
        this.id = id;
        this.pd = pd;
        this.payType = payType;
    }

    public BigDecimal getId() {
        return id;
    }

    public void setId(BigDecimal id) {
        this.id = id;
    }

    public BigDecimal getPd() {
        return pd;
    }

    public void setPd(BigDecimal pd) {
        this.pd = pd;
    }

    public BigDecimal getPayType() {
        return payType;
    }

    public void setPayType(BigDecimal payType) {
        this.payType = payType;
    }

    @Override
    public String toString() {
        return "ArriveTraverParams{" +
                "id=" + id +
                ", pd=" + pd +
                ", payType=" + payType +
                '}';
    }
}
