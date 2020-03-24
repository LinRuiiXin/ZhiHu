package com.sz.zhihu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.widget.FrameLayout;

import com.sz.zhihu.fragment.register.FinishRegisterFragment;
import com.sz.zhihu.fragment.register.MailFragment;
import com.sz.zhihu.utils.SystemUtils;
public class RegisterActivity extends AppCompatActivity {

    private FragmentManager supportFragmentManager;
    private MailFragment mailFragment;
    private FrameLayout frameLayout;
    public String mail;
    private FinishRegisterFragment finishRegisterFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        init();
    }

    private void init() {
        frameLayout = findViewById(R.id.register_frameLayout);
        //设置状态栏透明
        SystemUtils.setStatusBarFullTransparent(this);
        supportFragmentManager = getSupportFragmentManager();
        initFragment();
        supportFragmentManager.beginTransaction().replace(R.id.register_frameLayout,mailFragment).commit();
    }

    private void initFragment() {
        finishRegisterFragment = new FinishRegisterFragment();
        mailFragment = new MailFragment((mail->{
            this.mail = mail;
            supportFragmentManager.beginTransaction().replace(R.id.register_frameLayout,finishRegisterFragment).commit();
        }));
    }

}
