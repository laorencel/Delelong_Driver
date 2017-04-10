package com.delelong.diandiandriver.base.bean;

import java.io.Serializable;

/**
 * EventBus 发布的类继承此类
 * Created by Administrator on 2017/3/15.
 */

public class BaseEvent<T> extends  BaseBean implements Serializable {
    private int requestCode;
    private T content;
    public BaseEvent() {
    }

    public BaseEvent(int requestCode, T content) {
        this.requestCode = requestCode;
        this.content = content;
    }

    public int getRequestCode() {
        return requestCode;
    }

    public void setRequestCode(int requestCode) {
        this.requestCode = requestCode;
    }

    public T getContent() {
        return content;
    }

    public void setContent(T content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "BaseEvent{" +
                "requestCode=" + requestCode +
                ", content=" + content +
                '}';
    }
}
