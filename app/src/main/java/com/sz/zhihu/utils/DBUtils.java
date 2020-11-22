package com.sz.zhihu.utils;

import android.app.Activity;

import com.sz.zhihu.po.User;
import com.sz.zhihu.vo.RecommendViewBean;

import org.litepal.crud.DataSupport;

import java.util.List;

public class DBUtils {
    private static User user;
    public static User queryUserHistory(){
        if(user == null){
            synchronized (DBUtils.class){
                if(user == null){
                    user = DataSupport.findFirst(User.class);
                }
            }
        }
        return user;
    }
    public static boolean checkIsLogged(Activity activity){
        if(queryUserHistory() != null){
            return true;
        }else{
            DiaLogUtils.showPromptLoginDialog(activity);
            return false;
        }
    }

    public static void clearUserHistory(){
        DataSupport.deleteAll(User.class);
    }

    public static int getBrowseRecordCount(){
        int count = DataSupport.count(RecommendViewBean.class);
        return count;
    }
    public static List<RecommendViewBean> getBrowseRecord(int index){
        List<RecommendViewBean> recommendViewBeans = DataSupport.order("id desc").limit(10).offset(index).find(RecommendViewBean.class);
        return recommendViewBeans;
    }

}
