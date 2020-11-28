package com.sz.zhihu.fragment.index;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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
import com.sz.zhihu.R;
import com.sz.zhihu.adapter.AttentionListAdapter;
import com.sz.zhihu.adapter.NewInformationAdapter;
import com.sz.zhihu.dto.SimpleDto;
import com.sz.zhihu.interfaces.CustomFragmentFunction;
import com.sz.zhihu.po.Information;
import com.sz.zhihu.po.User;
import com.sz.zhihu.po.UserAttention;
import com.sz.zhihu.utils.DBUtils;
import com.sz.zhihu.utils.GsonUtils;
import com.sz.zhihu.utils.RequestUtils;
import com.sz.zhihu.utils.StringUtils;
import com.sz.zhihu.vo.NewInformationVo;
import com.sz.zhihu.vo.RecommendViewBean;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class CareFragment extends Fragment implements CustomFragmentFunction {

    private final Activity activity;
    private View view;
    private final String serverLocation;
    private User user;
    private RecyclerView attentionList;
    private RecyclerView information;
    private final List<User> attentionUsers;
    private final List<RecommendViewBean> informationViewBeans;
    private LinearLayoutManager attentionListLayoutManager;
    private LinearLayoutManager informationLayoutManager;
    private List<UserAttention> userAttentions;
    private final Gson gson;
    private AttentionListAdapter attentionListAdapter;
    private NewInformationAdapter newInformationAdapter;
    private boolean hasMore = true;
    private int index = 0;
    private SmartRefreshLayout refresher;

    public CareFragment(Activity activity){
        this.activity = activity;
        gson = GsonUtils.getGson();
        serverLocation = activity.getString(R.string.server_location);
        user = DBUtils.queryUserHistory();
        attentionUsers = new ArrayList<>();
        informationViewBeans = new ArrayList<>();
    }



    @RequiresApi(api = Build.VERSION_CODES.N)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(view == null){
            view = inflater.inflate(R.layout.fragment_index_care,container,false);
            init();
            refreshPage();
        }
        return view;
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private void init() {
        bindComponent();
        prepare();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void prepare() {
        attentionListAdapter = new AttentionListAdapter(activity, attentionUsers, this::userClickListener);
        newInformationAdapter = new NewInformationAdapter(activity, informationViewBeans);
        attentionListLayoutManager = new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false);
        informationLayoutManager = new LinearLayoutManager(activity);
        attentionList.setLayoutManager(attentionListLayoutManager);
        attentionList.setAdapter(attentionListAdapter);
        information.setLayoutManager(informationLayoutManager);
        information.setAdapter(newInformationAdapter);
        refresher.autoRefresh();
        refresher.setOnRefreshListener(refreshLayout -> refreshPage());
        refresher.setOnLoadMoreListener(refreshLayout -> loadMore(refresher::finishLoadMore));

    }

    private void bindComponent() {
        attentionList = view.findViewById(R.id.fic_attention_list);
        information = view.findViewById(R.id.fic_new_information);
        refresher = view.findViewById(R.id.fic_refresher);
    }

    private void userClickListener(User user) {

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void onUserAttentionCallBack(List<UserAttention> list) {
        this.userAttentions = list;
        String ids = StringUtils.jointString('-', list, "beAttentionUserId");
        getAttentionUsers(ids);
        getNewInformation(list,()->{loadMore(refresher::finishRefresh);});
    }


    private void getAttentionUsers(String ids) {
        String url = serverLocation + "/UserService/User/Batch/" + ids;
        RequestUtils.sendSimpleRequest(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                activity.runOnUiThread(()-> Toast.makeText(activity,"请求失败",Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                SimpleDto simpleDto = GsonUtils.parseJsonToSimpleDto(response.body().string());
                activity.runOnUiThread(()->{
                    if(simpleDto.isSuccess()){
                        attentionUsers.clear();
                        attentionUsers.addAll(gson.fromJson(gson.toJson(simpleDto.getObject()),new TypeToken<List<User>>(){}.getType()));
                        attentionListAdapter.notifyDataSetChanged();
                    }else{
                        Toast.makeText(activity,simpleDto.getMsg(),Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @SuppressLint("LongLogTag")
    private void getNewInformation(List<UserAttention> list, Runnable runnable) {
        String url = serverLocation + "/UserService/User/NewInformation";
        Log.i("CardFragment",list.toString());
        RequestUtils.sendRequestWithJson(list, url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                activity.runOnUiThread(()->{
                    Toast.makeText(activity,"请求失败",Toast.LENGTH_SHORT).show();
                    runnable.run();
                });
            }

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                SimpleDto simpleDto = GsonUtils.parseJsonToSimpleDto(response.body().string());
                activity.runOnUiThread(()->{
                    if(simpleDto.isSuccess()){
                        NewInformationVo newInformationVo = gson.fromJson(gson.toJson(simpleDto.getObject()), NewInformationVo.class);
                        if(newInformationVo != null){
                            int size = newInformationVo.getInformation().size();
                            DBUtils.updateAttentionVersion(newInformationVo.getNewVersion(),list);
                            Log.i("CardFragment",DBUtils.getUserAttentionList(user.getUserId()).toString());
                            DBUtils.insertInformation(newInformationVo.getInformation());
                            Toast.makeText(activity,"为你更新了"+size+"条数据",Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(activity,simpleDto.getMsg(),Toast.LENGTH_SHORT).show();
                    }
                    runnable.run();
                });
            }
        });
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private void loadMore(Consumer<Boolean> consumer) {
        if(hasMore){
            List<Information> newInformation = DBUtils.getNewInformation(index, 10);
            int size = newInformation.size();
            index += size;
            hasMore = size >= 10;
            if(size > 0){
                String url = serverLocation + "/RecommendService/Recommend/InformationViewBean";
                RequestUtils.sendRequestWithJson(newInformation, url, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        activity.runOnUiThread(()->{
                            Toast.makeText(activity,"请求失败",Toast.LENGTH_SHORT).show();
                            consumer.accept(false);
                        });
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        SimpleDto simpleDto = GsonUtils.parseJsonToSimpleDto(response.body().string());
                        activity.runOnUiThread(()->{
                            if(simpleDto.isSuccess()){
                                List<RecommendViewBean> viewBeans = gson.fromJson(gson.toJson(simpleDto.getObject()),new TypeToken<List<RecommendViewBean>>(){}.getType());
                                /*viewBeans.sort((RecommendViewBean r1, RecommendViewBean r2) -> {
                                        return r1.getTime().compareTo(r2.getTime());
                                    }
                                );*/
                                informationViewBeans.addAll(viewBeans);
                                newInformationAdapter.notifyDataSetChanged();
                                consumer.accept(true);
                            }else{
                                Toast.makeText(activity,simpleDto.getMsg(),Toast.LENGTH_SHORT).show();
                                consumer.accept(false);
                            }
                        });
                    }
                });
            }
        }else{
            Toast.makeText(activity,"没有更多了",Toast.LENGTH_SHORT).show();
            consumer.accept(true);
        }
    }

    private void refresherControl(boolean b) {
        refresher.setEnableRefresh(b);
        refresher.setEnableLoadMore(b);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void refreshPage() {
        DBUtils.getUserAttentionList(activity,user,this::onUserAttentionCallBack);
        index = 0;
    }
}
