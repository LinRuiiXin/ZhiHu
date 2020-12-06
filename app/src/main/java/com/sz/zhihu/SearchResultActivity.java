package com.sz.zhihu;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.astuetz.PagerSlidingTabStrip;
import com.google.gson.Gson;
import com.sz.zhihu.fragment.search.ArticleFragment;
import com.sz.zhihu.fragment.search.ComprehensiveFragment;
import com.sz.zhihu.fragment.search.QuestionFragment;
import com.sz.zhihu.fragment.search.SearchFragment;
import com.sz.zhihu.fragment.search.UserFragment;
import com.sz.zhihu.po.Keyword;
import com.sz.zhihu.utils.GsonUtils;

public class SearchResultActivity extends AbstractCustomActivity {

    private Keyword keyword;
    private String serverLocation;
    private Gson gson;
    private Toolbar toolbar;
    private TextView search;
    private ViewPager viewPager;
    private PagerSlidingTabStrip pagerHolder;
    private ComprehensiveFragment comprehensiveFragment;
    private QuestionFragment questionFragment;
    private ArticleFragment articleFragment;
    private UserFragment userFragment;
    private SearchFragment[] fragments;
    private TextView search1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        init();
    }

    private void init() {
        prepare();
        bindComponent();
        setListener();
        initToolBar();
        initViewPager();
    }

    private void prepare() {
        keyword = (Keyword) getIntent().getSerializableExtra("keyword");
        serverLocation = getString(R.string.server_location);
        gson = GsonUtils.getGson();
    }

    private void bindComponent() {
        toolbar = findViewById(R.id.asr_tool_bar);
        search = findViewById(R.id.asr_search);
        viewPager = findViewById(R.id.asr_view_pager);
        pagerHolder = findViewById(R.id.asr_pager_holder);
        search.setText(keyword.getTitle());
    }

    private void setListener() {
        search.setOnClickListener(v -> {
            Intent intent = new Intent(this, SearchActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void initToolBar() {
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            supportActionBar.setDisplayShowTitleEnabled(false);
        }
    }

    private void initViewPager() {
        fragments = new SearchFragment[]{new ComprehensiveFragment(keyword),new QuestionFragment(keyword),new ArticleFragment(keyword),new UserFragment(keyword)};
        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @NonNull
            @Override
            public Fragment getItem(int position) {
                return fragments[position];
            }

            @Nullable
            @Override
            public CharSequence getPageTitle(int position) {
                return fragments[position].getTitle();
            }

            @Override
            public int getCount() {
                return fragments.length;
            }
        });
        pagerHolder.setShouldExpand(true);
        pagerHolder.setViewPager(viewPager);
        pagerHolder.setDividerColor(Color.TRANSPARENT); //设置每个标签之间的间隔线颜色 ->透明
        pagerHolder.setUnderlineHeight(3); //设置标签栏下边的间隔线高度，单位像素
        pagerHolder.setIndicatorHeight(6); //设置Indicator 游标 高度，单位像素
    }
}