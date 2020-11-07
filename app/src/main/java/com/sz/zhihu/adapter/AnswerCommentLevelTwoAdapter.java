package com.sz.zhihu.adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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
import com.sz.zhihu.po.AnswerCommentLevelTwo;
import com.sz.zhihu.po.User;
import com.sz.zhihu.utils.DBUtils;
import com.sz.zhihu.utils.DateProcessor;
import com.sz.zhihu.utils.RequestUtils;
import com.sz.zhihu.vo.AnswerCommentLevelOneVo;
import com.sz.zhihu.vo.AnswerCommentLevelTwoVo;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

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
            if (answerCommentLevelTwoVo.isSupport()) {
                toSupport(answerCommentLevelTwoVo,viewHolder);
            }else{
                toUnSupport(answerCommentLevelTwoVo,viewHolder);
            }
            viewHolder.itemView.setOnClickListener(v->{
                consumer.accept(answerCommentLevelTwoVo);
            });
            viewHolder.support.setOnClickListener(v -> {
                viewHolder.support.setClickable(false);
                supportOrUnSupportComment(answerCommentLevelTwoVo,()->viewHolder.support.setClickable(true));
            });
        }
    }

    /*
     * 当点击评论点赞或在菜单栏点击 "点赞" 按钮时调用，即为该二级评论点赞
     * @vo 二级评论vo
     * @callBack 点赞或取消点赞调用后回调函数，你可以做一些如：让按钮恢复可点击或隐藏菜单栏之类的操作
     * */

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void supportOrUnSupportComment(AnswerCommentLevelTwoVo vo, Runnable callBack) {
        boolean support = vo.isSupport();
        String api = support ? "/UnSupport" : "/Support";
        String url = serverLocation + "/CommentService/Comment/LevelTwo" + api;
        Map<String,String> params = new HashMap(2);
        params.put("replyId",String.valueOf(vo.getAnswerCommentLevelTwo().getId()));
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
                        Log.i("Main",simpleDto.toString());
                        Toast.makeText(context,simpleDto.getMsg(),Toast.LENGTH_SHORT).show();
                    }
                    callBack.run();
                });
            }
        });
    }

    private void toSupport(AnswerCommentLevelTwoVo vo, AnswerCommentLevelTwoAdapter.ViewHolder viewHolder) {
        viewHolder.supportSum.setText(String.valueOf(vo.getAnswerCommentLevelTwo().getSupportSum()));
        viewHolder.supportSum.setTextColor(context.getResources().getColor(R.color.ZhiHuBlue));
        viewHolder.supportBtn.setBackground(context.getDrawable(R.drawable.icon_support_blue));
    }

    private void toUnSupport(AnswerCommentLevelTwoVo vo, AnswerCommentLevelTwoAdapter.ViewHolder viewHolder) {
        viewHolder.supportSum.setText(String.valueOf(vo.getAnswerCommentLevelTwo().getSupportSum()));
        viewHolder.supportSum.setTextColor(context.getResources().getColor(R.color.TextDefaultColor));
        viewHolder.supportBtn.setBackground(context.getDrawable(R.drawable.icon_support_gray));
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
        private final LinearLayout support;

        public ViewHolder(@NonNull View view) {
            super(view);
            portrait = view.findViewById(R.id.acti_portrait);
            replyTo = view.findViewById(R.id.acti_reply_to);
            replyUser = view.findViewById(R.id.acti_reply_user);
            content = view.findViewById(R.id.acti_content);
            date = view.findViewById(R.id.acti_date);
            supportSum = view.findViewById(R.id.acti_support_sum);
            supportBtn = view.findViewById(R.id.acti_support_btn);
            support = view.findViewById(R.id.acti_support);

        }
    }
}
