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
import com.sz.zhihu.HomePageActivity;
import com.sz.zhihu.R;
import com.sz.zhihu.adapter.SearchUserAdapter;
import com.sz.zhihu.dto.SimpleDto;
import com.sz.zhihu.po.Keyword;
import com.sz.zhihu.utils.GsonUtils;
import com.sz.zhihu.utils.RequestUtils;
import com.sz.zhihu.vo.UserDoc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class UserFragment extends SearchFragment {

    private View view;
    private Activity activity;
    private Keyword keyword;
    private SmartRefreshLayout refreshLayout;
    private RecyclerView content;
    private final List<UserDoc> data;
    private SearchUserAdapter searchUserAdapter;
    private LinearLayoutManager linearLayoutManager;
    private String serverLocation;
    private final Gson gson;

    public UserFragment(Keyword keyword) {
        super(keyword);
        this.keyword = keyword;
        data = new ArrayList<>();
        gson = GsonUtils.getGson();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof Activity){
            activity = (Activity) context;
            searchUserAdapter = new SearchUserAdapter(activity, data,userDoc -> {
                Intent intent = new Intent(activity, HomePageActivity.class);
                intent.putExtra("userId",userDoc.getId());
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
            view = inflater.inflate(R.layout.search_user_fragment,container,false);
            init();
        }
        return view;
    }

    private void init() {
        refreshLayout = view.findViewById(R.id.suf_refresh_layout);
        content = view.findViewById(R.id.suf_content);
        content.setAdapter(searchUserAdapter);
        content.setLayoutManager(linearLayoutManager);
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setOnLoadMoreListener(this);
        refreshLayout.autoRefresh();
    }

    @Override
    void refresh(int size, Consumer<Boolean> isSuccess, Consumer<Integer> len) {
        data.clear();
        searchUserAdapter.notifyDataSetChanged();
        loadMore(0,size,isSuccess,len);
    }

    @Override
    void loadMore(int limit, int size, Consumer<Boolean> isSuccess, Consumer<Integer> len) {
        String url = serverLocation + "/SearchService/Search/User/" + keyword.getTitle().trim() + "/" + limit + "/" + size;
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
                        List<UserDoc> userDocs = gson.fromJson(gson.toJson(simpleDto.getObject()),new TypeToken<List<UserDoc>>(){}.getType());
                        if(userDocs != null){
                            data.addAll(userDocs);
                            searchUserAdapter.notifyDataSetChanged();
                            len.accept(userDocs.size());
                        }
                        isSuccess.accept(true);
                    }else{
                        Toast.makeText(activity,simpleDto.getMsg(),Toast.LENGTH_SHORT).show();
                        isSuccess.accept(false);
                    }
                });
            }
        });
    }


    @Override
    public String getTitle() {
        return "用户";
    }
}
