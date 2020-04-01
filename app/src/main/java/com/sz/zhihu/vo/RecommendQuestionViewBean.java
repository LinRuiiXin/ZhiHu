package com.sz.zhihu.vo;

import com.sz.zhihu.po.Question;
import com.sz.zhihu.po.User;

public class RecommendQuestionViewBean {
    private Question question;
    private User user;

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

    @Override
    public String toString() {
        return "RecommendQuestionCardBean{" +
                "question=" + question +
                ", user=" + user +
                '}';
    }
}
