package com.sz.zhihu;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.os.Bundle;
import android.widget.LinearLayout;

import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;
import com.sz.zhihu.adapter.RecommendRecyclerViewAdapter;
import com.sz.zhihu.utils.DBUtils;
import com.sz.zhihu.utils.SystemUtils;
import com.sz.zhihu.vo.RecommendViewBean;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class BrowseActivity extends AbstractCustomActivity {
    private int RECYCLERVIEW_INDEX = 0;
    private final int SIZE = 10;
    private boolean hasMore = true;
    private RecyclerView recyclerView;
    private RecommendRecyclerViewAdapter adapter;
    private Toolbar toolbar;
    private LinearLayoutManager linearLayout;
    private List<RecommendViewBean> data;
    private SmartRefreshLayout refreshLayout;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse);
        SystemUtils.setStatusBarFullTransparent(this);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void init() {
        prepare();
        bindComponent();
        initToolBar();
    }

    private void prepare() {
        linearLayout = new LinearLayoutManager(this);
        data = new ArrayList<>();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void bindComponent() {
        recyclerView = findViewById(R.id.ab_recycler_view);
        toolbar = findViewById(R.id.ab_tool_bar);
        refreshLayout = findViewById(R.id.ab_refresher);
        refreshLayout.setOnRefreshListener(onRefresh());
        refreshLayout.setOnLoadMoreListener(onLoadMore());
        refreshLayout.autoRefresh();
        adapter = new RecommendRecyclerViewAdapter(this, data);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(linearLayout);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private OnRefreshListener onRefresh() {
        return refresher -> {
            RECYCLERVIEW_INDEX = 0;
            data.clear();
            loadMore(this::finishLoadMore);
        };
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private OnLoadMoreListener onLoadMore() {
        return refresher -> {
            if(hasMore){
                loadMore(this::finishLoadMore);
            }else{
                refresher.finishLoadMore();
            }
        };
    }

    private void finishLoadMore(List<RecommendViewBean> res) {
        data.addAll(res);
        hasMore = res.size() >= SIZE;
        RECYCLERVIEW_INDEX += res.size();
        refreshLayout.finishRefresh();
        refreshLayout.finishLoadMore();
        adapter.notifyDataSetChanged();
    }

    private void initToolBar() {
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v->finish());
        ActionBar supportActionBar = getSupportActionBar();
        if(supportActionBar != null){
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void loadMore(Consumer<List<RecommendViewBean>> consumer){
        consumer.accept(DBUtils.getBrowseRecord(RECYCLERVIEW_INDEX,SIZE));
    }
}
