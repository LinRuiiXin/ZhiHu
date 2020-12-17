package com.sz.zhihu.vo;

public class ArticleVo {
    private boolean support;
    private boolean attention;
    private Long supportSum;
    private String content;

    public ArticleVo() {
    }

    public ArticleVo(boolean isSupport, boolean isAttention, Long supportSum, String content) {
        this.support = isSupport;
        this.attention = isAttention;
        this.supportSum = supportSum;
        this.content = content;
    }

    public boolean isSupport() {
        return support;
    }

    public void setSupport(boolean support) {
        this.support = support;
    }

    public boolean isAttention() {
        return attention;
    }

    public void setAttention(boolean attention) {
        this.attention = attention;
    }

    public Long getSupportSum() {
        return supportSum;
    }

    public void setSupportSum(Long supportSum) {
        this.supportSum = supportSum;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void invertSelect(){
        if(support){
            decrementSupportSum();
        }else
            incrementSupportSum();
        support = !support;
    }

    public void incrementSupportSum(){
        supportSum++;
    }

    public void decrementSupportSum(){
        supportSum--;
    }

    @Override
    public String toString() {
        return "ArticleVo{" +
                "isSupport=" + support +
                ", isAttention=" + attention +
                ", supportSum=" + supportSum +
                ", content='" + content + '\'' +
                '}';
    }
}
