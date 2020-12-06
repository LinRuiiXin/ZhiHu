package com.sz.zhihu.po;

import org.litepal.crud.DataSupport;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

public class User extends DataSupport implements Serializable {
    //SQLite默认主键
    private Long id;
    //真正用户id
    private Long userId;
    private boolean isLoadAttentionList;
    private String userName;
    private String password;
    private String mail;
    private String phone;
    private Integer followSum;
    private Long fensSum;
    private Integer collectSum;
    private String profile;
    private String portraitFileName;
    private Date registerDate;
    public User(){}
    public User(Long id, Long userId, String userName, String password, String mail, String phone, String profile, String portraitFileName, Date registerDate) {
        this.id = id;
        this.userId = userId;
        this.userName = userName;
        this.password = password;
        this.mail = mail;
        this.phone = phone;
        this.profile = profile;
        this.portraitFileName = portraitFileName;
        this.registerDate = registerDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getFollowSum() {
        return followSum;
    }

    public void setFollowSum(Integer followSum) {
        this.followSum = followSum;
    }

    public Long getFensSum() {
        return fensSum;
    }

    public void setFensSum(Long fensSum) {
        this.fensSum = fensSum;
    }

    public Integer getCollectSum() {
        return collectSum;
    }

    public void setCollectSum(Integer collectSum) {
        this.collectSum = collectSum;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getPortraitFileName() {
        return portraitFileName;
    }

    public void setPortraitFileName(String portraitFileName) {
        this.portraitFileName = portraitFileName;
    }

    public Date getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(Date registerDate) {
        this.registerDate = registerDate;
    }

    public boolean isLoadAttentionList() {
        return isLoadAttentionList;
    }

    public void setLoadAttentionList(boolean loadAttentionList) {
        isLoadAttentionList = loadAttentionList;
    }

    public User clone(){
        return new User(getUserId(),0l,userName,password,mail,phone,profile,portraitFileName,registerDate);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", userId=" + userId +
                ", isLoadAttentionList=" + isLoadAttentionList +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", mail='" + mail + '\'' +
                ", phone='" + phone + '\'' +
                ", followSum=" + followSum +
                ", fensSum=" + fensSum +
                ", collectSum=" + collectSum +
                ", profile='" + profile + '\'' +
                ", portraitFileName='" + portraitFileName + '\'' +
                ", registerDate=" + registerDate +
                '}';
    }
}
