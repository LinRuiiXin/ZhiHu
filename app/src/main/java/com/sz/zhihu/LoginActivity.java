package com.sz.zhihu;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.sz.zhihu.dto.SimpleDto;
import com.sz.zhihu.interfaces.CustomEditTextListener;
import com.sz.zhihu.po.User;
import com.sz.zhihu.utils.DiaLogUtils;
import com.sz.zhihu.utils.RequestUtils;
import com.sz.zhihu.utils.StringUtils;
import com.sz.zhihu.utils.SystemUtils;
import com.sz.zhihu.utils.ValidateUtil;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class LoginActivity extends AppCompatActivity {

    private TextView changeText;
    private EditText mail;
    private EditText password;
    private Button login;
    private TextView findPassword;
    private TextView close;
    private TextView register;
    private String serverLocation;
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_mail_password);
        SystemUtils.setStatusBarFullTransparent(this);
        init();
    }

    private void init() {
        close = findViewById(R.id.login_top_bar_close);
        changeText = findViewById(R.id.login_top_bar_change);
        changeText.setText("免密码登录");
        mail = findViewById(R.id.login_mp_mail);
        password = findViewById(R.id.login_mp_password);
        login = findViewById(R.id.login_mp_log);
        findPassword = findViewById(R.id.login_mp_find_password);
        register = findViewById(R.id.login_mp_register);
        serverLocation = getResources().getString(R.string.server_location);
        gson = new Gson();
        addListener();
    }

    private void addListener() {
        close.setOnClickListener((v)->{
            finish();
        });
        //设置“免密码登录点击事件”
        changeText.setOnClickListener((v)->{
            DiaLogUtils.MailLoginDiaLog(this);
        });
        //校验输入框（邮箱点击事件）
        mail.addTextChangedListener(new CustomEditTextListener() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String content = s.toString();
                if(!ValidateUtil.validate(content,ValidateUtil.mailRegex)){
                    mail.setError("邮箱格式不正确");
                    login.setBackgroundResource(R.drawable.login_log_button_shape_disable);
                    return;
                }
                String passwordContent = password.getText().toString();
                if(!StringUtils.isEmpty(content) && !StringUtils.isEmpty(passwordContent)){
                    login.setEnabled(true);
                    login.setBackgroundResource(R.drawable.login_log_button_shape_enable);
                }else{
                    password.setError("不能为空");
                    login.setEnabled(false);
                    login.setBackgroundResource(R.drawable.login_log_button_shape_disable);
                }

            }
        });
        //校验设置输入框（密码点击事件）
        password.addTextChangedListener(new CustomEditTextListener() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String mailContent = mail.getText().toString();
                if (!ValidateUtil.validate(mailContent,ValidateUtil.mailRegex)) {
                    mail.setError("邮箱格式不正确");
                    login.setBackgroundResource(R.drawable.login_log_button_shape_disable);
                    return;
                }
                String passwordContent = s.toString();
                if(!StringUtils.isEmpty(mailContent) && !StringUtils.isEmpty(passwordContent)){
                    login.setEnabled(true);
                    login.setBackgroundResource(R.drawable.login_log_button_shape_enable);
                }else{
                    password.setError("不能为空");
                    login.setEnabled(false);
                    login.setBackgroundResource(R.drawable.login_log_button_shape_disable);
                }
            }
        });
        login.setOnClickListener((v)->{
            String mail = this.mail.getText().toString();
            String password = this.password.getText().toString();
            if(ValidateUtil.validate(mail,ValidateUtil.mailRegex)){
                if(ValidateUtil.validate(password,ValidateUtil.passwordRegex)){
                    String url = serverLocation + "/Login/"+mail+"/"+password;
                    RequestUtils.sendSimpleRequest(url, new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            runOnUiThread(()-> Toast.makeText(LoginActivity.this,"请求失败",Toast.LENGTH_SHORT).show());
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            SimpleDto simpleDto = gson.fromJson(response.body().string(),SimpleDto.class);
                            runOnUiThread(()->{
                                if(simpleDto.isSuccess()){
                                    User user = gson.fromJson(simpleDto.getObject().toString(),User.class);
                                    user.setUserId(user.getId());
                                    user.save();
                                    Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                                    startActivity(intent);
                                }else{
                                    Toast.makeText(LoginActivity.this,simpleDto.getMsg(),Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                }else {
                    Toast.makeText(this,"密码错误",Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(this,"请检查邮箱格式",Toast.LENGTH_SHORT).show();
            }
        });
        register.setOnClickListener((v)->{
            Intent intent = new Intent(this,RegisterActivity.class);
            startActivity(intent);
        });
    }
}
