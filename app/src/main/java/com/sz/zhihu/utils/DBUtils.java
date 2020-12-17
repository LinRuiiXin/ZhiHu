package com.sz.zhihu.utils;

import android.app.Activity;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sz.zhihu.R;
import com.sz.zhihu.dto.SimpleDto;
import com.sz.zhihu.po.Information;
import com.sz.zhihu.po.Keyword;
import com.sz.zhihu.po.User;
import com.sz.zhihu.po.UserAttention;
import com.sz.zhihu.vo.RecommendViewBean;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Level;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class DBUtils {
    private static User user;

    public static User queryUserHistory() {
        if (user == null) {
            synchronized (DBUtils.class) {
                if (user == null) {
                    user = DataSupport.findFirst(User.class);
                }
            }
        }
        return user;
    }

    public static boolean checkIsLogged(Activity activity) {
        if (queryUserHistory() != null) {
            return true;
        } else {
            DiaLogUtils.showPromptLoginDialog(activity);
            return false;
        }
    }

    public static void removeKeywordHistory(Keyword keyword){
        DataSupport.delete(Keyword.class,keyword.getId());
    }

    public static void clearUserHistory() {
        DataSupport.deleteAll(Information.class);
        DataSupport.deleteAll(User.class);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void getUserAttentionListFromServer(Activity activity, User user) {
        getUserAttentionListFromServer(activity,user,consumers -> {});
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void getUserAttentionListFromServer(Activity activity, User user, Consumer<List<UserAttention>> consumer) {
        clearUserAttention(user.getUserId());
        String serverLocation = activity.getString(R.string.server_location);
        String url = serverLocation + "/UserService/User/AttentionList/" + user.getUserId();
        RequestUtils.sendSimpleRequest(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                SimpleDto simpleDto = GsonUtils.parseJsonToSimpleDto(response.body().string());
                activity.runOnUiThread(()->{
                    if (simpleDto.isSuccess()) {
                        Gson gson = GsonUtils.getGson();
                        List<UserAttention> list = gson.fromJson(gson.toJson(simpleDto.getObject()),new TypeToken<List<UserAttention>>(){}.getType());
                        list.forEach(userAttention -> {
                            userAttention.save();
                        });
                        user.setLoadAttentionList(true);
                        user.update(user.getId());
                        consumer.accept(list);
                    }
                });
            }
        });
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void clearUserAttention(Long userId) {
        List<UserAttention> userAttentions = DataSupport.where("userId = ?", String.valueOf(userId)).find(UserAttention.class);
        userAttentions.forEach(userAttention -> {
            if (userAttention != null) {
                userAttention.delete();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void updateAttentionVersion(List<Integer> newVersion, List<UserAttention> userAttentions){
        for (int i = 0; i < userAttentions.size(); i++) {
            UserAttention userAttention = userAttentions.get(i);
            userAttention.setVersion(newVersion.get(i));
            userAttention.update(userAttention.getId());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void insertInformation(List<Information> information) {
        information.forEach(i->{
            i.setInformationId(i.getId());
            i.saveOrUpdate("informationId = ?",String.valueOf(i.getId()));
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void getUserAttentionList(Activity activity,User user, Consumer<List<UserAttention>> consumer) {
        if(user != null){
            if (user.isLoadAttentionList()) {
                consumer.accept(getUserAttentionList(user.getUserId()));
            } else {
                getUserAttentionListFromServer(activity,user,consumer);
            }
        }
    }

    public static List<Information> getNewInformation(int start,int size){
        List<Information> informationId_desc = DataSupport.order("informationId desc").offset(start).limit(size).find(Information.class);
        return informationId_desc;
    }

    public static List<UserAttention> getUserAttentionList(Long userId) {
        return DataSupport.where("userId = ?", String.valueOf(userId)).find(UserAttention.class);
    }

    public static int getBrowseRecordCount() {
        int count = DataSupport.count(RecommendViewBean.class);
        return count;
    }

    public static List<RecommendViewBean> getBrowseRecord(int from,int size) {
        List<RecommendViewBean> recommendViewBeans = DataSupport.order("id desc").limit(size).offset(from).find(RecommendViewBean.class);
        return recommendViewBeans;
    }


    public static List<Keyword> getSearchHistory(){
        List<Keyword> keywords = DataSupport.offset(0).limit(10).order("id desc").find(Keyword.class);
//        List<Keyword> keywords = DataSupport.findAll(Keyword.class);
        return keywords;
    }

    public static void saveIfNotExistKeyword(Keyword keyword){
        Keyword first = DataSupport.where("title = ?", keyword.getTitle()).findFirst(Keyword.class);
        if(first != null){
            first.delete();
        }
        Keyword newKeyword = new Keyword(keyword.getTitle());
        newKeyword.save();
    }

    public static void clearSearchHistory(){
        DataSupport.deleteAll(Keyword.class,"");
    }

    public static void updateUser(User user) {
        user.saveOrUpdate("userId = ?",String.valueOf(user.getUserId()));
    }
}
