package com.sz.zhihu.adapter;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.load.resource.bitmap.TransformationUtils;
import com.bumptech.glide.request.RequestOptions;
import com.makeramen.roundedimageview.RoundedImageView;
import com.sz.zhihu.R;
import com.sz.zhihu.vo.Information;

import java.security.MessageDigest;
import java.util.List;
import java.util.function.Consumer;

import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;

public class SearchInformationAdapter extends RecyclerView.Adapter {

    private Activity activity;
    private List<Information> data;
    private final LayoutInflater inflater;
    private Consumer<Information> consumer;

    private final int VIEW_ALL_TEXT = 1;
    private final int VIEW_HAS_IMAGE = 2;
    private final int VIEW_HAS_VIDEO = 3;
    private final String serverLocation;

    public SearchInformationAdapter(Activity activity, List<Information> data, Consumer<Information> consumer){
        this.activity = activity;
        this.data = data;
        inflater = LayoutInflater.from(activity);
        this.consumer = consumer;
        serverLocation = activity.getString(R.string.server_location);
    }

    @Override
    public int getItemViewType(int position) {
        Information information = data.get(position);
        int type = information.getType();
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
        RecyclerView.ViewHolder res = null;
        switch (viewType){
            case VIEW_ALL_TEXT:
                res = new AllTextViewHolder(inflater.inflate(R.layout.information_item_all_text,parent,false));
                break;
            case VIEW_HAS_IMAGE:
                res = new HasImageViewHolder(inflater.inflate(R.layout.information_item_has_image,parent,false));
                break;
            case VIEW_HAS_VIDEO:
                res = new HasVideoViewHolder(inflater.inflate(R.layout.information_item_has_video,parent,false));
                break;
        }
        return res;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Information information = data.get(position);
        if(holder instanceof InformationViewHolder){
            InformationViewHolder informationViewHolder = (InformationViewHolder) holder;
            informationViewHolder.loadView(information);
            informationViewHolder.itemView.setOnClickListener(v->consumer.accept(information));
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    private RequestOptions getOptions() {
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.mipmap.ic_launcher_round) //预加载图片
                .error(R.drawable.ic_launcher_foreground) //加载失败图片
                .priority(Priority.HIGH) //优先级
                .diskCacheStrategy(DiskCacheStrategy.NONE) //缓存
                .transform(new RecommendRecyclerViewAdapter.GlideRoundTransform(5)); //圆角
        return options;
    }

    abstract class InformationViewHolder extends RecyclerView.ViewHolder{

        public InformationViewHolder(@NonNull View itemView) {
            super(itemView);
        }
        /*
        * 渲染View的方法，子类需实现该方法渲染数据
        * */
        public abstract void loadView(Information information);
    }

    class AllTextViewHolder extends InformationViewHolder {

        private final TextView title;
        private final RoundedImageView portrait;
        private final TextView userName;
        private final TextView profile;
        private final TextView content;

        public AllTextViewHolder(@NonNull View view) {
            super(view);
            title = view.findViewById(R.id.iiat_all_text_title);
            portrait = view.findViewById(R.id.iiat_all_text_portrait);
            userName = view.findViewById(R.id.iiat_all_text_username);
            profile = view.findViewById(R.id.iiat_all_text_introduction);
            content = view.findViewById(R.id.iiat_all_text_content);
        }

        @Override
        public void loadView(Information information) {
            String url = serverLocation + "/res/User/" + information.getAuthorId() + "/" + information.portraitFileName();
            title.setText(information.title());
            Glide.with(activity).load(url).into(portrait);
            userName.setText(information.authorName());
            profile.setText(information.getProfile());
            content.setText(information.getContent());
        }
    }

    class HasImageViewHolder extends InformationViewHolder {

        private final TextView title;
        private final RoundedImageView portrait;
        private final TextView userName;
        private final TextView profile;
        private final TextView content;
        private final ImageView image;

        public HasImageViewHolder(@NonNull View view) {
            super(view);
            title = view.findViewById(R.id.iihi_title);
            portrait = view.findViewById(R.id.iihi_portrait);
            userName = view.findViewById(R.id.iihi_username);
            profile = view.findViewById(R.id.iihi_introduction);
            content = view.findViewById(R.id.iihi_content);
            image = view.findViewById(R.id.iihi_image);
        }

        @Override
        public void loadView(Information information) {
            String portraitUrl = serverLocation + "/res/User/" + information.getAuthorId() + "/" + information.getPortraitFileName();
            String imageUrl = serverLocation + "/res/Image/" + information.getThumbnail();
            title.setText(information.getTitle());
            Glide.with(activity).load(portraitUrl).into(portrait);
            userName.setText(information.getAuthorName());
            profile.setText(information.getProfile());
            content.setText(information.getContent());
            Glide.with(activity).load(imageUrl).apply(getOptions()).into(image);
        }
    }

    class HasVideoViewHolder extends InformationViewHolder {

        private final TextView title;
        private final RoundedImageView portrait;
        private final TextView userName;
        private final TextView profile;
        private final JCVideoPlayerStandard video;
        private final TextView content;

        public HasVideoViewHolder(@NonNull View view) {
            super(view);
            title = view.findViewById(R.id.iihv_title);
            portrait = view.findViewById(R.id.iihv_portrait);
            userName = view.findViewById(R.id.iihv_username);
            profile = view.findViewById(R.id.iihv_introduction);
            video = view.findViewById(R.id.iihv_video);
            video.backButton.setVisibility(View.GONE);
            video.tinyBackImageView.setVisibility(View.GONE);
            content = view.findViewById(R.id.iihv_content);
        }

        @Override
        public void loadView(Information information) {
            String portraitUrl = serverLocation + "/res/User/" + information.getAuthorId() + "/" + information.getPortraitFileName();
            String videoUrl= serverLocation + "/res/Video/" + information.getThumbnail();
            title.setText(information.getTitle());
            Glide.with(activity).load(portraitUrl).into(portrait);
            userName.setText(information.authorName());
            profile.setText(information.getProfile());
            video.setUp(videoUrl, JCVideoPlayerStandard.SCREEN_LAYOUT_NORMAL);
            video.startVideo();
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
