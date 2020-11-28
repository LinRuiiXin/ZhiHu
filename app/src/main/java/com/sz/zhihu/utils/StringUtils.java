package com.sz.zhihu.utils;

import java.lang.reflect.Field;
import java.util.List;

public class StringUtils {
    public static boolean isEmpty(String str){
        String content = str.trim();
        if(content.length() == 0 || content == null || content.equals("")){
            return true;
        }else{
            return false;
        }
    }
    public static String jointString(char sign, List<?> list, String field){
        try {
            if(list.size() > 0){
                StringBuilder stringBuilder = new StringBuilder();
                Class<?> aClass = list.get(0).getClass();
                Field f = aClass.getDeclaredField(field);
                f.setAccessible(true);
                int len = list.size();
                for (int i = 0; i < len; i++) {
                    stringBuilder.append(f.get(list.get(i)));
                    if(i != len-1){
                        stringBuilder.append(sign);
                    }
                }
                return stringBuilder.toString();
            }else{
                return "";
            }
        }catch (Exception e){
            e.printStackTrace();
            return "";
        }
    }
}
