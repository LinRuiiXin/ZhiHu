package com.sz.zhihu;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

public class HomePageActivity extends AppCompatActivity {

    private ImageView top;
    private ScrollLayout mScrollLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        initView();
    }

    private void initView() {
        top = findViewById(R.id.top);
        mScrollLayout = findViewById(R.id.why);
        mScrollLayout.setHeader(top);
    }
}
