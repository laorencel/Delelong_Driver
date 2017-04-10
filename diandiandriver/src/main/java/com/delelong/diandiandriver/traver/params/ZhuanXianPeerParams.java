package com.delelong.diandiandriver.traver.params;

import com.alibaba.fastjson.annotation.JSONField;
import com.delelong.diandiandriver.base.params.BaseParams;

import java.math.BigDecimal;

/**
 * Created by Administrator on 2017/3/9.
 */

public class ZhuanXianPeerParams extends BaseParams {
    /**
     * 乘客行程路线id
     */
    @JSONField(name = "id")
    private BigDecimal id;
    /**
     * 顺风车类型(1.拼车 2.包车 3.寄货)
     */
    @JSONField(name = "totalPeople")
    private BigDecimal totalPeople;

    public ZhuanXianPeerParams() {
    }

    public ZhuanXianPeerParams(BigDecimal id, BigDecimal totalPeople) {
        this.id = id;
        this.totalPeople = totalPeople;
    }

    public BigDecimal getId() {
        return id;
    }

    public void setId(BigDecimal id) {
        this.id = id;
    }

    public BigDecimal getType() {
        return totalPeople;
    }

    public void setType(BigDecimal type) {
        this.totalPeople = type;
    }

    @Override
    public String toString() {
        return "PeerParams{" +
                "id=" + id +
                ", totalPeople=" + totalPeople +
                '}';
    }
}
