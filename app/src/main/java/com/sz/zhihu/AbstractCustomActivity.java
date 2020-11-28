package com.sz.zhihu;

import android.os.Bundle;
import android.transition.Explode;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.sz.zhihu.utils.SystemUtils;

public abstract class AbstractCustomActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initActivity();
    }

    public void initActivity(){
        //设置状态栏透明
        SystemUtils.setStatusBarFullTransparent(this);
        getWindow().setEnterTransition(new Explode().setDuration(400));
        getWindow().setExitTransition(new Explode().setDuration(400));
    }

}
