package com.sz.zhihu.fragment.index;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.sz.zhihu.R;
import com.sz.zhihu.adapter.RecommendRecyclerViewAdapter;
import com.sz.zhihu.dto.SimpleDto;
import com.sz.zhihu.interfaces.CustomFragmentFunction;
import com.sz.zhihu.po.User;
import com.sz.zhihu.utils.RequestUtils;
import com.sz.zhihu.utils.DBUtils;
import com.sz.zhihu.vo.RecommendViewBean;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class RecommendFragment extends Fragment implements CustomFragmentFunction {

    private View view;
    private Activity activity;
    private List<RecommendViewBean> data;
    private final User user;
    private final String serverLocation;
    private final Gson gson;
    private RecommendRecyclerViewAdapter adapter;
    private RecyclerView recyclerView;
    private RefreshLayout refreshLayout;

    public RecommendFragment(Activity activity){
        this.activity = activity;
        data = new ArrayList();
        user = DBUtils.queryUserHistory();
        serverLocation = activity.getResources().getString(R.string.server_location);
        gson = new Gson();
    }
    @SuppressLint("ResourceAsColor")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(view == null) {
            view = inflater.inflate(R.layout.fragment_index_recommend, container, false);
            refreshLayout = view.findViewById(R.id.recommend_refresh_layout);
            refreshLayout.setOnRefreshListener(refresh -> {
                getData(list->{
                    recyclerView.smoothScrollToPosition(0);
                    data.clear();
                    data.addAll(list);
                    adapter.notifyDataSetChanged();
                    refresh.finishRefresh();
                });
            });
            refreshLayout.setOnLoadMoreListener(refresh -> {
                getData(list -> {
                    data.addAll(list);
                    adapter.notifyDataSetChanged();
                    refresh.finishLoadMore();
                });
            });
            recyclerView = view.findViewById(R.id.recommend_recyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            adapter = new RecommendRecyclerViewAdapter(getContext(), data);
            recyclerView.setAdapter(adapter);
            getData(list -> {
                data.addAll(list);
                adapter.notifyDataSetChanged();
            });
        }
        return view;
    }
    /*
    * 屏幕移动到最上面，并进行刷新操作
    * 点击首页时被调用，前提是当前在首页---推荐
    * */
    @Override
    public void refreshPage() {
//        swipeRefreshLayout.setRefreshing(true);
        recyclerView.smoothScrollToPosition(0);
        getData(list -> {
//            swipeRefreshLayout.setRefreshing(false);
            data.clear();
            data.addAll(list);
            adapter.notifyDataSetChanged();
        });
    }
    /*
    * 下拉刷新
    * */
    public SwipeRefreshLayout.OnRefreshListener onRefreshListener(){
        return ()->{
            data.clear();
            getData(list -> {
                data.clear();
                data.addAll(list);
                adapter.notifyDataSetChanged();
            });
        };
    }

    public void getData(Consumer<List<RecommendViewBean>> consumer){
        String url;
        if(user == null){
            url = serverLocation + "/RecommendService/Recommend/Index/-1";
        }else{
            url = serverLocation + "/RecommendService/Recommend/Index/" + user.getUserId();
        }

        RequestUtils.sendSimpleRequest(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                activity.runOnUiThread(()-> Toast.makeText(activity,"请求失败",Toast.LENGTH_SHORT).show());
            }

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                SimpleDto simpleDto = gson.fromJson(response.body().string(), SimpleDto.class);
                activity.runOnUiThread(()->{
                    if(simpleDto.isSuccess()){
                        String s = gson.toJson(simpleDto.getObject());
                        List list = gson.fromJson(s, List.class);
                        List<RecommendViewBean> res = new ArrayList<>();
                        list.forEach(o->{
                            RecommendViewBean recommendViewBean = gson.fromJson(gson.toJson(o), RecommendViewBean.class);
                            res.add(recommendViewBean);
                        });
                        Collections.shuffle(res);
                        consumer.accept(res);
                    }else{
                        Toast.makeText(activity,simpleDto.getMsg(),Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
