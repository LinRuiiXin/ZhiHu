package com.sz.zhihu.utils;

import android.app.Activity;
import android.app.Dialog;

import com.sz.zhihu.po.User;

import org.litepal.LitePal;
import org.litepal.crud.DataSupport;

public class UserUtils {
    private static User user;
    public static User queryUserHistory(){
        if(user == null){
            synchronized (UserUtils.class){
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
}
