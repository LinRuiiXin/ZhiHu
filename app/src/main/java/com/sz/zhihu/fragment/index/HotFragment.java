package com.sz.zhihu.fragment.index;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;
import com.sz.zhihu.QuestionInfoActivity;
import com.sz.zhihu.R;
import com.sz.zhihu.adapter.HotListAdapter;
import com.sz.zhihu.dto.SimpleDto;
import com.sz.zhihu.interfaces.CustomFragmentFunction;
import com.sz.zhihu.po.Question;
import com.sz.zhihu.utils.GsonUtils;
import com.sz.zhihu.utils.RequestUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class HotFragment extends Fragment implements CustomFragmentFunction {

    private View view;
    private boolean hasMore = true;
    private int limit = 0;
    private Activity activity;
    private Gson gson;
    private String serverLocation;
    private List<Question> data;
    private LinearLayoutManager linearLayoutManager;
    private HotListAdapter hotListAdapter;
    private final int SIZE = 10;
    private SmartRefreshLayout refresher;
    private RecyclerView content;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof Activity){
            this.activity = (Activity)context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(view == null) {
            view = inflater.inflate(R.layout.fragment_index_hot, container, false);
            init();
        }
        return view;
    }

    private void init() {
        prepare();
        bindComponent();
    }

    private void prepare() {
        gson = GsonUtils.getGson();
        serverLocation = activity.getString(R.string.server_location);
        data = new ArrayList<>();
        linearLayoutManager = new LinearLayoutManager(activity);
        hotListAdapter = new HotListAdapter(activity, data,this::onQuestionSelect);
    }

    private void bindComponent() {
        refresher = view.findViewById(R.id.fih_refresher);
        content = view.findViewById(R.id.fih_content);
        content.setAdapter(hotListAdapter);
        content.setLayoutManager(linearLayoutManager);
        refresher.setOnRefreshListener(refreshListener());
        refresher.setOnLoadMoreListener(loadMoreListener());
        refresher.autoRefresh();
    }

    private OnRefreshListener refreshListener() {
        return refresher -> {
            clearData();
            getHotList(refresher::finishRefresh);
        };
    }

    private OnLoadMoreListener loadMoreListener() {
        return refresher -> {
            if(hasMore){
                getHotList(refresher::finishLoadMore);
            }else{
                refresher.finishLoadMore(true);
            }
        };
    }

    private void onQuestionSelect(Question question) {
        Intent intent = new Intent(activity, QuestionInfoActivity.class);
        intent.putExtra("questionId",question.getId());
        intent.putExtra("title",question.getName());
        activity.startActivity(intent);
    }

    public void getHotList(Consumer<Boolean> consumer){
        String url = serverLocation + "/QuestionService/Question/Hot/" + limit + "/" + SIZE;
        RequestUtils.sendSimpleRequest(url, new Callback() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onFailure(Call call, IOException e) {
                activity.runOnUiThread(()->{
                    Toast.makeText(activity,"请求失败",Toast.LENGTH_SHORT).show();
                    consumer.accept(false);
                });
            }

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                SimpleDto simpleDto = GsonUtils.parseJsonToSimpleDto(response.body().string());
                activity.runOnUiThread(()->{
                    if(simpleDto.isSuccess()){
                        List<Question> res = gson.fromJson(gson.toJson(simpleDto.getObject()),new TypeToken<List<Question>>(){}.getType());
                        if(res != null){
                            data.addAll(res);
                            limit += res.size();
                            if(res.size() < SIZE) hasMore = false;
                            hotListAdapter.notifyDataSetChanged();
                        }
                    }else{
                        Toast.makeText(activity,simpleDto.getMsg(),Toast.LENGTH_SHORT).show();
                    }
                    consumer.accept(simpleDto.isSuccess());
                });
            }
        });
    }


    private void clearData() {
        limit = 0;
        hasMore = true;
        data.clear();
    }

    @Override
    public void refreshPage() {

    }
}
