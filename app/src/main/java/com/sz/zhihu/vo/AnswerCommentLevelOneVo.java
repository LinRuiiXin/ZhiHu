package com.sz.zhihu.vo;

import com.sz.zhihu.po.AnswerCommentLevelOne;
import com.sz.zhihu.po.User;

public class AnswerCommentLevelOneVo {
    private boolean isSupport;
    private User user;
    private AnswerCommentLevelOne commentLevelOne;

    public boolean isSupport() {
        return isSupport;
    }

    public void setSupport(boolean support) {
        isSupport = support;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public AnswerCommentLevelOne getCommentLevelOne() {
        return commentLevelOne;
    }

    public void setCommentLevelOne(AnswerCommentLevelOne commentLevelOne) {
        this.commentLevelOne = commentLevelOne;
    }

    @Override
    public String toString() {
        return "AnswerCommentLevelOneVo{" +
                "isSupport=" + isSupport +
                ", user=" + user +
                ", commentLevelOne=" + commentLevelOne +
                '}';
    }
}
