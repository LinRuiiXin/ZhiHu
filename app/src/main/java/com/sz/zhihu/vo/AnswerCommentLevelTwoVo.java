package com.sz.zhihu.vo;

import com.sz.zhihu.po.AnswerCommentLevelTwo;
import com.sz.zhihu.po.User;

public class AnswerCommentLevelTwoVo {
    private boolean isSupport;
    private AnswerCommentLevelTwo answerCommentLevelTwo;
    private User userReplyTo;
    private User replyUser;

    public AnswerCommentLevelTwoVo() {
    }

    public AnswerCommentLevelTwoVo(boolean isSupport, AnswerCommentLevelTwo answerCommentLevelTwo, User userReplyTo, User replyUser) {
        this.isSupport = isSupport;
        this.answerCommentLevelTwo = answerCommentLevelTwo;
        this.userReplyTo = userReplyTo;
        this.replyUser = replyUser;
    }

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

    public void support(){
        this.isSupport = true;
        answerCommentLevelTwo.incrementSupportSum();
    }

    public void unSupport(){
        this.isSupport = false;
        answerCommentLevelTwo.decrementSupportSum();
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
