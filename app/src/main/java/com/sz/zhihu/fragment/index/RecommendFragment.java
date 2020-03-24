package com.sz.zhihu.fragment.index;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.sz.zhihu.R;
import com.sz.zhihu.adapter.RecommendRecyclerViewAdapter;
import com.sz.zhihu.interfaces.CustomFragmentFunction;
import com.sz.zhihu.vo.CardViewBean;

import java.util.ArrayList;
import java.util.List;

public class RecommendFragment extends Fragment implements CustomFragmentFunction {

    private View view;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Context context;
    private Handler handler = new Handler(){
        //刷新，UI线程
        @Override
        public void handleMessage(@NonNull Message msg) {
            swipeRefreshLayout.setRefreshing(false);
        }
    };
    private List<CardViewBean> data;
    public RecommendFragment(Context context){
        this.context = context;
        data = new ArrayList<CardViewBean>();
        for(int i=0;i<10;i++){
            data.add(new CardViewBean(1L,1L,1,1,"hello12","林瑞鑫","devdevdec","理解大手大脚是多久啊是滴哦案件呆多久啊理解大手大脚是多久啊是滴哦案件呆多久啊理解大手大脚是多久啊是滴哦案件呆多久啊理解大手大脚是多久啊是滴哦案件呆多久啊",12344L,212134L));
            data.add(new CardViewBean(1L,1L,1,1,"hello23","林瑞鑫","devdevdec","理解大手大脚是多久啊是滴哦案件呆多久啊理解大手大脚是多久啊是滴哦案件呆多久啊理解大手大脚是多久啊是滴哦案件呆多久啊理解大手大脚是多久啊是滴哦案件呆多久啊",12344L,212134L));
            data.add(new CardViewBean(1L,1L,1,3,"hello34","林瑞鑫","devdevdec","理解大手大脚是多久啊是滴哦案件呆多久啊理解大手大脚是多久啊是滴哦案件呆多久啊理解大手大脚是多久啊是滴哦案件呆多久啊理解大手大脚是多久啊是滴哦案件呆多久啊",12344L,212134L));
            data.add(new CardViewBean(1L,1L,1,2,"hello34","林瑞鑫","devdevdec","理解大手大脚是多久啊是滴哦案件呆多久啊理解大手大脚是多久啊是滴哦案件呆多久啊理解大手大脚是多久啊是滴哦案件呆多久啊理解大手大脚是多久啊是滴哦案件呆多久啊",12344L,212134L));
        }
    }
    @SuppressLint("ResourceAsColor")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(view == null) {
            view = inflater.inflate(R.layout.fragment_index_recommend, container, false);
            swipeRefreshLayout = view.findViewById(R.id.index_recommend_refresh);
            swipeRefreshLayout.setColorSchemeColors(R.color.blue);
            swipeRefreshLayout.setOnRefreshListener(onRefreshListener());
            RecyclerView recyclerView = view.findViewById(R.id.recommend_recyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.setAdapter(new RecommendRecyclerViewAdapter(getContext(),data));
        }
        return view;
    }
    /*
    * 屏幕移动到最上面，并进行刷新操作
    * 点击首页时被调用，前提是当前在首页---推荐
    * ---------未完善-------------
    * */
    @Override
    public void refreshPage() {
        swipeRefreshLayout.setRefreshing(true);
        handler.sendEmptyMessageDelayed(111,3000);
    }
    /*
    * 下拉刷新
    * ---------未完善-------------
    * */
    public SwipeRefreshLayout.OnRefreshListener onRefreshListener(){
        return ()->{
            handler.sendEmptyMessageDelayed(0,3000);
        };
    }
}
