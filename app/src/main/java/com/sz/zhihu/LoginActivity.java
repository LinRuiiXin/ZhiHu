package com.sz.zhihu;

import android.app.Dialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.sz.zhihu.interfaces.CustomEditTextListener;
import com.sz.zhihu.utils.DiaLogUtils;
import com.sz.zhihu.utils.RegexUtils;
import com.sz.zhihu.utils.StringUtils;
import com.sz.zhihu.utils.SystemUtils;

public class LoginActivity extends AppCompatActivity {

    private TextView changeText;
    private EditText mail;
    private EditText password;
    private Button login;
    private TextView textView;
    private TextView close;

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
        textView = findViewById(R.id.login_mp_find_password);
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
                if(!RegexUtils.isEmail(content)){
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
                if (!RegexUtils.isEmail(mailContent)) {
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
        });
    }
}
