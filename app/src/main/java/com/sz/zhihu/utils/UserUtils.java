package com.sz.zhihu.utils;

import com.sz.zhihu.po.User;

import org.litepal.LitePal;
import org.litepal.crud.DataSupport;

public class UserUtils {
    public static boolean isLogged(){
        User first = DataSupport.findFirst(User.class);
        return first == null?false:true;
    }
    public static User queryUserHistory(){
        return DataSupport.findFirst(User.class);
    }
}
