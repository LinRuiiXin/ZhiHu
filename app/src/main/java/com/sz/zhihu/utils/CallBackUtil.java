package com.sz.zhihu.utils;

import android.app.Activity;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.google.gson.Gson;
import com.sz.zhihu.dto.SimpleDto;

import java.io.IOException;
import java.util.function.Consumer;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class CallBackUtil {

    public static Callback commonCallBack(Activity activity, Runnable onFail, Consumer<SimpleDto> onSuccess){
        return new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                activity.runOnUiThread(onFail);
            }

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                SimpleDto simpleDto = GsonUtils.parseJsonToSimpleDto(response.body().string());
                activity.runOnUiThread(()->{onSuccess.accept(simpleDto);});
            }
        };
    }

    public static Callback commonCallBack(Activity activity,Consumer<SimpleDto> consumer){
        return commonCallBack(activity,
                ()->{
                    Toast.makeText(activity,"请求失败",Toast.LENGTH_SHORT).show();
                },consumer);
    }
}
