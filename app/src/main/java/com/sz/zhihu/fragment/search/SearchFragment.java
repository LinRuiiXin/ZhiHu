package com.sz.zhihu.fragment.search;

import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;
import com.sz.zhihu.po.Keyword;

import java.util.function.Consumer;

public abstract class SearchFragment extends Fragment implements OnRefreshListener, OnLoadMoreListener {

    //每次刷新的条数
    private final int size;
    //下标
    private int limit;
    private boolean hasMore;

    public SearchFragment(Keyword keyword){
        this(keyword,0,10);
    }

    /*
    * 允许子类定制每次刷新的条数与起始位置
    * */
    public SearchFragment(Keyword keyword,int limit,int size){
        this.limit = limit;
        this.size = size;
        hasMore = true;
    }

    /*
    * 该类管理子类的刷新与加载的起始，大小，而子类仅需回调是否成功，刷新条数即可
    * */
    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        if(hasMore){
            loadMore(limit,size,refreshLayout::finishLoadMore,len -> {
                if(len < size){
                    hasMore = false;
                }
                limit += len;
            });
        }else{
            refreshLayout.finishLoadMore(true);
        }
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        limit = 0;
        refresh(size,refreshLayout::finishRefresh,len -> {
            if(len < size){
                hasMore = false;
            }
            limit += len;
        });

    }

    abstract void refresh(int size,Consumer<Boolean> isSuccess, Consumer<Integer> len);
    abstract void loadMore(int limit,int size,Consumer<Boolean> isSuccess, Consumer<Integer> len);

    public abstract String getTitle();
}
