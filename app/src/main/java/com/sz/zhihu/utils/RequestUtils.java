package com.sz.zhihu.utils;

import android.os.Build;
import android.provider.MediaStore;

import androidx.annotation.RequiresApi;

import com.google.gson.Gson;

import java.io.File;
import java.util.Map;

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
    private static OkHttpClient httpClient = new OkHttpClient();
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
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"),gson.toJson(o));
        Request request = new Request.Builder().post(requestBody).url(url).addHeader("contentType", "application/json").addHeader("acceptType", "application/json").build();
        Call call = httpClient.newCall(request);
        call.enqueue(callback);
    }
    public static void sendSingleFile(File file,String url,String fileName,Callback callback){
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(fileName, file.getName(), RequestBody.create(MediaType.parse("multipart/form-data"), file)).build();
        Request request = new Request.Builder().post(requestBody).url(url).build();
        Call call = httpClient.newCall(request);
        call.enqueue(callback);
    }
    /*
    * 携带参数上传文件
    * */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void sendFileWithParam(File file, String url, String fileName, Map<String,String> params, Callback callback){
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);
        if(file.exists()){
            builder.addFormDataPart(fileName, file.getName(), RequestBody.create(MediaType.parse("multipart/form-data"), file));
        }
        params.keySet().forEach(key->{
            builder.addFormDataPart(key,params.get(key));
        });
        MultipartBody requestBody = builder.build();
        Request request = new Request.Builder().post(requestBody).url(url).build();
        Call call = httpClient.newCall(request);
        call.enqueue(callback);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void postWithParams(String url, Map<String,String> params, Callback callback){
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);
        params.keySet().forEach(key->{
            builder.addFormDataPart(key,params.get(key));
        });
        MultipartBody requestBody = builder.build();
        Request request = new Request.Builder().post(requestBody).url(url).build();
        Call call = httpClient.newCall(request);
        call.enqueue(callback);
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void deleteWithParams(String url, Map<String,String> params, Callback callback){
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);
        params.keySet().forEach(key->{
            builder.addFormDataPart(key,params.get(key));
        });
        MultipartBody requestBody = builder.build();
        Request request = new Request.Builder().delete(requestBody).url(url).build();
        Call call = httpClient.newCall(request);
        call.enqueue(callback);
    }

}
