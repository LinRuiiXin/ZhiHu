package com.sz.zhihu.utils;

public class StringUtils {
    public static boolean isEmpty(String str){
        String content = str.trim();
        if(content.length() == 0 || content == null || content.equals("")){
            return true;
        }else{
            return false;
        }
    }
}
