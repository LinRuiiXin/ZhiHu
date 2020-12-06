package com.sz.zhihu.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sz.zhihu.dto.SimpleDto;

public class GsonUtils {

    private static final Gson gson;

    static {
        gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
    }

    public static Gson getGson(){
        return gson;
    }

    public static SimpleDto parseJsonToSimpleDto(String json){
        return gson.fromJson(json,SimpleDto.class);
    }
}
