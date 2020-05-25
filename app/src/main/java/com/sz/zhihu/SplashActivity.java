package com.sz.zhihu;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.transition.Explode;

import com.sz.zhihu.utils.SystemUtils;

/*
 * 启动页
 * */
public class SplashActivity extends AbstractCustomActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        jumpPage();
    }

    private void jumpPage() {
        new Thread(() -> {
            try {
                Thread.sleep(1000);
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                SplashActivity.this.runOnUiThread(() -> {
                    SplashActivity.this.startActivity(intent);
                });
                Thread.sleep(1000);
                finish();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
