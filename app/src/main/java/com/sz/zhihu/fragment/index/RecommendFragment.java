package com.sz.zhihu.fragment.index;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.sz.zhihu.R;
import com.sz.zhihu.adapter.RecommendRecyclerViewAdapter;
import com.sz.zhihu.dto.SimpleDto;
import com.sz.zhihu.interfaces.CustomFragmentFunction;
import com.sz.zhihu.po.User;
import com.sz.zhihu.utils.FileUtils;
import com.sz.zhihu.utils.HtmlUtils;
import com.sz.zhihu.utils.RequestUtils;
import com.sz.zhihu.utils.UserUtils;
import com.sz.zhihu.vo.RecommendViewBean;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class RecommendFragment extends Fragment implements CustomFragmentFunction {

    private View view;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Activity activity;
    private Handler handler = new Handler(msg->{
        swipeRefreshLayout.setRefreshing(false);
        return false;
    });
    private List<RecommendViewBean> data;
    private final User user;
    private final String serverLocation;
    private final Gson gson;
    private RecommendRecyclerViewAdapter adapter;

    public RecommendFragment(Activity activity){
        this.activity = activity;
        data = new ArrayList<RecommendViewBean>();
        user = UserUtils.queryUserHistory();
        serverLocation = activity.getResources().getString(R.string.server_location);
        gson = new Gson();
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
            swipeRefreshLayout.setRefreshing(true);
            RecyclerView recyclerView = view.findViewById(R.id.recommend_recyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            adapter = new RecommendRecyclerViewAdapter(getContext(), data);
            recyclerView.setAdapter(adapter);
            getData();
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
            data.clear();
            getData();
        };
    }
    public void getData(){
        String url;
        if(user == null){
            url = serverLocation + "/Recommend/Index/-1";
        }else{
            url = serverLocation + "/Recommend/Index/" + user.getUserId();
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
                        list.forEach(o->{
                            RecommendViewBean recommendViewBean = gson.fromJson(gson.toJson(o), RecommendViewBean.class);
                            data.add(recommendViewBean);
                        });
                        Collections.shuffle(data);
                        swipeRefreshLayout.setRefreshing(false);
                        adapter.notifyDataSetChanged();
                    }else{
                        Toast.makeText(activity,simpleDto.getMsg(),Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
