package com.sz.zhihu.po;


import org.litepal.crud.DataSupport;

import java.io.Serializable;

public class Keyword extends DataSupport implements Serializable {

    private int id;
    private String title;

    public Keyword() {
    }

    public Keyword(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Keyword{" +
                "id=" + id +
                ", title='" + title + '\'' +
                '}';
    }
}
