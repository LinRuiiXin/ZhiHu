package com.sz.zhihu.vo;

public class ArticleVo {
    private boolean isSupport;
    private boolean isAttention;
    private String content;

    public boolean isSupport() {
        return isSupport;
    }

    public void setSupport(boolean support) {
        isSupport = support;
    }

    public boolean isAttention() {
        return isAttention;
    }

    public void setAttention(boolean attention) {
        isAttention = attention;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "ArticleVo{" +
                "isSupport=" + isSupport +
                ", isAttention=" + isAttention +
                ", content='" + content + '\'' +
                '}';
    }
}
