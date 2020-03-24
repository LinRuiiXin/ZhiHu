package com.sz.zhihu.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.CountDownTimer;
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
import com.sz.zhihu.dialog.MailLoginDialog;
import com.sz.zhihu.dto.SimpleDto;
import com.sz.zhihu.interfaces.CustomEditTextListener;
import com.sz.zhihu.po.User;

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
        Drawable enable = activity.getDrawable(R.drawable.login_log_button_shape_enable);
        Drawable disable = activity.getDrawable(R.drawable.login_log_button_shape_disable);
        //2、设置布局
        View view = View.inflate(activity, R.layout.activity_login_only_mail, null);
        if(serverLocation == null){
            serverLocation = activity.getResources().getString(R.string.server_location);
        }
        EditText mail = view.findViewById(R.id.login_om_mail);
        EditText code = view.findViewById(R.id.login_om_code);
        Button senMail = view.findViewById(R.id.login_om_send_mail);
        Button login = view.findViewById(R.id.login_om_login);
        mail.addTextChangedListener(new CustomEditTextListener() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!ValidateUtil.validate(s.toString(),ValidateUtil.mailRegex)){
                    mail.setError("输入正确的邮箱格式");
                }
            }
        });
        senMail.setOnClickListener(v->{
            String mailNow = mail.getText().toString();
            if(ValidateUtil.validate(mailNow,ValidateUtil.mailRegex)){
                String url = serverLocation + "/Login/SecurityCode/"+mailNow;
                RequestUtils.sendSimpleRequest(url, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        activity.runOnUiThread(()->Toast.makeText(activity,"请求失败",Toast.LENGTH_SHORT).show());
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        SimpleDto simpleDto = gson.fromJson(response.body().string(),SimpleDto.class);
                        activity.runOnUiThread(()->{
                            if(simpleDto.isSuccess()){
                                MailLoginDialog.mail = mailNow;
                                senMail.setEnabled(false);
                                senMail.setBackground(disable);
                            }else{
                                Toast.makeText(activity,simpleDto.getMsg(),Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }else{
                Toast.makeText(activity,"请检查邮箱格式",Toast.LENGTH_SHORT).show();
            }
        });
        login.setOnClickListener(v->{
            if(ValidateUtil.validate(MailLoginDialog.mail,ValidateUtil.mailRegex)){
                String codeStr = code.getText().toString();
                if(codeStr.length() == 6){
                    String url = serverLocation + "/Login/NoPassword/"+ MailLoginDialog.mail+"/"+codeStr ;
                    RequestUtils.sendSimpleRequest(url, new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            activity.runOnUiThread(()->Toast.makeText(activity,"请求失败",Toast.LENGTH_SHORT).show());
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            SimpleDto simpleDto = gson.fromJson(response.body().string(),SimpleDto.class);
                            activity.runOnUiThread(()->{
                                if(simpleDto.isSuccess()){
                                    User user = gson.fromJson(simpleDto.getObject().toString(),User.class);
                                    user.save();
                                    Intent intent = new Intent(activity, MainActivity.class);
                                    activity.startActivity(intent);
                                }else{
                                    Toast.makeText(activity,simpleDto.getMsg(),Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                }else{
                    Toast.makeText(activity,"验证码错误",Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(activity,"请检查邮箱",Toast.LENGTH_SHORT).show();
            }
        });
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
