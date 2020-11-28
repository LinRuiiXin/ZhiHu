package com.sz.zhihu.adapter;


import android.app.Activity;
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
import com.sz.zhihu.po.User;

import java.util.List;
import java.util.function.Consumer;

public class AttentionListAdapter extends RecyclerView.Adapter {

    private final Activity activity;
    private final List<User> data;
    private final LayoutInflater inflater;
    private final String serverLocation;
    private final Consumer<User> consumer;

    public AttentionListAdapter(Activity activity, List<User> data, Consumer<User> consumer){
        this.activity = activity;
        this.data = data;
        this.consumer = consumer;
        inflater = LayoutInflater.from(activity);
        serverLocation = activity.getString(R.string.server_location);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.fic_attention_list_item,parent,false));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof ViewHolder){
            ViewHolder viewHolder = (ViewHolder) holder;
            User user = data.get(position);
            String imgUrl = serverLocation + "/res/User/"+user.getId()+"/"+user.getPortraitFileName();
            Glide.with(activity).load(imgUrl).into(viewHolder.portrait);
            viewHolder.userName.setText(user.getUserName());
            viewHolder.itemView.setOnClickListener(v->consumer.accept(user));
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        private final RoundedImageView portrait;
        private final TextView userName;

        public ViewHolder(@NonNull View view) {
            super(view);
            portrait = view.findViewById(R.id.fali_portrait);
            userName = view.findViewById(R.id.fali_user_name);
        }
    }
}
