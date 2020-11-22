package com.sz.zhihu.vo;

import com.sz.zhihu.po.AnswerCommentLevelTwo;
import com.sz.zhihu.po.ArticleCommentLevelTwo;
import com.sz.zhihu.po.User;

public class ArticleCommentLevelTwoVo {
    private boolean isSupport;
    private ArticleCommentLevelTwo articleCommentLevelTwo;
    private User userReplyTo;
    private User replyUser;

    public boolean isSupport() {
        return isSupport;
    }

    public void setSupport(boolean support) {
        isSupport = support;
    }

    public ArticleCommentLevelTwo getAnswerCommentLevelTwo() {
        return articleCommentLevelTwo;
    }

    public void setAnswerCommentLevelTwo(ArticleCommentLevelTwo articleCommentLevelTwo) {
        this.articleCommentLevelTwo = articleCommentLevelTwo;
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
        articleCommentLevelTwo.incrementSupportSum();
    }

    public void unSupport(){
        this.isSupport = false;
        articleCommentLevelTwo.decrementSupportSum();
    }

    @Override
    public String toString() {
        return "AnswerCommentLevelTwoVo{" +
                "isSupport=" + isSupport +
                ", ArticleCommentLevelTwo=" + articleCommentLevelTwo +
                ", userReplyTo=" + userReplyTo +
                ", replyUser=" + replyUser +
                '}';
    }
}
