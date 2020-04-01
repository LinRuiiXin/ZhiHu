package com.sz.zhihu.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.sz.zhihu.R;
import com.sz.zhihu.vo.RecommendViewBean;

import java.util.List;
/*
* RecommendRecyclerView适配器
* 负责渲染CardViewBean，传入CardViewBean的集合，就可以按照类型(纯文本，带图，带视频)将数据按照合适的布局显示
* */
public class RecommendRecyclerViewAdapter extends RecyclerView.Adapter {
    private Context context;
    private List<RecommendViewBean> data;
    private final LayoutInflater inflater;

    public RecommendRecyclerViewAdapter(Context context,List<RecommendViewBean> data){
        this.context = context;
        this.data = data;
        inflater = LayoutInflater.from(context);
    }
    private final int VIEW_ALL_TEXT = 1;
    private final int VIEW_HAS_IMAGE = 2;
    private final int VIEW_HAS_VIDEO = 3;
    @Override
    public int getItemViewType(int position) {
        RecommendViewBean cardViewBean = data.get(position);
        int type = cardViewBean.getType();
        switch (type){
            case VIEW_HAS_VIDEO:
                return VIEW_HAS_VIDEO;
            case VIEW_HAS_IMAGE:
                return VIEW_HAS_IMAGE;
            case VIEW_ALL_TEXT:
                return VIEW_ALL_TEXT;
        }
        return -1;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        if(viewType == VIEW_ALL_TEXT){
            viewHolder = new AllTextViewHolder(inflater.inflate(R.layout.answer_all_text,parent,false));
        }else if(viewType == VIEW_HAS_IMAGE){
            viewHolder = new HasImageViewHolder(inflater.inflate(R.layout.anwser_has_image,parent,false));
        }else if(viewType == VIEW_HAS_VIDEO){
            viewHolder = new HasVideoViewHolder(inflater.inflate(R.layout.anwser_has_video,parent,false));
        }
        return viewHolder;
    }

    @SuppressLint("ResourceType")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        RecommendViewBean cardViewBean = data.get(position);
        int type = cardViewBean.getType();
        if(type == VIEW_ALL_TEXT){
            if(holder instanceof AllTextViewHolder){
                AllTextViewHolder viewHolder = (AllTextViewHolder) holder;
                viewHolder.title.setText(cardViewBean.getTitle());
                Glide.with(context).load(R.string.server_location).into(viewHolder.portrait);
                viewHolder.username.setText(cardViewBean.getUsername());
                viewHolder.introduction.setText(cardViewBean.getIntroduction());
                viewHolder.content.setText(cardViewBean.getContent());
                viewHolder.support.setText(cardViewBean.getSupportSum()+"赞同");
                viewHolder.comments.setText(cardViewBean.getCommentSum()+"评论");
            }
        }else if(type == VIEW_HAS_IMAGE){
            if(holder instanceof HasImageViewHolder){
                HasImageViewHolder viewHolder = (HasImageViewHolder) holder;
                viewHolder.title.setText(cardViewBean.getTitle());
                Glide.with(context).load(R.string.server_location).into(viewHolder.portrait);
                viewHolder.username.setText(cardViewBean.getUsername());
                viewHolder.introduction.setText(cardViewBean.getIntroduction());
                viewHolder.content.setText(cardViewBean.getContent());
                Glide.with(context).load(R.string.server_location).into(viewHolder.image);
                viewHolder.support.setText(cardViewBean.getSupportSum()+"赞同");
                viewHolder.comments.setText(cardViewBean.getCommentSum()+"评论");
            }
        }else if(type == VIEW_HAS_VIDEO){
            if(holder instanceof HasVideoViewHolder){
                HasVideoViewHolder viewHolder = (HasVideoViewHolder) holder;
                viewHolder.title.setText(cardViewBean.getTitle());
                Glide.with(context).load(R.string.server_location).into(viewHolder.portrait);
                viewHolder.username.setText(cardViewBean.getUsername());
                viewHolder.introduction.setText(cardViewBean.getIntroduction());
                viewHolder.content.setText(cardViewBean.getContent());
                viewHolder.support.setText(cardViewBean.getSupportSum()+"赞同");
                viewHolder.comments.setText(cardViewBean.getCommentSum()+"评论");
            }
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class AllTextViewHolder extends RecyclerView.ViewHolder{

        public final TextView title;
        public final ImageView portrait;
        public final TextView username;
        public final TextView introduction;
        public final TextView content;
        public final TextView support;
        public final TextView comments;

        public AllTextViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.answer_all_text_title);
            portrait = itemView.findViewById(R.id.answer_all_text_portrait);
            username = itemView.findViewById(R.id.answer_all_text_username);
            introduction = itemView.findViewById(R.id.answer_all_text_introduction);
            content = itemView.findViewById(R.id.answer_all_text_content);
            support = itemView.findViewById(R.id.answer_all_text_support);
            comments = itemView.findViewById(R.id.answer_all_text_comments);
        }
    }

    class HasImageViewHolder extends RecyclerView.ViewHolder{

        public final TextView title;
        public final ImageView portrait;
        public final TextView username;
        public final TextView introduction;
        public final TextView content;
        public final ImageView image;
        public final TextView support;
        public final TextView comments;

        public HasImageViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.answer_has_image_title);
            portrait = itemView.findViewById(R.id.answer_has_image_portrait);
            username = itemView.findViewById(R.id.answer_has_image_username);
            introduction = itemView.findViewById(R.id.answer_has_image_introduction);
            content = itemView.findViewById(R.id.answer_has_image_content);
            image = itemView.findViewById(R.id.answer_has_image_image);
            support = itemView.findViewById(R.id.answer_has_image_support);
            comments = itemView.findViewById(R.id.answer_has_image_comments);
        }
    }
    class HasVideoViewHolder extends RecyclerView.ViewHolder{
        public final TextView title;
        public final ImageView portrait;
        public final TextView username;
        public final TextView introduction;
        public final VideoView video;
        public final TextView content;
        public final TextView support;
        public final TextView comments;

        public HasVideoViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.answer_has_video_title);
            portrait = itemView.findViewById(R.id.answer_has_video_portrait);
            username = itemView.findViewById(R.id.answer_has_video_username);
            introduction = itemView.findViewById(R.id.answer_has_video_introduction);
            video = itemView.findViewById(R.id.answer_has_video_video);
            content = itemView.findViewById(R.id.answer_has_video_content);
            support = itemView.findViewById(R.id.answer_has_video_support);
            comments = itemView.findViewById(R.id.answer_has_video_comments);
        }
    }
}
