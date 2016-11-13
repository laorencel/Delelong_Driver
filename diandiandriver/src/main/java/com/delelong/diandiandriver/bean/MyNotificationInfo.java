package com.delelong.diandiandriver.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/11/4.
 */

public class MyNotificationInfo implements Serializable {

    private String title;
    private String content;

    public MyNotificationInfo(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
