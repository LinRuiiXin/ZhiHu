package com.sz.zhihu.po;

import android.content.Intent;

import java.util.Date;

public class Answer {
    private Long id;
    private Long questionId;
    private Long userId;
    private String content;
    private Integer contentType;
    private Long supportSum;
    private Long commentSum;
    private Date time;

    public Answer() {
    }

    public Answer(Long id, Long questionId, Long userId, String content, Integer contentType, Long supportSum, Long commentSum, Date time) {
        this.id = id;
        this.questionId = questionId;
        this.userId = userId;
        this.content = content;
        this.contentType = contentType;
        this.supportSum = supportSum;
        this.commentSum = commentSum;
        this.time = time;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
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

    public Integer getContentType() {
        return contentType;
    }

    public void setContentType(Integer contentType) {
        this.contentType = contentType;
    }

    public Long getSupportSum() {
        return supportSum;
    }

    public void setSupportSum(Long supportSum) {
        this.supportSum = supportSum;
    }

    public Long getCommentSum() {
        return commentSum;
    }

    public void setCommentSum(Long commentSum) {
        this.commentSum = commentSum;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "Answer{" +
                "id=" + id +
                ", questionId=" + questionId +
                ", userId=" + userId +
                ", content='" + content + '\'' +
                ", contentType=" + contentType +
                ", supportSum=" + supportSum +
                ", commentSum=" + commentSum +
                ", time=" + time +
                '}';
    }
}
