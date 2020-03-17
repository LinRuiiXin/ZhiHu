package com.sz.zhihu.utils;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.sz.zhihu.R;
import com.sz.zhihu.dialog.MailLoginDialog;

public class DiaLogUtils {
    public static void MailLoginDiaLog(Context context){
        final Dialog dialog = new MailLoginDialog(context, R.style.DialogTheme);
        //2、设置布局
        View view = View.inflate(context, R.layout.activity_login_only_mail, null);
        dialog.setContentView(view);

        Window window = dialog.getWindow();
        //设置弹出位置
        window.setGravity(Gravity.BOTTOM);
        //设置弹出动画
        window.setWindowAnimations(R.style.main_menu_animStyle);
        //设置对话框大小
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();
    }
}
