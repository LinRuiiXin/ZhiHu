package com.sz.zhihu.po;

import java.util.Arrays;

public class Settings {
    private String name;
    private String [] params;
    private String [] className;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String[] getParams() {
        return params;
    }

    public void setParams(String[] params) {
        this.params = params;
    }

    public String[] getClassName() {
        return className;
    }

    public void setClassName(String[] className) {
        this.className = className;
    }

    @Override
    public String toString() {
        return "Settings{" +
                "name='" + name + '\'' +
                ", params=" + Arrays.toString(params) +
                ", className=" + Arrays.toString(className) +
                '}';
    }
}
