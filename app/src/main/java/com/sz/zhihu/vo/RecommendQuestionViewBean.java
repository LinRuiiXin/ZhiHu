package com.sz.zhihu.vo;

import com.sz.zhihu.po.Question;
import com.sz.zhihu.po.User;

public class RecommendQuestionViewBean {
    private boolean subscribe;
    private Question question;
    private User user;

    public RecommendQuestionViewBean() {
    }

    public RecommendQuestionViewBean(Question question) {
        this.question = question;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isSubscribe() {
        return subscribe;
    }

    public void setSubscribe(boolean subscribe) {
        this.subscribe = subscribe;
    }

    public static RecommendQuestionViewBean getLoadingViewBean(){
        return new RecommendQuestionViewBean(new Question(-1l));
    }

    @Override
    public String toString() {
        return "RecommendQuestionViewBean{" +
                "subscribe=" + subscribe +
                ", question=" + question +
                ", user=" + user +
                '}';
    }
}
