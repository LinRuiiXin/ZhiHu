package com.sz.zhihu.po;
import java.util.Date;

public class ArticleCommentLevelOne {

    private Long id;
    private Long articleId;
    private Long userId;
    private String content;
    private Date time;
    private Long supportSum;
    private int hasReply;

    public ArticleCommentLevelOne() {
    }

    public ArticleCommentLevelOne(Long id, Long articleId, Long userId, String content, Date time, Long supportSum, int hasReply) {
        this.id = id;
        this.articleId = articleId;
        this.userId = userId;
        this.content = content;
        this.time = time;
        this.supportSum = supportSum;
        this.hasReply = hasReply;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getArticleId() {
        return articleId;
    }

    public void setArticleId(Long articleId) {
        this.articleId = articleId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public Long getSupportSum() {
        return supportSum;
    }

    public void setSupportSum(Long supportSum) {
        this.supportSum = supportSum;
    }

    public int getHasReply() {
        return hasReply;
    }

    public void setHasReply(int hasReply) {
        this.hasReply = hasReply;
    }

    @Override
    public String toString() {
        return "ArticleCommentLevelOne{" +
                "id=" + id +
                ", articleId=" + articleId +
                ", userId=" + userId +
                ", content='" + content + '\'' +
                ", time=" + time +
                ", supportSum=" + supportSum +
                ", hasReply=" + hasReply +
                '}';
    }

    public void incrementSupportSum(){
        supportSum++;
    }
    public void decrementSupportSum(){
        supportSum--;
    }
    public void incrementReply(){
        hasReply++;
    }
    public void decrementReply(){
        hasReply--;
    }
}
