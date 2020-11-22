package com.sz.zhihu.vo;

import com.sz.zhihu.po.AnswerCommentLevelOne;
import com.sz.zhihu.po.ArticleCommentLevelOne;
import com.sz.zhihu.po.User;

public class ArticleCommentLevelOneVo {
    private boolean support;
    private User user;
    private ArticleCommentLevelOne articleCommentLevelOne;

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

    public ArticleCommentLevelOne getArticleCommentLevelOne() {
        return articleCommentLevelOne;
    }

    public void setCommentLevelOne(ArticleCommentLevelOne commentLevelOne) {
        this.articleCommentLevelOne = commentLevelOne;
    }


    public void support(){
        support = true;
        articleCommentLevelOne.incrementSupportSum();
    }
    public void unSupport(){
        support = false;
        articleCommentLevelOne.decrementSupportSum();
    }
}
