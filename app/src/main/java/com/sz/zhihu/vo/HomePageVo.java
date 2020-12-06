package com.sz.zhihu.vo;

import com.sz.zhihu.po.User;

import java.util.List;

public class HomePageVo {

    private boolean attention;
    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isAttention() {
        return attention;
    }

    public void setAttention(boolean attention) {
        this.attention = attention;
    }

    @Override
    public String toString() {
        return "HomePageVo{" +
                "attention=" + attention +
                ", user=" + user +
                '}';
    }
}
