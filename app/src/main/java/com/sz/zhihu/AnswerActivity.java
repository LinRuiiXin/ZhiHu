package com.sz.zhihu;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sz.zhihu.adapter.AnswerFragmentAdapter;
import com.sz.zhihu.dto.SimpleDto;
import com.sz.zhihu.fragment.answer.AnswerFragment;
import com.sz.zhihu.po.User;
import com.sz.zhihu.utils.RequestUtils;
import com.sz.zhihu.utils.SystemUtils;
import com.sz.zhihu.utils.DBUtils;
import com.sz.zhihu.vo.AnswerVo;
import com.sz.zhihu.vo.RecommendViewBean;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.sz.zhihu.R.id.answer_tool_bar;

public class AnswerActivity extends AbstractCustomActivity {

    private ViewPager viewPager;
    private List<Fragment> fragments;
    private String serverLocation;
    private RecommendViewBean viewBean;
    private Toolbar toolbar;
    private User user;
    private Gson gson;
    private AnswerFragmentAdapter adapter;
    private Button support;
    private CollapsingToolbarLayout toolbarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer);
        SystemUtils.setStatusBarFullTransparent(this);
        init();
    }

    private void init() {
        toolbarLayout = findViewById(R.id.aa_tool_bar_layout);
        toolbar = findViewById(answer_tool_bar);
        viewPager = findViewById(R.id.answer_view_pager);
        support = findViewById(R.id.cb_support);
        viewBean = (RecommendViewBean) getIntent().getSerializableExtra("viewBean");
        serverLocation = getResources().getString(R.string.server_location);
        fragments = new ArrayList<>();
        gson = new GsonBuilder().disableHtmlEscaping().create();
        this.user = DBUtils.queryUserHistory();
        recordBrowse();
        initToolBar();
        initViewPager();
    }

    private void recordBrowse() {
        if(user != null){
            String url = serverLocation + "/Question/Browse/" + viewBean.getQuestionId() + "/" + user.getUserId();
            RequestUtils.sendSimpleRequest(url);
        }
    }

    private void initToolBar() {
        toolbar.setTitle(viewBean.getTitle());
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v-> finish());
        ActionBar supportActionBar = getSupportActionBar();
        if(supportActionBar != null){
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public void initViewPager(){
        adapter = new AnswerFragmentAdapter(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }

            @Override
            public void onPageSelected(int position) {
                if(position == (fragments.size()-1)){
                    getNextAnswer();
                }
                updateUI(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });
        getInitData();
    }

    private void getInitData() {
        String url = "";
        if(user == null){
            url = serverLocation + "/Answer/Page/-1/" + viewBean.getQuestionId() + "/" + viewBean.getContentId();
        }else{
            url = serverLocation + "/Answer/Page/"+ user.getUserId() +"/" + viewBean.getQuestionId() + "/" + viewBean.getContentId();
        }
        RequestUtils.sendSimpleRequest(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(()-> Toast.makeText(AnswerActivity.this,"请求失败",Toast.LENGTH_SHORT).show());
            }

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                SimpleDto simpleDto = gson.fromJson(response.body().string(), SimpleDto.class);
                runOnUiThread(()->{
                    if(simpleDto.isSuccess()){
                        String json = gson.toJson(simpleDto.getObject());
                        List list = gson.fromJson(json,List.class);
                        list.forEach(o->{
                            String j = gson.toJson(o);
                            AnswerVo answerVo = gson.fromJson(j, AnswerVo.class);
                            fragments.add(new AnswerFragment(answerVo));
                        });
                        adapter.notifyDataSetChanged();
                        updateUI(0);
                    }else{
                        Toast.makeText(AnswerActivity.this,simpleDto.getMsg(),Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void updateUI(int index) {
        AnswerVo currentVo = ((AnswerFragment) fragments.get(index)).getAnswerVo();
        support.setText(currentVo.getAnswer().getSupportSum()+"赞同");
    }

    private void getNextAnswer(){
        Long answerId = ((AnswerFragment)fragments.get(viewPager.getCurrentItem())).getAnswerVo().getAnswer().getId();
        String url = user == null ? serverLocation + "/Answer/Next/-1/" + viewBean.getQuestionId() + "/" + answerId : serverLocation + "/Answer/Next/"+ user.getUserId() +"/" + viewBean.getQuestionId() + "/" + answerId;
        getAnswer(url,answerVo -> {
           fragments.add(new AnswerFragment(answerVo));
           adapter.notifyDataSetChanged();
        });
    }
    private void getAnswer(String url,Consumer<AnswerVo> callBack){
        RequestUtils.sendSimpleRequest(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(()->Toast.makeText(AnswerActivity.this,"请求失败",Toast.LENGTH_SHORT).show());
            }

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                SimpleDto simpleDto = gson.fromJson(response.body().string(), SimpleDto.class);
                runOnUiThread(()->{
                    if(simpleDto.isSuccess()){
                        String json = gson.toJson(simpleDto.getObject());
                        AnswerVo answerVo = gson.fromJson(json, AnswerVo.class);
                        callBack.accept(answerVo);
                    }else{
                        Toast.makeText(AnswerActivity.this,simpleDto.getMsg(),Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
