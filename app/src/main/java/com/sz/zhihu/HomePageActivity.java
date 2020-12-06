package com.sz.zhihu;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.manager.TargetTracker;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.sz.zhihu.adapter.InformationAdapter;
import com.sz.zhihu.compoent.ScrollLayout;
import com.sz.zhihu.dto.SimpleDto;
import com.sz.zhihu.po.User;
import com.sz.zhihu.utils.DBUtils;
import com.sz.zhihu.utils.GsonUtils;
import com.sz.zhihu.utils.RequestUtils;
import com.sz.zhihu.vo.HomePageVo;
import com.sz.zhihu.vo.RecommendViewBean;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class HomePageActivity extends AbstractCustomActivity {

    private String serverLocation;
    private Gson gson;
    private ImageView portrait;
    private Toolbar toolbar;
    private FloatingActionButton actionButton;
    private RecyclerView information;
    private long userId;
    private User user;
    private User targetUser;
    private Drawable icon_editAccount;
    private Drawable icon_follow;
    private Drawable icon_success;
    private HomePageVo homePageVo;
    private final List<RecommendViewBean> data;
    private LinearLayoutManager linearLayoutManager;
    private InformationAdapter informationAdapter;
    private SmartRefreshLayout refresher;
    private final int LOAD_LEN;
    private boolean hasMore;
    private int limit;
    private TextView followSum;
    private TextView fensSum;
    private TextView profile;
    private CollapsingToolbarLayout toolbarLayout;
    //    private final int EDIT_ACCOUNT_CODE = 14;

    public HomePageActivity() {
        LOAD_LEN = 10;
        targetUser = null;
        homePageVo = null;
        hasMore = true;
        limit = 0;
        gson = GsonUtils.getGson();
        data = new ArrayList<>();
        /*String [] json = new String[]{
                "{\"contentId\":1259,\"questionId\":19,\"userId\":31,\"contentType\":1,\"type\":2,\"title\":\"有哪些典型的学生思维？\",\"username\":\"LinRuiXin\",\"portraitFileName\":\"158581734675531.png\",\"introduction\":\"植发话题下优秀答者\",\"content\":\"我不知道我在微博上的健康雷声大khaklsd大声哭了加大了&nbsp;sadsad a使得d是打算打\",\"thumbnail\":\"160655427797031.jpg\",\"supportSum\":0,\"commentSum\":0,\"date\":\"2020-11-28T17:15:37.000+00:00\"}",
                "{\"contentId\":1259,\"questionId\":19,\"userId\":31,\"contentType\":1,\"type\":2,\"title\":\"有哪些典型的学生思维？\",\"username\":\"LinRuiXin\",\"portraitFileName\":\"158581734675531.png\",\"introduction\":\"植发话题下优秀答者\",\"content\":\"我不知道我在微博上的健康雷声大khaklsd大声哭了加大了&nbsp;sadsad a使得d是打算打\",\"thumbnail\":\"160655427797031.jpg\",\"supportSum\":0,\"commentSum\":0,\"date\":\"2020-11-28T17:15:37.000+00:00\"}",
                "{\"contentId\":1259,\"questionId\":19,\"userId\":31,\"contentType\":1,\"type\":2,\"title\":\"有哪些典型的学生思维？\",\"username\":\"LinRuiXin\",\"portraitFileName\":\"158581734675531.png\",\"introduction\":\"植发话题下优秀答者\",\"content\":\"我不知道我在微博上的健康雷声大khaklsd大声哭了加大了&nbsp;sadsad a使得d是打算打\",\"thumbnail\":\"160655427797031.jpg\",\"supportSum\":0,\"commentSum\":0,\"date\":\"2020-11-28T17:15:37.000+00:00\"}",
                "{\"contentId\":1259,\"questionId\":19,\"userId\":31,\"contentType\":1,\"type\":2,\"title\":\"有哪些典型的学生思维？\",\"username\":\"LinRuiXin\",\"portraitFileName\":\"158581734675531.png\",\"introduction\":\"植发话题下优秀答者\",\"content\":\"我不知道我在微博上的健康雷声大khaklsd大声哭了加大了&nbsp;sadsad a使得d是打算打\",\"thumbnail\":\"160655427797031.jpg\",\"supportSum\":0,\"commentSum\":0,\"date\":\"2020-11-28T17:15:37.000+00:00\"}",
                "{\"contentId\":1259,\"questionId\":19,\"userId\":31,\"contentType\":1,\"type\":2,\"title\":\"有哪些典型的学生思维？\",\"username\":\"LinRuiXin\",\"portraitFileName\":\"158581734675531.png\",\"introduction\":\"植发话题下优秀答者\",\"content\":\"我不知道我在微博上的健康雷声大khaklsd大声哭了加大了&nbsp;sadsad a使得d是打算打\",\"thumbnail\":\"160655427797031.jpg\",\"supportSum\":0,\"commentSum\":0,\"date\":\"2020-11-28T17:15:37.000+00:00\"}",
                "{\"contentId\":1260,\"questionId\":19,\"userId\":31,\"contentType\":1,\"type\":2,\"title\":\"有哪些典型的学生思维？\",\"username\":\"LinRuiXin\",\"portraitFileName\":\"158581734675531.png\",\"introduction\":\"植发话题下优秀答者\",\"content\":\"我不知道我在微博上的健康雷声大khaklsd大声哭了加大了&nbsp;sadsad a使得d是打算打\",\"thumbnail\":\"160655427797031.jpg\",\"supportSum\":0,\"commentSum\":0,\"date\":\"2020-11-28T17:15:39.000+00:00\"}"
        };
        for (String s : json) {
            data.add(gson.fromJson(s,RecommendViewBean.class));
        }*/
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        initView();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void initView() {
        prepare();
        bindComponent();
        initToolBar();
        getHomePageData();
    }

    private void prepare() {
        userId = getIntent().getLongExtra("userId", -1);
        user = DBUtils.queryUserHistory();
        icon_editAccount = getDrawable(R.drawable.icon_edit);
        icon_follow = getDrawable(R.drawable.icon_plus_white);
        icon_success = getDrawable(R.drawable.icon_success);
        serverLocation = getString(R.string.server_location);
        linearLayoutManager = new LinearLayoutManager(this);
        informationAdapter = new InformationAdapter(this, data);
    }

    private void bindComponent() {
        portrait = findViewById(R.id.ahp_portrait);
        toolbar = findViewById(R.id.ahp_toolbar);
        actionButton = findViewById(R.id.ahp_floating_action_btn);
        information = findViewById(R.id.ahp_information);
        refresher = findViewById(R.id.ahp_refresher);
        followSum = findViewById(R.id.ahp_follow_sum);
        fensSum = findViewById(R.id.ahp_fens_sum);
        profile = findViewById(R.id.ahp_profile);
        toolbarLayout = findViewById(R.id.ahp_toolbar_layout);
        refresher.setEnableRefresh(false);
        refresher.autoLoadMore();
        refresher.setOnLoadMoreListener(loadMore());
        information.setAdapter(informationAdapter);
        information.setLayoutManager(linearLayoutManager);
        buttonControl(false);
    }

    private void initToolBar() {
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void getHomePageData() {
        getHomePageVo(this::updateUserInfoUI);
        getUserInformation(0,LOAD_LEN,refresher::finishLoadMore);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void updateUserInfoUI(HomePageVo vo) {
        homePageVo = vo;
        targetUser = vo.getUser();
        if(user != null && user.getUserId() == targetUser.getId()) toEditAccount();
        else if(vo.isAttention()) toFollow();
        else toUnFollow();
        String imgUrl = serverLocation + "/res/User/" + targetUser.getId() + "/" + targetUser.getPortraitFileName();
        Glide.with(this).load(imgUrl).into(portrait);
        toolbarLayout.setTitle(targetUser.getUserName());
        setUserInfo();
    }

    private void setUserInfo() {
        followSum.setText(String.valueOf(targetUser.getFollowSum()));
        fensSum.setText(String.valueOf(targetUser.getFensSum()));
        profile.setText(targetUser.getProfile());
    }

    private void getUserInformation(int limit,int size,Consumer<Boolean> consumer) {
        String url = serverLocation + "/UserService/User/Information/" + userId + "/" + limit + "/" + size;
        RequestUtils.sendSimpleRequest(url, new Callback() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> {
                    Toast.makeText(HomePageActivity.this,"请求失败",Toast.LENGTH_SHORT).show();
                    consumer.accept(false);
                });
            }

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                SimpleDto simpleDto = GsonUtils.parseJsonToSimpleDto(response.body().string());
                runOnUiThread(()->{
                    if(simpleDto.isSuccess()){
                        List<RecommendViewBean> recommendViewBeans = gson.fromJson(gson.toJson(simpleDto.getObject()),new TypeToken<List<RecommendViewBean>>(){}.getType());
                        hasMore = recommendViewBeans.size() >= size;
                        HomePageActivity.this.limit += recommendViewBeans.size();
                        data.addAll(recommendViewBeans);
                        informationAdapter.notifyDataSetChanged();
                    }else
                        Toast.makeText(HomePageActivity.this,simpleDto.getMsg(),Toast.LENGTH_SHORT).show();
                    consumer.accept(simpleDto.isSuccess());
                });
            }
        });
    }

    private void getHomePageVo(Consumer<HomePageVo> consumer) {
        String getUserUrl = serverLocation + "/UserService/User/HomePage/" + userId + "/" + (user == null ? -1 : user.getUserId());
        RequestUtils.sendSimpleRequest(getUserUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(HomePageActivity.this,"请求失败",Toast.LENGTH_SHORT).show() );
            }

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                SimpleDto simpleDto = GsonUtils.parseJsonToSimpleDto(response.body().string());
                runOnUiThread(() -> {
                    if(simpleDto.isSuccess()){
                        HomePageVo homePageVo = gson.fromJson(gson.toJson(simpleDto.getObject()), HomePageVo.class);
                        consumer.accept(homePageVo);
                        buttonControl(true);
                    }else{
                        Toast.makeText(HomePageActivity.this,simpleDto.getMsg(),Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private OnLoadMoreListener loadMore() {
        return refreshLayout -> {
            if(hasMore){
                getUserInformation(limit,LOAD_LEN,refreshLayout::finishLoadMore);
            }else{
                Toast.makeText(this,"没有更多了",Toast.LENGTH_SHORT).show();
                refreshLayout.finishLoadMore(true);
            }
        };
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void follow(Consumer<Boolean> consumer){
        followAPI("Follow",consumer);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void unFollow(Consumer<Boolean> consumer){
        followAPI("UnFollow",consumer);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void followAPI(String api, Consumer<Boolean> consumer){
        if(DBUtils.checkIsLogged(this)){
            actionButton.setClickable(false);
            String url = serverLocation + "/UserService/User/" + api;
            Map<String,String> params = new HashMap<>(2);
            params.put("targetUserId",String.valueOf(userId));
            params.put("userId",String.valueOf(user.getUserId()));
            RequestUtils.postWithParams(url, params, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(()->{
                        Toast.makeText(HomePageActivity.this,"请求失败",Toast.LENGTH_SHORT).show();
                        actionButton.setClickable(true);
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    SimpleDto simpleDto = GsonUtils.parseJsonToSimpleDto(response.body().string());
                    runOnUiThread(()->{
                        consumer.accept(simpleDto.isSuccess());
                        actionButton.setClickable(true);
                    });
                }
            });
        }
    }

    private void toEditAccount(){
        actionButton.setImageDrawable(icon_editAccount);
        actionButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, EditAccountActivity.class);
            startActivity(intent);
            finish();
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void toFollow(){
        actionButton.setImageDrawable(icon_success);
        actionButton.setOnClickListener(v -> {
            unFollow(isSuccess -> {
                if(isSuccess){
                    targetUser.setFensSum(targetUser.getFensSum()-1);
                    setUserInfo();
                    toUnFollow();
                }
                else Toast.makeText(this,"请求失败",Toast.LENGTH_SHORT).show();
            });
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void toUnFollow(){
        actionButton.setImageDrawable(icon_follow);
        actionButton.setOnClickListener(v -> {
            follow(isSuccess -> {
                if(isSuccess){
                    toFollow();
                    targetUser.setFensSum(targetUser.getFensSum()+1);
                    setUserInfo();
                }else Toast.makeText(this,"请求失败",Toast.LENGTH_SHORT).show();
            });
        });
    }


    private void buttonControl(boolean b) {
        portrait.setClickable(b);
        actionButton.setClickable(b);
    }

}
