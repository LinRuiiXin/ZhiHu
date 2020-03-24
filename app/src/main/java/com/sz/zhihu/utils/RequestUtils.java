package com.sz.zhihu.utils;

import android.provider.MediaStore;

import com.google.gson.Gson;

import java.io.File;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
//请求工具类
public class RequestUtils {
    private static Gson gson = new Gson();
    /**
     * 传入服务器接口，回调接口实现(Get提交)
     */
    public static void sendSimpleRequest(String url, Callback callback){
        OkHttpClient httpClient = new OkHttpClient();
        Request.Builder builder = new Request.Builder().url(url).method("GET", null).addHeader("acceptType","application/json");
        Request request = builder.build();
        Call call = httpClient.newCall(request);
        call.enqueue(callback);
    }
    /*
    * JSON方式提交数据
    * @o 待提交数据，将会被转换为JSON格式
    * @url 服务器接口
    * @callback 回调接口
    * */
    public static void sendRequestWithJson(Object o,String url,Callback callback){
        OkHttpClient httpClient = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"),gson.toJson(o));
        Request request = new Request.Builder().post(requestBody).url(url).addHeader("contentType", "application/json").addHeader("acceptType", "application/json").build();
        Call call = httpClient.newCall(request);
        call.enqueue(callback);
    }
    public static void sendSingleFile(File file,String url,String fileName,Callback callback){
        OkHttpClient httpClient = new OkHttpClient();
//        String[] split = file.getName().split("\\.");
//        String format = new String();
//        switch (split[split.length-1]){
//            case "txt":
//                format = "text/plain";
//                break;
//            case "jpg":
//                format = "image/jpeg";
//                break;
//            case "png":
//                format = "image/png";
//                break;
//            case "mp4":
//                format = "text/plain";
//                break;
//        }
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("portrait", file.getName(), RequestBody.create(MediaType.parse("multipart/form-data"), file)).build();
        Request request = new Request.Builder().post(requestBody).url(url).build();
        Call call = httpClient.newCall(request);
        call.enqueue(callback);
    }
}
