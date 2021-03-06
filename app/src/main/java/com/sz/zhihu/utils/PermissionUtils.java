package com.sz.zhihu.utils;

import android.app.Activity;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PermissionUtils {
    public static final int REQUEST_CODE = 5;
    public static final int REQUEST_IMAGE = 6;
    public static final int REQUEST_VIDEO = 7;
    public static final int REQUEST_WRITE = 8;
    /*
    * 申请权限(Activity)
    * @activity:当前活动
    * @permissions:要申请的权限数组
    * @return: true---没有要申请的权限，false有要申请的权限
    * */
    public static boolean registerPerMission(Activity activity, String...permissions){
        /*List<String> permissionNeedToRegister = null;
        for(String permission:permissions){
            if(ContextCompat.checkSelfPermission(activity,permission) != PackageManager.PERMISSION_GRANTED){
                if(permissionNeedToRegister == null){
                    permissionNeedToRegister = new ArrayList<String>();
                }
                permissionNeedToRegister.add(permission);
            }
        }
        if(permissionNeedToRegister != null){
            ActivityCompat.requestPermissions(activity,permissionNeedToRegister.toArray(new String[permissionNeedToRegister.size()]),REQUEST_CODE);
            return false;
        }else{
            return true;
        }*/
        return registerPermissionWithCode(activity,REQUEST_CODE,permissions);
    }
    public static boolean registerFragmentPerMission(Activity activity,Fragment fragment, String...permissions){
        List<String> permissionNeedToRegister = null;
        for(String permission:permissions){
            if(ContextCompat.checkSelfPermission(activity,permission) != PackageManager.PERMISSION_GRANTED){
                if(permissionNeedToRegister == null){
                    permissionNeedToRegister = new ArrayList<String>();
                }
                permissionNeedToRegister.add(permission);
            }
        }
        if(permissionNeedToRegister != null){
            fragment.requestPermissions(permissions,REQUEST_CODE);
            return false;
        }else{
            return true;
        }
    }
    public static boolean registerPermissionWithCode(Activity activity,Integer requestCode, String...permissions){
        List<String> permissionNeedToRegister = null;
        for(String permission:permissions){
            if(ContextCompat.checkSelfPermission(activity,permission) != PackageManager.PERMISSION_GRANTED){
                if(permissionNeedToRegister == null){
                    permissionNeedToRegister = new ArrayList<String>();
                }
                permissionNeedToRegister.add(permission);
            }
        }
        if(permissionNeedToRegister != null){
            ActivityCompat.requestPermissions(activity,permissionNeedToRegister.toArray(new String[permissionNeedToRegister.size()]),requestCode);
            return false;
        }else{
            return true;
        }
    }
}
