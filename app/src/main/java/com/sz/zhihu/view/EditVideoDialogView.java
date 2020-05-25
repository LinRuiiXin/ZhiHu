package com.sz.zhihu.view;

import android.Manifest;
import android.app.Activity;
import android.view.View;

import com.sz.zhihu.R;
import com.sz.zhihu.utils.PermissionUtils;

public class EditVideoDialogView {
    private static View view;
    public static View getView(Activity activity){
        if(view == null){
            view = View.inflate(activity,R.layout.edit_video_dialog,null);
        }
        return view;
    }
}
