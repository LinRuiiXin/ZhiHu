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
import androidx.appcompat.app.AlertDialog;

import com.google.gson.Gson;
import com.sz.zhihu.MainActivity;
import com.sz.zhihu.R;
import com.sz.zhihu.dialog.ChoiceTypeDialog;
import com.sz.zhihu.dialog.MailLoginDialog;
import com.sz.zhihu.dialog.PromptLoginDialog;
import com.sz.zhihu.dto.SimpleDto;
import com.sz.zhihu.interfaces.CustomEditTextListener;
import com.sz.zhihu.po.User;
import com.sz.zhihu.view.ChoiceTypeView;
import com.sz.zhihu.view.LoginOnlyMailView;
import com.sz.zhihu.view.PromptLoginView;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class DiaLogUtils {

//    private static String serverLocation;
//    private static Gson gson = new Gson();
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
        final Dialog dialog = new PromptLoginDialog(activity,R.style.DialogTheme);
        View promptView = PromptLoginView.getPromptView(activity);
        dialog.setContentView(promptView);
//        Window window = dialog.getWindow();
//        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setOnDismissListener(d -> {
            ((ViewGroup)promptView.getParent()).removeView(promptView);
        });
        dialog.show();
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
    public static void showAboutAuthorDialog(Activity activity){
        AlertDialog alertDialog = new AlertDialog.Builder(activity)
                .setTitle("关于作者")
                .setMessage("深圳第二高级技工学校 林瑞鑫")
                .setPositiveButton("确定", (d, w) -> {}).create();
        alertDialog.show();
    }
    public static void showGitHubLocation(Activity activity){
        AlertDialog alertDialog = new AlertDialog.Builder(activity)
                .setTitle("GitHub地址")
                .setMessage("客户端：\ngithub.com/LinRuiiXin/ZhiHu \n\n服务端：\ngithub.com/LinRuiiXin/ZhiHuServer\n\n欢迎star!")
                .setPositiveButton("确定", (d, w) -> {}).create();
        alertDialog.show();
    }
    public static void showLicenses(Activity activity){
        AlertDialog alertDialog = new AlertDialog.Builder(activity)
                .setTitle("开源许可")
                .setMessage("MIT License\n" +
                        "\n" +
                        "Copyright (c) [year] [fullname]\n" +
                        "\n" +
                        "Permission is hereby granted, free of charge, to any person obtaining a copy\n" +
                        "of this software and associated documentation files (the \"Software\"), to deal\n" +
                        "in the Software without restriction, including without limitation the rights\n" +
                        "to use, copy, modify, merge, publish, distribute, sublicense, and/or sell\n" +
                        "copies of the Software, and to permit persons to whom the Software is\n" +
                        "furnished to do so, subject to the following conditions:\n" +
                        "\n" +
                        "The above copyright notice and this permission notice shall be included in all\n" +
                        "copies or substantial portions of the Software.\n" +
                        "\n" +
                        "THE SOFTWARE IS PROVIDED \"AS IS\", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR\n" +
                        "IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,\n" +
                        "FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE\n" +
                        "AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER\n" +
                        "LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,\n" +
                        "OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE\n" +
                        "SOFTWARE.")
                .setPositiveButton("确定", (d, w) -> {}).create();
        alertDialog.show();
    }
}
