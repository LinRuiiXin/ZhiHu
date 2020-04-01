package com.sz.zhihu.adapter;

import android.app.Activity;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;
import com.sz.zhihu.R;
import com.sz.zhihu.po.Question;
import com.sz.zhihu.po.User;
import com.sz.zhihu.vo.RecommendQuestionViewBean;


import java.util.List;

public class RecommendQuestionCardAdapter extends RecyclerView.Adapter {
    private Activity activity;
    private List<RecommendQuestionViewBean> data;
    private final LayoutInflater inflater;
    private final String serverLocation;

    public RecommendQuestionCardAdapter(Activity activity, List<RecommendQuestionViewBean> data){
        this.activity = activity;
        this.data = data;
        inflater = LayoutInflater.from(activity);
        serverLocation = activity.getResources().getString(R.string.server_location);
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
