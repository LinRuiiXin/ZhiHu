package com.sz.zhihu.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.makeramen.roundedimageview.RoundedImageView;
import com.sz.zhihu.R;
import com.sz.zhihu.dto.SimpleDto;
import com.sz.zhihu.po.AnswerCommentLevelOne;
import com.sz.zhihu.po.User;
import com.sz.zhihu.utils.DBUtils;
import com.sz.zhihu.utils.DateProcessor;
import com.sz.zhihu.utils.RequestUtils;
import com.sz.zhihu.vo.AnswerCommentLevelOneVo;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AnswerCommentLevelOneAdapter extends RecyclerView.Adapter {

    private final LayoutInflater inflater;
    private List<AnswerCommentLevelOneVo> data;
    private Context context;
    private final String serverLocation;
    private Consumer<AnswerCommentLevelOneVo> consumer;
    private Consumer<AnswerCommentLevelOneVo> itemCallBack;
    private final DateProcessor dateProcessor;

    public AnswerCommentLevelOneAdapter(Context context, List<AnswerCommentLevelOneVo> data, Consumer<AnswerCommentLevelOneVo> consumer, Consumer<AnswerCommentLevelOneVo> itemCallBack) {
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.data = data;
        this.consumer = consumer;
        dateProcessor = DateProcessor.getInstance();
        serverLocation = context.getString(R.string.server_location);
        this.itemCallBack = itemCallBack;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.answer_comment_level_one_item, parent, false));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolder) {
            ViewHolder viewHolder = (ViewHolder) holder;
            AnswerCommentLevelOneVo vo = data.get(position);
            User user = vo.getUser();
            AnswerCommentLevelOne commentLevelOne = vo.getCommentLevelOne();
            String portraitUrl = serverLocation + "/res/User/" + user.getId() + "/" + user.getPortraitFileName();
            Glide.with(context).load(portraitUrl).into(viewHolder.portrait);
            viewHolder.userName.setText(user.getUserName());
            viewHolder.content.setText(commentLevelOne.getContent());
            viewHolder.date.setText(dateProcessor.processorDate(commentLevelOne.getTime()));
            viewHolder.point.setVisibility(commentLevelOne.getHasReply() == 0 ? View.GONE : View.VISIBLE);
            viewHolder.viewReply.setVisibility(commentLevelOne.getHasReply() == 0 ? View.GONE : View.VISIBLE);
            viewHolder.supportSum.setText(String.valueOf(commentLevelOne.getSupportSum()));
            if (vo.isSupport()) {
                toSupport(vo,viewHolder);
            }else{
                toUnSupport(vo,viewHolder);
            }
            viewHolder.viewReply.setOnClickListener(v -> {
                consumer.accept(vo);
            });
            viewHolder.itemView.setOnClickListener(v -> {
                itemCallBack.accept(vo);
            });
            viewHolder.support.setOnClickListener(v -> {
                viewHolder.support.setClickable(false);
                supportOrUnSupportComment(vo,()-> viewHolder.support.setClickable(true));
            });
        }
    }

    /*
    * 当点击评论点赞或在菜单栏点击 "点赞" 按钮时调用，即为该一级评论点赞
    * @vo 一级评论vo
    * @callBack 点赞或取消点赞调用后回调函数，你可以做一些如：让按钮恢复可点击或隐藏菜单栏之类的操作
    * */

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void supportOrUnSupportComment(AnswerCommentLevelOneVo vo,Runnable callBack) {
        boolean support = vo.isSupport();
        String api = support ? "/UnSupport" : "/Support";
        String url = serverLocation + "/CommentService/Comment/LevelOne" + api;
        Map<String,String> params = new HashMap(2);
        params.put("commentId",String.valueOf(vo.getCommentLevelOne().getId()));
        params.put("userId",String.valueOf(DBUtils.queryUserHistory().getUserId()));
        RequestUtils.postWithParams(url, params, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                ((Activity)context).runOnUiThread(()-> {
                    Toast.makeText(context, "请求失败", Toast.LENGTH_SHORT).show();
                    callBack.run();
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                SimpleDto simpleDto = new Gson().fromJson(response.body().string(), SimpleDto.class);
                ((Activity)context).runOnUiThread(()->{
                    if(simpleDto.isSuccess()){
                        if(support)
                            vo.unSupport();
                        else
                            vo.support();
                        notifyDataSetChanged();
                    }else{
                        Toast.makeText(context,simpleDto.getMsg(),Toast.LENGTH_SHORT).show();
                    }
                    callBack.run();
                });
            }
        });
    }


    private void toSupport(AnswerCommentLevelOneVo vo, ViewHolder viewHolder) {
        viewHolder.supportSum.setText(String.valueOf(vo.getCommentLevelOne().getSupportSum()));
        viewHolder.supportSum.setTextColor(context.getResources().getColor(R.color.ZhiHuBlue));
        viewHolder.supportIcon.setBackground(context.getDrawable(R.drawable.icon_support_blue));
    }

    private void toUnSupport(AnswerCommentLevelOneVo vo, ViewHolder viewHolder) {
        viewHolder.supportSum.setText(String.valueOf(vo.getCommentLevelOne().getSupportSum()));
        viewHolder.supportSum.setTextColor(context.getResources().getColor(R.color.TextDefaultColor));
        viewHolder.supportIcon.setBackground(context.getDrawable(R.drawable.icon_support_gray));
    }




    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final RoundedImageView portrait;
        private final TextView userName;
        private final TextView content;
        private final TextView date;
        private final TextView point;
        private final TextView viewReply;
        private final TextView supportSum;
        private final ImageView supportIcon;
        private final LinearLayout support;

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
            support = itemView.findViewById(R.id.acoi_support);
        }
    }
}
