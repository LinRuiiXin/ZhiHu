package com.sz.zhihu.adapter;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.makeramen.roundedimageview.RoundedImageView;
import com.sz.zhihu.R;
import com.sz.zhihu.dto.SimpleDto;
import com.sz.zhihu.po.Question;
import com.sz.zhihu.po.User;
import com.sz.zhihu.utils.RequestUtils;
import com.sz.zhihu.utils.UserUtils;
import com.sz.zhihu.vo.RecommendQuestionViewBean;


import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class RecommendQuestionCardAdapter extends RecyclerView.Adapter {
    private Activity activity;
    private List<RecommendQuestionViewBean> data;
    private final LayoutInflater inflater;
    private final String serverLocation;
    private final Drawable enable;
    private final Drawable disable;
    private final Resources resources;
    private final int blue;
    private final int colorDisable;
    private final Gson gson;
    private final User localUser;

    public RecommendQuestionCardAdapter(Activity activity, List<RecommendQuestionViewBean> data){
        this.activity = activity;
        this.data = data;
        inflater = LayoutInflater.from(activity);
        serverLocation = activity.getResources().getString(R.string.server_location);
        resources = activity.getResources();
        enable = resources.getDrawable(R.drawable.rqc_button_enable_shape);
        disable = resources.getDrawable(R.drawable.rqc_button_disable_shape);
        blue = resources.getColor(R.color.ZhiHuBlue);
        colorDisable = resources.getColor(R.color.textColorDisable);
        gson = new Gson();
        localUser = UserUtils.queryUserHistory();
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CardViewHolder(inflater.inflate(R.layout.recommend_question_card,parent,false));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        CardViewHolder cardViewHolder = (CardViewHolder) holder;
        RecommendQuestionViewBean cardBean = data.get(position);
        Question question = cardBean.getQuestion();
        User user = cardBean.getUser();
        cardViewHolder.subscribeSum.setText("有"+question.getSubscribeSum()+"人关注了这个问题");
        cardViewHolder.question.setText(question.getName());
        cardViewHolder.answerSum.setText(question.getAnswerSum()+"个回答");
        String url = serverLocation + "/res/User/"+user.getId()+"/"+user.getPortraitFileName();
        Glide.with(activity).load(url).into(cardViewHolder.portrait);
        cardViewHolder.username.setText(user.getUserName());
        cardViewHolder.care.setText(cardBean.isSubscribe()?"已关注":"+ 关注问题");
        cardViewHolder.care.setBackground(cardBean.isSubscribe()?disable:enable);
        cardViewHolder.care.setTextColor(cardBean.isSubscribe()?colorDisable:blue);
        cardViewHolder.care.setOnClickListener(v->{
            if(cardBean.isSubscribe()){
                String disSubUrl = serverLocation + "/Attention/Question";
                Map<String,String> map = new HashMap<>();
                map.put("questionId",String.valueOf(question.getId()));
                map.put("userId",String.valueOf(localUser.getUserId()));
                RequestUtils.deleteWithParams(disSubUrl, map, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        activity.runOnUiThread(()-> Toast.makeText(activity,"请求失败",Toast.LENGTH_SHORT).show());
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        SimpleDto simpleDto = gson.fromJson(response.body().string(),SimpleDto.class);
                        activity.runOnUiThread(()->{
                            if(simpleDto.isSuccess()){
                                cardBean.setSubscribe(false);
                                cardViewHolder.care.setTextColor(blue);
                                cardViewHolder.care.setText("关注问题");
                                cardViewHolder.care.setBackground(enable);
                            }else{
                                Toast.makeText(activity,simpleDto.getMsg(),Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }else{
                String subUrl = serverLocation + "/Attention/Question";
                Map<String,String> map = new HashMap<>();
                map.put("questionId",String.valueOf(question.getId()));
                map.put("userId",String.valueOf(localUser.getUserId()));
                RequestUtils.postWithParams(subUrl, map, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        activity.runOnUiThread(()->Toast.makeText(activity,"请求失败",Toast.LENGTH_SHORT).show());
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        SimpleDto simpleDto = gson.fromJson(response.body().string(),SimpleDto.class);
                        activity.runOnUiThread(()->{
                            if(simpleDto.isSuccess()){
                                cardBean.setSubscribe(true);
                                cardViewHolder.care.setTextColor(colorDisable);
                                cardViewHolder.care.setBackground(disable);
                                cardViewHolder.care.setText("已关注");
                            }else{
                                Toast.makeText(activity,simpleDto.getMsg(),Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }
    class CardViewHolder extends RecyclerView.ViewHolder{

        private final TextView subscribeSum;
        private final TextView question;
        private final TextView answerSum;
        private final RoundedImageView portrait;
        private final TextView username;
        private final Button care;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            subscribeSum = itemView.findViewById(R.id.rqc_subscribeSum);
            question = itemView.findViewById(R.id.rqc_question);
            answerSum = itemView.findViewById(R.id.rqc_answer_sum);
            portrait = itemView.findViewById(R.id.rqc_portrait);
            username = itemView.findViewById(R.id.rqc_username);
            care = itemView.findViewById(R.id.rqc_care);
        }
    }
}
