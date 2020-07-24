package com.sz.zhihu.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.load.resource.bitmap.TransformationUtils;
import com.bumptech.glide.request.RequestOptions;
import com.sz.zhihu.AnswerActivity;
import com.sz.zhihu.ArticleActivity;
import com.sz.zhihu.R;
import com.sz.zhihu.SplashActivity;
import com.sz.zhihu.vo.RecommendViewBean;

import java.security.MessageDigest;
import java.util.List;

import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;


/*
 * RecommendRecyclerView适配器
 * 负责渲染CardViewBean，传入CardViewBean的集合，就可以按照类型(纯文本，带图，带视频)将数据按照合适的布局显示
 * */
public class RecommendRecyclerViewAdapter extends RecyclerView.Adapter {
    private Activity context;
    private List<RecommendViewBean> data;
    private final LayoutInflater inflater;
    private final String serverLocation;

    public RecommendRecyclerViewAdapter(Context context, List<RecommendViewBean> data) {
        if (context instanceof Activity) {
            this.context = (Activity) context;
        }
        this.data = data;
        inflater = LayoutInflater.from(context);
        serverLocation = context.getResources().getString(R.string.server_location);
    }

    private final int VIEW_ALL_TEXT = 1;
    private final int VIEW_HAS_IMAGE = 2;
    private final int VIEW_HAS_VIDEO = 3;

    @Override
    public int getItemViewType(int position) {
        RecommendViewBean cardViewBean = data.get(position);
        int type = cardViewBean.getType();
        switch (type) {
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
        if (viewType == VIEW_ALL_TEXT) {
            viewHolder = new AllTextViewHolder(inflater.inflate(R.layout.answer_all_text, parent, false));
        } else if (viewType == VIEW_HAS_IMAGE) {
            viewHolder = new HasImageViewHolder(inflater.inflate(R.layout.anwser_has_image, parent, false));
        } else if (viewType == VIEW_HAS_VIDEO) {
            viewHolder = new HasVideoViewHolder(inflater.inflate(R.layout.anwser_has_video, parent, false));
        }
        return viewHolder;
    }

    @SuppressLint("ResourceType")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        RecommendViewBean cardViewBean = data.get(position);
        int type = cardViewBean.getType();
        if (type == VIEW_ALL_TEXT) {
            if (holder instanceof AllTextViewHolder) {
                AllTextViewHolder viewHolder = (AllTextViewHolder) holder;
                viewHolder.title.setText(cardViewBean.getTitle());
                Glide.with(context).load(serverLocation + "/res/User/" + cardViewBean.getUserId() + "/" + cardViewBean.getPortraitFileName()).into(viewHolder.portrait);
                viewHolder.username.setText(cardViewBean.getUsername());
                viewHolder.introduction.setText(cardViewBean.getIntroduction());
                viewHolder.content.setText(cardViewBean.getContent());
                viewHolder.support.setText(cardViewBean.getSupportSum() + "赞同");
                viewHolder.comments.setText(cardViewBean.getCommentSum() + "评论");
                viewHolder.setOnClickListener(cardViewBean);
            }
        } else if (type == VIEW_HAS_IMAGE) {
            if (holder instanceof HasImageViewHolder) {
                HasImageViewHolder viewHolder = (HasImageViewHolder) holder;
                viewHolder.title.setText(cardViewBean.getTitle());
                Glide.with(context).load(serverLocation + "/res/User/" + cardViewBean.getUserId() + "/" + cardViewBean.getPortraitFileName()).into(viewHolder.portrait);
                viewHolder.username.setText(cardViewBean.getUsername());
                viewHolder.introduction.setText(cardViewBean.getIntroduction());
                viewHolder.content.setText(cardViewBean.getContent());
                Glide.with(context).load(serverLocation + "/res/Image/" + cardViewBean.getThumbnail()).apply(getOptions()).into(viewHolder.image);
                viewHolder.support.setText(cardViewBean.getSupportSum() + "赞同");
                viewHolder.setOnClickListener(cardViewBean);
                viewHolder.comments.setText(cardViewBean.getCommentSum() + "评论");
            }
        } else if (type == VIEW_HAS_VIDEO) {
            if (holder instanceof HasVideoViewHolder) {
                HasVideoViewHolder viewHolder = (HasVideoViewHolder) holder;
                viewHolder.title.setText(cardViewBean.getTitle());
                Glide.with(context).load(serverLocation + "/res/User/" + cardViewBean.getUserId() + "/" + cardViewBean.getPortraitFileName()).into(viewHolder.portrait);
                viewHolder.username.setText(cardViewBean.getUsername());
                viewHolder.video.setUp(serverLocation + "/res/Video/" + cardViewBean.getThumbnail(), JCVideoPlayerStandard.SCREEN_LAYOUT_NORMAL);
//                viewHolder.video.thumbImageView.setImage("http://p.qpic.cn/videoyun/0/2449_43b6f696980311e59ed467f22794e792_1/640");
                viewHolder.introduction.setText(cardViewBean.getIntroduction());
                viewHolder.content.setText(cardViewBean.getContent());
                viewHolder.support.setText(cardViewBean.getSupportSum() + "赞同");
                viewHolder.comments.setText(cardViewBean.getCommentSum() + "评论");
                viewHolder.setOnClickListener(cardViewBean);
            }
        }
    }

