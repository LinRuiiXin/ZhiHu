package com.sz.zhihu.vo;
/*
* 首页---推荐
*   记录每个卡片的信息
* */
public class RecommendViewBean {
    private Long contentId;//内容id，用contentType区分文章、回答
    private Long userId;//用户id
    private int contentType; //1-回答 2.文章
    private int type;//1-纯文本 2-带图片 3-带视频
    private String title;//标题
    private String username;//用户名
    private String introduction;//个人简介
    private String content;//内容
    private Long supportSum;//点赞数
    private Long commentSum;//评论数


    public RecommendViewBean(Long contentId, Long userId, int contentType, int type, String title, String username, String introduction, String content, Long supportSum, Long commentSum) {
        this.contentId = contentId;
        this.userId = userId;
        this.contentType = contentType;
        this.type = type;
        this.title = title;
        this.username = username;
        this.introduction = introduction;
        this.content = content;
        this.supportSum = supportSum;
        this.commentSum = commentSum;
    }

    public Long getContentId() {
        return contentId;
    }

    public void setContentId(Long contentId) {
        this.contentId = contentId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public int getContentType() {
        return contentType;
    }

    public void setContentType(int contentType) {
        this.contentType = contentType;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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

    @Override
    public String toString() {
        return "CardViewBean{" +
                "contentId=" + contentId +
                ", userId=" + userId +
                ", contentType=" + contentType +
                ", type=" + type +
                ", title='" + title + '\'' +
                ", username='" + username + '\'' +
                ", introduction='" + introduction + '\'' +
                ", content='" + content + '\'' +
                ", supportSum=" + supportSum +
                ", commentSum=" + commentSum +
                '}';
    }
}
