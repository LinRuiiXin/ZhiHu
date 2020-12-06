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
import com.google.gson.Gson;
import com.makeramen.roundedimageview.RoundedImageView;
import com.sz.zhihu.R;
import com.sz.zhihu.utils.GsonUtils;
import com.sz.zhihu.vo.UserDoc;

import java.util.List;
import java.util.function.Consumer;

public class SearchUserAdapter extends RecyclerView.Adapter {

    private final Activity activity;
    private final List<UserDoc> data;
    private final LayoutInflater inflater;
    private final String serverLocation;
    private Consumer<UserDoc> consumer;

    public SearchUserAdapter(Activity activity, List<UserDoc> data, Consumer<UserDoc> consumer){
        this.activity = activity;
        this.data = data;
        inflater = LayoutInflater.from(activity);
        serverLocation = activity.getString(R.string.server_location);
        this.consumer = consumer;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.search_user_item,parent,false));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof ViewHolder){
            ViewHolder viewHolder = (ViewHolder) holder;
            viewHolder.loadView(data.get(position));
            viewHolder.itemView.setOnClickListener(v->consumer.accept(data.get(position)));
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        private final RoundedImageView portrait;
        private final TextView userName;
        private final TextView profile;

        public ViewHolder(@NonNull View view) {
            super(view);
            portrait = view.findViewById(R.id.sui_portrait);
            userName = view.findViewById(R.id.sui_user_name);
            profile = view.findViewById(R.id.sui_profile);
        }

        public void loadView(UserDoc userDoc){
            String portraitUrl = serverLocation + "/res/User/" + userDoc.getId() + "/" + userDoc.getPortraitFileName();
            Glide.with(activity).load(portraitUrl).into(portrait);
            userName.setText(userDoc.getUserName());
            profile.setText(userDoc.getProfile());
        }
    }
}