    private RequestOptions getOptions() {
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.mipmap.ic_launcher_round) //预加载图片
                .error(R.drawable.ic_launcher_foreground) //加载失败图片
                .priority(Priority.HIGH) //优先级
                .diskCacheStrategy(DiskCacheStrategy.NONE) //缓存
                .transform(new GlideRoundTransform(5)); //圆角
        return options;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {
        private View view;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            this.view = itemView;
        }

        public void setOnClickListener(RecommendViewBean viewBean) {
            this.view.setOnClickListener(v -> {
                Intent intent = null;
                if (viewBean.getContentType() == 1) {
                    intent = new Intent(context, AnswerActivity.class);
                } else {
                    intent = new Intent(context, ArticleActivity.class);
                }
                intent.putExtra("viewBean", viewBean);
                context.startActivity(intent);
                viewBean.save();
            });
        }
    }

    class AllTextViewHolder extends CustomViewHolder {

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

    class HasImageViewHolder extends CustomViewHolder {

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

    class HasVideoViewHolder extends CustomViewHolder {
        public final TextView title;
        public final ImageView portrait;
        public final TextView username;
        public final TextView introduction;
        public final JCVideoPlayerStandard video;
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
            video.backButton.setVisibility(View.GONE);
            video.tinyBackImageView.setVisibility(View.GONE);
            content = itemView.findViewById(R.id.answer_has_video_content);
            support = itemView.findViewById(R.id.answer_has_video_support);
            comments = itemView.findViewById(R.id.answer_has_video_comments);
        }
    }

    public static class GlideRoundTransform extends BitmapTransformation {
        private static float radius = 0f;

        public GlideRoundTransform() {
            this(4);
        }

        public GlideRoundTransform(int dp) {
            super();
            this.radius = Resources.getSystem().getDisplayMetrics().density * dp;
        }


        @Override
        protected Bitmap transform(@NonNull BitmapPool pool, @NonNull Bitmap toTransform, int outWidth, int outHeight) {
            //变换的时候裁切
            Bitmap bitmap = TransformationUtils.centerCrop(pool, toTransform, outWidth, outHeight);
            return roundCrop(pool, bitmap);
        }

        @Override
        public void updateDiskCacheKey(MessageDigest messageDigest) {

        }


        private static Bitmap roundCrop(BitmapPool pool, Bitmap source) {
            if (source == null) {
                return null;
            }
            Bitmap result = pool.get(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
            if (result == null) {
                result = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
            }
            Canvas canvas = new Canvas(result);
            Paint paint = new Paint();
            paint.setShader(new BitmapShader(source, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
            paint.setAntiAlias(true);
            RectF rectF = new RectF(0f, 0f, source.getWidth(), source.getHeight());
            canvas.drawRoundRect(rectF, radius, radius, paint);
            //左上角、右上角圆角
            //        RectF rectRound = new RectF(0f, 100f, source.getWidth(), source.getHeight());
            //        canvas.drawRect(rectRound, paint);
            return result;
        }
    }
}
