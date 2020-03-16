package com.sz.zhihu.utils;

import android.content.pm.PackageManager;
import java.util.ArrayList;
import java.util.List;

public class ArrayUtils {
    /*
    * 权限申请回调后，将未通过申请的权限筛选出来
    * @arr1:权限数组
    * @arr2:权限状态
    * */
    public static String[] getPermissionNotAgrees(String[] arr1, int[] arr2){
        List<String> res = new ArrayList<>();
        for(int i = 0 ; i<arr2.length ; i++){
            if(arr2[i] != PackageManager.PERMISSION_GRANTED){
                res.add(arr1[i]);
            }
        }
        return res.toArray(new String[res.size()]);
    }
}
