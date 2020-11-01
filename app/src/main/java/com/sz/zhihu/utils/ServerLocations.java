package com.sz.zhihu.utils;

public class ServerLocations {
    public static final String LOCATION = "http://192.168.0.102:8080";
    public static final String ANSWER = "/AnswerService/Answer";
    public static final String QUESTION = "/QuestionService/Question";
    public static final String COMMENT = "/CommentService/Comment";
    public static final String USER = "/UserService/User";

    public static String getServerLocation(String serverName){
        return LOCATION+"/"+serverName;
    }
}
