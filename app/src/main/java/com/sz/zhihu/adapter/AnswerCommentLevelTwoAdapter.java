package com.sz.zhihu.adapter;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;
import com.sz.zhihu.R;
import com.sz.zhihu.po.AnswerCommentLevelTwo;
import com.sz.zhihu.po.User;
import com.sz.zhihu.utils.DateProcessor;
import com.sz.zhihu.vo.AnswerCommentLevelTwoVo;

import org.w3c.dom.Text;

import java.util.List;
import java.util.function.Consumer;

public class AnswerCommentLevelTwoAdapter extends RecyclerView.Adapter {

    private List<AnswerCommentLevelTwoVo> data;
    private Context context;
    private final LayoutInflater inflater;
    private final String serverLocation;
    private final Consumer<AnswerCommentLevelTwoVo> consumer;

    public AnswerCommentLevelTwoAdapter(Context context, List<AnswerCommentLevelTwoVo> data, Consumer<AnswerCommentLevelTwoVo> consumer){
        this.context = context;
        this.data = data;
        inflater = LayoutInflater.from(context);
        serverLocation = context.getResources().getString(R.string.server_location);
        this.consumer = consumer;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.answer_comment_level_two_item,parent,false));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof ViewHolder){
            ViewHolder viewHolder = (ViewHolder) holder;
            AnswerCommentLevelTwoVo answerCommentLevelTwoVo = data.get(position);
            User replyUser = answerCommentLevelTwoVo.getReplyUser();
            User userReplyTo = answerCommentLevelTwoVo.getUserReplyTo();
            AnswerCommentLevelTwo answerCommentLevelTwo = answerCommentLevelTwoVo.getAnswerCommentLevelTwo();
            String url = serverLocation + "/res/User/" + replyUser.getId() + "/" + replyUser.getPortraitFileName();
            Glide.with(context).load(url).into(viewHolder.portrait);
            viewHolder.replyUser.setText(replyUser.getUserName());
            viewHolder.replyTo.setText(userReplyTo.getUserName());
            viewHolder.content.setText(answerCommentLevelTwo.getContent());
            viewHolder.supportSum.setText(answerCommentLevelTwo.getSupportSum()+"");
            viewHolder.itemView.setOnClickListener(v->{
                consumer.accept(answerCommentLevelTwoVo);
            });
        }
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        private final RoundedImageView portrait;
        private final TextView replyTo;
        private final TextView replyUser;
        private final TextView content;
        private final TextView date;
        private final TextView supportSum;
        private final TextView supportBtn;

        public ViewHolder(@NonNull View view) {
            super(view);
            portrait = view.findViewById(R.id.acti_portrait);
            replyTo = view.findViewById(R.id.acti_reply_to);
            replyUser = view.findViewById(R.id.acti_reply_user);
            content = view.findViewById(R.id.acti_content);
            date = view.findViewById(R.id.acti_date);
            supportSum = view.findViewById(R.id.acti_support_sun);
            supportBtn = view.findViewById(R.id.acti_support_btn);

        }
    }
}
