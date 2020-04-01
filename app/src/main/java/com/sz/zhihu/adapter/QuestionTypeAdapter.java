package com.sz.zhihu.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.sz.zhihu.AskQuestionActivity;
import com.sz.zhihu.R;
import com.sz.zhihu.dto.SimpleDto;
import com.sz.zhihu.po.Classify;
import com.sz.zhihu.utils.RequestUtils;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class QuestionTypeAdapter extends RecyclerView.Adapter {
    private Activity context;
    private List<Classify> data;
    private final LayoutInflater inflater;
    private final int blue;
    private final String severLocation;
    private EditText editText;
    private final Gson gson;
    private final int black;
    private Consumer<Integer> consumer;
    private List<Classify> classifies = new ArrayList<>();
    private final Drawable enable;
    private final Drawable disable;

    public QuestionTypeAdapter(Activity context, EditText editText,Consumer<Integer> consumer) {
        this.context = context;
        this.editText = editText;
        this.consumer = consumer;
        inflater = LayoutInflater.from(context);
        gson = new Gson();
        Resources resources = context.getResources();
        blue = resources.getColor(R.color.ZhiHuBlue);
        black = resources.getColor(R.color.black);
        severLocation = resources.getString(R.string.server_location);
        enable = resources.getDrawable(R.drawable.login_log_button_shape_enable);
        disable = resources.getDrawable(R.drawable.login_log_button_shape_disable);
    }
    public void setData(List<Classify> classifies){
        data = classifies;
    }

    @Override
    public int getItemViewType(int position) {
        if(position == data.size()){
            return 1;
        }else{
            return 0;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == 0){
            return new TypeViewHolder(inflater.inflate(R.layout.dialog_search_item,parent,false));
        }else{
            return new LoadMoreViewHolder(inflater.inflate(R.layout.dialog_search_load_more,parent,false));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(position < data.size()){
            TypeViewHolder typeViewHolder = (TypeViewHolder) holder;
            Classify classify = data.get(position);
            typeViewHolder.name.setText(classify.getName());
            typeViewHolder.button.setOnClickListener(v->{
                if(!AskQuestionActivity.types.contains(classify)){
                    AskQuestionActivity.types.add(classify);
                    typeViewHolder.button.setBackground(disable);
                    typeViewHolder.button.setText("取消");
                }else{
                    AskQuestionActivity.types.remove(classify);
                    typeViewHolder.button.setBackground(enable);
                    typeViewHolder.button.setText("添加");
                }
                consumer.accept(AskQuestionActivity.types.size());
            });
        }else{
            LoadMoreViewHolder loadMoreViewHolder = (LoadMoreViewHolder) holder;
            if(data.size() != 0){
                loadMoreViewHolder.itemView.setVisibility(View.VISIBLE);
                if(data.size() % 5 == 0){
                    loadMoreViewHolder.loadMore.setText("点击加载更多");
                    loadMoreViewHolder.loadMore.setTextColor(blue);
                    loadMoreViewHolder.itemView.setOnClickListener(v->{
                        String url = severLocation + "/Classify/" + editText.getText().toString() + "/" + data.size();
                        RequestUtils.sendSimpleRequest(url, new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                context.runOnUiThread(()-> Toast.makeText(context,"请求失败",Toast.LENGTH_SHORT).show());
                            }

                            @RequiresApi(api = Build.VERSION_CODES.N)
                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                SimpleDto simpleDto = gson.fromJson(response.body().string(), SimpleDto.class);
                                context.runOnUiThread(()->{
                                    if(simpleDto.isSuccess()){
                                        List list = gson.fromJson(simpleDto.getObject().toString(),List.class);
                                        list.forEach(o -> {
                                            QuestionTypeAdapter.this.data.add(gson.fromJson(o.toString(),Classify.class));
                                        });
                                        QuestionTypeAdapter.this.notifyDataSetChanged();
                                    }
                                });
                            }
                        });
                    });
                }else{
                    loadMoreViewHolder.loadMore.setText("没有更多了");
                    loadMoreViewHolder.loadMore.setTextColor(black);
                }
            }else {
                loadMoreViewHolder.itemView.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return data.size()+1;
    }

    class TypeViewHolder extends RecyclerView.ViewHolder{

        private final TextView name;
        private final Button button;

        public TypeViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.dialog_search_item_name);
            button = itemView.findViewById(R.id.dialog_search_item_add);
        }
    }
    class LoadMoreViewHolder extends RecyclerView.ViewHolder{

        private final TextView loadMore;

        public LoadMoreViewHolder(@NonNull View itemView) {
            super(itemView);
            loadMore = itemView.findViewById(R.id.dialog_search_load_more);
        }
    }
}
