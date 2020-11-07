package com.sz.zhihu.po;

import java.util.Date;

public class AnswerCommentLevelOne {
    private Long id;
    private Long answerId;
    private Long userId;
    private String content;
    private Date time;
    private Long supportSum;
    private int hasReply;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAnswerId() {
        return answerId;
    }

    public void setAnswerId(Long answerId) {
        this.answerId = answerId;
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

    public void incrementSupportSum(){
        supportSum++;
    }
    public void decrementSupportSum(){
        supportSum--;
    }

    @Override
    public String toString() {
        return "AnswerCommentLevelOne{" +
                "id=" + id +
                ", answerId=" + answerId +
                ", userId=" + userId +
                ", content='" + content + '\'' +
                ", time=" + time +
                ", supportSum=" + supportSum +
                ", hasReply=" + hasReply +
                '}';
    }
}
