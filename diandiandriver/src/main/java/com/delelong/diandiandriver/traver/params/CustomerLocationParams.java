package com.delelong.diandiandriver.traver.params;

import com.alibaba.fastjson.annotation.JSONField;
import com.delelong.diandiandriver.base.params.BaseParams;

import java.math.BigDecimal;

/**
 * Created by Administrator on 2017/3/25.
 */

public class CustomerLocationParams extends BaseParams {

    /**
     * 客户id
     */
    @JSONField(name = "id")
    private BigDecimal id;

    public CustomerLocationParams(BigDecimal id) {
        this.id = id;
    }

    public BigDecimal getId() {
        return id;
    }

    public void setId(BigDecimal id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "CustomerLocationParams{" +
                "id=" + id +
                '}';
    }
}
