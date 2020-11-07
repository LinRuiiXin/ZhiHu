package com.sz.zhihu.vo;

import com.sz.zhihu.po.AnswerCommentLevelOne;
import com.sz.zhihu.po.User;

public class AnswerCommentLevelOneVo {
    private boolean support;
    private User user;
    private AnswerCommentLevelOne commentLevelOne;

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

    public AnswerCommentLevelOne getCommentLevelOne() {
        return commentLevelOne;
    }

    public void setCommentLevelOne(AnswerCommentLevelOne commentLevelOne) {
        this.commentLevelOne = commentLevelOne;
    }

    public void support(){
        support = true;
        commentLevelOne.incrementSupportSum();
    }
    public void unSupport(){
        support = false;
        commentLevelOne.decrementSupportSum();
    }

    @Override
    public String toString() {
        return "AnswerCommentLevelOneVo{" +
                "isSupport=" + support +
                ", user=" + user +
                ", commentLevelOne=" + commentLevelOne +
                '}';
    }
}
