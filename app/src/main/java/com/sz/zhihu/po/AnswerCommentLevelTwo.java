package com.sz.zhihu.po;

import java.util.Date;

public class AnswerCommentLevelTwo {
    private Long id;
    private Long levelOneId;
    private Long replyToUserId;
    private Long replyUserId;
    private String content;
    private Date time;
    private Long supportSum;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getLevelOneId() {
        return levelOneId;
    }

    public void setLevelOneId(Long levelOneId) {
        this.levelOneId = levelOneId;
    }

    public Long getReplyToUserId() {
        return replyToUserId;
    }

    public void setReplyToUserId(Long replyToUserId) {
        this.replyToUserId = replyToUserId;
    }

    public Long getReplyUserId() {
        return replyUserId;
    }

    public void setReplyUserId(Long replyUserId) {
        this.replyUserId = replyUserId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        content = content;
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

    public void incrementSupportSum(){
        supportSum++;
    }

    public void decrementSupportSum(){
        supportSum--;
    }
    @Override
    public String toString() {
        return "AnswerCommentLevelTwo{" +
                "id=" + id +
                ", levelOneId=" + levelOneId +
                ", replyToUserId=" + replyToUserId +
                ", replyUserId=" + replyUserId +
                ", Content='" + content + '\'' +
                ", time=" + time +
                ", supportSum=" + supportSum +
                '}';
    }
}
