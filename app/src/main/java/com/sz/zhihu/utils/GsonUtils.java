package com.sz.zhihu.utils;

import com.google.gson.Gson;
import com.sz.zhihu.dto.SimpleDto;

public class GsonUtils {

    private static final Gson gson;

    static {
        gson = new Gson();
    }

    public static Gson getGson(){
        return gson;
    }

    public static SimpleDto parseJsonToSimpleDto(String json){
        return gson.fromJson(json,SimpleDto.class);
    }
}
