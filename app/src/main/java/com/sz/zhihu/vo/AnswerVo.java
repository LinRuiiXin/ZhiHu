package com.sz.zhihu.vo;

import com.sz.zhihu.po.Answer;
import com.sz.zhihu.po.User;

public class AnswerVo {
    private boolean attention;
    private boolean support;
    private User user;
    private Answer answer;

    public AnswerVo(User user, Answer answer) {
        this.user = user;
        this.answer = answer;
    }

    public AnswerVo() {
    }

    public boolean isAttention() {
        return attention;
    }

    public void setAttention(boolean attention) {
        this.attention = attention;
    }

    public boolean isSupport() {
        return support;
    }

    public void setSupport(boolean support) {
        this.support = support;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Answer getAnswer() {
        return answer;
    }

    public void setAnswer(Answer answer) {
        this.answer = answer;
    }

    @Override
    public String toString() {
        return "AnswerVo{" +
                "attention=" + attention +
                ", support=" + support +
                ", user=" + user +
                ", answer=" + answer +
                '}';
    }
}
