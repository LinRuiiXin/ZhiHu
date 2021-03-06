package com.sz.zhihu.po;

import java.io.Serializable;

public class Question implements Serializable {
    private Long id;
    private String name;
    private Long questionerId;
    private int hasDescribe;
    private Long subscribeSum;
    private Long browseSum;
    private Long answerSum;
    public Question(){}

    public Question(Long questionerId) {
        this.questionerId = questionerId;
    }

    public Question(Long id, String name, Long questionerId, int hasDescribe, Long subscribeSum, Long browseSum, Long answerSum) {
        this.id = id;
        this.name = name;
        this.questionerId = questionerId;
        this.hasDescribe = hasDescribe;
        this.subscribeSum = subscribeSum;
        this.browseSum = browseSum;
        this.answerSum = answerSum;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getQuestionerId() {
        return questionerId;
    }

    public void setQuestionerId(Long questionerId) {
        this.questionerId = questionerId;
    }

    public int getHasDescribe() {
        return hasDescribe;
    }

    public void setHasDescribe(int hasDescribe) {
        this.hasDescribe = hasDescribe;
    }

    public Long getSubscribeSum() {
        return subscribeSum;
    }

    public void setSubscribeSum(Long subscribeSum) {
        this.subscribeSum = subscribeSum;
    }

    public Long getBrowseSum() {
        return browseSum;
    }

    public void setBrowseSum(Long browseSum) {
        this.browseSum = browseSum;
    }

    public Long getAnswerSum() {
        return answerSum;
    }

    public void setAnswerSum(Long answerSum) {
        this.answerSum = answerSum;
    }

    @Override
    public String toString() {
        return "Question{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", questionerId=" + questionerId +
                ", hasDescribe=" + hasDescribe +
                ", subscribeSum=" + subscribeSum +
                ", browseSum=" + browseSum +
                ", answerSum=" + answerSum +
                '}';
    }
}
