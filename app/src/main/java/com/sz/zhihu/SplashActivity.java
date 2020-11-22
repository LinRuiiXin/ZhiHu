package com.sz.zhihu;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.transition.Explode;

import com.sz.zhihu.utils.SystemUtils;
import com.sz.zhihu.vo.RecommendViewBean;

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
        Intent intent = new Intent(this, AskQuestionActivity.class);
        startActivity(intent);
    }

    /*private void jumpPage() {
        RecommendViewBean recommendViewBean = new RecommendViewBean(1117l, 4l, 31l, 1, 0, "抢大熊猫的竹子吃犯法吗？", "LinRuiXin", "158581734675531.png", "植发话题下优秀答者", null, 0l, 0l);
        Intent intent = new Intent(this, AnswerActivity.class);
        intent.putExtra("viewBean", recommendViewBean);
        startActivity(intent);
    }*/

    /*private void jumpPage() {
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
    }*/

}
