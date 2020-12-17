package com.sz.zhihu.fragment.search;
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
import com.sz.zhihu.AnswerActivity;
import com.sz.zhihu.ArticleActivity;
import com.sz.zhihu.R;
import com.sz.zhihu.VideoActivity;
import com.sz.zhihu.adapter.SearchInformationAdapter;
import com.sz.zhihu.dto.SimpleDto;
import com.sz.zhihu.po.Keyword;
import com.sz.zhihu.utils.GsonUtils;
import com.sz.zhihu.utils.RequestUtils;
import com.sz.zhihu.vo.Information;
import com.sz.zhihu.vo.RecommendViewBean;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class ArticleFragment extends SearchFragment {

    private final List<Information> data;
    private View view;
    private Activity activity;
    private Keyword keyword;
    private final Gson gson;
    private SearchInformationAdapter informationAdapter;
    private LinearLayoutManager linearLayoutManager;
    private String serverLocation;

    public ArticleFragment(Keyword keyword) {
        super(keyword);
        this.keyword = keyword;
        this.data = new ArrayList<>();
        gson = GsonUtils.getGson();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof Activity){
            activity = (Activity) context;
            informationAdapter = new SearchInformationAdapter(activity, data, information -> {
                RecommendViewBean recommendViewBean = convertInformationToViewBean(information);
                Intent intent = new Intent(activity,recommendViewBean.getType() == 4 ? VideoActivity.class : ArticleActivity.class);
                intent.putExtra("viewBean",recommendViewBean);
                activity.startActivity(intent);
            });
            linearLayoutManager = new LinearLayoutManager(activity);
            serverLocation = activity.getString(R.string.server_location);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(view == null){
            view = inflater.inflate(R.layout.search_article_fragment,container,false);
            init();
        }
        return view;
    }

    private void init() {
        SmartRefreshLayout refreshLayout = view.findViewById(R.id.saf_refresh_layout);
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setOnLoadMoreListener(this);
        refreshLayout.autoRefresh();
        RecyclerView content = view.findViewById(R.id.saf_content);
        content.setAdapter(informationAdapter);
        content.setLayoutManager(linearLayoutManager);
    }

    @Override
    void refresh(int size, Consumer<Boolean> isSuccess, Consumer<Integer> len) {
        data.clear();
        informationAdapter.notifyDataSetChanged();
        loadMore(0,size,isSuccess,len);
    }

    @Override
    void loadMore(int limit, int size, Consumer<Boolean> isSuccess, Consumer<Integer> len) {
        String url = serverLocation + "/SearchService/Search/Article/" + keyword.getTitle() + "/" + limit + "/" + size;
        RequestUtils.sendSimpleRequest(url, new Callback() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onFailure(Call call, IOException e) {
                activity.runOnUiThread(()->{
                    Toast.makeText(activity,"请求失败",Toast.LENGTH_SHORT).show();
                    isSuccess.accept(false);
                });
            }

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                SimpleDto simpleDto = GsonUtils.parseJsonToSimpleDto(response.body().string());
                activity.runOnUiThread(()->{
                    if(simpleDto.isSuccess()){
                        List<com.sz.zhihu.vo.Information> res = gson.fromJson(gson.toJson(simpleDto.getObject()),new TypeToken<List<Information>>(){}.getType());
                        data.addAll(res);
                        isSuccess.accept(true);
                        len.accept(res.size());
                    }else{
                        Toast.makeText(activity,simpleDto.getMsg(),Toast.LENGTH_SHORT).show();
                        isSuccess.accept(false);
                    }
                });
            }
        });

    }

    public RecommendViewBean convertInformationToViewBean(Information information){
        return new RecommendViewBean()
                .contentType(information.contentType())
                .questionId(information.getQuestionId())
                .userId(information.getAuthorId())
                .contentId(information.contentId())
                .type(information.getType())
                .title(information.title())
                .username(information.getAuthorName())
                .portraitFileName(information.getPortraitFileName())
                .introduction(information.getProfile())
                .content(information.content());

    }

    @Override
    public String getTitle() {
        return "文章";
    }
}
