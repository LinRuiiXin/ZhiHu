package com.sz.zhihu.holder;

import java.security.Key;

public class CommentHolder {
    private boolean isLoad;
    private int level;
    private int limit;

    public CommentHolder() {
        isLoad = false;
        level = 1;
        limit = 0;
    }

    public void setLoad(boolean load) {
        isLoad = load;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public boolean isLoad(){
        return isLoad;
    }

    public boolean isLevelOne(){
        return level == 1;
    }

    public boolean isLevelTwo(){
        return level == 2;
    }

    public void switchLevelOne(){
        level = 1;
    }

    public void switchLevelTwo(){
        level = 2;
    }

    public void addLimit(int loadDataSum){
        limit += loadDataSum;
    }

    public int getLimit() {
        return limit;
    }

    public void clear(){
        isLoad = false;
        level = 1;
        limit = 0;
    }
}
