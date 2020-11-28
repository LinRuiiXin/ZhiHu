package com.sz.zhihu;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.rex.editor.view.RichEditorNew;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.sz.zhihu.adapter.QuestionInfoAnswerAdapter;
import com.sz.zhihu.dto.SimpleDto;
import com.sz.zhihu.po.Question;
import com.sz.zhihu.po.User;
import com.sz.zhihu.utils.DBUtils;
import com.sz.zhihu.utils.GsonUtils;
import com.sz.zhihu.utils.HtmlUtils;
import com.sz.zhihu.utils.RequestUtils;
import com.sz.zhihu.vo.QuestionInfoVo;
import com.sz.zhihu.vo.RecommendViewBean;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class QuestionInfoActivity extends AbstractCustomActivity {

    private long questionId;
    private String title;
    private Gson gson;
    private Toolbar toolbar;
    private TextView questionDescribe;
    private LinearLayout showAll;
    private TextView showAllIcon;
    private RelativeLayout writeAnswer;
    private RelativeLayout attentionQuestion;
    private TextView answerSum;
    private TextView subscribeSum;
    private TextView broseSum;
    private SmartRefreshLayout refreshLayout;
    private RecyclerView answerList;
    private List<RecommendViewBean> answerViewBean;
    private LinearLayoutManager linearLayoutManager;
    private AnswerListHolder answerListHolder;
    private QuestionInfoAnswerAdapter questionInfoAnswerAdapter;
    private String serverLocation;
    private QuestionInfoHolder questionInfoHolder;
    private boolean isShowAll = false;
    private Drawable icon_folding;
    private Drawable icon_unFold;
    private TextView showAllText;
    private User user;
    private Drawable has_attention_question;
    private Drawable has_not_attention_question;
    private TextView attentionQuestionIcon;
    private TextView attentionQuestionText;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_info);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void init() {
        prepare();
        bindComponent();
        bindListeners();
        initPage();
        initAnswerList();
    }

    /*
    * 初始化某些对象及获取初始数据
    * */
    private void prepare() {
        Intent intent = getIntent();
        serverLocation = getString(R.string.server_location);
        user = DBUtils.queryUserHistory();
        questionId = intent.getLongExtra("questionId",0);
        title = intent.getStringExtra("title");
        gson = GsonUtils.getGson();
        linearLayoutManager = new LinearLayoutManager(this);
        answerListHolder = new AnswerListHolder();
        questionInfoHolder = new QuestionInfoHolder();
        icon_folding = getDrawable(R.drawable.icon_show_all);
        icon_unFold = getDrawable(R.drawable.icon_hide_describe);
        has_attention_question = getDrawable(R.drawable.icon_has_attention);
        has_not_attention_question = getDrawable(R.drawable.icon_attention_question);
    }

    /*
    * 绑定组件
    * */
    @SuppressLint("ResourceAsColor")
    private void bindComponent() {
        toolbar = findViewById(R.id.aqi_tool_bar);
        questionDescribe = findViewById(R.id.aqi_question_describe);
        questionDescribe.setBackgroundColor(Color.parseColor("#f4f4f4"));
        showAll = findViewById(R.id.aqi_show_all);
        showAllIcon = findViewById(R.id.aqi_show_all_icon);
        showAllText = findViewById(R.id.aqi_show_all_text);
        writeAnswer = findViewById(R.id.aqi_write_answer);
        attentionQuestion = findViewById(R.id.aqi_attention_question);
        attentionQuestionIcon = findViewById(R.id.aqi_attention_question_icon);
        attentionQuestionText = findViewById(R.id.aqi_attention_question_text);
        answerList = findViewById(R.id.aqi_answer_list);
        answerSum = findViewById(R.id.aqi_answer_sum);
        subscribeSum = findViewById(R.id.aqi_subscribe_sum);
        broseSum = findViewById(R.id.aqi_browse_sum);
        refreshLayout = findViewById(R.id.aqi_refresh);
        refreshLayout.setEnableRefresh(false);
        initToolBar();
    }

    /*
     * 绑定监听
     * */
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void bindListeners() {
        refreshLayout.setOnLoadMoreListener(listener -> {
            if(!answerListHolder.isLoaded)
                listener.finishLoadMore(false);
            else if(!answerListHolder.hasMore) {
                listener.finishLoadMore();
                Toast.makeText(this,"没有更多了",Toast.LENGTH_SHORT).show();
            } else{
                getAnswerList(listener::finishLoadMore);
            }
        });
        writeAnswer.setOnClickListener(v -> {
            Intent intent = new Intent(this, EditAnswerActivity.class);
            intent.putExtra("question",new Question(questionId,title,0l,0,0l,0l,0l));
            startActivity(intent);
        });
        attentionQuestion.setOnClickListener(v -> {
            attentionQuestion.setClickable(false);
            if(DBUtils.checkIsLogged(this)){
                String api = questionInfoHolder.isAttention ? "UnAttention" : "Attention";
                String url = serverLocation + "/QuestionService/Question/" + api;
                Map<String,String> params = new HashMap<>(2);
                params.put("questionId",String.valueOf(questionId));
                params.put("userId",String.valueOf(user.getUserId()));
                RequestUtils.postWithParams(url, params, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        runOnUiThread(()->{
                            Toast.makeText(QuestionInfoActivity.this,"请求失败",Toast.LENGTH_SHORT).show();
                            attentionQuestion.setClickable(true);
                        });
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        SimpleDto simpleDto = GsonUtils.parseJsonToSimpleDto(response.body().string());
                        runOnUiThread(()->{
                            if(simpleDto.isSuccess()){
                                questionInfoHolder.isAttention = !(questionInfoHolder.isAttention);
                                changeAttentionButton();
                            }else{
                                Toast.makeText(QuestionInfoActivity.this,simpleDto.getMsg(),Toast.LENGTH_SHORT).show();
                            }
                            attentionQuestion.setClickable(true);
                        });
                    }
                });
            }
        });
        buttonControl(false);
    }


    /*
    * 初始化页面所需数据
    * */
    private void initPage() {
        Long uid = user == null ? -1 : user.getUserId();
        String url = serverLocation + "/QuestionService/Question/" + questionId + "/" + uid;
        RequestUtils.sendSimpleRequest(url, new Callback() {

            private QuestionInfoVo questionInfoVo;

            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(()->Toast.makeText(QuestionInfoActivity.this,"请求失败",Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                SimpleDto simpleDto = GsonUtils.parseJsonToSimpleDto(response.body().string());
                runOnUiThread(()->{
                    if(simpleDto.isSuccess()){
                        questionInfoVo = gson.fromJson(gson.toJson(simpleDto.getObject()), QuestionInfoVo.class);
                        changePageInitData(questionInfoVo);
                        buttonControl(true);
                    }else{
                        Toast.makeText(QuestionInfoActivity.this,simpleDto.getMsg(),Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }


    /*
    * 获取到页面数据后，更新页面UI
    * */
    private void changePageInitData(QuestionInfoVo questionInfoVo) {
        Question question = questionInfoVo.getQuestion();
        answerSum.setText(String.valueOf(question.getAnswerSum()));
        subscribeSum.setText(String.valueOf(question.getSubscribeSum()));
        broseSum.setText(String.valueOf(question.getBrowseSum()));
        if(questionInfoVo.getQuestion().getHasDescribe() != 0){
            questionInfoHolder.allDescribe = questionInfoVo.getDescribe();
            decisionIsNeedShowAllOption(questionInfoHolder);
            if(questionInfoHolder.isNeedShowAllOption){
                showAll.setVisibility(View.VISIBLE);
                questionDescribe.setText(questionInfoHolder.brief);
                showAll.setOnClickListener(showAllListener(questionInfoHolder));
                buttonControl(true);
            }else{
                questionDescribe.setText(questionInfoHolder.allDescribe);
            }
        }
        questionInfoHolder.isAttention = questionInfoVo.isAttention();
        changeAttentionButton();
    }



    /*
    * 初始化 "回答" 列表
    * */
    private void initAnswerList() {
        answerViewBean = new ArrayList<>();
        questionInfoAnswerAdapter = new QuestionInfoAnswerAdapter(this, answerViewBean);
        answerList.setAdapter(questionInfoAnswerAdapter);
        answerList.setLayoutManager(linearLayoutManager);
        refreshLayout.setEnableLoadMore(true);
        getAnswerList(refreshLayout::finishLoadMore);

    }

    private void getAnswerList(Consumer<Boolean> consumer) {
        String url = serverLocation + "/AnswerService/Answer/Order/" + questionId + "/" + answerListHolder.limit;
        RequestUtils.sendSimpleRequest(url, new Callback() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(()->{
                    Toast.makeText(QuestionInfoActivity.this,"请求失败",Toast.LENGTH_SHORT).show();
                    consumer.accept(false);
                });
            }

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                SimpleDto simpleDto = GsonUtils.parseJsonToSimpleDto(response.body().string());
                runOnUiThread(()->{
                    if(simpleDto.isSuccess()){
                        List list = gson.fromJson(gson.toJson(simpleDto.getObject()),List.class);
                        list.forEach(o -> {
                            RecommendViewBean recommendViewBean = gson.fromJson(gson.toJson(o), RecommendViewBean.class);
                            recommendViewBean.setTitle(title);
                            answerViewBean.add(recommendViewBean);
                        });
                        consumer.accept(true);
                        answerListHolder.isLoaded = true;
                        answerListHolder.limit += list.size();
                        if(list.size() < 10)
                            answerListHolder.hasMore = false;
                        questionInfoAnswerAdapter.notifyDataSetChanged();
                    }else{
                        Toast.makeText(QuestionInfoActivity.this,simpleDto.getMsg(),Toast.LENGTH_SHORT);
                        consumer.accept(false);
                    }
                });
            }
        });
    }


    private void initToolBar() {
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v-> finish());
        ActionBar supportActionBar = getSupportActionBar();
        if(supportActionBar != null){
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    /*
    *  决定是否需要 "显示更多" 选项按钮
    * */
    private void decisionIsNeedShowAllOption(QuestionInfoHolder questionInfoHolder) {
        String describeAllText = questionInfoHolder.allDescribe;
        if(describeAllText.length() > 100){
            questionInfoHolder.isNeedShowAllOption = true;
            questionInfoHolder.brief = describeAllText.substring(0,100);
        }
    }

    private View.OnClickListener showAllListener(QuestionInfoHolder questionInfoHolder) {
        return v -> {
            if(isShowAll){
                questionDescribe.setText(questionInfoHolder.brief);
                isShowAll = false;
                changeShowAllToUnFold();
            }else{
                questionDescribe.setText(questionInfoHolder.allDescribe);
                isShowAll = true;
                changeShowAllToFolding();
            }
        };
    }


    private void changeShowAllToFolding() {
        showAllIcon.setBackground(icon_unFold);
        showAllText.setText("折叠");
    }
    private void changeShowAllToUnFold() {
        showAllIcon.setBackground(icon_folding);
        showAllText.setText("显示全部");
    }


    private void changeAttentionButton() {
        if(questionInfoHolder.isAttention)
            changeAttentionButtonToAttention();
        else
            changeAttentionButtonToUnAttention();
    }

    private void changeAttentionButtonToAttention(){
        attentionQuestionIcon.setBackground(has_attention_question);
        attentionQuestionText.setText("已关注");
    }

    private void changeAttentionButtonToUnAttention(){
        attentionQuestionIcon.setBackground(has_not_attention_question);
        attentionQuestionText.setText("关注问题");
    }

    private void buttonControl(boolean b) {
        showAll.setClickable(b);
        writeAnswer.setClickable(b);
        attentionQuestion.setClickable(b);
    }

    class QuestionInfoHolder{
        boolean isNeedShowAllOption;
        boolean isAttention;
        String brief;
        String allDescribe;
    }

    class AnswerListHolder{
        public boolean isLoaded = false;
        public boolean hasMore = true;
        public int limit = 0;
    }
}