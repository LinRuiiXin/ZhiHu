package com.sz.zhihu.vo;

import com.sz.zhihu.po.AnswerCommentLevelTwo;
import com.sz.zhihu.po.User;

public class AnswerCommentLevelTwoVo {
    private boolean isSupport;
    private AnswerCommentLevelTwo answerCommentLevelTwo;
    private User userReplyTo;
    private User replyUser;

    public boolean isSupport() {
        return isSupport;
    }

    public void setSupport(boolean support) {
        isSupport = support;
    }

    public AnswerCommentLevelTwo getAnswerCommentLevelTwo() {
        return answerCommentLevelTwo;
    }

    public void setAnswerCommentLevelTwo(AnswerCommentLevelTwo answerCommentLevelTwo) {
        this.answerCommentLevelTwo = answerCommentLevelTwo;
    }

    public User getUserReplyTo() {
        return userReplyTo;
    }

    public void setUserReplyTo(User userReplyTo) {
        this.userReplyTo = userReplyTo;
    }

    public User getReplyUser() {
        return replyUser;
    }

    public void setReplyUser(User replyUser) {
        this.replyUser = replyUser;
    }

    @Override
    public String toString() {
        return "AnswerCommentLevelTwoVo{" +
                "isSupport=" + isSupport +
                ", answerCommentLevelTwo=" + answerCommentLevelTwo +
                ", userReplyTo=" + userReplyTo +
                ", replyUser=" + replyUser +
                '}';
    }
}
