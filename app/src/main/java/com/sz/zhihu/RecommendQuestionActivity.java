package com.sz.zhihu;

import androidx.annotation.RequiresApi;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bakerj.infinitecards.InfiniteCardView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sz.zhihu.adapter.RecommendQuestionAdapter;
import com.sz.zhihu.dto.SimpleDto;
import com.sz.zhihu.po.User;
import com.sz.zhihu.utils.GsonUtils;
import com.sz.zhihu.utils.RequestUtils;
import com.sz.zhihu.utils.SystemUtils;
import com.sz.zhihu.utils.DBUtils;
import com.sz.zhihu.vo.RecommendQuestionViewBean;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class RecommendQuestionActivity extends AbstractCustomActivity {

    private TextView close;
    private InfiniteCardView card;
//    private TextView writeAnswer;
    private List<RecommendQuestionViewBean> beans;
    private String serverLocation;
    private Gson gson;
    private User user;
    private RecommendQuestionAdapter adapter;
    private TextView refresh;
    private TextView next;

    public RecommendQuestionActivity() {
        beans = new ArrayList<>();
        gson = GsonUtils.getGson();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend_question);
        SystemUtils.setStatusBarFullTransparent(this);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void init() {
        serverLocation = getResources().getString(R.string.server_location);
        close = findViewById(R.id.arq_close);
        card = findViewById(R.id.arq_card);
        refresh = findViewById(R.id.arq_refresh);
//        writeAnswer = findViewById(R.id.arq_write_answer);
        next = findViewById(R.id.arq_next);
        user = DBUtils.queryUserHistory();
        adapter = new RecommendQuestionAdapter(this, beans,this::onCardSelect);
        card.setAdapter(adapter);
        beans.add(RecommendQuestionViewBean.getLoadingViewBean());
        getRecommendQuestion(this::refreshRecommend);
        setListeners();
    }

    private void setListeners() {
        close.setOnClickListener(v -> finish());
        refresh.setOnClickListener(v -> { getRecommendQuestion(this::refreshRecommend); });
        next.setOnClickListener(v->{
            card.bringCardToFront(beans.size()-1);
        });
    }

    private void onCardSelect(RecommendQuestionViewBean recommendQuestionViewBean) {
        Intent intent = new Intent(this, EditAnswerActivity.class);
        intent.putExtra("question",recommendQuestionViewBean.getQuestion());
        startActivity(intent);
    }

    public void refreshRecommend(List<RecommendQuestionViewBean> questionViewBeans){
        beans.clear();
        adapter.notifyDataSetChanged();
        beans.addAll(questionViewBeans);
        adapter.notifyDataSetChanged();
    }

    private void getRecommendQuestion(Consumer<List<RecommendQuestionViewBean>> consumer) {
        if (user != null) {
            String url = serverLocation + "/QuestionService/Question/Random/" + user.getUserId();
            RequestUtils.sendSimpleRequest(url, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(() -> Toast.makeText(RecommendQuestionActivity.this, "请求失败", Toast.LENGTH_SHORT).show());
                }

                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    SimpleDto simpleDto = GsonUtils.parseJsonToSimpleDto(response.body().string());
                    runOnUiThread(() -> {
                        if (simpleDto.isSuccess()) {
                            List<RecommendQuestionViewBean> res = gson.fromJson(gson.toJson(simpleDto.getObject()), new TypeToken<List<RecommendQuestionViewBean>>() {
                            }.getType());
                            consumer.accept(res);
                        } else {
                            Toast.makeText(RecommendQuestionActivity.this, simpleDto.getMsg(), Toast.LENGTH_SHORT);
                        }
                    });
                }
            });
        }
    }
}
