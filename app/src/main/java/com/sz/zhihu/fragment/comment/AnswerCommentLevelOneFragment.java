package com.sz.zhihu.fragment.comment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.sz.zhihu.AnswerActivity;
import com.sz.zhihu.R;
import com.sz.zhihu.adapter.AnswerCommentLevelOneAdapter;
import com.sz.zhihu.dto.SimpleDto;
import com.sz.zhihu.utils.RequestUtils;
import com.sz.zhihu.vo.AnswerCommentLevelOneVo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AnswerCommentLevelOneFragment extends Fragment {

    private Context context;
    private final Gson gson;
    private final String serverLocation;
    private final Long answerId;
    private final Long userId;
    private Runnable runnable;
    private RecyclerView recyclerView;
    private List<AnswerCommentLevelOneVo> data;
    private int limit = 0;
    private AnswerCommentLevelOneAdapter adapter;

    public AnswerCommentLevelOneFragment(String serverLocation, Long answerId, Long userId, Runnable runnable){
        this.answerId = answerId;
        this.userId = userId;
        this.runnable = runnable;
        this.serverLocation = serverLocation;
        gson = new Gson();
        data = new ArrayList<>();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.answer_comment_level_one_fragment, container, false);
        SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.aco_swipeRefreshLayout);
        recyclerView = view.findViewById(R.id.aco_recyclerview);
        swipeRefreshLayout.setEnabled(false);
        initData();
        return view;
    }

    private void initData() {
        adapter = new AnswerCommentLevelOneAdapter(context, data);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        getCommentLevelOne();
    }

    /*
     * 获取当前回答评论数据
     * */
    private void getCommentLevelOne() {
        String url = serverLocation + "/Comment/LevelOne/"+answerId+"/"+userId+"/"+limit;
        RequestUtils.sendSimpleRequest(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(context,"服务器繁忙",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                SimpleDto simpleDto = gson.fromJson(response.body().string(), SimpleDto.class);
                if(simpleDto.isSuccess()){
                    Object object = simpleDto.getObject();
                    List list = gson.fromJson(gson.toJson(object), List.class);
                    List<AnswerCommentLevelOneVo> res = new ArrayList<>(list.size());
                    for(Object o : list){
                        res.add(gson.fromJson(gson.toJson(o),AnswerCommentLevelOneVo.class));
                    }
                    loadFinish(res);
                }
            }
        });
    }

    private void loadFinish(List<AnswerCommentLevelOneVo> res) {
        if(res.size() == 0 || res.size() < 10){
            Toast.makeText(context,"没有更多了", Toast.LENGTH_SHORT).show();
        }
        if(res.size() == 0) return;
        limit+=res.size();
        data.addAll(res);
        adapter.notifyDataSetChanged();
    }
}
