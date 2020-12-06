package com.sz.zhihu.adapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;
import com.sz.zhihu.HomePageActivity;
import com.sz.zhihu.R;
import com.sz.zhihu.dto.SimpleDto;
import com.sz.zhihu.po.User;
import com.sz.zhihu.utils.DBUtils;
import com.sz.zhihu.utils.GsonUtils;
import com.sz.zhihu.utils.RequestUtils;
import com.sz.zhihu.vo.RecommendQuestionViewBean;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class RecommendQuestionAdapter extends BaseAdapter {

    private final Activity activity;
    private final List<RecommendQuestionViewBean> data;
    private final LayoutInflater inflater;
    private Consumer<RecommendQuestionViewBean> consumer;
    private final User user;
    private final String serverLocation;

    public RecommendQuestionAdapter(Activity activity, List<RecommendQuestionViewBean> data,Consumer<RecommendQuestionViewBean> consumer){
        this.activity = activity;
        this.data = data;
        inflater = LayoutInflater.from(activity);
        this.consumer = consumer;
        user = DBUtils.queryUserHistory();
        serverLocation = activity.getString(R.string.server_location);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View card = inflater.inflate(R.layout.recommend_question_card, null, false);
        RecommendQuestionViewBean viewBean = data.get(i);
        if(viewBean.getQuestion().getQuestionerId() == -1){
            loadingQuestionCar(card,viewBean);
        }else{
            loadQuestionCard(card,viewBean);
        }
        return card;
    }

    private void loadingQuestionCar(View card, RecommendQuestionViewBean viewBean) {
        RoundedImageView portrait = card.findViewById(R.id.rqc_portrait);
        Button subscribeQuestion = card.findViewById(R.id.rqc_care);
        portrait.setClickable(false);
        subscribeQuestion.setClickable(false);
        TextView title = card.findViewById(R.id.rqc_question);
        title.setText("正在加载中...");
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void loadQuestionCard(View card, RecommendQuestionViewBean viewBean) {
        TextView subscribeSum = card.findViewById(R.id.rqc_subscribeSum);
        TextView question = card.findViewById(R.id.rqc_question);
        TextView answerSum = card.findViewById(R.id.rqc_answer_sum);
        RoundedImageView portrait = card.findViewById(R.id.rqc_portrait);
        TextView userName = card.findViewById(R.id.rqc_username);
        Button subscribeQuestion = card.findViewById(R.id.rqc_care);
        RelativeLayout cardWrapper = card.findViewById(R.id.rqc_card_wrapper);
        subscribeSum.setText(viewBean.getQuestion().getSubscribeSum()+"订阅");
        question.setText(viewBean.getQuestion().getName());
        answerSum.setText(String.valueOf(viewBean.getQuestion().getAnswerSum()));
        User targetUser = viewBean.getUser();
        String portraitUrl = activity.getString(R.string.server_location) + "/res/User/" + targetUser.getId() + "/" + targetUser.getPortraitFileName();
        Glide.with(activity).load(portraitUrl).into(portrait);
        userName.setText(user.getUserName());
        subscribeQuestion.setVisibility(viewBean.isSubscribe() ? View.GONE : View.VISIBLE);
        subscribeQuestion.setOnClickListener(v -> {
            subscribeQuestion(viewBean.getQuestion().getId(),isSuccess -> {subscribeQuestion.setVisibility(isSuccess ? View.GONE : View.VISIBLE); });
        });
        portrait.setOnClickListener(v -> {
            Intent intent = new Intent(activity, HomePageActivity.class);
            intent.putExtra("userId",targetUser.getId());
            activity.startActivity(intent);
        });
        cardWrapper.setOnClickListener(v -> {
            consumer.accept(viewBean);
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void subscribeQuestion(Long questionId, Consumer<Boolean> consumer){
        String url = serverLocation + "/QuestionService/Question/Attention";
        Map<String,String> params = new HashMap<>(2);
        params.put("questionId",String.valueOf(questionId));
        params.put("userId",String.valueOf(user.getUserId()));
        RequestUtils.postWithParams(url, params, new Callback() {
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
                    if(!simpleDto.isSuccess())
                        Toast.makeText(activity,simpleDto.getMsg(),Toast.LENGTH_SHORT).show();
                    consumer.accept(simpleDto.isSuccess());
                });
            }
        });
    }
}
