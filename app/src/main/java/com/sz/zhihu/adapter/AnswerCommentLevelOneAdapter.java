package com.sz.zhihu.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;
import com.sz.zhihu.R;
import com.sz.zhihu.po.AnswerCommentLevelOne;
import com.sz.zhihu.po.User;
import com.sz.zhihu.vo.AnswerCommentLevelOneVo;
import java.util.Date;
import java.util.List;

public class AnswerCommentLevelOneAdapter extends RecyclerView.Adapter {

    private final LayoutInflater inflater;
    private List<AnswerCommentLevelOneVo> data;
    private Context context;
    private final String serverLocation;

    public AnswerCommentLevelOneAdapter(Context context, List<AnswerCommentLevelOneVo> data){
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.data = data;
        serverLocation = context.getString(R.string.server_location);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.answer_comment_level_one_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof ViewHolder){
            ViewHolder viewHolder = (ViewHolder) holder;
            AnswerCommentLevelOneVo vo = data.get(position);
            User user = vo.getUser();
            AnswerCommentLevelOne commentLevelOne = vo.getCommentLevelOne();
            String portraitUrl = serverLocation + "/res/User/" + user.getId() + "/" + user.getPortraitFileName();
            Glide.with(context).load(portraitUrl).into(viewHolder.portrait);
            viewHolder.userName.setText(user.getUserName());
            viewHolder.content.setText(commentLevelOne.getContent());
            viewHolder.date.setText(transDate(commentLevelOne.getTime()));
            viewHolder.point.setVisibility(commentLevelOne.getHasReply() == 0 ? View.GONE : View.VISIBLE);
            viewHolder.viewReply.setVisibility(commentLevelOne.getHasReply() == 0 ? View.GONE : View.VISIBLE);
            viewHolder.supportSum.setText(String.valueOf(commentLevelOne.getSupportSum()));
            if(vo.isSupport()){
                viewHolder.supportSum.setTextColor(Color.parseColor("3f87f6"));
                viewHolder.supportIcon.setImageResource(R.drawable.icon_support_blue);
            }
        }
    }

    private String transDate(Date time) {
        return "0";
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        private final RoundedImageView portrait;
        private final TextView userName;
        private final TextView content;
        private final TextView date;
        private final TextView point;
        private final TextView viewReply;
        private final TextView supportSum;
        private final ImageView supportIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            portrait = itemView.findViewById(R.id.acoi_portrait);
            userName = itemView.findViewById(R.id.acoi_username);
            content = itemView.findViewById(R.id.acoi_content);
            date = itemView.findViewById(R.id.acoi_date);
            point = itemView.findViewById(R.id.acoi_point);
            viewReply = itemView.findViewById(R.id.acoi_view_reply);
            supportSum = itemView.findViewById(R.id.acoi_support_sum);
            supportIcon = itemView.findViewById(R.id.acoi_support_icon);
        }
    }
}
