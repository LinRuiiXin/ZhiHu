package com.sz.zhihu.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.sz.zhihu.R;
import com.sz.zhihu.po.VideoMedia;

import java.io.File;
import java.util.List;

public class EditVideoDialogAdapter extends BaseQuickAdapter {
    private Activity activity;
    public EditVideoDialogAdapter(Activity activity, @Nullable List data) {
        super(R.layout.edit_video_item, data);
        this.activity = activity;
    }

    @Override
    protected void convert(BaseViewHolder helper, Object item) {
        VideoMedia videoMedia = (VideoMedia) item;
        helper.setText(R.id.evi_duration,getVideoDuration(videoMedia.getDuration()));
        Glide.with(activity).load(new File(videoMedia.getThumbPath())).into((ImageView) helper.getView(R.id.evi_image));
    }
    /*private Activity activity;
    private List<VideoMedia> data;
    public EditVideoDialogAdapter(Activity activity, List<VideoMedia> data){
        this.activity = activity;
        this.data = data;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new EditVideoViewHolder(LayoutInflater.from(activity).inflate(R.layout.edit_video_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof EditVideoViewHolder){
            EditVideoViewHolder viewHolder = (EditVideoViewHolder) holder;
            VideoMedia videoMedia = data.get(position);
            Glide.with(activity).load(new File(videoMedia.getThumbPath())).into(viewHolder.imageView);
            viewHolder.duration.setText(getVideoDuration(videoMedia.getDuration()));
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
    */
    private String getVideoDuration(int duration) {
        String durationStr = "";
        int second = duration / 1000;
        int min = second / 60;
        if(min - 10 < 0){
            durationStr += "0" + min+":";
        }else durationStr += min+":";
        if(second<60){
            if(second-10<0){
                durationStr += "0"+second;
            }else durationStr += second;
        }else durationStr += (second%60);
        return durationStr;
    }
    /*
    class EditVideoViewHolder extends RecyclerView.ViewHolder{

        private final ImageView imageView;
        private final TextView duration;

        public EditVideoViewHolder(@NonNull View view) {
            super(view);
            imageView = view.findViewById(R.id.evi_image);
            duration = view.findViewById(R.id.evi_duration);
        }
    }*/
}
