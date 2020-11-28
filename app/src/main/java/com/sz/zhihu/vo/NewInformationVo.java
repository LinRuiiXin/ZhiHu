package com.sz.zhihu.vo;

import com.sz.zhihu.po.Information;
import com.sz.zhihu.po.UserAttention;

import java.util.List;

public class NewInformationVo {

    private List<Integer> newVersion;
    private List<Information> information;

    public List<Integer> getNewVersion() {
        return newVersion;
    }

    public void setNewVersion(List<Integer> newVersion) {
        this.newVersion = newVersion;
    }

    public List<Information> getInformation() {
        return information;
    }

    public void setInformation(List<Information> information) {
        this.information = information;
    }

    @Override
    public String toString() {
        return "NewInformationVo{" +
                "newVersion=" + newVersion +
                ", information=" + information +
                '}';
    }
}
