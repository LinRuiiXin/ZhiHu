package com.sz.zhihu.po;

import org.litepal.crud.DataSupport;

public class UserAttention extends DataSupport {
    private int id; // SqlLite默认主键
    private Long userId; // 用户Id
    private Long beAttentionUserId; // 被关注用户Id
    private int version; // 版本号(用来与关注用户的最新版本号比较，如果最新版本号高于本地，则认为数据不是最新)

    public UserAttention(){}

    public UserAttention(Long userId, Long beAttentionUserId, int version) {
        this.userId = userId;
        this.beAttentionUserId = beAttentionUserId;
        this.version = version;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getBeAttentionUserId() {
        return beAttentionUserId;
    }

    public void setBeAttentionUserId(Long beAttentionUserId) {
        this.beAttentionUserId = beAttentionUserId;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "UserAttention{" +
                "id=" + id +
                ", userId=" + userId +
                ", beAttentionUserId=" + beAttentionUserId +
                ", version=" + version +
                '}';
    }
}
