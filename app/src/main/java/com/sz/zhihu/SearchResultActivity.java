package com.sz.zhihu;


import android.os.Bundle;

import com.sz.zhihu.po.Keyword;

public class SearchResultActivity extends AbstractCustomActivity {

    private Keyword keyword;
    private String serverLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        init();
    }

    private void init() {
        prepare();
    }

    private void prepare() {
        keyword = (Keyword) getIntent().getSerializableExtra("keyword");
        serverLocation = getString(R.string.server_location);
    }
}