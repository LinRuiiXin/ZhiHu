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

import com.sz.zhihu.R;
import com.sz.zhihu.po.Keyword;

import java.util.List;
import java.util.function.Consumer;

public class SearchKeywordResultAdapter extends RecyclerView.Adapter {

    private final Activity activity;
    private final List<Keyword> data;
    private final LayoutInflater inflater;
    private Consumer<Keyword> onItemClickListener;

    public SearchKeywordResultAdapter(Activity activity, List<Keyword> data, Consumer<Keyword> onItemClickListener){
        this.activity = activity;
        this.data = data;
        inflater = LayoutInflater.from(activity);
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.search_keyword_result_item,parent,false));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof ViewHolder){
            ViewHolder viewHolder = (ViewHolder) holder;
            Keyword keyword = data.get(position);
            viewHolder.text.setText(keyword.getTitle());
            viewHolder.itemView.setOnClickListener(v -> {
                onItemClickListener.accept(keyword);
            });
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        private final TextView text;

        public ViewHolder(@NonNull View view) {
            super(view);
            text = view.findViewById(R.id.skr_text);
        }
    }
}
