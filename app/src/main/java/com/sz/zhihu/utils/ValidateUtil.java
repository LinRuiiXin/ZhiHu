package com.sz.zhihu.utils;

import android.widget.EditText;
/*
    数据校验工具，只需传入待校验数据与正则表达式即可
    @return true：校验通过 false：校验不通过
 */
public class ValidateUtil {
    //中文，英文，数字或下划线
    public static final String userNameRegex = "^[\\u4E00-\\u9FA5A-Za-z0-9_]+$";
    //密码至少包含 数字和英文，长度6-20
    public static final String passwordRegex = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,20}$";
    //邮箱
    public static final String mailRegex = "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$";
    //手机号码
    public static final String phoneRegex = "^(13[0-9]|14[5|7]|15[0|1|2|3|5|6|7|8|9]|18[0|1|2|3|5|6|7|8|9])\\d{8}$";

    public static boolean validatePassword(String password){
        return (!StringUtils.isEmpty(password)&&password.matches(passwordRegex));
    }
    public static boolean validateMail(String mail){
        return (!StringUtils.isEmpty(mail)&&mail.matches(mailRegex));
    }
    public static boolean validatePhone(String phone){
        return (!StringUtils.isEmpty(phone)&&phone.matches(phoneRegex));
    }
    public static boolean validate(String str,String regex){
        String content = str.trim();
        if(content.equals("") || content.length() == 0 || !content.matches(regex)){
            return false;
        }else{
            return true;
        }
    }
}
