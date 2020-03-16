package com.sz.zhihu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.sz.zhihu.utils.SystemUtils;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        SystemUtils.setStatusBarFullTransparent(this);
        new Thread(()->{
            Intent intent = new Intent(SplashActivity.this,MainActivity.class);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            startActivity(intent);
        }).start();
    }
}
