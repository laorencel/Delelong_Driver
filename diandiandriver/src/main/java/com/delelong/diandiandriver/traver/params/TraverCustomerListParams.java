package com.delelong.diandiandriver.traver.params;

import com.alibaba.fastjson.annotation.JSONField;
import com.delelong.diandiandriver.base.params.BasePageParams;

import java.math.BigDecimal;

/**
 * Created by Administrator on 2017/3/9.
 */

public class TraverCustomerListParams extends BasePageParams {
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

    public TraverCustomerListParams() {
    }

    public TraverCustomerListParams(BigDecimal id, BigDecimal pd) {
        this.id = id;
        this.pd = pd;
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

    @Override
    public String toString() {
        return "TraverCustomerListParams{" +
                "id=" + id +
                ", pd=" + pd +
                '}';
    }
}
