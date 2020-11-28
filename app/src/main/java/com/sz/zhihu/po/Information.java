package com.sz.zhihu.po;

import org.litepal.crud.DataSupport;

public class Information extends DataSupport {
    private Long id;
    private Long informationId;
    private Integer type;
    private Long contentId;
    private Long authorId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getInformationId() {
        return informationId;
    }

    public void setInformationId(Long informationId) {
        this.informationId = informationId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Long getContentId() {
        return contentId;
    }

    public void setContentId(Long contentId) {
        this.contentId = contentId;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    @Override
    public String toString() {
        return "Information{" +
                "id=" + id +
                ", informationId=" + informationId +
                ", type=" + type +
                ", contentId=" + contentId +
                ", authorId=" + authorId +
                '}';
    }
}
