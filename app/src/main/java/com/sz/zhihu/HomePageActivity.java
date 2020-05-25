package com.sz.zhihu;

import android.os.Bundle;
import android.widget.ImageView;

import com.sz.zhihu.compoent.ScrollLayout;

public class HomePageActivity extends AbstractCustomActivity {

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
