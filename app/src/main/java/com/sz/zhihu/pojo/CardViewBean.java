package com.sz.zhihu.pojo;

public class CardViewBean {
    private Long contentId;
    private Long userId;
    private int contentType; //1-Answer 2.Artical
    private int type;//1-all text 2-has image 3-has video
    private String title;
    private String username;
    private String introduction;
    private String content;
    private Long supportSum;
    private Long commentSum;


    public CardViewBean(Long contentId, Long userId, int contentType, int type, String title, String username, String introduction, String content, Long supportSum, Long commentSum) {
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
