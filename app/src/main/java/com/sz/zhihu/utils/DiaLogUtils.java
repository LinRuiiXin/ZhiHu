package com.sz.zhihu.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.google.gson.Gson;
import com.sz.zhihu.MainActivity;
import com.sz.zhihu.R;
import com.sz.zhihu.dialog.ChoiceTypeDialog;
import com.sz.zhihu.dialog.MailLoginDialog;
import com.sz.zhihu.dto.SimpleDto;
import com.sz.zhihu.interfaces.CustomEditTextListener;
import com.sz.zhihu.po.User;
import com.sz.zhihu.view.ChoiceTypeView;
import com.sz.zhihu.view.LoginOnlyMailView;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class DiaLogUtils {

    private static String serverLocation;
    private static Gson gson = new Gson();
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static void MailLoginDiaLog(Activity activity){
        final Dialog dialog = new MailLoginDialog(activity, R.style.DialogTheme);
        View view = LoginOnlyMailView.getView(activity);
        dialog.setContentView(view);
        Window window = dialog.getWindow();
        //设置弹出位置
        window.setGravity(Gravity.BOTTOM);
        //设置弹出动画
        window.setWindowAnimations(R.style.main_menu_animStyle);
        //设置对话框大小
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setOnDismissListener(d -> {
            ((ViewGroup)view.getParent()).removeView(view);
        });
        dialog.show();
    }

    public static void showPromptLoginDialog(Activity activity){

    }
    public static void showChoiceTypeDialog(Activity activity){
        final Dialog dialog = new ChoiceTypeDialog(activity, R.style.DialogTheme);
        View view = ChoiceTypeView.getView(activity);
        dialog.setContentView(view);
        Window window = dialog.getWindow();
        //设置弹出位置
        window.setGravity(Gravity.BOTTOM);
        //设置弹出动画
        window.setWindowAnimations(R.style.main_menu_animStyle);
        //设置对话框大小
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setOnDismissListener(d -> {
            ((ViewGroup)view.getParent()).removeView(view);
        });
        dialog.show();
    }
}
