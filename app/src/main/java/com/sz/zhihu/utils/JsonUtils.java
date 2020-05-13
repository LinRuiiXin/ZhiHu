package com.sz.zhihu.utils;

import android.app.Activity;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.sz.zhihu.po.Settings;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class JsonUtils {
    private static Gson gson = new Gson();
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static List<Settings> getSettingFromJson(Activity activity) {
        InputStream open = null;
        List<Settings> settings = new ArrayList<>();

        try {
            open = activity.getAssets().open("settings.json");
            byte [] buffer = new byte[open.available()];
            open.read(buffer);
            String s = new String(buffer);
            List list = gson.fromJson(s, List.class);
            list.forEach(o -> settings.add(gson.fromJson(gson.toJson(o),Settings.class)));
            return settings;
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            if(open != null) {
                try {
                    open.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}
