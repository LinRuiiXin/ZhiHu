package com.sz.zhihu;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.sz.zhihu.adapter.RecommendRecyclerViewAdapter;
import com.sz.zhihu.utils.DBUtils;
import com.sz.zhihu.utils.SystemUtils;

public class BrowseActivity extends AbstractCustomActivity {
    private int RECYCLERVIEW_INDEX = 0;
    private RecyclerView recyclerView;
    private RecommendRecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse);
        SystemUtils.setStatusBarFullTransparent(this);
        init();
    }

    private void init() {
        recyclerView = findViewById(R.id.ab_recycler_view);
        Toolbar toolbar = findViewById(R.id.ab_tool_bar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v->finish());
        ActionBar supportActionBar = getSupportActionBar();
        if(supportActionBar != null){
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }
        bindData();
    }

    private void bindData() {
        adapter = new RecommendRecyclerViewAdapter(this, DBUtils.getBrowseRecord(RECYCLERVIEW_INDEX));
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        RECYCLERVIEW_INDEX = RECYCLERVIEW_INDEX + 10;
    }
}
